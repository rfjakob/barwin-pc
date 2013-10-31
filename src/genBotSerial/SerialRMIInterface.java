package genBotSerial;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface SerialRMIInterface extends Remote {

	public String write() throws RemoteException;
	public String read() throws RemoteException;

}
