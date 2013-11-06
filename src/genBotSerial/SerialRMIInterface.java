package genBotSerial;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface SerialRMIInterface extends Remote {
	public void write(String str) throws RemoteException, Exception;
	public String read() throws RemoteException, Exception;
	public boolean isConnected() throws RemoteException;
	public String[] getSerialPorts() throws RemoteException;
	public void connect(String portName) throws RemoteException, Exception;
}
