package genBot;

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
	
	public int getRankedPopulationSize() {
		Cocktail[] cocktails = getPopulation();
		int ret = 0;
		
		for (int i = 0; i< cocktails.length; i++) {
			if (cocktails[i].isFitnessSet()) {
				ret++;
			}
		}
		return ret;
	}
	
	public int getUnRankedPopulationSize() {
		return getPopulationSize() - getRankedPopulationSize();
	}
	
	public Cocktail[] getPopulation() {
		return population;
	}
	
	public CocktailWithName[] getNamedPopulation(String evolutionStackName, int generationNumber) {
		CocktailWithName[] retCocktails = new CocktailWithName[getPopulationSize()];
		
		for (int i = 0; i < getPopulation().length; i++) {
			retCocktails[i] = new CocktailWithName(evolutionStackName, generationNumber, i, getPopulation()[i]);
		}
		
		return retCocktails;
	}
	
	public Cocktail[] getRankedPopulation() {
		Cocktail[] cocktails = getPopulation();
		Cocktail[] retCocktails = new Cocktail[getRankedPopulationSize()];
		int j = 0;
		
		for (int i = 0; i < cocktails.length; i++) {
			if (cocktails[i].isFitnessSet()) {
				retCocktails[j] = cocktails[i];
				j++;
			}
		}
		return retCocktails;
	}
	
	public CocktailWithName[] getRankedPopulationWithName(String evolutionStackName, int generationNumber) {
		CocktailWithName[] namedPopulation = getNamedPopulation(evolutionStackName, generationNumber);
		CocktailWithName[] retCocktails = new CocktailWithName[getRankedPopulation().length];
		
		int j = 0;
		
		for (int i = 0; i < namedPopulation.length; i++) {
			if (namedPopulation[i].getCocktail().isFitnessSet()) {
				retCocktails[j] = namedPopulation[i];
				j++;
			}
		}
		
		return retCocktails;
	}

	public Cocktail[] getUnRankedPopulation() {
		Cocktail[] cocktails = getPopulation();
		Cocktail[] retCocktails = new Cocktail[getUnRankedPopulationSize()];
		int j = 0;
		
		for (int i = 0; i < cocktails.length; i++) {
			if (!cocktails[i].isFitnessSet()) {
				retCocktails[j] = cocktails[i];
				j++;
			}
		}
		return retCocktails;
	}

	public CocktailWithName[] getUnRankedPopulationWithName(String evolutionStackName, int generationNumber) {
		CocktailWithName[] namedPopulation = getNamedPopulation(evolutionStackName, generationNumber);
		CocktailWithName[] retCocktails = new CocktailWithName[getUnRankedPopulationSize()];
		
		int j = 0;
		
		for (int i = 0; i < namedPopulation.length; i++) {
			if (!namedPopulation[i].getCocktail().isFitnessSet()) {
				retCocktails[j] = namedPopulation[i];
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
	
	public CocktailWithName getNamedCocktail(int cocktailNumber, String evolutionStackName, int generationNumber) {
		return new CocktailWithName(evolutionStackName, generationNumber, cocktailNumber, getCocktail(cocktailNumber));
	}
	
	private boolean hasNextRandomCocktail() {
		if (randomPopulationPosition >= randomPopulationOrder.length) {
			this.randomPopulationOrder = generateRandomPopulationOrder();
			this.randomPopulationPosition = 0;
			
			return false;
		}
		return true;
	}
	
	public boolean hasNextRandomNamedCocktail() {
		return hasNextRandomCocktail();
	}
	
	/*
	 * This works like an iterator - it returns the next random cocktail
	 * @return the next random cocktail
	 */
	private Cocktail getNextRandomCocktail() {
		Cocktail theCocktail =  getPopulation()[randomPopulationOrder[randomPopulationPosition]];
		randomPopulationPosition = randomPopulationPosition + 1;
		
		return theCocktail;
	}
	
	public CocktailWithName getNextRandomNamedCocktail(String evolutionStackName, int generationNumber) {
		Cocktail theCocktail = getNextRandomCocktail();
		int cocktailNumber = -1;
		
		for (int i = 0; i < getPopulationSize(); i++) {
			if (getPopulation()[i].equals(theCocktail)) {
				cocktailNumber = i;
			}
		}
		
		return new CocktailWithName(evolutionStackName, generationNumber, cocktailNumber, theCocktail);
	}
	
//	public CocktailGeneration clone() {
//		CocktailGeneration nextGen = new CocktailGeneration(getPopulationSize(), getPopulation());
//		
//		return nextGen;
//	}
	
	public int getCocktailNumber(Cocktail cocktail) {
		for (int i = 0; i < getPopulationSize(); i++) {
			if (getPopulation()[i].equals(cocktail)) {
				return i;
			}
		}
		
		return -1;
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
	
	public CocktailWithName[] rankNamedCocktails(String evolutionStackName, int generationNumber) {
		Cocktail[] rankedCocktails = rankCocktails();
		CocktailWithName[] rankedCocktailsWithName = new CocktailWithName[rankedCocktails.length];
		
		for (int i = 0; i < rankedCocktails.length; i++) {
			for (int j = 0; i < getPopulationSize(); i++) {
				if (rankedCocktails[i].equals(getPopulation()[j])) {
					rankedCocktailsWithName[i] = new CocktailWithName(evolutionStackName, generationNumber, i, rankedCocktails[i]);
				}
			}
		}
		
		return rankedCocktailsWithName;
	}
	
	public String toString() {
		String out = "";
		
		for (int i = 0; i < getPopulationSize(); i++) {
			out += "Cocktail " + i + ": " + population[i].toString() + "\n";
		}
		
		return out;
	}
	
	public String toString(String evolutionStackName, int generationNumber) {
		String out = "";
		
		for (int i = 0; i < getPopulationSize(); i++) {
			out += "Cocktail " + i + ": " + getNamedCocktail(i, evolutionStackName, generationNumber).toString() + "\n";
		}
		
		return out;
	}
	
	/*public String randomToString() {
		String out = "";
		
		for (int i = 0; i < getPopulationSize(); i++) {
			out += "Cocktail " + randomPopulationOrder[i] + ": " + population[randomPopulationOrder[i]].toString() + "\n";
		}
		
		return out;
	}
	
	public String randomToString(String evolutionStackName, int generationNumber) {
		String out = "";
		
		for (int i = 0; i < getPopulationSize(); i++) {
			out += "Cocktail " + randomPopulationOrder[i] + ": " + getNamedCocktail(randomPopulationOrder[i], evolutionStackName, generationNumber).toString() + "\n";
		}
		
		return out;
	}*/



	public static CocktailGeneration loadFromString(String gStr) {
		String cStr[] = gStr.split("\\r?\\n");

		Cocktail[] population = new Cocktail[cStr.length];
		int i = 0;
		for(String t: cStr) {
			System.out.println(t);
			population[i++] = Cocktail.loadFromString(t);
		}

		return new CocktailGeneration(population);
	}

	public String getSaveString() {
		String str = "";
		for (Cocktail c: population)
			str += c.getSaveString() + "\n";
		return str;
	}
}
