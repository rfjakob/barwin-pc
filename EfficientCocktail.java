package genBot2;

import java.util.Random;

public class EfficientCocktail implements CheckFitness {
	
	public EfficientCocktail() {
	}

	@Override
	public double checkFitness(Cocktail cocktail, double fitnessInput) {
		double efficiency = fitnessInput - cocktail.getCosts();
		
		return efficiency;
	}

}
