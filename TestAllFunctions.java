package genBot2;

import java.net.MalformedURLException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.sql.SQLException;
import serialRMI.SerialRMIException;

public class TestAllFunctions {

	public static void main(String[] args) {
		
		Ingredient[] alleZutaten = IngredientArray.getInstance().getAllIngredients();
		Ingredient[] erlaubteZutaten1 = {alleZutaten[2], alleZutaten[3], alleZutaten[4]};
		Ingredient[] erlaubteZutaten2 = {alleZutaten[0], alleZutaten[3], alleZutaten[4], alleZutaten[5], alleZutaten[6]};
		Ingredient[] erlaubteZutaten3 = alleZutaten;

		CocktailQueue queue = new CocktailQueue();

		QueueManager queueManager;
		

		try {
			System.out.println("Ich fang jetz an!");
			
			queueManager = new QueueManager(queue, "", "", 250);
			queueManager.start();
			
			RemoteOrderImpl remoteOrderImpl = new RemoteOrderImpl(queueManager);
			
			remoteOrderImpl.generateEvolutionStack("SuperDrink", erlaubteZutaten1);
			
			remoteOrderImpl.removeEvolutionStack("SuperDrink");
			
			remoteOrderImpl.loadEvolutionStack("SuperDrink");
						
			String[] list = remoteOrderImpl.listPossibleEvolutionStacks();
			
			for (int i = 0; i < list.length; i++) {
				System.out.println(list[i]);
			}

			CocktailWithName[] superdrink = remoteOrderImpl.getNamedPopulation("SuperDrink");

			for (int i = 0; i < 4; i++) {
//				for (int j = 0; j < 15; j++) {
//					System.out.println(j);
					queue.addCocktail("SuperDrink", remoteOrderImpl.getNamedPopulation("SuperDrink")[j].getName());
//					System.out.println("Generation " + i);
//				}

				for (int j = (i + 1) * 15; j < j + 15; j++) {
					CocktailWithName actQueue = remoteOrderImpl.getNamedPopulation("SuperDrink")[j];
					actQueue.getCocktail().setPoured(true);
					remoteOrderImpl.setCocktailFitness("SuperDrink", actQueue.getName(), 8);
//					System.out.println(actQueue.toString());
				}
//			}
			
			remoteOrderImpl.deleteEvolutionStack("SuperDrink");
			
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