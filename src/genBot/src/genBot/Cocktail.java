package genBot;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Random;
import java.text.DecimalFormat;

/*
 * A cocktail consists of a multiple IngredientAmounts (each ingredient in
 * the IngredientArray must have an IngredientAmount).
 * At the moment also the Cocktails Fitness is stored in the Cocktail class,
 * however it might be better to take the fitness to it's own class later!
 */

public class Cocktail implements Comparable<Cocktail>, Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private IngredientAmount[] ingredientAmounts;
	private Double fitness;
	private boolean rated;
	private boolean poured;
	private boolean queued;
	private boolean eliteCocktail;
	private boolean truncated;
	
	private double rating;
	
	/*
	 * Constructor
	 * When the sum of all amounts is not equal to one, the amounts get normalized
	 * so that this requirement is met.
	 * @param amount an array of amounts (as doubles). The length of this array must
	 * equal the length in IngredientArray
	 * @see IngredientArray
	 */
	public Cocktail(double[] amount) {
		
		Ingredient[] ingredients = IngredientArray.getInstance().getAllIngredients();
		
		if (amount.length != ingredients.length) {
			throw new IllegalArrayLengthException("amount has length " + amount.length + ", should have length " + ingredients.length);
		}
		
		double sum = 0;
		for (int i = 0; i < amount.length; i++) {
			if (amount[i] < 0) {
				throw new IllegalIngredientException("No ingredients < 0 allowed");
			}
			sum = sum + amount[i];
		}
		
		ingredientAmounts = new IngredientAmount[ingredients.length];
		
		for (int i = 0; i < ingredients.length; i++) {
			ingredientAmounts[i] = new IngredientAmount(ingredients[i], amount[i] / sum);
		}
		
		this.rated 		= false;
		this.poured 	= false;
		this.queued 	= false;
		this.eliteCocktail = false;
		this.truncated = false;
	}
	
	/*
	 * The first generation needs random cocktails. They are constructed in this
	 * static method. It may be better to create an own class for that.
	 */
	/*
	// Unused
	public static Cocktail newRandomCocktail() {
		int ingredientNumber = IngredientArray.getInstance().getAllIngredients().length;
		
		double[] randoms = new double[ingredientNumber + 1];
		Random generator = new Random();
		
		for (int i = 0; i < randoms.length - 2; i++) {
			randoms[i] = generator.nextDouble();
		}
		randoms[randoms.length - 2] = 0;
		randoms[randoms.length - 1] = 1;
		
		Arrays.sort(randoms);
		
		double[] ingredientAmounts = new double[ingredientNumber];
		
		for (int i = 0; i < ingredientNumber; i++) {
			ingredientAmounts[i] = randoms[i + 1] - randoms[i];
		}
		
		return new Cocktail(ingredientAmounts);
	}
	*/

	public static Cocktail newRandomCocktail(boolean[] booleanAllowedIngredients) {
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
	
	public IngredientAmount[] getIngredientAmounts() {
		return ingredientAmounts;
	}
	
	/*
	 * Returns the amount for an ingredient specified
	 * @param ingredient The ingredient
	 */
	public double getAmount(Ingredient ingredient) {
		for (int i = 0; i < ingredientAmounts.length; i++) {
			if (ingredientAmounts[i].getIngredient().equals(ingredient)) {
				return ingredientAmounts[i].getAmount();
			}
		}
				
		throw new IllegalIngredientException("Ingredient " + ingredient.getName() + " is not defined");
	}
	
	/*
	 * Returns the amount for an ingredient specified
	 * @param ingredientName The ingredients name
	 */
	public double getAmount(String ingredientName) {
		Ingredient ingredient = IngredientArray.getInstance().getByName(ingredientName);
		
		try {
			return getAmount(ingredient);
		} catch (IllegalIngredientException e) {
			throw new IllegalIngredientNameException("The ingredient " + ingredientName + " has not been defined!");
		}
	}

	public boolean isRated() {
		return rated;
	}
	
	public boolean isPoured() {
		return poured;
	}
	
	public void setPoured(boolean p) {
		poured = p;
	}
	
	public boolean isQueued() {
		return queued;
	}
	
	public void setQueued(boolean bool) {
		this.queued = bool;
	}
		
	/*
	 * Returns the fitness if the fitness was set before. Throws an exception otherwise.
	 * @return Coctails fitness
	 */
	public double getFitness() throws FitnessNotSetException {
		if (rated) {
			return fitness;
		} else {
			throw new FitnessNotSetException("Fitness was not set yet!");
		}
	}
	
	public void setFitness(double fitness) {
		this.fitness = fitness;
	}
	
	public double getRating() {
		return rating;
	}

	public void setRating(double rating) {
		rated = true;
		this.rating = rating;
	}
	
	public double[] getAmountsAsDouble() {
		IngredientArray ingredients = IngredientArray.getInstance();
		double[] amounts = new double[ingredients.getNumberOfIngredients()];
		for (int i = 0; i < amounts.length; i++) {
			amounts[i] = getAmount(ingredients.getAllIngredients()[i]);
		}
		return(amounts);
	}
	
	public boolean isEliteCocktail() {
		return eliteCocktail;
	}

	public boolean isTruncated() {
		return truncated;
	}

	public void setTruncated(boolean t) {
		this.truncated = t;
	}
	
	public Cocktail copyElite() {
		Cocktail retCocktail = copy();
		this.eliteCocktail = true;
		
		return retCocktail;
	}
	
	public Cocktail copy() {
		return new Cocktail(getAmountsAsDouble());
	}
	
	public double getCosts() {
		double costs = 0;
		for (int i = 0; i < IngredientArray.getInstance().getNumberOfIngredients(); i++) {
			costs += getIngredientAmounts()[i].getAmountPrice();
		}
		return costs;
	}

	@Override
	public int compareTo(Cocktail otherCocktail) {
		try {
			if (getFitness() < otherCocktail.getFitness()) {
				return -1;
			} else if (getFitness() > otherCocktail.getFitness()) {
				return 1;
			} else {
				return 0;
			}
		} catch (FitnessNotSetException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
		}
		return 0;
	}
	
	public void changeAmounts(double[] amounts) {
		if (amounts.length != ingredientAmounts.length) {
			throw new IllegalArgumentException("You try to set " + amounts.length + " ingredients but this cocktail has " + ingredientAmounts.length + "ingredients!");
		}
		Ingredient[] ingredients = IngredientArray.getInstance().getAllIngredients();
		
		int sum = 0;
		
		for (int i = 0; i < amounts.length; i++) {
			sum += amounts[i];
		}
		
		for (int i = 0; i < amounts.length; i++) {
			ingredientAmounts[i] = new IngredientAmount(ingredients[i], amounts[i] / sum);
		}
	}
	
	public boolean pricePerLiterHigherAs(double price) {
		if (getCosts() > price) {
			return true;
		} else {
			return false;
		}
	}

	public static Cocktail loadFromString(String cStr) {
		String aStr[] = cStr.split("\\t");

		Ingredient[] ingredients = IngredientArray.getInstance().getAllIngredients();

		double amounts[] = new double[ingredients.length];

		int i = 0;
		for(Ingredient ing: ingredients) {
			//System.out.println(aStr[i]);
			amounts[i] = Double.valueOf(aStr[i]);
			i++;
		}

		Cocktail c = new Cocktail(amounts);

		String rating = aStr[i++];
		if(!rating.equals("-")) {
			try {
				c.rating = Double.valueOf(rating);
			} catch (NumberFormatException e) {

			}
			c.rated = true;
			c.poured = true;
		}

		String fitness = aStr[i++];
		if(!fitness.equals("-")) {
			try {
				c.fitness = Double.valueOf(fitness);
			} catch (NumberFormatException e) {

			}
		}

		return c;
	}

	public String getSaveString() {
		String str = "";

		DecimalFormat df = new DecimalFormat("#.##");

		for (IngredientAmount d: ingredientAmounts)
			str += df.format(d.getAmount()) + "\t";

		if(rated) {
			str += df.format(rating) + "\t";
		} else {
			str += "-\t";
		}

		if(fitness != null)
			str += df.format(fitness) + "\t";
		else
			str += "-\t";

		str += df.format(getCosts()) + "\t";

		if(fitness != null) {
			str += Boolean.toString(isEliteCocktail()) + "\t";
			str += Boolean.toString(isTruncated()) + "\t";
		}
		return str;
	}
}
