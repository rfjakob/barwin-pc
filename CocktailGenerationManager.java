package genBot2;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Random;

public class CocktailGenerationManager implements Serializable {

	private static final long serialVersionUID = 1L;
	private int generationNumber;
	private CocktailGeneration cocktailGeneration;
	private final String cocktailStackName;

	public CocktailGenerationManager(int initialPopulationSize, String cocktailStackName) {
		this.generationNumber = 0;
		
		Cocktail[] cocktails = new Cocktail[initialPopulationSize];
		for (int i = 0; i < initialPopulationSize; i++) {
			cocktails[i] = generateRandomCocktail();
		}
		this.cocktailGeneration = new CocktailGeneration(cocktails);
		this.cocktailStackName = cocktailStackName;
	}
	
	/*
	 * generates a random cocktail
	 * @return a random cocktail
	 */
	private Cocktail generateRandomCocktail() {
		int ingredientNumber = IngredientArray.getInstance().getNumberOfIngredients();
		
		double[] ingredientShare = new double[ingredientNumber + 1];
		
		Random rnd = new Random();
		
		for (int i = 0; i < ingredientShare.length - 2; i++) {
			ingredientShare[i] = rnd.nextDouble();
		}
		ingredientShare[ingredientShare.length - 2] = 0;
		ingredientShare[ingredientShare.length - 1] = 1;
		
		Arrays.sort(ingredientShare);
		
		double[] cocktailIngredients = new double[ingredientShare.length - 1];
		
		for (int i = 0; i < cocktailIngredients.length; i++) {
			cocktailIngredients[i] = ingredientShare[i + 1] - ingredientShare[i];
		}
		
		Cocktail cocktail = new Cocktail(cocktailIngredients);
		
		return cocktail;
	}
	
	public String getCocktailStackName() {
		return cocktailStackName;
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
	
	public String meanFitnessToString() throws FitnessNotSetException {
		return "Generation number " + getGenerationNumber() + ": " + getCocktailGeneration().getMeanFitness();
	}
	
	public String bestFitnessToString() throws FitnessNotSetException {
		return "Generation number " + getGenerationNumber() + ": " + getCocktailGeneration().getBestFitness();
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

}
