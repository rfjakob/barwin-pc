package genBot;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.ArrayList;
import java.io.PrintWriter;
import java.util.Scanner;
import java.lang.Math;

public class EvolutionAlgorithmManager {
	private String evolutionStackName;
	private Properties props;

	private CocktailGeneration cocktailGeneration;
	private int generationNumber;

	private int maxAttemptsToMeetPriceConstraints = 10000;

	/*** CONSTRUCTOR ***/

	public EvolutionAlgorithmManager(String evolutionStackName) throws GeneratingRandomCocktailsException, IOException {
		this.evolutionStackName = evolutionStackName;
		this.props = loadProps(evolutionStackName);
		try {
			load();
		} catch (IOException ex) {
			this.generationNumber = 0;
			generateCocktailGeneration();
			save();
		}
	}

	public EvolutionAlgorithmManager(String evolutionStackName, Properties props) throws GeneratingRandomCocktailsException {
		this.evolutionStackName = evolutionStackName;
		this.props = props;
		saveProps();
		this.generationNumber = 0;
		generateCocktailGeneration();
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

	public int getGenerationNumber() {
		return generationNumber;
	}

	/**************************************************/

	private void generateCocktailGeneration() throws GeneratingRandomCocktailsException {
		boolean[] booleanAllowedIngredients = getAllowedIngredients();
		double[] initMeanValues = getInitMeanValues();
		double[] initOffsets = getInitOffsets();

		if (booleanAllowedIngredients.length != initMeanValues.length | booleanAllowedIngredients.length != initOffsets.length) {
			throw new IllegalArgumentException("One of the input arrays has the wrong length!");
		}
		
		double sumMeanValues = 0;
		for (int i = 0; i < initMeanValues.length; i++) {
			sumMeanValues += initMeanValues[i];
		}
		if (Math.round(sumMeanValues*100) != 100) {
			throw new IllegalArgumentException("The values of initMeanValues does not sum up to one!");
		}
				
		Cocktail[] cocktails = new Cocktail[getPopulationSize()];
		
		int countToThrowException = 0;
		
		int i = 0;
		outerloop:
		while (i < getPopulationSize()) {
			if (countToThrowException >= maxAttemptsToMeetPriceConstraints) 
					throw new GeneratingRandomCocktailsException("Tried " + maxAttemptsToMeetPriceConstraints + " times to find a cocktail that meets the cost and content constraints.");
			
			Cocktail c = Cocktail.newRandomCocktail(booleanAllowedIngredients);
			
			boolean resetCocktail = false;
			
			for (int j = 0; j < booleanAllowedIngredients.length; j++) {
				double val = c.getAmountsAsDouble()[j];
				
				if (val < initMeanValues[j] - initOffsets[j] || val > initMeanValues[j] + initOffsets[j]) {
					countToThrowException++;
					continue outerloop;
				}
			}
			
			if (c.pricePerLiterHigherAs(getMaxPricePerLiter())) {
				countToThrowException++;
				continue;
			}
			
			cocktails[i++] = c;
			countToThrowException = 0;
		}
		cocktailGeneration = new CocktailGeneration(cocktails);
	}

	public boolean canEvolve() {
		return (cocktailGeneration.getRatedPopulationSize() > (getTruncation() + getElitism())); 
	}
		
	public void evolve() throws NotEnoughRatedCocktailsException {		
		if (!canEvolve())
			throw new NotEnoughRatedCocktailsException("Only " + cocktailGeneration.getRatedPopulationSize() + " cocktails are rated. As " + getTruncation() + " cocktails should be truncated and the best " + getElitism() + "cocktails should be copied to the next generation we would need at least " + (getTruncation() + getElitism() + 1) + " rated cocktails.");

		Cocktail[] ratedCocktails = cocktailGeneration.getRatedPopulation();

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
				} catch (GeneratingRandomCocktailsException e) {
					e.printStackTrace();
					recombinationSucceeded = false;
				}
			}
		} catch (FitnessNotSetException e) {
			e.printStackTrace();
		}

		nextGeneration = applyElitism(new CocktailGeneration(ratedCocktails), nextGeneration);
		save(); // LAST TIME THE OLD GENERATION
		generationNumber++;
		cocktailGeneration = nextGeneration;
		save();
	}
	
	public CocktailGeneration truncate(CocktailGeneration tmpCocktailGeneration) {
		int truncation = getTruncation();

		if (truncation < 0) 
			throw new IllegalArgumentException("Invalid number of truncated cocktails (" + truncation + ")!");
		else if (truncation >= tmpCocktailGeneration.getPopulationSize())
			throw new IllegalArgumentException("You try to truncate all cocktails of the generation. This is impossible");

		Cocktail[] rankedCocktails = tmpCocktailGeneration.rankCocktails();
		Cocktail[] truncatedCocktails = new Cocktail[rankedCocktails.length - truncation];
		
		for (int i = 0; i < rankedCocktails.length - truncation; i++)
			truncatedCocktails[i] = rankedCocktails[i];

		for (int i = rankedCocktails.length - truncation; i < rankedCocktails.length; i++)
			rankedCocktails[i].setTruncated(true);
		
		return new CocktailGeneration(truncatedCocktails);
	}

	public CocktailGeneration applyElitism(CocktailGeneration oldCocktailGeneration, CocktailGeneration newCocktailGeneration) {
		int elitism = getElitism();

		if (elitism < 0)
			throw new IllegalArgumentException("Invalid number of elite-Cocktails (" + elitism + ")!");
		
		if (elitism > newCocktailGeneration.getPopulationSize()) 
			elitism = newCocktailGeneration.getPopulationSize();
		if (elitism > oldCocktailGeneration.getPopulationSize())
			elitism = oldCocktailGeneration.getPopulationSize();
		
		Cocktail[] oldCocktails = oldCocktailGeneration.rankCocktails();
		
		Cocktail[] newPopulation = newCocktailGeneration.getPopulation();
		int[] randomOrder = newCocktailGeneration.generateRandomPopulationOrder();
		
		// now we have the previous cocktails ranked. Now replace <elitism> cocktails in the current population
		for (int i = 0; i < elitism; i++)
			newPopulation[randomOrder[i]] = oldCocktails[i].copyElite();
		
		return new CocktailGeneration(newPopulation);
	}
	
	public void setRating(String name, double rating) {
		getCocktailByName(name).setRating(rating);
		save();
	}

	public CocktailGeneration getCocktailGeneration() {
		return cocktailGeneration;
	}
	
	public CocktailWithName[] getNamedCocktailGeneration() {
		return cocktailGeneration.getNamedPopulation(evolutionStackName, getGenerationNumber());
	}
	
	public Cocktail getCocktailByName(String name) {
		CocktailWithName[] namedCocktails = getNamedCocktailGeneration();
		
		for (int i = 0; i < namedCocktails.length; i++) {
			if (namedCocktails[i].getName().equals(name)) {
				return namedCocktails[i].getCocktail();
			}
		}
		throw new IllegalArgumentException("No Cocktail with name " + name);
	}	

	public void queue(String name) {
		// TODO Implement this method
		//		getGenManager().getCocktailByName(name).pour;
		getCocktailByName(name).setQueued(true);
	}

	/*** SAVING AND LOADING ***/ 

	public void save() {
		try {
			File directory = new File(GenBotConfig.storePath + evolutionStackName);
		    if (!directory.exists())
		    	directory.mkdir();
		    //System.out.println(cocktailGeneration);
		    String fileName = GenBotConfig.storePath + evolutionStackName + "/" + String.format("%03d", generationNumber) + ".txt";

			PrintWriter out = new PrintWriter(fileName);
			out.print(cocktailGeneration.getSaveString());
			out.close();

			Properties props = new Properties();
			props.setProperty("currentGeneration", String.valueOf(generationNumber));
			props.store(new FileOutputStream(new File(GenBotConfig.storePath + evolutionStackName + "/info.txt")), null);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void load() throws IOException {
		int generation = readGenerationNumber();
		load(generation);
	}

	public void load(int generation) throws IOException {
		String fileName = GenBotConfig.storePath + evolutionStackName + "/" + String.format("%03d", generation) + ".txt";
		//String content = new String(Files.readAllBytes(Paths.get(fileName)));
		String content = new Scanner(new File(fileName)).useDelimiter("\\Z").next();
		

		generationNumber   = generation;
		cocktailGeneration = CocktailGeneration.loadFromString(content);
	}

	private int readGenerationNumber() throws IOException {
		Properties props2 = new Properties();
		props2.load(new FileInputStream(GenBotConfig.storePath + evolutionStackName + "/info.txt"));
		if(props2.containsKey("currentGeneration"))
	    	return Integer.parseInt(props2.getProperty("currentGeneration"));
	    else
	    	return -1;
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
