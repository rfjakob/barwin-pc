package controllers;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import genBot2.*;
import genBotSerial.*;
import play.libs.Json;
//import play.*;
//import play.cache.Cache;
import play.mvc.*;
import views.html.*;

import java.rmi.*;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.sun.jmx.snmp.Timestamp;

public class Application extends Controller {
	private static SerialRMIInterface genBotSerialRMIConnect() throws Exception {
		return (SerialRMIInterface) Naming.lookup("//127.0.0.1/genBotSerial");
	}

	private static RemoteOrderInterface genBotRMIConnect() throws Exception {
		return (RemoteOrderInterface) Naming.lookup("//127.0.0.1/rmiImpl");
	}

	private static Result error(Exception e) {
		return ok(error.render(e));
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
			System.out.println("EXCEPION " + e.getMessage());
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
					"datenbank", true, null, null, "eigenschaften");

			String[] list = genBotRMI.listEvolutionStacks();
			for (int i = 0; i < list.length; i++)
				str += list[i];
			// return ok(list.toString());
			return ok(str);
		} catch (Exception e) {
			System.out.println("EXCEPION " + e.getMessage());
			return error(e);
		}

	}

	public static Result serial() {
		return ok(serial.render());
	}

	public static Result serialWrite() {
		Map<String, String[]> parameters = request().body().asFormUrlEncoded();
		try {
			SerialRMIInterface serialRMI = genBotSerialRMIConnect();
			ObjectNode result = Json.newObject();
			String text = parameters.get("text")[0] + "\r\n";
			serialRMI.write(text);
			result.put("valid", true);
			//Timestamp timestamp = new Timestamp(new Date().getTime());
			SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
			result.put("timestamp", sdf.format(new Date().getTime()));
			result.put("string", text);
			return ok(result);
		} catch (Exception e) {
			System.out.println("EXCEPION " + e.getMessage());
			return error(e);
		}
	}

	public static Result serialRead() {
		try {
			SerialRMIInterface serialRMI = genBotSerialRMIConnect();
			ObjectNode result = Json.newObject();
			String str = serialRMI.read();
			result.put("valid", true);
			//Timestamp timestamp = new Timestamp(new Date().getTime());
			SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
			result.put("timestamp", sdf.format(new Date().getTime()));
			result.put("string", str/*.replaceAll("\\r", "")*/);
			return ok(result);
		} catch (Exception e) {
			System.out.println("EXCEPION " + e.getMessage());
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
			System.out.println("EXCEPION " + e.getMessage());
			return error(e);
		}
		return ok(parameters.get("fitness")[0]);
	}
}
