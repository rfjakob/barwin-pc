package genBot2;

import java.util.Arrays;
import java.util.Random;

/*
 * A cocktail consists of a multiple IngredientAmounts (each ingredient in
 * the IngredientArray must have an IngredientAmount).
 * At the moment also the Cocktails Fitness is stored in the Cocktail class,
 * however it might be better to take the fitness to it's own class later!
 */

public class Cocktail implements Comparable<Cocktail> {
	
	private IngredientAmount[] ingredientAmounts;
	private double fitness;
	private boolean fitnessIsSet;
	
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
		
		fitnessIsSet = false;
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
		throw new IllegalIngredientException("Ingredient " + ingredient.getName() + "is not defined");
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
			throw new IllegalIngredientNameException("The ingredient " + ingredientName + "has not been defined!");
		}
	}

	public boolean isFitnessSet() {
		return fitnessIsSet;
	}
	
	public void setFitness(double fitness) {
		this.fitness = fitness;
		fitnessIsSet = true;
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
	
	/*
	 * Mutates the cocktail. An ingredient is randomly chosen and then changed
	 * following a normal distribution with a standard deviation specified.
	 * All other ingredients are then also changed so that the sum of the ingredients
	 * equals one again. This is maybe not implemented optimal (check the code) but I 
	 * found no better way.
	 * @param stdDeviation the standard deviation
	 * @return A mutated Cocktail
	 */
	public Cocktail mutate(double stdDeviation) {
		if (stdDeviation < 0) {
			throw new IllegalArgumentException("stdDeviation must be greater than 0 (" + stdDeviation + "was specified)!");
		}
		
		Ingredient[] ingredients = IngredientArray.getInstance().getAllIngredients();
		double[] amounts = new double[IngredientArray.getInstance().getNumberOfIngredients()];
		
		for (int i = 0; i < IngredientArray.getInstance().getNumberOfIngredients(); i++) {
			amounts[i] = getAmount(ingredients[i]);
		}
		
		// mutate one of the amounts
		Random rnd = new Random();
		int mutatedIngredient = rnd.nextInt(IngredientArray.getInstance().getNumberOfIngredients());
		double change = rnd.nextGaussian() * stdDeviation;
		
		// Make sure the bounds are not violated
		if (change + amounts[mutatedIngredient] < 0) {
			change = amounts[mutatedIngredient] * (-1);
		} else if (change + amounts[mutatedIngredient] > 1) {
			change = 1 - amounts[mutatedIngredient];
		}
		
		// change the amount of the specific ingredient
		amounts[mutatedIngredient] = amounts[mutatedIngredient] + change;
		
		// now change the other amounts
		double changeOthers = (change / (IngredientArray.getInstance().getNumberOfIngredients() - 1)) * (-1);
		
		for (int i = 0; i < IngredientArray.getInstance().getNumberOfIngredients(); i++) {
			if (i != mutatedIngredient) {
				amounts[i] = amounts[i] + changeOthers;
				
				// this is not very elegant, but i found no better solution
				if (amounts[i] < 0) {
					amounts[i] = 0;
				} else if (amounts[i] > 1) {
					amounts[i] = 1;
				}
			}
		}
		
		return new Cocktail(amounts);
	}
	
	public void setFitness(CheckFitness fitnessCheck) {
		fitness = fitnessCheck.checkFitness(this);
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
}
