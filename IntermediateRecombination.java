package genBot2;

import java.util.Random;

public class IntermediateRecombination extends RouletteWheelSelection implements Recombination {

	private double variableArea;
	
	/*
	 * Constructor
	 * @param variableArea influences the interval to choose the new cocktail from 
	 * the naive approach would be variableArea = 0 - then the interval is [0,1]
	 * that means a new cocktail's ingredient is 
	 * parent1(ingredient) * p + parent2(ingredient) * (1 - p)
	 * 
	 * However, this leads to the possible area of ingredient values getting smaller
	 * and smaller.
	 * With variableArea = 0.25 the interval is [-0.25, 1.25] which ensures
	 * statistically, that the offsprings area is the same as it was for the parents
	 * see http://www.geatbx.com/docu/algindex-03.html#P570_30836
	 */
	public IntermediateRecombination(double variableArea) {
		if (variableArea < 0) {
			throw new IllegalArgumentException("variableArea must be bigger than 0!");
		}
		this.variableArea = variableArea;
	}
	
	/*
	 * returns two new Cocktails based on the selection algorithm
	 * @param cocktailGeneration the current cocktailGeneration
	 * @return two new cocktails based on the recombination
	 */
	public Cocktail[] crossover(CocktailGeneration cocktailGeneration) throws FitnessNotSetException {
		Random rnd = new Random();
		
		Cocktail[] population = cocktailGeneration.getPopulation();
		// choose cocktail 1
		double cocktailSelector1 = rnd.nextDouble();
		Cocktail cocktail1 = population[0];
		
		double[] rouletteWheel1 = generateRouletteWheel(population);
				
		for (int i = 1; i < rouletteWheel1.length; i++) {
			if (cocktailSelector1 > rouletteWheel1[i - 1] && cocktailSelector1 < rouletteWheel1[i]) {
				cocktail1 = population[i];
			}
		}
		
		// choose cocktail 2 (a bit more tricky)
		Cocktail[] restPopulation = new Cocktail[cocktailGeneration.getPopulationSize() - 1];
		int restPopulationIndex = 0;
		for (int i = 0; i < population.length; i++) {
			if (!population[i].equals(cocktail1)) {
				restPopulation[restPopulationIndex] = population[i];
				restPopulationIndex = restPopulationIndex + 1;
			}
		}
		
		double cocktailSelector2 = rnd.nextDouble();
		Cocktail cocktail2 = restPopulation[0];
		
		double[] rouletteWheel2 = generateRouletteWheel(restPopulation);
		
		for (int i = 1; i < rouletteWheel2.length; i++) {
			if (cocktailSelector2 > rouletteWheel2[i - 1] && cocktailSelector2 < rouletteWheel2[i]) {
				cocktail2 = restPopulation[i];
			}
		}
		
		// now perform the recombination
		IngredientArray ingredientArray = IngredientArray.getInstance();
		
		// generate the recombination shares
		double[] recombinationShares1 = new double[ingredientArray.getNumberOfIngredients()];
		double[] recombinationShares2 = new double[ingredientArray.getNumberOfIngredients()];

		for (int i = 0; i < ingredientArray.getNumberOfIngredients(); i++) {
			recombinationShares1[i] = rnd.nextDouble() * (1 + (2 * variableArea)) - variableArea;
			recombinationShares2[i] = 1 + variableArea - recombinationShares1[i];
		}
		
		double[][] ingredients = new double[2][ingredientArray.getNumberOfIngredients()];
		
		for (int i = 0; i < ingredientArray.getNumberOfIngredients(); i++) {
			ingredients[0][i] = recombinationShares1[i] * cocktail1.getAmount(ingredientArray.getAllIngredients()[i]) + (1.0 - recombinationShares1[i]) * cocktail2.getAmount(ingredientArray.getAllIngredients()[i]);
			ingredients[1][i] = recombinationShares2[i] * cocktail1.getAmount(ingredientArray.getAllIngredients()[i]) + (1.0 - recombinationShares2[i]) * cocktail2.getAmount(ingredientArray.getAllIngredients()[i]);
			
			if (ingredients[0][i] < 0) {
				ingredients[0][i] = 0;
			}
			if (ingredients[1][i] < 0) {
				ingredients[1][i] = 0;
			}
			if (ingredients[0][i] > 1) {
				ingredients[0][1] = 1;
			}
			if (ingredients[1][i] > 1) {
				ingredients[1][1] = 1;
			}
		}
		
		Cocktail[] returnCocktails = {new Cocktail(ingredients[0]), new Cocktail(ingredients[1])};
		return returnCocktails;
	}
	
	/*
	 * makes all crossovers for the Generation
	 * @return a new cocktail generation
	 */
	public CocktailGeneration allCrossovers(CocktailGeneration cocktailGeneration, int newPopulationSize) throws FitnessNotSetException {
		Cocktail[] population = new Cocktail[newPopulationSize];
		
		for (int i = 0; i < newPopulationSize; i = i + 2) {
			Cocktail[] children = crossover(cocktailGeneration);
			population[i] = children[0];
			// we have to check if both children fit in the population (only even numbers)
			if (i + 1 < population.length) {
				population[i + 1] = children[1];
			}
		}
		
		return new CocktailGeneration(population);
	}

	@Override
	public CocktailGeneration recombine(CocktailGeneration population,
			int newPopulationSize, boolean[] booleanAllowedIngredients) throws FitnessNotSetException {
		return allCrossovers(population, newPopulationSize);
	}

}
