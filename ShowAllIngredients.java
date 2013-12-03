package genBot2;

import java.net.MalformedURLException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.Queue;

import serialRMI.SerialRMIException;

public class ShowAllIngredients {

	public static void main(String[] args) throws MalformedURLException, RemoteException, NotBoundException, SerialRMIException {
		Ingredient[] allIngs = IngredientArray.getInstance().getAllIngredients();
		
		for (Ingredient ing : allIngs) {
			System.out.println(ing.getName());
		}
		
		System.out.println("--Different--");
		
		CocktailQueue queue = new CocktailQueue();
		RemoteOrderInterface remInt = new RemoteOrderImpl(new QueueManager(queue, "", "", 250));
		
		Ingredient[] allIngs1 = remInt.getIngredients();
		
		for (Ingredient ing : allIngs1) {
			System.out.println(ing.getName());
		}
	}
}
