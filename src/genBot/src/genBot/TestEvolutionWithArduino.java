package genBot;

import java.net.MalformedURLException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.sql.SQLException;
import java.util.Scanner;

import serialRMI.SerialRMIException;

public class TestEvolutionWithArduino {
	
	public static void main(String[] args) throws MalformedURLException, RemoteException, NotBoundException, SerialRMIException, SQLException, NotEnoughRatedCocktailsException, MaxAttemptsToMeetPriceConstraintException {
		
		Ingredient[] alleZutaten = IngredientArray.getInstance().getAllIngredients();
		Ingredient[] testZutaten = {alleZutaten[0], alleZutaten[1]};
		
		CocktailQueue queue = new CocktailQueue();
		
		QueueManager queuemngr = new QueueManager(queue, "", "", 50);
		queuemngr.start();
		
		RemoteOrderImpl rmt = new RemoteOrderImpl(queuemngr);
		
		rmt.generateEvolutionStack("Test Drink", testZutaten, 7);
		
		Double value = 0.0;
		
		while (true) {
			CocktailWithName[] namedCocktails = rmt.getNamedPopulation("Test Drink");
			for (int i = 0; i < 15; i++) {
				value = 0.0;
				
//				System.out.println("Press <Enter> to pour a drink");
//				try {
//				  System.in.read();
//				} catch (IOException e) {
//				  e.printStackTrace();
//				} 
				
				rmt.queueCocktail("Test Drink", namedCocktails[i].getName());
				System.out.println("Just queued " + namedCocktails[i].getName());
				System.out.println(namedCocktails[i].getCocktail().toString());
				
				System.out.println("Please evaluate!");
					value = getNextValidInput();
					
					rmt.setCocktailFitness("Test Drink", namedCocktails[i].getName(), value);
				}
			System.out.println("Generatin " + (rmt.readGenerationManager("Test Drink").getGenerationNumber() - 1) + " completed. Evolving!");
		}		
	}

	private static double getNextValidInput() {
		Scanner sc = new Scanner(System.in);
		
		if (sc.hasNextDouble()) {
			return sc.nextDouble();
		} else if (sc.hasNextInt()) {
			return (double) sc.nextInt();
		} else {
			System.out.println("Sorry, that was not a valid input (a number)! Please try again!");
			return getNextValidInput();
		}
	}
}
