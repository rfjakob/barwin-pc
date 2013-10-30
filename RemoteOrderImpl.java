package genBot2;

import java.rmi.RemoteException;
import java.sql.SQLException;

public class RemoteOrderImpl implements RemoteOrderInterface {
	
	private EvolutionAlgorithmManager evolutionAlgorithmManager;

	public RemoteOrderImpl(EvolutionAlgorithmManager evolutionAlgorithmManager) {
		this.evolutionAlgorithmManager = evolutionAlgorithmManager;
	}

	@Override
	public CocktailWithName[] getNamedPopulation() throws RemoteException {
		return evolutionAlgorithmManager.getGenManager().getNamedCocktailGeneration();
	}

	@Override
	public void setCocktailFitness(String name, double fitnessInput) throws RemoteException, SQLException, NotEnoughRatedCocktailsException {
		evolutionAlgorithmManager.setFitness(name, fitnessInput);
		
		if (evolutionAlgorithmManager.getGenManager().getUnRatedNamedCocktailGeneration().length == 0) {
			evolutionAlgorithmManager.evolve();
		}
	}

	@Override
	public boolean canEvolve() throws RemoteException {
		return evolutionAlgorithmManager.canEvolve();
	}

	@Override
	public void evolve() throws RemoteException, SQLException, NotEnoughRatedCocktailsException {
		evolutionAlgorithmManager.evolve();
	}
}
