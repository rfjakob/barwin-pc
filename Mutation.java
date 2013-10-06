package genBot2;

import java.util.Random;

public class Mutation implements Recombination {
	
	private double stdDeviation;

	public double getStdDeviation() {
		if (stdDeviation < 0) {
			throw new IllegalArgumentException("stdDeviation must be greater than 0 (" + stdDeviation + "was specified)!");
		}
		return stdDeviation;
	}

	public void setStdDeviation(double stdDeviation) {
		if (stdDeviation < 0) {
			throw new IllegalArgumentException("stdDeviation must be greater than 0 (" + stdDeviation + "was specified)!");
		}
		this.stdDeviation = stdDeviation;
	}

	public Mutation(double stdDeviation) {
		this.stdDeviation = stdDeviation;
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
	public Cocktail mutate(double stdDeviation, Cocktail cocktail) {
		if (stdDeviation < 0) {
			throw new IllegalArgumentException("stdDeviation must be greater than 0 (" + stdDeviation + "was specified)!");
		}
		
		Ingredient[] ingredients = IngredientArray.getInstance().getAllIngredients();
		double[] amounts = new double[IngredientArray.getInstance().getNumberOfIngredients()];
		
		for (int i = 0; i < IngredientArray.getInstance().getNumberOfIngredients(); i++) {
			amounts[i] = cocktail.getAmount(ingredients[i]);
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
	
	/*
	 * mutates the cocktails of the generation
	 * @param stdDeviation the standard deviation of the mutation (how big should the
	 * change be?).
	 * @param newPopulationSize size of the next generation (should in principle be
	 * equal to population's size, because the selection should not take place in this
	 * class)
	 */
	
	public CocktailGeneration mutateCocktails(CocktailGeneration population, int newPopulationSize) {
		Cocktail[] newCocktails = new Cocktail[newPopulationSize];
		int[] randomOrder = population.generateRandomPopulationOrder();
		
		for (int i = 0; i < newPopulationSize; i++) {
			newCocktails[i] = mutate(stdDeviation, population.getCocktail(randomOrder[i]));
		}
		
		return new CocktailGeneration(newCocktails);
	}

	@Override
	public CocktailGeneration recombine(CocktailGeneration population,
			int newPopulationSize) throws FitnessNotSetException {
		return mutateCocktails(population, newPopulationSize);
	}

}
