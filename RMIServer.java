package genBot2;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class RMIServer {
	public static void main(String[] args) {		
		QueueManager queueManager;
		CocktailQueue queue = new CocktailQueue();
		try {
			//queueManager = new QueueManager(queue, "rmi://127.0.0.1:12121/serial", "/dev/ttyACM0", 250);
			queueManager = new QueueManager(queue, "rmi://10.20.30.190:12121/serial", "/dev/ttyUSB0", 250);
			
			queueManager.start();
			
			//System.setProperty("java.rmi.server.hostname", "10.20.30.160");
			LocateRegistry.createRegistry( Registry.REGISTRY_PORT );
			RemoteOrderImpl rmiImpl = new RemoteOrderImpl(queueManager);
			RemoteOrderInterface stub = (RemoteOrderInterface) UnicastRemoteObject.exportObject(rmiImpl, 0);
			Registry registry = LocateRegistry.getRegistry();
			//RemoteServer.setLog(System.out);
			registry.rebind( "genBot", stub );
	
		} catch (Exception e2) {
			e2.printStackTrace();
		}
	
	}
}
