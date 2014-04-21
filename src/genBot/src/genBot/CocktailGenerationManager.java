package genBot;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Random;

public class CocktailGenerationManager implements Serializable {

	private static final long serialVersionUID = 1L;
	private int generationNumber;
	private CocktailGeneration cocktailGeneration;
	private final String evolutionStackName;
	
	private int maxAttemptsToMeetPriceConstraints = 3000;

	public CocktailGenerationManager(int initialPopulationSize, String cocktailStackName, boolean[] booleanAllowedIngredients, double[] initMeanValues, double[] initOffsets, double maxPricePerLiter) throws MaxAttemptsToMeetPriceConstraintException {
		this.generationNumber = 0;
				
		if (booleanAllowedIngredients.length != initMeanValues.length | booleanAllowedIngredients.length != initOffsets.length) {
			throw new IllegalArgumentException("One of the input arrays has the wrong length!");
		}
		
		double sumMeanValues = 0;
		for (int i = 0; i < initMeanValues.length; i++) {
			sumMeanValues += initMeanValues[i];
		}
		if (sumMeanValues != 1) {
			throw new IllegalArgumentException("The values of initMeanValues does not sum up to one!");
		}
				
		Cocktail[] cocktails = new Cocktail[initialPopulationSize];
		
		int countToThrowException = 0;
		
		int i = 0;
		while (i < initialPopulationSize) {
			cocktails[i] = generateRandomCocktail(booleanAllowedIngredients);
			
			boolean resetCocktail = false;
			
			for (int j = 0; j < booleanAllowedIngredients.length; j++) {
				double val = cocktails[i].getAmountsAsDouble()[j];
				
				if (val < initMeanValues[j] - initOffsets[j] | val > initMeanValues[j] + initOffsets[j]) {
					resetCocktail = true;
				}
			}
			
			if (cocktails[i].pricePerLiterHigherAs(maxPricePerLiter)) {
				resetCocktail = true;
			}
			
			if (resetCocktail = true) {
				countToThrowException++;
				if (countToThrowException >= maxAttemptsToMeetPriceConstraints) {
					throw new MaxAttemptsToMeetPriceConstraintException("Tried " + maxAttemptsToMeetPriceConstraints + " times to find a cocktail that meets the cost constraint of " + maxPricePerLiter + " Euros per Liter. Didn't succeed. I give up now.");
				}
			} else {
				i++;
				countToThrowException = 0;
			}
		}

		this.cocktailGeneration = new CocktailGeneration(cocktails);
		
		this.evolutionStackName = cocktailStackName;
	}
	
	/*
	 * generates a random cocktail
	 * @return a random cocktail
	 */
	private Cocktail generateRandomCocktail(boolean[] booleanAllowedIngredients) {
		int ingredientNumber = IngredientArray.getInstance().getNumberOfIngredients();
		
		int numberOfAllowedIngredients = 0;
		for (int i = 0; i < booleanAllowedIngredients.length; i++) {
			if (booleanAllowedIngredients[i]) {
				numberOfAllowedIngredients++;
			}
		}
		
		double[] allowedIngredientShare = new double[numberOfAllowedIngredients + 1];
		
		Random rnd = new Random();
		
		for (int i = 0; i < allowedIngredientShare.length - 2; i++) {
			allowedIngredientShare[i] = rnd.nextDouble();
		}
		allowedIngredientShare[allowedIngredientShare.length - 2] = 0;
		allowedIngredientShare[allowedIngredientShare.length - 1] = 1;
		
		Arrays.sort(allowedIngredientShare);
		
		double[] allowedCocktailIngredients = new double[allowedIngredientShare.length - 1];
		
		for (int i = 0; i < allowedCocktailIngredients.length; i++) {
			allowedCocktailIngredients[i] = allowedIngredientShare[i + 1] - allowedIngredientShare[i];
		}
		
		double[] cocktailIngredients = new double[ingredientNumber];
		int iAllowedCocktailIngredients = 0;
		
		for (int i = 0; i < cocktailIngredients.length; i++) {
			if (booleanAllowedIngredients[i]) {
				cocktailIngredients[i] = allowedCocktailIngredients[iAllowedCocktailIngredients];
				iAllowedCocktailIngredients++;
			} else {
				cocktailIngredients[i] = 0;
			}
		}
		
		Cocktail cocktail = new Cocktail(cocktailIngredients);
		
		return cocktail;
	}
	
	public String getEvolutionStackName() {
		return evolutionStackName;
	}
	
	public void increaseGenerationNumber() {
		generationNumber++;
	}
	
	public void setGeneration(CocktailGeneration cocktailGeneration) {
		this.cocktailGeneration = cocktailGeneration;
	}
	
	public int getGenerationNumber() {
		return generationNumber;
	}
	
	public int getCurrentPopulationSize() {
		return cocktailGeneration.getPopulationSize();
	}
	
	public CocktailGeneration getCocktailGeneration() {
		return cocktailGeneration;
	}
	
	public CocktailWithName[] getNamedCocktailGeneration() {
		return getCocktailGeneration().getNamedPopulation(getEvolutionStackName(), getGenerationNumber());
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
	
	public String toString() {
		String out = "Generation number " + getGenerationNumber() + "\n";
		out += getCocktailGeneration().toString();
		
		return out;
	}

	/*
	 * prints the cocktail generation in random order
	 */
	public String randomToString() {
		String out = "Generation number " + getGenerationNumber() + ":\n";
		out += getCocktailGeneration().randomToString();
		
		return out;
	}
	
	public CocktailWithName[] getRatedNamedCocktailGeneration() {
		return getCocktailGeneration().getRankedPopulationWithName(getEvolutionStackName(), getGenerationNumber());
	}
	
	public CocktailWithName[] getUnRatedNamedCocktailGeneration() {
		return getCocktailGeneration().getUnRankedPopulationWithName(getEvolutionStackName(), getGenerationNumber());
	}
}
