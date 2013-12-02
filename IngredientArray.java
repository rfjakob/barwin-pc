package genBot2;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Properties;

/*
 * IngredientArray is a Singleton to keep the Ingredients in order over
 * the whole program.
 * Please note that the ingredients are not sanity-checked. Make sure
 * everything is correct.
 */

public class IngredientArray {
	private static IngredientArray ingredientArray = null;
	private static String ingredientFolder = "ingredients/";
	
	private Ingredient[] ingredients;
	
	/*
	 * Private constructor (because it's a singleton).
	 * The Ingredients and their order are defined here.
	 */
	private IngredientArray() {
		String files;
		File folder = new File(ingredientFolder);
		File[] listOfFiles = folder.listFiles();
		
		ArrayList<String> fileNames = new ArrayList<String>();
		
		for (int i = 0; i < listOfFiles.length; i++) {
			if (listOfFiles[i].isFile()) {
				files = listOfFiles[i].getName();
				
				if (files.endsWith(".properties") || files.endsWith(".PROPERTIES")) {
					fileNames.add(files);
				}
			}
		}
		ingredients = new Ingredient[fileNames.size()];
		
		Properties ingProps = new Properties();
		
		try {
			for (int i = 0; i < ingredients.length; i++) {
				ingProps.load(new FileInputStream(ingredientFolder + fileNames.get(i)));
				
				ingredients[i] = new Ingredient(
						ingProps.getProperty("name"),
						Double.parseDouble(ingProps.getProperty("bottlePrice")),
						Double.parseDouble(ingProps.getProperty("bottleSize")),
						Integer.parseInt(ingProps.getProperty("arduinoOutputLine")),
						ingredientFolder
						);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
	
	public static void newIngredient(String name, double bottlePrice, double bottleSize, int arduinoOutputLine) {
		new Ingredient(name, bottlePrice, bottleSize, arduinoOutputLine, ingredientFolder);
		
		IngredientArray ingredientArray = getInstance();
		ingredientArray.loadIngredients();
	}
	
	/*
	 * loads all Ingredients from the properties files in the properties folder
	 */
	public void loadIngredients() {
		ingredientArray = new IngredientArray();
	}
	
	/*
	 * Returns just the Array of Ingredients, not the whole class 
	 * IngredientArray. This method is just here to avoid too long
	 * method trains (I think this is not the correct name...).
	 * @return an array of 
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
