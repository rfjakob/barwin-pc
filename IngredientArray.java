package genBot2;

/*
 * IngredientArray is a Singleton to keep the Ingredients in order over
 * the whole program. The available Ingredients are hard-coded in this Class
 * (this is not considered very bad, but could maybe be changed...).
 */

public class IngredientArray {

	private static IngredientArray ingredientArray = null;
	
	private Ingredient[] ingredients;
	
	/*
	 * Private constructor (because it's a singleton).
	 * The Ingredients and their order are defined here.
	 */
	private IngredientArray() {
		ingredients = new Ingredient[]{
				new Ingredient("Tequila", 12.50, 0.75, 0),
				new Ingredient("Orange Juice", 1.20, 0.75, 1),
				new Ingredient("Grenadine", 3.50, 0.5, 2),
				new Ingredient("Vodka", 15.0, 0.75, 3),
				new Ingredient("Whiskey", 20.0, 0.75, 4),
				new Ingredient("Club Mate", 2.0, 0.5, 5),
				new Ingredient("Rum", 20.0, 0.75, 6)
		};
	}
	
	/*
	 * Returns the singleton IngredientArray.
	 * If there is no IngredientArray yet, it will be constructed now.
	 * @return IngredientArray an instance of the IngredientArray
	 */
	public static IngredientArray getInstance() {
		if (ingredientArray == null) {
			ingredientArray = new IngredientArray();
		}
		return ingredientArray;
	}
	
	/*
	 * Returns just the Array of Ingredients, not the whole class 
	 * IngredientArray. This method is just here to avoid too long
	 * method trains (I think this is not the correct name...).
	 * @return an array of Ingredients
	 */
	public Ingredient[] getAllIngredients() {
		IngredientArray ingredientList = getInstance();
		
		return ingredientList.ingredients;
	}
	
	/*
	 * Returns an Ingredient known by it's name. In fact, as far as I remember
	 * this method turned out to be less useful than I thought.
	 * @param ingredientName The ingredient's name
	 * @return the Ingredient
	 */
	public Ingredient getByName(String ingredientName) {
		Ingredient[] array = getAllIngredients();
		
		for (int i = 0; i < array.length; i++) {
			if (array[i].getName().equals(ingredientName)) {
				return array[i];
			}
		}
		
		throw new IllegalIngredientNameException("The ingredient " + ingredientName + "has not been defined!");
	}
	
	/*
	 * Returns the number of Ingredients in the ingredientArray. This is mainly
	 * used in for-loops to iterate over ingredients. Again this method is not really
	 * necessary but avoids method trains.
	 */
	public int getNumberOfIngredients() {
		return getInstance().getAllIngredients().length;
	}
}
