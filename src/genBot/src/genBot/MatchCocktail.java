package genBot;

import java.util.Random;

public class MatchCocktail implements CheckFitness {
	
	Cocktail referenceCocktail;

	public MatchCocktail(Cocktail referenceCocktail) {
		this.referenceCocktail = referenceCocktail;
	}

	@Override
	public void setFitness(Cocktail cocktail) {
		Ingredient[] ingredients = IngredientArray.getInstance().getAllIngredients();
		
		double squaredDistance = 0;
		
		for (int i = 0; i < ingredients.length; i++) {
			squaredDistance += Math.pow(referenceCocktail.getAmount(ingredients[i]) - cocktail.getAmount(ingredients[i]), 2);
		}
		
		Random rnd = new Random();
		
		squaredDistance = squaredDistance + rnd.nextGaussian();
		
		cocktail.setFitness((-1) * squaredDistance);
	}

}
