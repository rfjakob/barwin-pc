package genBot2;

public class genBot2 {

	public static void main(String[] args) {
		CheckFitness fitnessCheck = new MatchCocktail(new Cocktail(new double[] {3.0/10.0, 6.0/10.0, 1.0/10.0}));

		CocktailGenerationManager manager = new CocktailGenerationManager(0, 10, fitnessCheck);
		
		int generationSize = manager.getPopulationSize();
		
		for (int i = 0; i < generationSize; i++) {
			manager.evaluate();
		}
		
		System.out.println(manager.randomToString());
		
		try {
			while (manager.getCocktailGeneration().getBestFitness() < -0.00005) {
				manager.evolve(0.002, 2);
				for (int i = 0; i < generationSize; i++) {
					manager.evaluate();
				}
				
				System.out.println(manager.bestFitnessToString());
			}
			
			System.out.println("The best Cocktail is: " + manager.getCocktailGeneration().getBestCocktail().toString());
		} catch (FitnessNotSetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
