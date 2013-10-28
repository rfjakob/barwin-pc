package genBot2;

import java.rmi.RemoteException;

public class RMIImpl implements RMIInterface {
	
	private CocktailGeneration cocktailGeneration;

	public RMIImpl(CocktailGeneration cocktailGeneration) {
		this.cocktailGeneration = cocktailGeneration;
	}

	@Override
	public Cocktail[] getPopulation() throws RemoteException {
		return cocktailGeneration.getPopulation();
	}

}
