package genBot2;

import java.net.MalformedURLException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.sql.SQLException;
import serialRMI.SerialRMIException;

public class TestAllFunctions {

	public static void main(String[] args) {
		System.out.println("ok???????");
		
		Ingredient[] alleZutaten = IngredientArray.getInstance().getAllIngredients();
		Ingredient[] erlaubteZutaten1 = {alleZutaten[2], alleZutaten[3], alleZutaten[4]};
		Ingredient[] erlaubteZutaten2 = {alleZutaten[0], alleZutaten[3], alleZutaten[4], alleZutaten[5], alleZutaten[6]};
		Ingredient[] erlaubteZutaten3 = alleZutaten;

		CocktailQueue queue = new CocktailQueue();

		QueueManager queueManager;
		
		System.out.println("ok??");

		try {
			System.out.println("Ich fang jetz an");
			
			queueManager = new QueueManager(queue, "", "", 250);
			queueManager.start();
			RemoteOrderImpl remoteOrderImpl = new RemoteOrderImpl(queueManager);
			remoteOrderImpl.generateEvolutionStack("testStack1", erlaubteZutaten1, 15, 3, 2, "datenbank", false, "EfficientCocktail", "MutationAndIntermediateRecombination", 0.001, "eigenschaften1");
			remoteOrderImpl.generateEvolutionStack("testStack2", erlaubteZutaten2, 15, 3, 2, "datenbank", true, "EfficientCocktail", "MutationAndIntermediateRecombination", 0.001, "eigenschaften2");
			remoteOrderImpl.generateEvolutionStack("testStack3", erlaubteZutaten3, 15, 3, 2, "datenbank", false, "EfficientCocktail", "MutationAndIntermediateRecombination", 0.001, "eigenschaften3");
			
			remoteOrderImpl.listPossibleEvolutionStacks();

			CocktailWithName[] testStack1 = remoteOrderImpl.getNamedPopulation("testStack1");
			CocktailWithName[] testStack2 = remoteOrderImpl.getNamedPopulation("testStack2");
			CocktailWithName[] testStack3 = remoteOrderImpl.getNamedPopulation("testStack3");

			for (int i = 0; i < 4; i++) {
				for (int j = 0; j < 15; j++) {
//					System.out.println(j);
					queue.addCocktail("testStack1", remoteOrderImpl.getNamedPopulation("testStack1")[j].getName());
//					System.out.println("Generation " + i);
//				}

//				for (int j = (i + 1) * 15; j < j + 15; j++) {
					CocktailWithName actQueue = remoteOrderImpl.getNamedPopulation("testStack1")[j];
					actQueue.getCocktail().setPoured(true);
					remoteOrderImpl.setCocktailFitness("testStack1", actQueue.getName(), 8);
//					System.out.println(actQueue.toString());
				}
			}
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NotBoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SerialRMIException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NotEnoughRatedCocktailsException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("Now finished!");
	}
}