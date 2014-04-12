package genBot2;

import java.net.MalformedURLException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.RemoteServer;
import java.rmi.server.UnicastRemoteObject;

import serialRMI.SerialRMIException;

public class RMIServerTest {

	public static void main(String[] args) {
		try {
			LocateRegistry.createRegistry( Registry.REGISTRY_PORT );
			
			RemoteOrderImpl rem = new RemoteOrderImpl(new QueueManager(new CocktailQueue(), "", "", 250));
			
			RemoteOrderInterface remInt = (RemoteOrderInterface) UnicastRemoteObject.exportObject( rem, 0 );
			
			RemoteServer.setLog(System.out);
			
			Registry registry = LocateRegistry.getRegistry();
			registry.rebind("rem", remInt);
			
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NotBoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SerialRMIException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
