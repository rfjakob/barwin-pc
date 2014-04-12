package genBot2;

import java.net.MalformedURLException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.sql.SQLException;

import serialRMI.SerialRMIException;

public class DeleteFromQueue {

	public static void main(String[] args) throws MalformedURLException, RemoteException, NotBoundException, SerialRMIException, SQLException, MaxAttemptsToMeetPriceConstraintException, FitnessNotSetException {
		Ingredient[] alleZutaten = IngredientArray.getInstance().getAllIngredients();
		Ingredient[] erlaubteZutaten1 = {alleZutaten[5], alleZutaten[6]};

		CocktailQueue queue = new CocktailQueue();

		QueueManager queueManager;

		queueManager = new QueueManager(queue, "", "", 250);
		queueManager.start();

		RemoteOrderImpl remoteOrderImpl = new RemoteOrderImpl(queueManager);
		
		remoteOrderImpl.generateEvolutionStack("Test1", erlaubteZutaten1, 20);
		System.out.println("generated");
		
		remoteOrderImpl.setProps("Test1", 15, 0, 2, 0.05, 5, "blub", "0000011");
		int gen = 0;
					
			CocktailWithName[] cg1 = remoteOrderImpl.getNamedPopulation("Test1");
			for (int i = 0; i < cg1.length; i++) {
				System.out.println(cg1[i].toString());
			}

			for (int num = 0; num < 15; num++) {
				String name = "Test1-" + gen + "-" + num;
			
				Cocktail cocktail = remoteOrderImpl.readGenerationManager("Test1").getCocktailByName(name);
				if (cocktail.getAmount("Orange Juice") != 0) {
					System.out.println(name + ": " + cocktail);
				}
				
				remoteOrderImpl.queueCocktail("Test1", name);
			}
			System.out.println("All queued!");
			
			remoteOrderImpl.deleteCocktailFromQueue("Test1-0-8");
			System.out.println("deleted");
			
			remoteOrderImpl.setCocktailFitness("Test1", "Test1-0-3", 4);
			
<<<<<<< HEAD
=======
			//System.out.println(remoteOrderImpl.getFitnessPlusPrice("Test1", 0, "Test1-0-3"));
>>>>>>> b2e17b97f511c5ad54592609d51d586a401525da
	}

}
