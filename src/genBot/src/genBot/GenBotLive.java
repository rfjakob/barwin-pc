package genBot;

import java.net.MalformedURLException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.sql.SQLException;
import java.util.Scanner;

import serialRMI.SerialRMIException;

public class GenBotLive {
	
	public static void main(String[] args) {
		CocktailQueue queue = new CocktailQueue();
		
		QueueManager queuemngr;
		try {
			queuemngr = new QueueManager(queue, "rmi://127.0.0.1:12121/serial", "/dev/ttyACM1", 50);
			
			queuemngr.start();
			
			RemoteOrderImpl rmt = new RemoteOrderImpl(queuemngr);
			
			Ingredient[] ings = rmt.getIngredients();
//			for (int i = 0; i < ings.length; i++) {
//				System.out.println(i + ": " + ings[i].getName());
//			}
			
			Ingredient[] vodkaOrange = {ings[0], ings[4]};
			Ingredient[] tschunk = {ings[3], ings[1], ings[5]};
			Ingredient[] spezi = {ings[4], ings[3], ings[5]};
			
			rmt.generateEvolutionStack("Vodka Orange", vodkaOrange, 15, 3, 2, "datenbank", true, "EfficientCocktail", "normal", 0.05, 6.0, "vodkaOrange");
			rmt.generateEvolutionStack("Tschunk", tschunk, 15, 3, 2, "datenbank", true, "EfficientCocktail", "normal", 0.05, 8.0, "tschunk");
			rmt.generateEvolutionStack("Spezi", spezi, 15, 3, 2, "datenbank", true, "EfficientCocktail", "normal", 0.05, 6.0, "spezi");
			
			String[] loaded = rmt.listLoadedEvolutionStacks();

			int voGen = 0;
			int tschunkGen = 0;
			int speziGen = 0;

			int voCur = 0;
			int tschunkCur = 0;
			int speziCur = 0;
			while (true) {
				
				System.out.println("What do you want to drink?");
				System.out.println("Choose:");
				for (int i = 0; i< loaded.length; i++) {
					System.out.println((i + 1) + ": " + loaded[i]);
				}
				int input = getNextValidInt();
				
				if (input == 1) {
					String name = "Vodka Orange-" + voGen + "-" + voCur;
					rmt.queueCocktail("Vodka Orange", name);
					
					System.out.println("Please try your cocktail!");
					System.out.println("How much would you pay for this cocktail?");
					double rating = getNextValidDouble();
					
					rmt.setCocktailFitness("Vodka Orange", name, rating);

					System.out.println("Thank you!");
					
					voCur++;
					if (voCur >= 15) {
						voCur = 0;
						voGen++;
					}
				} else if (input == 2) {
					String name = "Tschunk-" + tschunkGen + "-" + tschunkCur;
					rmt.queueCocktail("Tschunk", name);

					System.out.println("Please try your cocktail!");
					System.out.println("How much would you pay for this cocktail?");
					double rating = getNextValidDouble();

					rmt.setCocktailFitness("Tschunk", name, rating);
					
					System.out.println("Thank you!");
					
					tschunkCur++;
					if (tschunkCur >= 15) {
						tschunkCur = 0;
						tschunkGen++;
					}
				} else if (input == 3) {
					String name = "Spezi-" + speziGen + "-" + speziCur;
					rmt.queueCocktail("Spezi", name);

					System.out.println("Please try your cocktail!");
					System.out.println("How much would you pay for this cocktail?");
					double rating = getNextValidDouble();
					
					rmt.setCocktailFitness("Spezi", name, rating);

					System.out.println("Thank you!");
					
					speziCur++;
					if (speziCur >= 15) {
						speziCur = 0;
						speziGen++;
					}					
				} else {
					System.out.println("Sorry, I didn't understand. Please try again!");
				}
			}
		} catch (MalformedURLException | RemoteException | NotBoundException
				| SerialRMIException e) {
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
	
	private static int getNextValidInt() {
		Scanner sc = new Scanner(System.in);
		
		if (sc.hasNextInt()) {
			return sc.nextInt();
		} else if (sc.hasNextDouble()) {
			return (int) sc.nextDouble();
		} else {
			System.out.println("Sorry, that was not a valid input (a number)! Please try again!");
			return getNextValidInt();
		}
	}

	private static double getNextValidDouble() {
		Scanner sc = new Scanner(System.in);
		
		if (sc.hasNextDouble()) {
			return sc.nextDouble();
		} else if (sc.hasNextDouble()) {
			return (double) sc.nextInt();
		} else {
			System.out.println("Sorry, that was not a valid input (a number)! Please try again!");
			return getNextValidInt();
		}
	}


}
