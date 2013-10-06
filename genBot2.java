package genBot2;

import java.io.IOException;
import java.util.Scanner;

public class genBot2 {

	public static void main(String[] args) {
		Scanner input = new Scanner(System.in);
		
		System.out.println("Starting the genetic bot.\n\n(Keep in mind decimal points are localized (auf deutsch: Beistrich))\n");
		
		Ingredient[] ingredients = IngredientArray.getInstance().getAllIngredients();
		double[] referenceAmounts = new double[ingredients.length];
		
		for (int i = 0; i < ingredients.length; i++) {
			System.out.println("Enter " + ingredients[i].getName() + " amount:");
			
			referenceAmounts[i] = input.nextDouble();
		}
		
		// CheckFitness fitnessCheck = new MatchCocktail(new Cocktail(new double[] {3.0/10.0, 6.0/10.0, 1.0/10.0}));
		Cocktail referenceCocktail = new Cocktail(referenceAmounts);
		CheckFitness fitnessCheck = new MatchCocktail(referenceCocktail);
		
		System.out.println("The reference cocktail is " + referenceCocktail.toString());

		System.out.println("Enter target accuracy (the maximum squared distance to be accepted as a solution):");
		double target = input.nextDouble();
		
		System.out.println("Enter generation size:");
		int firstGenerationSize = input.nextInt();
	
		System.out.println("Enter truncation amount (how many of the worst Cocktails should be truncated in crossover:");
		int truncation = input.nextInt();

		System.out.println("Enter elitism amount (how many of the best Cocktails should be in the next generation:");
		int elitism = input.nextInt();
		
		System.out.println("Enter mutation standard deviation:");
		double mutation = input.nextDouble();

		// Set a recombination
		Recombination mutationCrossover = new MutationAndOnePointCrossover(mutation);

		CocktailGenerationManager manager = new CocktailGenerationManager(0, firstGenerationSize, fitnessCheck, mutationCrossover);
		
		int generationSize = manager.getPopulationSize();
		
		for (int i = 0; i < generationSize; i++) {
			manager.evaluate();
		}
		
		System.out.println("First Generation:");
		System.out.println(manager.randomToString());
		System.out.println("Press <Enter> to start the genetic algorithm");
		try {
			  System.in.read();
			} catch (IOException e) {
			  e.printStackTrace();
			}
		
		try {
			while (manager.getCocktailGeneration().getBestFitness() < -1 * Math.abs(target)) {
				manager.evolve(truncation, elitism);
				for (int i = 0; i < generationSize; i++) {
					manager.evaluate();
				}
				
				System.out.println(manager.bestFitnessToString());
			}
			
			System.out.println("The best Cocktail is: " + manager.getCocktailGeneration().getBestCocktail().toString());
			System.out.println("The ref. Cocktail is: " + referenceCocktail.toString());
		} catch (FitnessNotSetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
