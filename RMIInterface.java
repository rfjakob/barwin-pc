package genBot2;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface RMIInterface extends Remote {

	public Cocktail[] getPopulation() throws RemoteException;
}
