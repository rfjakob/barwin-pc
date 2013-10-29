package models;

import genBot2.*;

//import java.io.IOException;
import java.io.Serializable;
import java.util.Random;

public class GenBotWrapper implements Serializable {
	
	private static final long serialVersionUID = 1L;
	public Ingredient[] ingredients = IngredientArray.getInstance().getAllIngredients();
	public int firstGenerationSize = 8;
	public int truncation = 4;
	public int elitism = 3;
	public double mutation = 0.05;
	Cocktail referenceCocktail;
	CheckFitness fitnessCheck;
	Recombination mutationCrossover;
	public CocktailGenerationManager manager;
	
	public GenBotWrapper() {
	
	}
	
	public void init() {
		
		double[] referenceAmounts = new double[ingredients.length];
		
		Random rnd = new Random();
		
		for (int i = 0; i < ingredients.length; i++) {
			referenceAmounts[i] = rnd.nextDouble();
		}
		


			referenceCocktail = new Cocktail(referenceAmounts);
			fitnessCheck = new MatchCocktail(referenceCocktail);
			

			mutationCrossover = new MutationAndIntermediateRecombination(0.25, mutation);
			//manager = new CocktailGenerationManager(firstGenerationSize, truncation, elitism, dbDriverPath, true, fitnessCheck, mutationCrossover, propPath);
			//manager = new CocktailGenerationManager(firstGenerationSize, truncation, elitism, null, true, fitnessCheck, mutationCrossover, propPath);
			
			
			//int generationSize = manager.getPopulationSize();
			
			//for (int i = 0; i < generationSize; i++) {
			//	manager.evaluate();
			//}
			
	}
}
