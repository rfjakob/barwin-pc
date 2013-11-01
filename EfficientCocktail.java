package genBot2;

public class EfficientCocktail implements CheckFitness {
	
	public EfficientCocktail() {
	}

	@Override
	public double checkFitness(Cocktail cocktail, double fitnessInput) {
		double efficiency = fitnessInput - cocktail.getCosts();
		
		return efficiency;
	}

}
