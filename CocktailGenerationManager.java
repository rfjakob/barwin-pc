package genBot2;

import java.util.Arrays;
import java.util.Random;

public class CocktailGenerationManager {
	
	private int generationNumber;
	protected CocktailGeneration cocktailGeneration;
	private int populationSize;
	private CheckFitness fitnessCheck;
		
	public CocktailGenerationManager(int generationNumber, int generationSize, CheckFitness fitnessCheck) {
		this.generationNumber = generationNumber;
		this.populationSize = generationSize;
		this.fitnessCheck = fitnessCheck;
		
		Cocktail[] cocktails = new Cocktail[generationSize];
		
		for (int i = 0; i < generationSize; i++) {
			cocktails[i] = generateRandomCocktail();
		}
		
		cocktailGeneration = new CocktailGeneration(generationSize, cocktails);
	}
	
	private Cocktail generateRandomCocktail() {
		int ingredientNumber = IngredientArray.getInstance().getNumberOfIngredients();
		
		double[] ingredientShare = new double[ingredientNumber + 1];
		
		Random rnd = new Random();
		
		for (int i = 0; i < ingredientShare.length - 2; i++) {
			ingredientShare[i] = rnd.nextDouble();
		}
		ingredientShare[ingredientShare.length - 2] = 0;
		ingredientShare[ingredientShare.length - 1] = 1;
		
		Arrays.sort(ingredientShare);
		
		double[] cocktailIngredients = new double[ingredientShare.length - 1];
		
		for (int i = 0; i < cocktailIngredients.length; i++) {
			cocktailIngredients[i] = ingredientShare[i + 1] - ingredientShare[i];
		}
		
		Cocktail cocktail = new Cocktail(cocktailIngredients);
		
		return cocktail;
	}
	
	public CocktailGeneration evolve(double stdDeviation, int elitism) {
		generationNumber = generationNumber + 1;
		
		// Clone current generation
		// CocktailGeneration nextGeneration = cocktailGeneration.clone();
		
		// Crossover
		CocktailGeneration nextGeneration = cocktailGeneration.allCrossovers(populationSize);
		// Mutate
		nextGeneration.mutateCocktails(stdDeviation);
		// Elitism
		nextGeneration.applyElitism(elitism, getCocktailGeneration());
		
		return nextGeneration;
	}

	public int getGenerationNumber() {
		return generationNumber;
	}
	
	public CocktailGeneration getCocktailGeneration() {
		return cocktailGeneration;
	}
	
	public int getPopulationSize() {
		return populationSize;
	}
	
	public void evaluate() {
		cocktailGeneration.getNextRandomCocktail().setFitness(fitnessCheck);
	}
	
	public String toString() {
		String out = "Generation number " + getGenerationNumber() + "\n";
		out += getCocktailGeneration().toString();
		
		return out;
	}
	
	public String randomToString() {
		String out = "Generation number " + getGenerationNumber() + ":\n";
		out += getCocktailGeneration().randomToString();
		
		return out;
	}
}
