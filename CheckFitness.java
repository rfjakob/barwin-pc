package genBot2;

public interface CheckFitness {
	/*
	 * Returns the fitness of a cocktail (higher values are better)
	 */
	public double checkFitness(Cocktail cocktail, double cocktailSize, double fitnessInput);
}

