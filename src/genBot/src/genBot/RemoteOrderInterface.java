package genBot;

import java.io.IOException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.sql.SQLException;
import java.util.Properties;

import serialRMI.SerialRMIException;

public interface RemoteOrderInterface extends Remote {

	public CocktailWithName[] getNamedPopulation(String evolutionStackName) throws RemoteException;
	
	public void setCocktailFitness(String evolutionStackName, String name, double fitnessInput) throws RemoteException;
	
	public boolean canEvolve(String evolutionStackName) throws RemoteException;
	
	public void evolve(String evolutionStackName) throws RemoteException, NotEnoughRatedCocktailsException;
	
	public void loadEvolutionStack(String evolutionStackName) throws Exception;
	
	public String[] listPossibleEvolutionStacks() throws RemoteException;
	
	public String[] listLoadedEvolutionStacks() throws RemoteException;
	
	//public boolean containsEvolutionStack(String evolutionStackName) throws RemoteException;
	
	public Properties getProps(String evolutionStackName) throws RemoteException, IOException;
	
	public void queueCocktail(CocktailWithName cocktail) throws RemoteException;
	
	public void queueCocktail(String evolutionStackName, String cocktailName) throws RemoteException;
	
	public void deleteCocktailFromQueue(String cocktailName) throws RemoteException;
	
	public void reorderQueue(String cocktailNameList[]) throws RemoteException;
	
	public CocktailQueue getQueue() throws RemoteException;
	
	public CocktailWithName getCurrentlyPouringCocktail() throws RemoteException;

	//public CocktailGenerationManager getOldGeneration(String evolutionStackName, int generationNumber) throws Exception;
	
	//public CocktailGenerationManager readGenerationManager(String evolutionStackName) throws RemoteException;
	
	public int getCocktailSize() throws RemoteException;
	
	public void setCocktailSize(int milliLiters) throws RemoteException;
	
	public double getMutationStdDeviation(String evolutionStackName) throws RemoteException;
	
	public void setMutationStdDeviation(String evolutionStackName, double stdDeviation) throws RemoteException;
	
	public boolean isEvolutionStackNameLoaded(String evolutionStackName) throws RemoteException;
	
	public void removeEvolutionStack(String evolutionStackName) throws RemoteException;
	
	public void deleteEvolutionStack(String evolutionStackName) throws RemoteException;
	
	public String getStatusMessage() throws RemoteException;

	public void setMaxPricePerLiter(String evolutionStackName, double maxPricePerLiter)
			throws RemoteException;

	public double getMaxPricePerLiter(String evolutionStackName)
			throws RemoteException;
	
	public void sendToArduino(ArduinoMessage m) throws RemoteException, SerialRMIException;

	public Ingredient[] getIngredients() throws RemoteException;

	public Ingredient[] getAllowedIngredients(String evolutionStackName) throws RemoteException;

	public int getStatusCode() throws RemoteException;

	public void setCocktailToUnpoured(String evolutionStackName, String name) throws RemoteException;

	public ArduinoMessage[] getReceivedMessages()  throws RemoteException;
}
