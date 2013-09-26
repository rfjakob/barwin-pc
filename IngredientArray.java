package genBot2;

public class IngredientArray {

	private static IngredientArray ingredientArray = null;
	
	private Ingredient[] ingredients;
	
	private IngredientArray() {
		ingredients = new Ingredient[]{
				new Ingredient("tequila", 12.50, 0.75, 1),
				new Ingredient("Orange Juice", 1.20, 1, 2),
				new Ingredient("Grenadine", 3.50, 0.5, 3)
		};
	}
	
	public static IngredientArray getInstance() {
		if (ingredientArray == null) {
			ingredientArray = new IngredientArray();
		}
		return ingredientArray;
	}
	
	public Ingredient[] getAllIngredients() {
		IngredientArray ingredientList = getInstance();
		
		return ingredientList.ingredients;
	}
	
	public Ingredient getByName(String ingredientName) {
		Ingredient[] array = getAllIngredients();
		
		for (int i = 0; i < array.length; i++) {
			if (array[i].getName().equals(ingredientName)) {
				return array[i];
			}
		}
		
		throw new IllegalIngredientNameException("The ingredient " + ingredientName + "has not been defined!");
	}
	
	public int getNumberOfIngredients() {
		return getInstance().getAllIngredients().length;
	}
}
