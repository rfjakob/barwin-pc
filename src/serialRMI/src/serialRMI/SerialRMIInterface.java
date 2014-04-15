package serialRMI;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface SerialRMIInterface extends Remote {
	public void write(String str) throws RemoteException, SerialRMIException;
	public void writeLine(String str) throws RemoteException, SerialRMIException;
	public String read() throws RemoteException, SerialRMIException;
	public String[] readLines() throws RemoteException, SerialRMIException;
	public boolean isConnected() throws RemoteException;
	public String[] getSerialPorts() throws RemoteException;
	public void connect(String portName) throws RemoteException, SerialRMIException;
}
