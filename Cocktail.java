package genBot2;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Random;

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
	private double fitness;
	private boolean fitnessIsSet;
	private boolean poured;
	private boolean pouring;
	private boolean queued;
	private boolean eliteCocktail;
	
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
		
		this.fitnessIsSet = false;
		this.poured = false;
		this.pouring = false;
		this.queued = false;
		this.eliteCocktail = false;
	}
	
	/*
	 * The first generation needs random cocktails. They are constructed in this
	 * static method. It may be better to create an own class for that.
	 */
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

	public boolean isFitnessSet() {
		return fitnessIsSet;
	}
	
	public boolean isPoured() {
		return poured;
	}
	
	public void setPoured(boolean p) {
		poured = p;
		if(p)
			setPouring(false);
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
		if (isFitnessSet()) {
			return fitness;
		} else {
			throw new FitnessNotSetException("Fitness was not set yet!");
		}
	}
	
	public void setFitness(CheckFitness fitnessCheck, double fitnessInput) {
		fitness = fitnessCheck.checkFitness(this, fitnessInput);
		fitnessIsSet = true;
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
	
	public void setEliteCocktailTrue() {
		this.eliteCocktail = true;
	}
	
	public Cocktail copyElite() {
		Cocktail retCocktail = copy();
		retCocktail.setEliteCocktailTrue();
		
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

	public String toString() {
			String out = "";
			
			for (int i = 0; i < ingredientAmounts.length; i++) {
				out += ingredientAmounts[i].toString();
	//			if (i < ingredientAmounts.length - 1) {
					out += ", ";
	//			}
			}
			out += "Fitness: " + fitness;
			
			return out;
		}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 * This should maybe also be moved to another class
	 */
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

	public void setPouring(boolean p) {
		pouring = p;
	}

	public boolean isPouring() {
		return pouring;
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
}
