package genBot2;

public abstract class RouletteWheelSelection {

	public RouletteWheelSelection() {
		// TODO Auto-generated constructor stub
	}

	/*
	 * Transforms the array to an "additive array" - shares like
	 * {0.4, 0.5, 0.1} are transformed to {0.4, 0.9, 0.1}
	 * @return an additive array
	 */
	public double[] generateRouletteWheel(Cocktail[] cocktails) throws FitnessNotSetException {
		double[] rouletteWheelShare = rouletteWheelShares(cocktails);
		
		double[] rouletteWheel = new double[rouletteWheelShare.length];
		
		rouletteWheel[0] = rouletteWheelShare[0];
		for (int i = 1; i < rouletteWheel.length; i++) {
			rouletteWheel[i] = rouletteWheel[i - 1] + rouletteWheelShare[i];
		}
		
		return rouletteWheel;
	}
	
	/*
	 * Returns all fitnesses as shares of the sum of all fitnesses
	 * @return an array of the shares
	 */
	public double[] rouletteWheelShares(Cocktail[] cocktails) throws FitnessNotSetException {		
		double[] rouletteWheelShare = new double[cocktails.length];
		for (int i = 0; i < cocktails.length; i++) {
			rouletteWheelShare[i] = cocktails[i].getFitness();
		}
		for (int i = 0; i < cocktails.length; i++) {
			rouletteWheelShare[i] = cocktails[i].getFitness() / getFitnessSum(cocktails);
		}
		
		return rouletteWheelShare;
	}
	
	public double getFitnessSum(Cocktail[] cocktails) throws FitnessNotSetException {
		double fitnessSum = 0;
		for (int i = 0; i < cocktails.length; i++) {
			if (cocktails[i].isFitnessSet()) {
				fitnessSum = fitnessSum + cocktails[i].getFitness();
			}
			else {
				throw new FitnessNotSetException("Fitness of Cocktail " + i + "is not set!");
			}
		}
		return fitnessSum;
	}

}
