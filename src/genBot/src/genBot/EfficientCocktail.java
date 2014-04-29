package genBot;

public class EfficientCocktail implements CheckFitness {
	
	public EfficientCocktail() {
	}

	@Override
	public void setFitness(Cocktail c) {
		double cocktailCosts = c.getCosts() * GenBotConfig.cocktailSize;
		
		double absoluteEfficiency = c.getRating() - cocktailCosts;
		if (absoluteEfficiency < 0) {
			absoluteEfficiency = 0;
		}
		
		double efficiencyPerML = absoluteEfficiency / GenBotConfig.cocktailSize;
		
		c.setFitness(efficiencyPerML);
	}

}
