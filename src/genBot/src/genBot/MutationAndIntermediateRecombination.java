package genBot;

public class MutationAndIntermediateRecombination implements Recombination {

	private IntermediateRecombination intermediateRecombination;
	private StandardMutation mutation;
	private double maxPricePerLiter;
	
	public MutationAndIntermediateRecombination(double variableArea, double stdDeviation, double maxPricePerLiter) {
		this.maxPricePerLiter = maxPricePerLiter;
		this.intermediateRecombination = new IntermediateRecombination(variableArea, maxPricePerLiter);
		this.mutation = new StandardMutation(stdDeviation, maxPricePerLiter);
	}

	@Override
	public CocktailGeneration recombine(CocktailGeneration population,
			int newPopulationSize, boolean[] booleanAllowedIngredients) throws FitnessNotSetException, GeneratingRandomCocktailsException {
		
		CocktailGeneration nextGeneration = intermediateRecombination.recombine(population, newPopulationSize, booleanAllowedIngredients);		
		nextGeneration = mutation.recombine(nextGeneration, newPopulationSize, booleanAllowedIngredients);
		
		return nextGeneration;
	}

	@Override
	public double getMutationStdDeviation() {
		return mutation.getStdDeviation();
	}

	@Override
	public void setMutationStdDeviation(double stdDeviation) {
		mutation.setMutationStdDeviation(stdDeviation);
	}

	@Override
	public double getMaxPricePerLiter() {
		return maxPricePerLiter;
	}

	@Override
	public void setMaxPricePerLiter(double maxPricePerLiter) {
		this.maxPricePerLiter = maxPricePerLiter;
		intermediateRecombination.setMaxPricePerLiter(maxPricePerLiter);
		mutation.setMaxPricePerLiter(maxPricePerLiter);
	}

}
