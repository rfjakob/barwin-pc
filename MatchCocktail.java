package genBot2;

public class MatchCocktail implements CheckFitness {
	
	Cocktail referenceCocktail;

	public MatchCocktail(Cocktail referenceCocktail) {
		this.referenceCocktail = referenceCocktail;
	}

	@Override
	public double checkFitness(Cocktail cocktail) {
		Ingredient[] ingredients = IngredientArray.getInstance().getAllIngredients();
		
		double squaredDistance = 0;
		
		for (int i = 0; i < ingredients.length; i++) {
			squaredDistance += Math.pow(referenceCocktail.getAmount(ingredients[i]) - cocktail.getAmount(ingredients[i]), 2);
		}
		
		return (1.0 / squaredDistance);
	}

}
