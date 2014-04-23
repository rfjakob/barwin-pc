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
	public void setCocktailFitness(String evolutionStackName, String name, double fitnessInput) throws RemoteException, SQLException {
		EvolutionAlgorithmManager evoAlgMngr = evolutionStackController.getEvolutionAlgorithmManager(evolutionStackName);		

		evoAlgMngr.setFitness(name, queueManager.getCocktailSizeMilliliter() / 1000, fitnessInput);
		
		if (evoAlgMngr.getGenManager().getUnRatedNamedCocktailGeneration().length == 0) {
			try {
				evoAlgMngr.evolve();
			} catch (NotEnoughRatedCocktailsException e) {
				e.printStackTrace();
			}
		}
	}
	
	@Override
	public void setCocktailToUnpoured(String evolutionStackName, String name) throws RemoteException, SQLException {
		evolutionStackController.getEvolutionAlgorithmManager(evolutionStackName).getGenManager().getCocktailByName(name).setPoured(false);
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
			String recombinationName, boolean resetDbTable, String propPath, double stdDeviation, double[] initMeanValues, double[] initOffsets, double maxPricePerLiter)
			throws RemoteException, SQLException, MaxAttemptsToMeetPriceConstraintException {
		CheckFitness fitnessCheck = new EfficientCocktail();
		if (!fitnessCheckName.equals("EfficientCocktail")) {
			fitnessCheck = null;
		}
		Recombination recombination = new MutationAndIntermediateRecombination(0.25, stdDeviation, maxPricePerLiter);
		if (recombinationName.equals("StandardMutation")) {
			recombination = new StandardMutation(stdDeviation, maxPricePerLiter);
		} else if (recombinationName.equals("IntermediateRecombination")) {
			recombination = new IntermediateRecombination(0.25, maxPricePerLiter);
		}
		// variableArea is hard coded... but it should be 0.25

		evolutionStackController.addEvolutionAlgorithmManager(evolutionStackName, fitnessCheck, recombination, resetDbTable, propPath);
	}

	@Override
	public void generateEvolutionStack(String evolutionStackName,
			Ingredient[] allowedIngredients, int populationSize,
			int truncation, int elitism, String dbDriverPath,
			boolean resetDbTable, String fitnessCheckName, String recombinationName, double stdDeviation, double[] initMeanValues, double[] initOffsets, double maxPricePerLiter,
			String propPath) throws RemoteException, SQLException, MaxAttemptsToMeetPriceConstraintException {
		CheckFitness fitnessCheck = new EfficientCocktail();
		if (!fitnessCheckName.equals("EfficientCocktail")) {
			fitnessCheck = null;
		}
		Recombination recombination = new MutationAndIntermediateRecombination(0.25, stdDeviation, maxPricePerLiter);
		if (recombinationName.equals("StandardMutation")) {
			recombination = new StandardMutation(stdDeviation, maxPricePerLiter);
		} else if (recombinationName.equals("IntermediateRecombination")) {
			recombination = new IntermediateRecombination(0.25, maxPricePerLiter);
		}
		// variableArea is hard coded... but it should be 0.25
		
		evolutionStackController.addEvolutionAlgorithmManager(evolutionStackName, allowedIngredients, populationSize, truncation, elitism, dbDriverPath, resetDbTable, fitnessCheck, recombination, stdDeviation, initMeanValues, initOffsets, maxPricePerLiter, propPath);
	}

	@Override
	public void generateEvolutionStack(String evolutionStackName,
			Ingredient[] allowedIngredients, double[] initMeanValues, double[] initOffsets, double maxPricePerLiter) throws RemoteException,
			SQLException, MaxAttemptsToMeetPriceConstraintException {
		
		generateEvolutionStack(evolutionStackName, allowedIngredients, 8, 2, 1, "cocktailDataBase", true, "EfficientCocktail", "", 0.05, initMeanValues, initOffsets, maxPricePerLiter, evolutionStackName);
	}
	
	@Override
	public String[] listPossibleEvolutionStacks() throws RemoteException {		
		String files;
		
		/*File folder2 = new File(".");
		File[] listOfFiles2 = folder2.listFiles();
		for (int i = 0; i < listOfFiles2.length; i++) {
			System.out.println(listOfFiles2[i].getName());
		}*/

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
			throws RemoteException, SQLException, MaxAttemptsToMeetPriceConstraintException, FileNotFoundException {
		
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
		
		Properties props = getProps(evolutionStackName);
		
		// variableArea is hard coded... but it should be 0.25
		Recombination recombination = new MutationAndIntermediateRecombination(0.25, Double.parseDouble(props.getProperty("stdDeviation")), Double.parseDouble(props.getProperty("maxPricePerLiter")));
		
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
		return EvolutionAlgorithmManager.loadProps(evolutionStackName);
	}
	

	@Override
	public void setProps(Properties props)
			throws RemoteException {
		String evolutionStackName = props.getProperty("evolutionStackName");
		int populationSize = Integer.parseInt(props.getProperty("populationSize"));
		int truncation = Integer.parseInt(props.getProperty("truncation"));
		int elitism = Integer.parseInt(props.getProperty("elitism"));
		double stdDeviation = Double.parseDouble(props.getProperty("stdDeviation"));
		double maxPricePerLiter = Double.parseDouble(props.getProperty("maxPricePerLiter"));
		String  dbDriverPath = props.getProperty("dbDriverPath");
		String booleanAllowedIngredientsString = props.getProperty("booleanAllowedIngredients");

		
		String[] initMeanStrings = props.getProperty("initMeanValue").split(" ");
		String[] initOffsetStrings = props.getProperty("initOffsets").split(" ");
		
		double[] initMeanValues = new double[booleanAllowedIngredientsString.length()];
		double[] initOffsets = new double[booleanAllowedIngredientsString.length()];
		
		for (int i = 0; i < initMeanStrings.length; i++) {
			initMeanValues[i] = Double.parseDouble(initMeanStrings[i]);
			initOffsets[i] = Double.parseDouble(initOffsetStrings[i]);
		}
		
		setProps(evolutionStackName, populationSize, truncation, elitism, stdDeviation, initMeanValues, initOffsets, maxPricePerLiter, dbDriverPath, booleanAllowedIngredientsString);
	}

	@Override
	public void setProps(String evolutionStackName, int populationSize,
			int truncation, int elitism, double stdDeviation, double[] initMeanValues, double[] initOffsets, double maxPricePerLiter,
			String dbDriverPath, String booleanAllowedIngredientsString)
			throws RemoteException {
		evolutionStackController.getEvolutionAlgorithmManager(evolutionStackName).storeProps(evolutionStackName, populationSize, truncation, elitism, stdDeviation, initMeanValues, initOffsets, maxPricePerLiter, dbDriverPath, booleanAllowedIngredientsString);
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
	
	@Override
	public double getMaxPricePerLiter(String evolutionStackName) throws RemoteException {
		return evolutionStackController.getEvolutionAlgorithmManager(evolutionStackName).getMaxPricePerLiter();
	}

	@Override
	public void setMaxPricePerLiter(String evolutionStackName, double maxPricePerLiter)
			throws RemoteException {
		evolutionStackController.getEvolutionAlgorithmManager(evolutionStackName).setMaxPricePerLiter(maxPricePerLiter);
	}	
	
	/*private Properties loadProps(String propFile) {
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
	}*/

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
		Properties props = EvolutionAlgorithmManager.loadProps(evolutionStackName);
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
	    
	    // Attempt to delete it
	    boolean success = f.delete();

	    if (!success) {
	    	throw new IllegalArgumentException("Delete: deletion failed");
	    }
	}
	
	@Override
	public Ingredient[] getAllowedIngredients(String evolutionStackName) throws RemoteException {
		boolean[] allIngsBool = evolutionStackController.getEvolutionAlgorithmManager(evolutionStackName).getBooleanAllowedIngredients();
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
		return IngredientArray.getInstance()
				.getAllIngredients();
	}

}
