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
			System.out.println(manager.evolve(0.2, 2).toString());
		} catch (FitnessNotSetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
