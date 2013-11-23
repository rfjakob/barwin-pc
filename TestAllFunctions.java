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
			queueManager = new QueueManager(queue, "", "", 250);
			queueManager.start();
			RemoteOrderInterface remoteOrderImpl = new RemoteOrderImpl(queueManager);
			remoteOrderImpl.generateEvolutionStack("testStack1", erlaubteZutaten1, 15, 3, 2, "datenbank", true, "EfficientCocktail", "MutationAndIntermediateRecombination", 0.001, "eigenschaften");
			remoteOrderImpl.generateEvolutionStack("testStack2", erlaubteZutaten2, 15, 3, 2, "datenbank", true, "EfficientCocktail", "MutationAndIntermediateRecombination", 0.001, "eigenschaften");
			remoteOrderImpl.generateEvolutionStack("testStack3", erlaubteZutaten3, 15, 3, 2, "datenbank", true, "EfficientCocktail", "MutationAndIntermediateRecombination", 0.001, "eigenschaften");
			String[] evolutionStacks = remoteOrderImpl.listEvolutionStacks();
			System.out.println(evolutionStacks.length);
			System.out.println(evolutionStacks[0]);
			
			CocktailWithName[] namedCocktails = remoteOrderImpl.getNamedPopulation(evolutionStacks[0]);
			
			for (int i = 0; i < namedCocktails.length; i++) {
				System.out.println(namedCocktails[i].getCocktail().toString());
			}
			
			System.out.println(namedCocktails[1].getName());
			
			remoteOrderImpl.setCocktailFitness(evolutionStacks[0], namedCocktails[1].getName(), 10);
			
			System.out.println(remoteOrderImpl.canEvolve("testStack1"));
			
			// now test the queue
			queue.addCocktail("testStack1", remoteOrderImpl.getNamedPopulation("testStack1")[1].getName());
			queue.addCocktail("testStack2", remoteOrderImpl.getNamedPopulation("testStack2")[0].getName());
			queue.addCocktail("testStack3", remoteOrderImpl.getNamedPopulation("testStack3")[5].getName());
			Thread.sleep(5000);
			queue.addCocktail("testStack1", remoteOrderImpl.getNamedPopulation("testStack1")[3].getName());
			Thread.sleep(5000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
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
			} catch (NotEnoughRatedCocktailsException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SerialRMIException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			System.out.println("Now finished!");
	}
}