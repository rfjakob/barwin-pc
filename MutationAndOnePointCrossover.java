package genBot2;

public class MutationAndOnePointCrossover implements Recombination {

	private OnePointCrossover onePointCrossover;
	private StandardMutation mutation;
	
	public MutationAndOnePointCrossover(double stdDeviation) {
		this.onePointCrossover = new OnePointCrossover();
		this.mutation = new StandardMutation(stdDeviation);
	}

	@Override
	public CocktailGeneration recombine(CocktailGeneration population,
			int newPopulationSize) throws FitnessNotSetException {

		CocktailGeneration nextGeneration = onePointCrossover.recombine(population, newPopulationSize);
		nextGeneration = mutation.recombine(nextGeneration, newPopulationSize);
		
		return nextGeneration;
	}

}
