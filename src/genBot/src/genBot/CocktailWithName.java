package genBot;

import java.io.Serializable;

public class CocktailWithName implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
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
	
	public String toString() {
		String returnString = 
			getName() + ": " + 
			getCocktail().toString() + ", " + 
			((getCocktail().isEliteCocktail()) ? "Elite" : "Not-Elite") + ", " + 
			((getCocktail().isPoured()) ? "poured" : "not poured") + ", "+
			((getCocktail().isFitnessSet()) ? "rated" : "not rated")
			;
		return returnString;
	}
}
