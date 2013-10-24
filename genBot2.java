package genBot2;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Random;
import java.util.Scanner;

public class genBot2 {

	public static void main(String[] args) {
		Scanner input = new Scanner(System.in);
		
		System.out.println("Starting the genetic bot.\n\n(Keep in mind decimal points are localized (auf deutsch: Beistrich))\n");
		
		Ingredient[] ingredients = IngredientArray.getInstance().getAllIngredients();
		double[] referenceAmounts = new double[ingredients.length];
		
		Random rnd = new Random();
		
		for (int i = 0; i < ingredients.length; i++) {
			referenceAmounts[i] = rnd.nextDouble();
		}
		
		try {
			// Set a db-file
			DataBaseDriver dbDriver = new DataBaseDriver("superdatabase", true);
		
			// CheckFitness fitnessCheck = new MatchCocktail(new Cocktail(new double[] {3.0/10.0, 6.0/10.0, 1.0/10.0}));
			Cocktail referenceCocktail = new Cocktail(referenceAmounts);
			CheckFitness fitnessCheck = new MatchCocktail(referenceCocktail);
		
			System.out.println("The reference cocktail is " + referenceCocktail.toString());

			System.out.println("Enter target accuracy (the maximum squared distance to be accepted as a solution) - something lower than 0.01 suggested, you can also try very very low values:");
//			double target = input.nextDouble();
//			double target = -20;
		
			System.out.println("Enter generation size - shouldn't be too low. Bigger generations make the algorithm slower (especially in reality, when the cocktails need to be evaluated by humans), too small generations reduce possibilities of evolution:");
//			int firstGenerationSize = input.nextInt();
			int firstGenerationSize = 10;
	
			System.out.println("Enter truncation amount (how many of the worst Cocktails should be truncated in crossover - 2 to 4 suggested, try also something different:");
//			int truncation = input.nextInt();
			int truncation = 3;

			System.out.println("Enter elitism amount (how many of the best Cocktails should be in the next generation -  I usually take 2, 1 is probably also fine:");
//			int elitism = input.nextInt();
			int elitism = 2;
		
			System.out.println("Enter mutation standard deviation - 0.05 maybe?:");
//			double mutation = input.nextDouble();
			double mutation = 0.05;
		
			// Set a recombination
			Recombination mutationCrossover = new MutationAndIntermediateRecombination(0.25, mutation);
		
			CocktailGenerationManager manager = new CocktailGenerationManager(firstGenerationSize, fitnessCheck, mutationCrossover, dbDriver);
		
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
//				while (manager.getCocktailGeneration().getMeanFitness() < -1 * Math.abs(target)) {
				while (true) {
					manager.evolve(truncation, elitism);
					for (int i = 0; i < generationSize; i++) {
						manager.evaluate();
					}
					
					System.out.println("Mean: " + manager.meanFitnessToString() + ", ");
					System.out.print("Best: " + manager.bestFitnessToString());
				}
				
//				System.out.println("The mean Cocktail is: " + manager.meanFitnessToString());
//				System.out.println("The best Cocktail is: " + manager.getCocktailGeneration().getBestCocktail().toString());
//				System.out.println("The ref. Cocktail is: " + referenceCocktail.toString());
			} catch (FitnessNotSetException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
}
