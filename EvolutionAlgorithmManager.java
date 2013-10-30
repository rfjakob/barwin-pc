package genBot2;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Properties;

/*
 * some kind of a container class - this class is responsible for generating a new
 * generation of cocktails. Apart from information stored in the CocktailGeneration
 * this class stores the generation number.
 */
public class EvolutionAlgorithmManager {
	
	private CocktailGenerationManager genManager;
	
	private int truncation;
	private int elitism;
	
	private int populationSize;
	private CheckFitness fitnessCheck;
	private Recombination recombination;
	private DataBaseDriver dbDriver;
	
	private String propPath;
	private String dbDriverPath;
	
	private boolean didJustLoad = false;
	
	private String evolutionStackName;
	
	private boolean[] booleanAllowedIngredients;
		
	/*
	 * constructor
	 * @param generationNumber the number of the generation
	 * @param generationSize how many Cocktails should be in the generation
	 * @param fitnessCheck a class that implements CheckFitness and performs a fitness check
	 */
	public EvolutionAlgorithmManager(String evolutionStackName, Ingredient[] allowedIngredients, int populationSize, int truncation, int elitism, String dbDriverPath, boolean dbReset, CheckFitness fitnessCheck, Recombination recombination, String propPath) throws SQLException {		
		Ingredient[] possibleIngredients = IngredientArray.getInstance().getAllIngredients();
		this.booleanAllowedIngredients = new boolean[possibleIngredients.length];
		
		for (int i = 0; i < possibleIngredients.length; i++) {
			this.booleanAllowedIngredients[i] = false;
			for (int j = 0; j < allowedIngredients.length; j++) {
				if (possibleIngredients[i].equals(allowedIngredients[j])) {
					this.booleanAllowedIngredients[i] = true;
				}
			}
		}
		
		Properties props = new Properties();
		props.setProperty("populationSize", String.valueOf(populationSize));
		props.setProperty("truncation", String.valueOf(truncation));
		props.setProperty("elitism", String.valueOf(elitism));
		if (dbDriverPath != null) {
			props.setProperty("dbDriverPath", dbDriverPath);
		}
		props.setProperty("booleanAllowedIngredients", booleanAllowedIngrediensToString());
			
		try {
			props.store(new FileOutputStream(new File(propPath + ".properties")), null);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		loadProps(propPath);
		
		constructRest(evolutionStackName, fitnessCheck, recombination, dbReset, propPath);
	}
	
	public EvolutionAlgorithmManager(CheckFitness fitnessCheck, Recombination recombination, boolean dbReset, String propPath) throws SQLException {
		loadProps(propPath);
		
		try {
			constructRest(evolutionStackName, fitnessCheck, recombination, dbReset, propPath);
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void loadProps(String propPath) {
		Properties props = new Properties();
		try {
			props.load(new FileInputStream(propPath + ".properties"));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		System.out.println(props.getProperty("populationSize"));
		updateProps(
				Integer.parseInt(props.getProperty("populationSize")), 
				Integer.parseInt(props.getProperty("truncation")), 
				Integer.parseInt(props.getProperty("elitism")), 
				props.getProperty("dbDriverPath"),
				props.getProperty("booleanAllowedIngredients")
				);
	}
	
	private void updateProps(int populationSize, int truncation, int elitism, String dbDriverPath, String booleanAllowedIngredientsString) {
		this.populationSize = populationSize;
		this.truncation = truncation;
		this.elitism = elitism;
		this.dbDriverPath = dbDriverPath;
		this.booleanAllowedIngredients = readBooleanAllowedIngredients(booleanAllowedIngredientsString);
	}

	private void constructRest(String cocktailStackName, CheckFitness fitnessCheck, Recombination recombination, boolean dbReset, String propPath) throws SQLException {
		this.evolutionStackName = cocktailStackName;
		this.fitnessCheck = fitnessCheck;
		this.recombination = recombination;
		
		this.propPath = propPath;
		
		if (dbDriverPath != null) {
			this.dbDriver = new DataBaseDriver(dbDriverPath, dbReset, evolutionStackName);

		
			if (dbDriver.getLastGenerationNumber(evolutionStackName) == 0) {
				this.genManager = new CocktailGenerationManager(populationSize, cocktailStackName);
			} else {
				this.genManager = load();
				this.didJustLoad = true;
			}
		} else {
			this.genManager = new CocktailGenerationManager(populationSize, cocktailStackName);
		}
		
	}
	
	public boolean canEvolve() {
		if (getGenManager().getCocktailGeneration().getRankedPopulation().length <= (truncation + elitism)) {
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
	public void evolve() throws SQLException, NotEnoughRatedCocktailsException {
		if (didJustLoad) {
			didJustLoad = false;
		} else {
			if (dbDriver != null) {
				save();
			}
		}
		
		// load properties - they may have been updated
		loadProps(propPath);
		
		genManager.increaseGenerationNumber();
		
		// reduce generation to the rated cocktails
		Cocktail[] ratedCocktails = getGenManager().getCocktailGeneration().getRankedPopulation();
		
		for (int i = 0; i < ratedCocktails.length; i++) {
			ratedCocktails[i] = ratedCocktails[i];
		}

		// throw an exception if not enough cocktails are rated
		if (!canEvolve()) {
			throw new NotEnoughRatedCocktailsException("Only " + ratedCocktails.length + " cocktails are rated. As " + truncation + " cocktails should be truncated and the best " + elitism + "cocktails should be copied to the next generation we would need at least " + (truncation + elitism + 1) + " rated cocktails.");
		}

		CocktailGeneration nextGeneration = new CocktailGeneration(ratedCocktails);

		// Truncation
		nextGeneration = truncation(truncation, genManager.getCocktailGeneration());

		// Crossover & Mutation
		try {
			nextGeneration = recombination.recombine(nextGeneration, populationSize, getBooleanAllowedIngredients());
		} catch (FitnessNotSetException e) {
			// This should really not happen!
			e.printStackTrace();
		}

		// Elitism
		nextGeneration = applyElitism(elitism, genManager.getCocktailGeneration(), nextGeneration);
		
		genManager.setGeneration(nextGeneration);
	}
	
	public CocktailGenerationManager load() throws SQLException {
		return load(dbDriver.getLastGenerationNumber(evolutionStackName));
	}
	
	public CocktailGenerationManager load(int number) throws SQLException {
		return dbDriver.select(evolutionStackName, number);
	}
	
	public void loadFromDB(int number) throws SQLException {
		genManager = load(number);
		
		didJustLoad = true;
	}
	
	public void loadLastFromDB() throws SQLException {
		genManager = load();
		
		didJustLoad = true;
	}
	
	public void save() throws SQLException {
		doSave(genManager.getGenerationNumber(), genManager);
	}
	
	public void doSave(int generationNumber, CocktailGenerationManager cocktailGenerationManager) throws SQLException {
		dbDriver.insert(evolutionStackName, generationNumber, cocktailGenerationManager);
	}
	
	/*
	 * checks the fitness for the whole cocktail generation
	 */
//	public void evaluate() {
//		if (genManager.getCocktailGeneration().hasNextRandomNamedCocktail()) {
//			genManager.getCocktailGeneration().getNextRandomNamedCocktail(evolutionStackName, getGenManager().getGenerationNumber()).getCocktail().setFitness(fitnessCheck);
//		}
//	}

	/*
	 * applies truncation to the generation - the worst cocktails are removed from the
	 * generation
	 * @param truncation how many cocktails should be removed from the generation
	 * @param cocktailGeneration 
	 */
	public CocktailGeneration truncation(int truncation, CocktailGeneration cocktailGeneration) {
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
	public CocktailGeneration applyElitism(int elitism, CocktailGeneration oldCocktailGeneration, CocktailGeneration newCocktailGeneration) {
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
			newPopulation[randomOrder[i]] = oldCocktails[i].copy();
		}
		
		return new CocktailGeneration(newPopulation);
	}
	
	public CocktailGenerationManager getGenManager() {
		return genManager;
	}
	
	public boolean[] getBooleanAllowedIngredients() {
		return booleanAllowedIngredients;
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
	
	public void setFitness(String name, double fitnessInput) {
		getGenManager().getCocktailByName(name).setFitness(fitnessCheck, fitnessInput);
	}
	
	public void pour(String name) {
// TODO Implement this method
//		getGenManager().getCocktailByName(name).pour;
		getGenManager().getCocktailByName(name).setPouredTrue();
	}
}
