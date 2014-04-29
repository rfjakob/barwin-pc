package genBot;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.rmi.RemoteException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Properties;

import serialRMI.SerialRMIException;

public class RemoteOrderImpl implements RemoteOrderInterface {
	
	private EvolutionStackContainer evolutionStackController;
	private QueueManager queueManager;

	// make sure this is the same as in EvolutionAlgorithmManager.java
	private String propertiesPath = "../etc/evolutionStackSettings/";
	
	public RemoteOrderImpl(QueueManager queueManager) {
		this.evolutionStackController = EvolutionStackContainer.getInstance();
		this.queueManager = queueManager;
	}

	@Override
	public CocktailWithName[] getNamedPopulation(String evolutionStackName) throws RemoteException {
		return evolutionStackController.getEvolutionAlgorithmManager(evolutionStackName).getGenManager().getNamedCocktailGeneration();
	}

	@Override
	public void setCocktailFitness(String evolutionStackName, String name, double rating) throws RemoteException {
		EvolutionAlgorithmManager evoAlgMngr = evolutionStackController.getEvolutionAlgorithmManager(evolutionStackName);		
		evoAlgMngr.setRating(name, rating);
	}
	
	@Override
	public void setCocktailToUnpoured(String evolutionStackName, String name) throws RemoteException {
		evolutionStackController.getEvolutionAlgorithmManager(evolutionStackName).getGenManager().getCocktailByName(name).setPoured(false);
	}

	@Override
	public boolean canEvolve(String evolutionStackName) throws RemoteException {
		return evolutionStackController.getEvolutionAlgorithmManager(evolutionStackName).canEvolve();
	}

	@Override
	public void evolve(String evolutionStackName) throws RemoteException, NotEnoughRatedCocktailsException {
		evolutionStackController.getEvolutionAlgorithmManager(evolutionStackName).evolve();
	}
	
	@Override
	public String[] listPossibleEvolutionStacks() throws RemoteException {		
		return evolutionStackController.getListOfAvailableStacks();
	}

	@Override
	public void loadEvolutionStack(String evolutionStackName) throws Exception {
		evolutionStackController.load(evolutionStackName);
	}

	@Override
	public String[] listLoadedEvolutionStacks() throws RemoteException {
		return evolutionStackController.listEvolutionStacks();
	}

	@Override
	public boolean containsEvolutionStack(String evolutionStackName) throws RemoteException {
		return evolutionStackController.containsEvolutionStack(evolutionStackName);
	}

	@Override
	public Properties getProps(String evolutionStackName) throws RemoteException, IOException {
		return EvolutionAlgorithmManager.loadProps(evolutionStackName);
	}

	@Override
	public void queueCocktail(String evolutionStackName, String cocktailName)
			throws RemoteException {
		//evolutionStackController.getEvolutionAlgorithmManager(evolutionStackName).queue(cocktailName);
		queueManager.getQueue().addCocktail(evolutionStackName, cocktailName);
	}

	@Override
	public void queueCocktail(CocktailWithName cocktail) throws RemoteException {
		queueManager.getQueue().addCocktail(cocktail);	
	}

	@Override
	public void deleteCocktailFromQueue(String cocktailName) throws RemoteException {
		queueManager.getQueue().deleteCocktail(cocktailName);
	}
	
	@Override
	public void reorderQueue(String cocktailNameList[]) throws RemoteException {
		queueManager.getQueue().reorder(cocktailNameList);
	}

	@Override
	public CocktailGenerationManager getOldGeneration(String evolutionStackName, int generationNumber) throws Exception {
		return evolutionStackController.getEvolutionAlgorithmManager(evolutionStackName).load(generationNumber);
	}

	@Override
	public CocktailQueue getQueue() throws RemoteException {
		return queueManager.getQueue();
	}

	@Override
	public String getStatusMessage() throws RemoteException {
		return queueManager.getStatusMessage();
	}

	@Override
	public int getStatusCode() throws RemoteException {
		return queueManager.getStatusCode();
	}

	@Override
	public CocktailGenerationManager readGenerationManager(
			String evolutionStackName) throws RemoteException {
		return evolutionStackController.getEvolutionAlgorithmManager(evolutionStackName).getGenManager();
	}

	@Override
	public CocktailWithName getCurrentlyPouringCocktail() throws RemoteException {
		return queueManager.getCurrentlyPouringCocktail();
	}

	@Override
	public int getCocktailSize() throws RemoteException {
		return GenBotConfig.cocktailSize;
	}

	@Override
	public void setCocktailSize(int milliLiters) throws RemoteException {
		GenBotConfig.cocktailSize = milliLiters;
	}

	@Override
	public double getMutationStdDeviation(String evolutionStackName) throws RemoteException {
		return evolutionStackController.getEvolutionAlgorithmManager(evolutionStackName).getMutationStdDeviation();
	}

	@Override
	public void setMutationStdDeviation(String evolutionStackName, double stdDeviation)
			throws RemoteException {
		evolutionStackController.getEvolutionAlgorithmManager(evolutionStackName).setMutationStdDeviation(stdDeviation);
	}
	
	@Override
	public double getMaxPricePerLiter(String evolutionStackName) throws RemoteException {
		return evolutionStackController.getEvolutionAlgorithmManager(evolutionStackName).getMaxPricePerLiter();
	}

	@Override
	public void setMaxPricePerLiter(String evolutionStackName, double maxPricePerLiter)
			throws RemoteException {
		evolutionStackController.getEvolutionAlgorithmManager(evolutionStackName).setMaxPricePerLiter(maxPricePerLiter);
	}

	@Override
	public boolean isEvolutionStackNameLoaded(String evolutionStackName)
			throws RemoteException {
		String[] stacks = listLoadedEvolutionStacks();
		
		for (int i = 0; i < stacks.length; i++) {
			if (stacks[i].equals(evolutionStackName)) {
				return true;
			}
		}
		
		return false;
	}

	@Override
	public void removeEvolutionStack(String evolutionStackName)
			throws RemoteException {
		if (isEvolutionStackNameLoaded(evolutionStackName)) {
			evolutionStackController.remove(evolutionStackName);
		} else {
			throw new IllegalArgumentException(evolutionStackName + " is not loaded!");
		}
		
	}

	@Override
	public void deleteEvolutionStack(String evolutionStackName) throws RemoteException {
		// unload stack if it is loaded
		if (isEvolutionStackNameLoaded(evolutionStackName)) {
			removeEvolutionStack(evolutionStackName);
		}
		
	}
	
	@Override
	public Ingredient[] getAllowedIngredients(String evolutionStackName) throws RemoteException {
		boolean[] allIngsBool = evolutionStackController.getEvolutionAlgorithmManager(evolutionStackName).getAllowedIngredients();
		Ingredient[] ings = IngredientArray.getInstance().getAllIngredients();
		
		int numOfAllowed = 0;
		for (int i = 0; i < allIngsBool.length; i++) {
			if (allIngsBool[i]) {
				numOfAllowed++;
			}
		}
		
		Ingredient[] retIngs = new Ingredient[numOfAllowed];
		
		int allIngsCount = 0;
		for (int i = 0; i < allIngsBool.length; i++) {
			if (allIngsBool[i]) {
				retIngs[allIngsCount] = ings[i];
				allIngsCount++;
			}
		}
		
		return retIngs;
	}
	
	@Override
	public void sendToSerial(String s) throws RemoteException, SerialRMIException {
		queueManager.sendToSerial(s);
	}

	@Override
	public ArduinoMessage[] getReceivedMessages() {
		return queueManager.getReceivedMessagesAndClearQueue();
	}

	@Override
	public Ingredient[] getIngredients() throws RemoteException {
		return IngredientArray.getInstance().getAllIngredients();
	}

}
