package genBot2;

public class CocktailWithName {
	
	private final String evolutionStackName;
	private final int generationNumber;
	private final int cocktailNumber;
	
	private final Cocktail cocktail;

	public CocktailWithName(String evolutionStackName, int generationNumber, int cocktailNumber, Cocktail cocktail) {
		this.evolutionStackName = evolutionStackName;
		this.generationNumber = generationNumber;
		this.cocktailNumber = cocktailNumber;
		
		this.cocktail = cocktail;
	}

	public String getEvolutionStackName() {
		return evolutionStackName;
	}

	public int getGenerationNumber() {
		return generationNumber;
	}

	public int getCocktailNumber() {
		return cocktailNumber;
	}
	
	public String getName() {
		return evolutionStackName + "-" + generationNumber + "-" + cocktailNumber;
	}
	
	public Cocktail getCocktail() {
		return cocktail;
	}
}
