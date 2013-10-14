package genBot2;

import java.util.Random;

public class StandardMutation implements Recombination {
	
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

	public StandardMutation(double stdDeviation) {
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
		
		// mutate amount by probability 1/n
		Random rnd = new Random();
		for (int i = 0; i < amounts.length; i++) {
			if (rnd.nextDouble() >=  (1 - (1 / amounts.length))) {
				double mutatedIngredient = amounts[i];
				
				double change = rnd.nextGaussian() * stdDeviation;
				
				// Make sure the bounds are not violated
				if (change + mutatedIngredient < 0) {
					change = mutatedIngredient * (-1);
				} else if (change + mutatedIngredient > 1) {
					change = 1 - mutatedIngredient;
				}
				
				// change the amount of the specific ingredient
				amounts[i] = mutatedIngredient + change;
				
				// now change the other amounts
				double changeOthers = (change / (IngredientArray.getInstance().getNumberOfIngredients() - 1)) * (-1);
				
				for (int j = 0; j < IngredientArray.getInstance().getNumberOfIngredients(); j++) {
					if (j != i) {
						amounts[j] = amounts[j] + changeOthers;
						
						// this is not very elegant, but i found no better solution
						if (amounts[j] < 0) {
							amounts[j] = 0;
						} else if (amounts[j] > 1) {
							amounts[j] = 1;
						}
					}
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
