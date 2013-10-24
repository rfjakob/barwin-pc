package genBot2;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collections;
import java.util.Random;

/*
 * A genetic algorithm works in generations. This class defines one generation
 * of cocktails.
 */

public class CocktailGeneration implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Cocktail[] population;
	
	private int[] randomPopulationOrder;
	private int randomPopulationPosition;

	/*
	 * constructor
	 * @param population an Array of cocktails
	 */
	public CocktailGeneration(Cocktail[] population) {		
		this.population = population;
		
		this.randomPopulationOrder = generateRandomPopulationOrder();
		
		this.randomPopulationPosition = 0;
	}
		
	/*
	 * This method returns the numbers 0 to populationSize - 1 in random order
	 */
	public int[] generateRandomPopulationOrder() {
		int[] randomPopulationOrder = new int[getPopulationSize()];
		for (int i = 0; i < getPopulationSize(); i++) {
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
	
	public int getFitnessedPopulationSize() {
		Cocktail[] cocktails = getPopulation();
		int ret = 0;
		
		for (int i = 0; i< cocktails.length; i++) {
			if (cocktails[i].isFitnessSet()) {
				ret++;
			}
		}
		return ret;
	}
	
	public Cocktail[] getPopulation() {
		return population;
	}
	
	public Cocktail[] getFitnessedPopulation() {
		Cocktail[] cocktails = getPopulation();
		Cocktail[] retCocktails = new Cocktail[getFitnessedPopulationSize()];
		int j = 0;
		
		for (int i = 0; i < cocktails.length; i++) {
			if (cocktails[i].isFitnessSet()) {
				retCocktails[j] = cocktails[i];
				j++;
			}
		}
		return retCocktails;
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
			this.randomPopulationOrder = generateRandomPopulationOrder();
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
		Cocktail[] previousCocktails = rankCocktails();
		
		// now we have the previous cocktails ranked. Now replace <elitism> cocktails in the current population
		for (int i = 0; i < elitism; i++) {
			population[i] = previousCocktails[i].copy();
		}
	}
	
	public Cocktail[] rankCocktails() {
		int[] otherRandomOrder = generateRandomPopulationOrder();
		Cocktail[] rankedCocktails = new Cocktail[getPopulationSize()];
		
		for (int i = 0; i < getPopulationSize(); i++) {
			rankedCocktails[i] = getCocktail(otherRandomOrder[i]);
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
		return (getFitnessSum(getFitnessedPopulation()) / getFitnessedPopulationSize());
	}
	
	public Cocktail getBestCocktail() {
		return rankCocktails()[0];
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
