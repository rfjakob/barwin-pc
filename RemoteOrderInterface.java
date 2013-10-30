package genBot2;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.sql.SQLException;

public interface RemoteOrderInterface extends Remote {

	public CocktailWithName[] getNamedPopulation() throws RemoteException;
	
	public void setCocktailFitness(String name, double fitnessInput) throws RemoteException, NotEnoughRatedCocktailsException, SQLException;
	
	public boolean canEvolve() throws RemoteException;
	
	public void evolve() throws RemoteException, SQLException, NotEnoughRatedCocktailsException;
}
