package genBot;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.ArrayList;

public class EvolutionAlgorithmManager {
	private String evolutionStackName;
	private Properties props;
	private CocktailGenerationManager genManager;

	/*** CONSTRUCTOR ***/

	public EvolutionAlgorithmManager(String evolutionStackName) throws Exception {
		this.evolutionStackName = evolutionStackName;
		this.props = loadProps(evolutionStackName);
		try {
			this.genManager = load();
		} catch (IOException ex) {
			this.genManager = new CocktailGenerationManager(getPopulationSize(), evolutionStackName, getAllowedIngredients(), 
											getInitMeanValues(), getInitOffsets(), getMaxPricePerLiter());
			save();
		}
	}

	public EvolutionAlgorithmManager(String evolutionStackName, Properties props) throws Exception {
		this.evolutionStackName = evolutionStackName;
		this.props = props;
		saveProps();

		this.genManager = new CocktailGenerationManager(getPopulationSize(), evolutionStackName, getAllowedIngredients(), 
												getInitMeanValues(), getInitOffsets(), getMaxPricePerLiter());
		save();
	}

	/*** SETTER AND GETTER ***/
	
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
		return Integer.parseInt(props.getProperty("elitism"));
	}

	public int getPopulationSize() {
		return Integer.parseInt(props.getProperty("populationSize"));
	}

	public double getMutationStdDeviation() {
		return Double.parseDouble(props.getProperty("mutationStdDeviation"));
	}

	public void setMutationStdDeviation(double rate) {
		props.setProperty("mutationStdDeviation", String.valueOf(rate));
		saveProps();
	}

	public String getRecombination() {
		return props.getProperty("recombination");
	}

	public String getCheckFitness() {
		return props.getProperty("checkFitness");
	}
	
	public double getMaxPricePerLiter() {
		return Double.parseDouble(props.getProperty("maxPricePerLiter"));
	}

	public void setMaxPricePerLiter(double price) {
		props.setProperty("maxPricePerLiter", String.valueOf(price));
		saveProps();
	}

	public double[] getInitMeanValues() {
		String[] initMeanStrings = props.getProperty("initMeanValues").split(" ");
		double[] retInitMeanValues = new double[initMeanStrings.length];
		
		for (int i = 0; i < initMeanStrings.length; i++) {
			retInitMeanValues[i] = Double.parseDouble(initMeanStrings[i]);
		}
		return retInitMeanValues;
	}
	
	public void setInitMeanValuesToString(double[] means) {
		String retString = "";
		
		for (int i = 0; i < means.length; i++)
			retString += String.valueOf(means[i]) + " ";

		retString = retString.substring(0, retString.length() - 1);
		
		props.setProperty("initMeanValues", retString);
		saveProps();
	}
	
	public double[] getInitOffsets() {
		String[] initOffsetsStrings = props.getProperty("initOffsets").split(" ");
		double[] retInitOffsets = new double[initOffsetsStrings.length];
		
		for (int i = 0; i < initOffsetsStrings.length; i++) {
			retInitOffsets[i] = Double.parseDouble(initOffsetsStrings[i]);
		}

		return retInitOffsets;
	}

	public void setInitOffsetsToString(double[] offsets) {
		String retString = "";
		
		for (int i = 0; i < offsets.length; i++)
			retString += String.valueOf(offsets[i]) + " ";

		retString = retString.substring(0, retString.length() - 1);

		props.setProperty("initOffsets", retString);
		saveProps();
	}

	public void setAllowedIngrediens(boolean[] allowedIngredients) {
		String retString = "";
		
		for (int i = 0; i < allowedIngredients.length; i++) {
			if (allowedIngredients[i]) {
				retString += "1";
			} else {
				retString += "0";
			}
		}
		props.setProperty("allowedIngredients", retString);
		saveProps();
	}

	public boolean[] getAllowedIngredients() {
		String allowedIngredients = props.getProperty("allowedIngredients");
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

	public String getName() {
		return evolutionStackName;
	}

	/**************************************************/

	public boolean canEvolve() {
		return (getGenManager().getCocktailGeneration().getRatedPopulationSize() > (getTruncation() + getElitism())); 
	}
		
	public void evolve() throws NotEnoughRatedCocktailsException {		
		if (!canEvolve())
			throw new NotEnoughRatedCocktailsException("Only " + getGenManager().getCocktailGeneration().getRatedPopulationSize() + " cocktails are rated. As " + getTruncation() + " cocktails should be truncated and the best " + getElitism() + "cocktails should be copied to the next generation we would need at least " + (getTruncation() + getElitism() + 1) + " rated cocktails.");

		Cocktail[] ratedCocktails = getGenManager().getCocktailGeneration().getRatedPopulation();

		CheckFitness fitnessCheck;
		String checkFitnessName = getCheckFitness();
		if (checkFitnessName.equals("EfficientCocktail")) {
			fitnessCheck = new EfficientCocktail();
		} else if (checkFitnessName.equals("BestRating")) {
			fitnessCheck = new BestRating();
		} else {
			fitnessCheck = new BestRating();
		}

		for(Cocktail c: ratedCocktails)
			if(c.isRated())
				fitnessCheck.setFitness(c);

		
		CocktailGeneration nextGeneration = new CocktailGeneration(ratedCocktails);
		nextGeneration = truncate(nextGeneration);

		Recombination recombination;

		String recombinationName = getRecombination();
		
		double stdDeviation = getMutationStdDeviation();
		double maxPricePerLiter = getMaxPricePerLiter();

		if (recombinationName.equals("StandardMutation")) {
			recombination = new StandardMutation(stdDeviation, maxPricePerLiter);
		} else if (recombinationName.equals("IntermediateRecombination")) {
			recombination = new IntermediateRecombination(0.25, maxPricePerLiter);
		} else if (recombinationName.equals("MutationAndIntermediateRecombination")) {
			recombination = new MutationAndIntermediateRecombination(0.25, stdDeviation, maxPricePerLiter);
		} else {
			recombination = new MutationAndIntermediateRecombination(0.25, stdDeviation, maxPricePerLiter);
		}

		// Crossover & Mutation
		try {
			boolean recombinationSucceeded = false;
			while (!recombinationSucceeded) {
				try {
					nextGeneration = recombination.recombine(nextGeneration, getPopulationSize(), getAllowedIngredients());
					recombinationSucceeded = true;
				} catch (MaxAttemptsToMeetPriceConstraintException e) {
					e.printStackTrace();
					recombinationSucceeded = false;
				}
			}
		} catch (FitnessNotSetException e) {
			e.printStackTrace();
		}

		nextGeneration = applyElitism(genManager.getCocktailGeneration(), nextGeneration);
		save();
		genManager.increaseGenerationNumber();
		genManager.setGeneration(nextGeneration);
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

		for (int i = rankedCocktails.length - truncation; i < rankedCocktails.length; i++) {
			rankedCocktails[i].setTruncated(true);
		}
		
		return new CocktailGeneration(truncatedCocktails);
	}

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
	
	public void setRating(String name, double rating) {
		getGenManager().getCocktailByName(name).setRating(rating);
		save();
	}
	
	public void queue(String name) {
		// TODO Implement this method
		//		getGenManager().getCocktailByName(name).pour;
		getGenManager().getCocktailByName(name).setQueued(true);
	}

	/*** SAVING AND LOADING ***/ 

	public void save() {
		try {
			genManager.save();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public CocktailGenerationManager load() throws IOException {
		return CocktailGenerationManager.load(evolutionStackName);
	}

	public CocktailGenerationManager load(int generation) throws IOException {
		return CocktailGenerationManager.load(evolutionStackName, generation);
	}

	public int getCurrentGenerationNumber() throws IOException {
		return CocktailGenerationManager.getCurrentGenerationNumber(evolutionStackName);
	}

	/*** PROPERTIES ***/ 

	public static Properties loadProps(String evolutionStackName) throws IOException {
		Properties props = new Properties();
		props.load(new FileInputStream(GenBotConfig.stackPath + evolutionStackName + ".properties"));
		return props;
	}

	public Properties getProps() {
		return props;
	}

	public void saveProps() {
		try {
			props.store(new FileOutputStream(new File(GenBotConfig.stackPath + evolutionStackName + ".properties")), null);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
