package controllers;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import genBot2.*;
import serialRMI.*;
import play.libs.Json;
//import play.*;
//import play.cache.Cache;
import play.mvc.*;
import views.html.*;

import java.rmi.*;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import com.fasterxml.jackson.databind.node.ObjectNode;

public class Application extends GenBotController {

	private static RemoteOrderInterface genBotRMIConnect() throws Exception {
		return (RemoteOrderInterface) Naming.lookup("//127.0.0.1:12122/rmiImpl");
	}
	
	public static Result index() {
		Ingredient[] alleZutaten = IngredientArray.getInstance()
				.getAllIngredients();
		try {
			RemoteOrderInterface genBotRMI = genBotRMIConnect();

			for (String name: genBotRMI.listEvolutionStacks())
				System.out.println(name);

			return ok(index.render(genBotRMI.listEvolutionStacks(), genBotRMI,
					alleZutaten));
		} catch (Exception e) {
			return error(e);
		}
	}

	public static Result generate() {
		Map<String, String[]> parameters = request().body().asFormUrlEncoded();

		CheckFitness wasZahlst = new EfficientCocktail();
		Recombination fortpflanzung = new MutationAndIntermediateRecombination(
				0.25, 0.005);
		Ingredient[] alleZutaten = IngredientArray.getInstance()
				.getAllIngredients();
		Ingredient[] erlaubteZutaten = { alleZutaten[2], alleZutaten[3],
				alleZutaten[4] };

		try {
			RemoteOrderInterface genBotRMI = genBotRMIConnect();
			String str = ""; // genBotRMI.toString();
			System.out.println(parameters.get("name")[0]);
			//String name = "adsf adf";
			String name = parameters.get("name")[0];
			genBotRMI.generateEvolutionStack(name, erlaubteZutaten, 10, 3, 2,
					"datenbank", true, null, null, 0.2, "eigenschaften");
		           // generateEvolutionStack(String evolutionStackName, Ingredient[] allowedIngredients, int populationSize, int truncation, int elitism, String dbDriverPath, boolean dbReset, String fitnessCheckName, String recombinationName, double stdDeviation, String propPath)  throws RemoteException, SQLException;
			
			String[] list = genBotRMI.listEvolutionStacks();
			for (int i = 0; i < list.length; i++)
				str += list[i];
			// return ok(list.toString());
			return ok(str);
		} catch (Exception e) {
			return error(e);
		}

	}


	public static Result generation(Long id) {
		// return ok(index.render("Your new application is ready."));
		// return ok("talking about my generation");
		return ok("blup");
	}

	public static Result setFitness() {
		Map<String, String[]> parameters = request().body().asFormUrlEncoded();

		try {
			RemoteOrderInterface genBotRMI = genBotRMIConnect();

			//genBotRMI.setCocktailFitness();

		} catch (Exception e) {
			return error(e);
		}
		return ok(parameters.get("fitness")[0]);
	}
}
