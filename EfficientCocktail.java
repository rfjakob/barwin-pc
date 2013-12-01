package genBot2;

public class EfficientCocktail implements CheckFitness {
	
	public EfficientCocktail() {
	}

	@Override
	public double checkFitness(Cocktail cocktail, double cocktailSize, double fitnessInput) {
		double cocktailCosts = cocktail.getCosts() * cocktailSize;
		
		double absoluteEfficiency = fitnessInput - cocktailCosts;
		
		double efficiencyPerML = absoluteEfficiency / cocktailSize;
		
		return efficiencyPerML;
	}

}
