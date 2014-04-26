package genBot;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

/*
 * some kind of a container class - this class is responsible for generating a new
 * generation of cocktails. Apart from information stored in the CocktailGeneration
 * this class stores the generation number.
 */
public class EvolutionAlgorithmManager {
	
	private CocktailGenerationManager genManager;
	
	private double stdDeviation;
	
	private double maxPricePerLiter;
	
	private CheckFitness fitnessCheck;
	private Recombination recombination;
	
	private static String basePropPath = "../etc/evolutionStackSettings/";
	private static String baseStorePath = "../var/genBot/";
	
	private String evolutionStackName;
	
	private boolean[] booleanAllowedIngredients;
	private double[] initMeanValues;
	private double[] initOffsets;
		
	/*
	 * constructor
	 * @param generationNumber the number of the generation
	 * @param generationSize how many Cocktails should be in the generation
	 * @param fitnessCheck a class that implements CheckFitness and performs a fitness check
	 */
	/*public EvolutionAlgorithmManager(String evolutionStackName, Ingredient[] allowedIngredients, int populationSize, int truncation, int elitism, String dbDriverPath, boolean resetDbTable, CheckFitness fitnessCheck, Recombination recombination, double stdDeviation, double[] initMeanValues, double[] initOffsests, double maxPricePerLiter, String propPath) throws MaxAttemptsToMeetPriceConstraintException {		
		Ingredient[] possibleIngredients = IngredientArray.getInstance().getAllIngredients();
		this.booleanAllowedIngredients = new boolean[possibleIngredients.length];
		this.initMeanValues = initMeanValues;
		this.initOffsets = initOffsests;
		
		for (int i = 0; i < possibleIngredients.length; i++) {
			this.booleanAllowedIngredients[i] = false;
			for (int j = 0; j < allowedIngredients.length; j++) {
				if (possibleIngredients[i].equals(allowedIngredients[j])) {
					this.booleanAllowedIngredients[i] = true;
				}
			}
		}
		
		this.fitnessCheck = fitnessCheck;
		this.recombination = recombination;
		
		this.propPath = propPath;
		


		storeProps(evolutionStackName, populationSize, truncation, elitism, stdDeviation, initMeanValues, initOffsets, maxPricePerLiter, dbDriverPath, booleanAllowedIngrediensToString());
		
		convertProps();	
	}*/
	
	public boolean getAutoLoad() {
		return Boolean.parseBoolean(props.getProperty("autoLoad"));
	}

	public void setAutoLoad(boolean autoLoad) {
		props.setProperty("autoLoad", String.valueOf(autoLoad));
		saveProps();
	}

	public int getTruncation() {
		return Integer.parseInt(props.getProperty("truncation"));
	}

	public int getElitism() {
		return Integer.parseInt(props.getProperty("elitsm"));
	}

	public int getPopulationSize() {
		return Integer.parseInt(props.getProperty("populationSize"));
	}
	

	public double getMutationStdDeviation() {
		return recombination.getMutationStdDeviation();
	}
	

	public void setMutationStdDeviation(double stdDeviation) {
		this.stdDeviation = stdDeviation;
		recombination.setMutationStdDeviation(stdDeviation);

		//storeProps(evolutionStackName, populationSize, truncation, elitism, stdDeviation, initMeanValues, initOffsets, maxPricePerLiter, booleanAllowedIngrediensToString());
	}
	
	public double getMaxPricePerLiter() {
		return recombination.getMaxPricePerLiter();
	}
	
	public void setMaxPricePerLiter(double maxPricePerLiter) {
		this.maxPricePerLiter = maxPricePerLiter;
		recombination.setMaxPricePerLiter(maxPricePerLiter);
		
		//storeProps(evolutionStackName, populationSize, truncation, elitism, stdDeviation, initMeanValues, initOffsets, maxPricePerLiter, booleanAllowedIngrediensToString());
	}

	/*public EvolutionAlgorithmManager(CheckFitness fitnessCheck, Recombination recombination, boolean resetDbTable, String propPath) throws MaxAttemptsToMeetPriceConstraintException {
		this.propPath = propPath;		
		this.fitnessCheck = fitnessCheck;
		this.recombination = recombination;
		
		convertProps();
	}*/
	

	
	/*private void convertProps() throws NumberFormatException {
		Properties props = loadProps(propPath);
		
		updateProps(
				props.getProperty("evolutionStackName"),
				Integer.parseInt(props.getProperty("populationSize")), 
				Integer.parseInt(props.getProperty("truncation")), 
				Integer.parseInt(props.getProperty("elitism")), 
				Double.parseDouble(props.getProperty("stdDeviation")),
				Double.parseDouble(props.getProperty("maxPricePerLiter")),
				props.getProperty("initMeanValues"),
				props.getProperty("initOffsets"),
				props.getProperty("booleanAllowedIngredients")
				);
	}
	
	private void updateProps(String evolutionStackName, int populationSize, int truncation, int elitism, double stdDeviation, double maxPricePerLiter, String initMeanValues, String initOffsets, String booleanAllowedIngredientsString) {
		this.evolutionStackName = evolutionStackName;
		this.populationSize = populationSize;
		this.truncation = truncation;
		this.elitism = elitism;
		this.stdDeviation = stdDeviation;
		this.maxPricePerLiter = maxPricePerLiter;
		this.booleanAllowedIngredients = readBooleanAllowedIngredients(booleanAllowedIngredientsString);
		
		// This part is old - I think it just adds an error without helping at all
//		if (!dbDriverPath.equals(this.dbDriverPath)) {
//			this.dbDriverPath = dbDriverPath;
			
//			accessDB(dbDriverPath, true);
//		}
		// This is the replacement
		
		this.initMeanValues = readInitMeanValues(initMeanValues);
		this.initOffsets = readInitOffsets(initOffsets);
		
		setMutationStdDeviation(this.stdDeviation);
		setMaxPricePerLiter(this.maxPricePerLiter);
	}*/
	
	public boolean canEvolve() {
		if (getGenManager().getCocktailGeneration().getRankedPopulation().length <= (getTruncation() + getElitism())) {
			return false;
		} else {
			return true;
		}
	}
		
	/*
	 * Evolves a cocktail generation with a specified stdDeviation and elitism. First
	 * Crossover is applied, next mutation and then elitism
	 * @param stdDeviation standard deviation
	 * @param elitism number of cocktails to come to enter the next generation
	 * @return the new cocktail generation
	 */
	public void evolve() throws NotEnoughRatedCocktailsException {		
		Cocktail[] ratedCocktails = getGenManager().getCocktailGeneration().getRankedPopulation();
		
		// throw an exception if not enough cocktails are rated
		if (!canEvolve())
			throw new NotEnoughRatedCocktailsException("Only " + ratedCocktails.length + " cocktails are rated. As " + getTruncation() + " cocktails should be truncated and the best " + getElitism() + "cocktails should be copied to the next generation we would need at least " + (getTruncation() + getElitism() + 1) + " rated cocktails.");

		genManager.increaseGenerationNumber();
		
		CocktailGeneration nextGeneration = new CocktailGeneration(ratedCocktails);

		// Truncation
		nextGeneration = truncate(nextGeneration);

		// Crossover & Mutation
		try {
			boolean recombinationSucceeded = false;
			while (!recombinationSucceeded) {
				try {
					nextGeneration = recombination.recombine(nextGeneration, getPopulationSize(), getBooleanAllowedIngredients());
					recombinationSucceeded = true;
				} catch (MaxAttemptsToMeetPriceConstraintException e) {
					e.printStackTrace();
					
					recombinationSucceeded = false;
					//convertProps();
				}
			}
		} catch (FitnessNotSetException e) {
			// This should really not happen!
			e.printStackTrace();
		}

		// Elitism
		nextGeneration = applyElitism(genManager.getCocktailGeneration(), nextGeneration);
		
		genManager.setGeneration(nextGeneration);
		
		// save
		save();
	}
	


	public CocktailGeneration truncate(CocktailGeneration cocktailGeneration) {
		int truncation = getTruncation();
		if (truncation < 0) {
			throw new IllegalArgumentException("Invalid number of truncated cocktails (" + truncation + ")!");
		} else if (truncation >= cocktailGeneration.getPopulationSize()) {
			throw new IllegalArgumentException("You try to truncate all cocktails of the generation. This is impossible");
		}
		Cocktail[] rankedCocktails = cocktailGeneration.rankCocktails();
		
		Cocktail[] truncatedCocktails = new Cocktail[rankedCocktails.length - truncation];
		
		for (int i = 0; i < rankedCocktails.length - truncation; i++) {
			truncatedCocktails[i] = rankedCocktails[i];
		}
		
		return new CocktailGeneration(truncatedCocktails);
	}

	/*
	 * applies elitism to the generation - some random cocktails are replaced with the
	 * best cocktails from the previous generation
	 * @param elitism number of cocktails to be replaced
	 * @param oldCocktailGeneration the previous cocktail generation
	 * @param newCocktailGeneration the new cocktail generation
	 * @return a cocktail generation with elitism applied
	 */
	public CocktailGeneration applyElitism(CocktailGeneration oldCocktailGeneration, CocktailGeneration newCocktailGeneration) {
		int elitism = getElitism();
		if (elitism < 0) {
			throw new IllegalArgumentException("Invalid number of elite-Cocktails (" + elitism + ")!");
		}
		
		if (elitism > newCocktailGeneration.getPopulationSize()) {
			elitism = newCocktailGeneration.getPopulationSize();
		}
		if (elitism > oldCocktailGeneration.getPopulationSize()) {
			elitism = oldCocktailGeneration.getPopulationSize();
		}
		
		// rank
		Cocktail[] oldCocktails = oldCocktailGeneration.rankCocktails();
		
		Cocktail[] newPopulation = newCocktailGeneration.getPopulation();
		int[] randomOrder = newCocktailGeneration.generateRandomPopulationOrder();
		
		// now we have the previous cocktails ranked. Now replace <elitism> cocktails in the current population
		for (int i = 0; i < elitism; i++) {
			newPopulation[randomOrder[i]] = oldCocktails[i].copyElite();
		}
		
		return new CocktailGeneration(newPopulation);
	}
	
	public CocktailGenerationManager getGenManager() {
		return genManager;
	}
	
	public double[] getInitMeanValues() {
		return initMeanValues;
	}
	
	public double[] getInitOffsets() {
		return initOffsets;
	}
	
	public boolean[] getBooleanAllowedIngredients() {
		return booleanAllowedIngredients;
	}
	
	public String initMeanValuesToString() {
		String retString = "";
		
		for (int i = 0; i < getInitMeanValues().length; i++) {
			retString += String.valueOf(getInitMeanValues()[i]) + " ";
		}
		retString = retString.substring(0, retString.length() - 1);
		
		return retString;
	}
	
	public String initOffsetsToString() {
		String retString = "";
		
		for (int i = 0; i < getInitOffsets().length; i++) {
			retString += String.valueOf(getInitOffsets()[i]) + " ";
		}
		retString = retString.substring(0, retString.length() - 1);
		
		return retString;
	}
	
	public String booleanAllowedIngrediensToString() {
		String retString = "";
		
		for (int i = 0; i < getBooleanAllowedIngredients().length; i++) {
			if (getBooleanAllowedIngredients()[i]) {
				retString += "1";
			} else {
				retString += "0";
			}
		}
		
		return retString;
	}
	
	public double[] readInitMeanValues(String initMeanValues) {
		String[] initMeanStrings = initMeanValues.split(" ");
		
		double[] retInitMeanValues = new double[initMeanStrings.length];
		
		for (int i = 0; i < initMeanStrings.length; i++) {
			retInitMeanValues[i] = Double.parseDouble(initMeanStrings[i]);
		}
		return retInitMeanValues;
	}
	
	public double[] readInitOffsets(String initOffsets) {
		String[] initOffsetsStrings = initOffsets.split(" ");
		
		double[] retInitOffsets = new double[initOffsetsStrings.length];
		
		for (int i = 0; i < initOffsetsStrings.length; i++) {
			retInitOffsets[i] = Double.parseDouble(initOffsetsStrings[i]);
		}
		return retInitOffsets;
	}
	
	public boolean[] readBooleanAllowedIngredients(String allowedIngredients) {
		boolean[] retBoolean = new boolean[allowedIngredients.length()];
		char[] allowedChars = allowedIngredients.toCharArray();
		
		for (int i = 0; i < retBoolean.length; i++) {
			if (allowedChars[i] == '0') {
				retBoolean[i] = false;
			} else if (allowedChars[i] == '1') {
				retBoolean[i] = true;
			} else {
				throw new IllegalArgumentException("Argument was neither 1 nor 0!");
			}
		}
		
		return retBoolean;
	}
	
	public void setFitness(String name, double cocktailSize, double fitnessInput) {
		getGenManager().getCocktailByName(name).setFitness(fitnessCheck, cocktailSize, fitnessInput);
		save();
	}
	
	public void queue(String name) {
		// TODO Implement this method
		//		getGenManager().getCocktailByName(name).pour;
		getGenManager().getCocktailByName(name).setQueued(true);
	}
	
	/*public void storeProps(String evolutionStackName, int populationSize, int truncation, int elitism, double stdDeviation, double[] initMeanValues, double[] initOffsets, double maxPricePerLiter, String booleanAllowedIngredientsString) {		
		props.setProperty("evolutionStackName", evolutionStackName);
		props.setProperty("populationSize", String.valueOf(populationSize));
		props.setProperty("truncation", String.valueOf(truncation));
		props.setProperty("elitism", String.valueOf(elitism));
		props.setProperty("stdDeviation", String.valueOf(stdDeviation));
		props.setProperty("initMeanValues", initMeanValuesToString());
		props.setProperty("initOffsets", initOffsetsToString());
		props.setProperty("maxPricePerLiter", String.valueOf(maxPricePerLiter));
		props.setProperty("booleanAllowedIngredients", booleanAllowedIngrediensToString());
			

		
		try {
			props.store(new FileOutputStream(new File(basePropPath + propPath + ".properties")), null);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}*/

	public String getName() {
		return evolutionStackName;
	}

	private Properties props;

	public EvolutionAlgorithmManager(String evolutionStackName) throws Exception {
		this.evolutionStackName = evolutionStackName;
		this.props = loadProps(evolutionStackName);
		load();
	}

	/*** SAVING AND LOADING ***/ 

	public void save() {
		try {
			genManager.save(baseStorePath);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public CocktailGenerationManager load() throws Exception {
		return CocktailGenerationManager.load(evolutionStackName, baseStorePath);
	}

	public CocktailGenerationManager load(int generation) throws Exception {
		return CocktailGenerationManager.load(evolutionStackName, baseStorePath, generation);
	}

	/*** PROPERTIES ***/ 

	public static Properties loadProps(String evolutionStackName) {
		Properties props = new Properties();
		try {
			props.load(new FileInputStream(basePropPath + evolutionStackName + ".properties"));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return props;
	}

	public Properties getProps() {
		return props;
	}

	public void saveProps() {
		try {
			props.store(new FileOutputStream(new File(basePropPath + evolutionStackName + ".properties")), null);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}
