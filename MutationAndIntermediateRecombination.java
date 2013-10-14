package genBot2;

public class MutationAndIntermediateRecombination implements Recombination {

	private IntermediateRecombination intermediateRecombination;
	private SureMutation mutation;
	
	public MutationAndIntermediateRecombination(double variableArea, double stdDeviation) {
		this.intermediateRecombination = new IntermediateRecombination(variableArea);
		this.mutation = new SureMutation(stdDeviation);
	}

	@Override
	public CocktailGeneration recombine(CocktailGeneration population,
			int newPopulationSize) throws FitnessNotSetException {

		CocktailGeneration nextGeneration = intermediateRecombination.recombine(population, newPopulationSize);
		nextGeneration = mutation.recombine(nextGeneration, newPopulationSize);
		
		return nextGeneration;
	}

}
