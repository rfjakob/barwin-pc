package genBot2;

import java.util.Random;

public class OnePointCrossover extends RouletteWheelSelection implements Recombination {

	public OnePointCrossover() {
		// Nothing at the moment
	}

	/*
	 * returns two new Cocktails based on the selection algorithm 
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
		
		// now perform the crossover
		IngredientArray ingredientArray = IngredientArray.getInstance();
		
		double[] ingredients = new double[ingredientArray.getNumberOfIngredients()];
		int split = rnd.nextInt(ingredients.length);
		
		double[] child1Ingredients = new double[ingredients.length];
		double[] child2Ingredients = new double[ingredients.length];
		
		for (int i = 0; i < ingredients.length; i++) {
			if (i <= split) {
				child1Ingredients[i] = cocktail1.getAmount(ingredientArray.getAllIngredients()[i]);
				child2Ingredients[i] = cocktail2.getAmount(ingredientArray.getAllIngredients()[i]);
			} else {
				child2Ingredients[i] = cocktail1.getAmount(ingredientArray.getAllIngredients()[i]);
				child1Ingredients[i] = cocktail2.getAmount(ingredientArray.getAllIngredients()[i]);
			}
		}
		
		Cocktail[] children = {new Cocktail(child1Ingredients), new Cocktail(child2Ingredients)};
		return children;
	}
	
	/*
	 * makes all crossovers for the Generation
	 * @return a new cocktail generation
	 */
	public CocktailGeneration allCrossovers(CocktailGeneration cocktailGeneration, int populationSize) throws FitnessNotSetException {
		Cocktail[] population = new Cocktail[populationSize];
		
		for (int i = 0; i < populationSize; i = i + 2) {
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
			int newPopulationSize) throws FitnessNotSetException {
		return allCrossovers(population, newPopulationSize);
	}

}
