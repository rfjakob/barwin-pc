package genBot2;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Random;

public class CocktailGenerationManager implements Serializable {

	private static final long serialVersionUID = 1L;
	private int generationNumber;
	private CocktailGeneration cocktailGeneration;
	private final String evolutionStackName;

	public CocktailGenerationManager(int initialPopulationSize, String cocktailStackName, boolean[] booleanAllowedIngredients, double maxPricePerLiter) {
		this.generationNumber = 0;
		
		Cocktail[] cocktails = new Cocktail[initialPopulationSize];
		
		int i = 0;
		while (i < initialPopulationSize) {
			cocktails[i] = generateRandomCocktail(booleanAllowedIngredients);
			
			if (!cocktails[i].pricePerLiterHigherAs(maxPricePerLiter)) {
				i++;
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
