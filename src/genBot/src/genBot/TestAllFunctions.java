package genBot;

import java.net.MalformedURLException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.sql.SQLException;
import serialRMI.SerialRMIException;

public class TestAllFunctions {

	public static void main(String[] args) {
		
		Ingredient[] alleZutaten = IngredientArray.getInstance().getAllIngredients();
		Ingredient[] erlaubteZutaten1 = {alleZutaten[2], alleZutaten[3], alleZutaten[4]};
		CocktailQueue queue = new CocktailQueue();

		QueueManager queueManager;
		

			
		try {
			queueManager = new QueueManager(queue, "", "", 250);
			queueManager.start();
			
			RemoteOrderImpl remoteOrderImpl = new RemoteOrderImpl(queueManager);
			
			remoteOrderImpl.generateEvolutionStack("Super Super Drink", erlaubteZutaten1, 11);
			
			remoteOrderImpl.setCocktailFitness("Super Super Drink", "Super Super Drink-0-0", 20.0);
			
			
			System.out.println("Now removing and then loading again.");
			
			remoteOrderImpl.removeEvolutionStack("Super Super Drink");
			
			remoteOrderImpl.loadEvolutionStack("Super Super Drink");

			remoteOrderImpl.setCocktailFitness("Super Super Drink", "Super Super Drink-0-0", 4);
			System.out.println("can evolve: " + remoteOrderImpl.canEvolve("Super Super Drink"));

			remoteOrderImpl.setCocktailFitness("Super Super Drink", "Super Super Drink-0-1", 4);
			System.out.println("can evolve: " + remoteOrderImpl.canEvolve("Super Super Drink"));
			
			remoteOrderImpl.setCocktailFitness("Super Super Drink", "Super Super Drink-0-2", 4);
			System.out.println("can evolve: " + remoteOrderImpl.canEvolve("Super Super Drink"));
			
			remoteOrderImpl.setCocktailFitness("Super Super Drink", "Super Super Drink-0-3", 4);
			System.out.println("can evolve: " + remoteOrderImpl.canEvolve("Super Super Drink"));

			remoteOrderImpl.setCocktailFitness("Super Super Drink", "Super Super Drink-0-4", 4);
			System.out.println("can evolve: " + remoteOrderImpl.canEvolve("Super Super Drink"));

			remoteOrderImpl.setCocktailFitness("Super Super Drink", "Super Super Drink-0-5", 4);
			System.out.println("can evolve: " + remoteOrderImpl.canEvolve("Super Super Drink"));

			remoteOrderImpl.setCocktailFitness("Super Super Drink", "Super Super Drink-0-6", 4);
			System.out.println("can evolve: " + remoteOrderImpl.canEvolve("Super Super Drink"));

			remoteOrderImpl.setCocktailFitness("Super Super Drink", "Super Super Drink-0-0", 4);
			System.out.println("can evolve: " + remoteOrderImpl.canEvolve("Super Super Drink"));

			remoteOrderImpl.setCocktailFitness("Super Super Drink", "Super Super Drink-0-0", 4);
			System.out.println("can evolve: " + remoteOrderImpl.canEvolve("Super Super Drink"));



//			System.out.println("Fitness: " + remoteOrderImpl.getFitnessPlusPrice("Super Super Drink", 0, "Super Super Drink-0-0"));
			
//			remoteOrderImpl.deleteEvolutionStack("Super Super Drink");
						
//			String[] list = remoteOrderImpl.listPossibleEvolutionStacks();
//			for (int i = 0; i < list.length; i++) {
//				System.out.println(list[i]);
//			}
			
		} catch (MalformedURLException | RemoteException
				| NotBoundException | SerialRMIException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MaxAttemptsToMeetPriceConstraintException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}