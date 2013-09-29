package genBot2;

import java.util.Arrays;
import java.util.Collections;
import java.util.Random;

/*
 * A genetic algorithm works in generations. This class defines one generation
 * of cocktails.
 */

public class CocktailGeneration {
	
	private Cocktail[] population;
	
	private int[] randomPopulationOrder;
	private int randomPopulationPosition;

	/*
	 * constructor
	 * @param population an Array of cocktails
	 */
	public CocktailGeneration(Cocktail[] population) {		
		this.population = population;
		
		this.randomPopulationOrder = generateRandomPopulationOrder(population.length);
		
		this.randomPopulationPosition = 0;
	}
		
	/*
	 * This method returns the numbers 0 to populationSize - 1 in random order
	 */
	private int[] generateRandomPopulationOrder(int populationSize) {
		int[] randomPopulationOrder = new int[populationSize];
		for (int i = 0; i < populationSize; i++) {
			randomPopulationOrder[i] = i;
		}
		
		Random rnd = new Random();
		for (int i = randomPopulationOrder.length - 1; i > 0; i--) {
			int index = rnd.nextInt(i + 1);
			// Simple swap
			int a = randomPopulationOrder[index];
			randomPopulationOrder[index] = randomPopulationOrder[i];
			randomPopulationOrder[i] = a;
		}

		return randomPopulationOrder;
	}

	public int getPopulationSize() {
		return getPopulation().length;
	}
	
	public Cocktail[] getPopulation() {
		return population;
	}
	
	/*
	 * Returns the Cocktail with number cocktailNumber (usually used for random access)
	 * @param cocktalNumber the position of the cocktail
	 * @return the cocktail
	 */
	public Cocktail getCocktail(int cocktailNumber) {
		if (cocktailNumber < 0 || cocktailNumber >= population.length) {
			throw new IllegalArgumentException("There is no Cocktail number " + cocktailNumber + "in this generation!");
		} else {
			return population[cocktailNumber];
		}
	}
	
	public boolean hasNextRandomCocktail() {
		if (randomPopulationPosition >= randomPopulationOrder.length) {
			this.randomPopulationOrder = generateRandomPopulationOrder(population.length);
			this.randomPopulationPosition = 0;
			
			return false;
		}
		return true;
	}
	
	/*
	 * This works like an iterator - it returns the next random cocktail
	 * @return the next random cocktail
	 */
	public Cocktail getNextRandomCocktail() {
		Cocktail theCocktail =  getPopulation()[randomPopulationOrder[randomPopulationPosition]];
		randomPopulationPosition = randomPopulationPosition + 1;
		
		return theCocktail;
	}
	
//	public CocktailGeneration clone() {
//		CocktailGeneration nextGen = new CocktailGeneration(getPopulationSize(), getPopulation());
//		
//		return nextGen;
//	}
	
	/*
	 * Returns all fitnesses as shares of the sum of all fitnesses
	 * @return an array of the shares
	 */
	public double[] rouletteWheelShares(Cocktail[] cocktails) throws FitnessNotSetException {		
		double[] rouletteWheelShare = new double[cocktails.length];
		for (int i = 0; i < cocktails.length; i++) {
			rouletteWheelShare[i] = cocktails[i].getFitness();
		}
		for (int i = 0; i < cocktails.length; i++) {
			rouletteWheelShare[i] = cocktails[i].getFitness() / getFitnessSum(cocktails);
		}
		
		return rouletteWheelShare;
	}
	
	/*
	 * Transforms the array to an "additive array" - shares like
	 * {0.4, 0.5, 0.1} are transformed to {0.4, 0.9, 0.1}
	 * @return an additive array
	 */
	public double[] generateRouletteWheel(Cocktail[] cocktails) throws FitnessNotSetException {
		double[] rouletteWheelShare = rouletteWheelShares(cocktails);
		
		double[] rouletteWheel = new double[rouletteWheelShare.length];
		
		rouletteWheel[0] = rouletteWheelShare[0];
		for (int i = 1; i < rouletteWheel.length; i++) {
			rouletteWheel[i] = rouletteWheel[i - 1] + rouletteWheelShare[i];
		}
		
		return rouletteWheel;
	}
	
	/*
	 * returns two new Cocktails based on the selection algorithm (is could be also taken
	 * to another class - besides this is not really correct at the moment
	 */
	public Cocktail[] crossover() throws FitnessNotSetException {
		Random rnd = new Random();
		
		// choose cocktail 1
		double cocktailSelector1 = rnd.nextDouble();
		Cocktail cocktail1 = population[0];
		
		double[] rouletteWheel1 = generateRouletteWheel(population);
				
		for (int i = 1; i < rouletteWheel1.length; i++) {
			if (cocktailSelector1 > rouletteWheel1[i - 1] && cocktailSelector1 < rouletteWheel1[i]) {
				cocktail1 = population[i];
			}
		}
		
		// choose cocktail 2 (a bit more tricky)
		Cocktail[] restPopulation = new Cocktail[getPopulationSize() - 1];
		int restPopulationIndex = 0;
		for (int i = 0; i < population.length; i++) {
			if (!population[i].equals(cocktail1)) {
				restPopulation[restPopulationIndex] = population[i];
				restPopulationIndex = restPopulationIndex + 1;
			}
		}
		
		double cocktailSelector2 = rnd.nextDouble();
		Cocktail cocktail2 = restPopulation[0];
		
		double[] rouletteWheel2 = generateRouletteWheel(restPopulation);
		
		for (int i = 1; i < rouletteWheel2.length; i++) {
			if (cocktailSelector2 > rouletteWheel2[i - 1] && cocktailSelector2 < rouletteWheel2[i]) {
				cocktail2 = restPopulation[i];
			}
		}
		
		// now perform the crossover
		IngredientArray ingredientArray = IngredientArray.getInstance();
		
		double[] ingredients = new double[ingredientArray.getNumberOfIngredients()];
		int split = rnd.nextInt(ingredients.length);
		
		double[] child1Ingredients = new double[ingredients.length];
		double[] child2Ingredients = new double[ingredients.length];
		
		for (int i = 0; i < ingredients.length; i++) {
			if (i <= split) {
				child1Ingredients[i] = cocktail1.getAmount(ingredientArray.getAllIngredients()[i]);
				child2Ingredients[i] = cocktail2.getAmount(ingredientArray.getAllIngredients()[i]);
			} else {
				child2Ingredients[i] = cocktail1.getAmount(ingredientArray.getAllIngredients()[i]);
				child1Ingredients[i] = cocktail2.getAmount(ingredientArray.getAllIngredients()[i]);
			}
		}
		
		Cocktail[] children = {new Cocktail(child1Ingredients), new Cocktail(child2Ingredients)};
		return children;
	}
	
	/*
	 * makes all crossovers for the Generation
	 * @return a new cocktail generation
	 */
	public CocktailGeneration allCrossovers(int populationSize) throws FitnessNotSetException {
		Cocktail[] population = new Cocktail[populationSize];
		
		for (int i = 0; i < populationSize; i = i + 2) {
			Cocktail[] children = crossover();
			population[i] = children[0];
			// we have to check if both children fit in the population (only even numbers)
			if (i + 1 < population.length) {
				population[i + 1] = children[1];
			}
		}
		
		return new CocktailGeneration(population);
	}
	
	/*
	 * mutates the cocktails of the generation
	 * @param stdDeviation the standard deviation of the mutation (how big should the
	 * change be?).
	 */
	public void mutateCocktails(double stdDeviation) {
		if (stdDeviation < 0) {
			throw new IllegalArgumentException("stdDeviation must be greater than 0 (" + stdDeviation + "was specified)!");
		}
		for (int i = 0; i < getPopulationSize(); i++) {
			population[i] = population[i].mutate(stdDeviation);
		}
	}

	/*
	 * applies elitism to the generation - some random cocktails are replaced with the
	 * best cocktails from the previous generation
	 * @param elitism number of cocktails to be replaced
	 * @param cocktailGeneration the previous cocktail generation
	 */
	public void applyElitism(int elitism, CocktailGeneration cocktailGeneration) {
		if (elitism < 0 || elitism > getPopulationSize()) {
			throw new IllegalArgumentException("Invalid number of elite-Cocktails (" + elitism + ")!");
		}
		
		// rank
		Cocktail[] previousCocktails = rankCocktails(cocktailGeneration.getPopulation());
		
		// now we have the previous cocktails ranked. Now replace <elitism> cocktails in the current population
		for (int i = 0; i < elitism; i++) {
			population[i] = previousCocktails[i].copy();
		}
	}
	
	public Cocktail[] rankCocktails(Cocktail[] cocktails) {
		int[] otherRandomOrder = generateRandomPopulationOrder(cocktails.length);
		Cocktail[] rankedCocktails = new Cocktail[cocktails.length];
		
		for (int i = 0; i < getPopulationSize(); i++) {
			rankedCocktails[i] = cocktails[otherRandomOrder[i]];
		}
		
		Arrays.sort(rankedCocktails, Collections.reverseOrder());
		
		return rankedCocktails;
	}
	
	public double getFitnessSum(Cocktail[] cocktails) throws FitnessNotSetException {
		double fitnessSum = 0;
		for (int i = 0; i < cocktails.length; i++) {
			if (cocktails[i].isFitnessSet()) {
				fitnessSum = fitnessSum + cocktails[i].getFitness();
			}
			else {
				throw new FitnessNotSetException("Fitness of Cocktail " + i + "is not set!");
			}
		}
		return fitnessSum;
	}
	
	public double getMeanFitness() throws FitnessNotSetException {
		return (getFitnessSum(getPopulation()) / getPopulationSize());
	}
	
	public Cocktail getBestCocktail() {
		return rankCocktails(getPopulation())[0];
	}
	
	public double getBestFitness() throws FitnessNotSetException {
		return getBestCocktail().getFitness();
	}
	
	public String toString() {
		String out = "";
		
		for (int i = 0; i < getPopulationSize(); i++) {
			out += "Cocktail " + i + ": " + population[i].toString() + "\n";
		}
		
		return out;
	}
	
	public String randomToString() {
		String out = "";
		
		for (int i = 0; i < getPopulationSize(); i++) {
			out += "Cocktail " + randomPopulationOrder[i] + ": " + population[randomPopulationOrder[i]].toString() + "\n";
		}
		
		return out;
	}

}
