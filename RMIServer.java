package genBot2;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.RemoteServer;
import java.rmi.server.UnicastRemoteObject;
import java.util.Random;

public class RMIServer {

	public static void main(String[] args) {

		System.out.println("Starting the genetic bot.\n\n(Keep in mind decimal points are localized (auf deutsch: Beistrich))\n");

		Ingredient[] ingredients = IngredientArray.getInstance().getAllIngredients();
		double[] referenceAmounts = new double[ingredients.length];

		Random rnd = new Random();

		for (int i = 0; i < ingredients.length; i++) {
			referenceAmounts[i] = rnd.nextDouble();
		}

		try {
			LocateRegistry.createRegistry( Registry.REGISTRY_PORT );

			RemoteOrderImpl rmiImpl = new RemoteOrderImpl();
			RemoteOrderInterface stub = (RemoteOrderInterface) UnicastRemoteObject.exportObject(rmiImpl, 0);
			RemoteServer.setLog(System.out);

			Registry registry = LocateRegistry.getRegistry();
			registry.rebind( "rmiImpl", stub );

		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
