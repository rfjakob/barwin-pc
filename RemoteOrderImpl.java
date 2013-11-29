package genBot2;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.rmi.RemoteException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Properties;

public class RemoteOrderImpl implements RemoteOrderInterface {
	
	private EvolutionStackContainer evolutionStackController;
	private QueueManager queueManager;

	// make sure this is the same as in EvolutionAlgorithmManager.java
	private String propertiesPath = "evolutionStackSettings/";
	
	public RemoteOrderImpl(QueueManager queueManager) {
		this.evolutionStackController = EvolutionStackContainer.getInstance();
		this.queueManager = queueManager;
	}

	@Override
	public CocktailWithName[] getNamedPopulation(String evolutionStackName) throws RemoteException {
		return evolutionStackController.getEvolutionAlgorithmManager(evolutionStackName).getGenManager().getNamedCocktailGeneration();
	}

	@Override
	public void setCocktailFitness(String evolutionStackName, String name, double fitnessInput) throws RemoteException, SQLException, NotEnoughRatedCocktailsException {
		EvolutionAlgorithmManager evoAlgMngr = evolutionStackController.getEvolutionAlgorithmManager(evolutionStackName);		

		evoAlgMngr.setFitness(name, fitnessInput);
		
		if (evoAlgMngr.getGenManager().getUnRatedNamedCocktailGeneration().length == 0) {
			evoAlgMngr.evolve();
		}
	}

	@Override
	public boolean canEvolve(String evolutionStackName) throws RemoteException {
		return evolutionStackController.getEvolutionAlgorithmManager(evolutionStackName).canEvolve();
	}

	@Override
	public void evolve(String evolutionStackName) throws RemoteException, SQLException, NotEnoughRatedCocktailsException {
		evolutionStackController.getEvolutionAlgorithmManager(evolutionStackName).evolve();
	}

	@Override
	public void generateEvolutionStack(String evolutionStackName, String fitnessCheckName,
			String recombinationName, boolean resetDbTable, String propPath, double stdDeviation)
			throws RemoteException, SQLException {
		CheckFitness fitnessCheck = new EfficientCocktail();
		if (!fitnessCheckName.equals("EfficientCocktail")) {
			fitnessCheck = null;
		}
		Recombination recombination = new MutationAndIntermediateRecombination(0.25, stdDeviation);
		if (recombinationName.equals("StandardMutation")) {
			recombination = new StandardMutation(stdDeviation);
		} else if (recombinationName.equals("IntermediateRecombination")) {
			recombination = new IntermediateRecombination(0.25);
		}
		// variableArea is hard coded... but it should be 0.25

		evolutionStackController.addEvolutionAlgorithmManager(evolutionStackName, fitnessCheck, recombination, resetDbTable, propPath);
	}

	@Override
	public void generateEvolutionStack(String evolutionStackName,
			Ingredient[] allowedIngredients, int populationSize,
			int truncation, int elitism, String dbDriverPath,
			boolean resetDbTable, String fitnessCheckName, String recombinationName, double stdDeviation,
			String propPath) throws RemoteException, SQLException {
		CheckFitness fitnessCheck = new EfficientCocktail();
		if (!fitnessCheckName.equals("EfficientCocktail")) {
			fitnessCheck = null;
		}
		Recombination recombination = new MutationAndIntermediateRecombination(0.25, stdDeviation);
		if (recombinationName.equals("StandardMutation")) {
			recombination = new StandardMutation(stdDeviation);
		} else if (recombinationName.equals("IntermediateRecombination")) {
			recombination = new IntermediateRecombination(0.25);
		}
		// variableArea is hard coded... but it should be 0.25
		
		evolutionStackController.addEvolutionAlgorithmManager(evolutionStackName, allowedIngredients, populationSize, truncation, elitism, dbDriverPath, resetDbTable, fitnessCheck, recombination, stdDeviation, propPath);
	}

	@Override
	public void generateEvolutionStack(String evolutionStackName,
			Ingredient[] allowedIngredients) throws RemoteException,
			SQLException {
		
		generateEvolutionStack(evolutionStackName, allowedIngredients, 15, 3, 2, "cocktailDataBase", true, "EfficientCocktail", "", 0.05, evolutionStackName);
	}
	
	@Override
	public String[] listPossibleEvolutionStacks() throws RemoteException {		
		String files;
		File folder = new File(propertiesPath);
		File[] listOfFiles = folder.listFiles();
		
		ArrayList<String> fileNames = new ArrayList<String>();
		
		for (int i = 0; i < listOfFiles.length; i++) {
			if (listOfFiles[i].isFile()) {
				files = listOfFiles[i].getName();
				
				if (files.endsWith(".properties") || files.endsWith(".PROPERTIES")) {
					String files1 = files.substring(0, files.lastIndexOf('.'));
					fileNames.add(files1);
				}
			}
		}
		
		return fileNames.toArray(new String[fileNames.size()]);
	}

	@Override
	public void loadEvolutionStack(String evolutionStackName)
			throws RemoteException, SQLException {
		
		String[] possibleNames = listPossibleEvolutionStacks();
		boolean containsName = false;
		
		for (int i = 0; i < possibleNames.length; i++) {
			if (possibleNames[i].equals(evolutionStackName)) {
				containsName = true;
			}
		}
		
		if (!containsName) {
			throw new IllegalArgumentException(evolutionStackName + " is not a .properties file in the folder");
		}
		
		CheckFitness fitnessCheck = new EfficientCocktail();
		
		Properties props = loadProps(evolutionStackName);
		
		// variableArea is hard coded... but it should be 0.25
		Recombination recombination = new MutationAndIntermediateRecombination(0.25, Double.parseDouble(props.getProperty("stdDeviation")));
		
		evolutionStackController.addEvolutionAlgorithmManager(evolutionStackName, fitnessCheck, recombination, false, evolutionStackName);
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
	public Properties getProps(String evolutionStackName) throws RemoteException, FileNotFoundException {
		return evolutionStackController.getEvolutionAlgorithmManager(evolutionStackName).loadProps();
	}

	@Override
	public void setProps(String evolutionStackName, int populationSize,
			int truncation, int elitism, double stdDeviation,
			String dbDriverPath, String booleanAllowedIngredientsString)
			throws RemoteException {
		evolutionStackController.getEvolutionAlgorithmManager(evolutionStackName).storeProps(evolutionStackName, populationSize, truncation, elitism, stdDeviation, dbDriverPath, booleanAllowedIngredientsString);
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
	public CocktailGenerationManager getOldGeneration(String evolutionStackName, int generationNumber) throws RemoteException, SQLException {
		return evolutionStackController.getEvolutionAlgorithmManager(evolutionStackName).getOldGeneration(generationNumber);
	}

	@Override
	public CocktailQueue getQueue() throws RemoteException {
		return queueManager.getQueue();
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
		return queueManager.getCocktailSizeMilliliter();
	}

	@Override
	public void setCocktailSize(int milliLiters) throws RemoteException {
		queueManager.setCocktailSizeMilliliter(milliLiters);
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
	
	private Properties loadProps(String propFile) {
		Properties props = new Properties();
		
		try {
			props.load(new FileInputStream(propertiesPath + propFile + ".properties"));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return props;
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
	public void deleteEvolutionStack(String evolutionStackName)
			throws RemoteException, SQLException {
		
		// unload stack if it is loaded
		if (isEvolutionStackNameLoaded(evolutionStackName)) {
			removeEvolutionStack(evolutionStackName);
		}
		
		// get the database file
		Properties props = loadProps(evolutionStackName);
		String dbFile = props.getProperty("dbDriverPath");
		
		DataBaseDriver dbDriver = DataBaseDriver.getInstance(dbFile);
		
		// delete from data base
		dbDriver.delete(evolutionStackName);

		// delete from directory
		File f = new File(propertiesPath + evolutionStackName + ".properties");

	    // Make sure the file or directory exists and isn't write protected
	    if (!f.exists())
	      throw new IllegalArgumentException(
	          "Delete: no such file or directory: " + evolutionStackName + ".properties");

	    if (!f.canWrite())
	      throw new IllegalArgumentException("Delete: write protected: "
	          + evolutionStackName + ".properties");

	    // If it is a directory, make sure it is empty
	    if (f.isDirectory()) {
	      String[] files = f.list();
	      if (files.length > 0)
	        throw new IllegalArgumentException(
	            "Delete: directory not empty: " + evolutionStackName + ".properties");
	    }
	}
	
}
