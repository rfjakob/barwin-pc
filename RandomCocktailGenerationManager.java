package genBot2;

import java.util.Arrays;
import java.util.Random;

public class RandomCocktailGenerationManager extends CocktailGenerationManager {

	public RandomCocktailGenerationManager(int generationNumber, int generationSize) {
		super(generationNumber, generationSize);
		
		Cocktail[] cocktails = new Cocktail[generationSize];
		
		for (int i = 0; i < generationSize; i++) {
			cocktails[i] = generateRandomCocktail();
		}
		
		cocktailGeneration = new CocktailGeneration(generationSize, cocktails);
	}
	
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

}
