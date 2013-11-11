package controllers;



import java.util.Map;

import genBot2.*;
//import play.libs.Json;
//import play.*;
//import play.cache.Cache;
import play.mvc.*;
import views.html.*;

import java.rmi.*;

public class Application extends GenBotController {

	private static RemoteOrderInterface genBotRMIConnect() throws Exception {
		return (RemoteOrderInterface) Naming.lookup("rmi://10.20.30.160/rmiImpl");
		//return (RemoteOrderInterface) Naming.lookup("rmi://127.0.0.1/rmiImpl");
		
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

		Ingredient[] allIngredients = IngredientArray.getInstance().getAllIngredients();
		
		Ingredient[] selectedIngredients = new Ingredient[parameters.get("ingredients").length];
		for(int i = 0; i < parameters.get("ingredients").length; i++) {
			System.out.println(parameters.get("ingredients")[i] + " " + allIngredients[Integer.parseInt(parameters.get("ingredients")[i])].getName());
			selectedIngredients[i] = allIngredients[Integer.parseInt(parameters.get("ingredients")[i])];
		}


		try {
			RemoteOrderInterface genBotRMI = genBotRMIConnect();
			String name = parameters.get("name")[0];
			genBotRMI.generateEvolutionStack(name, selectedIngredients);
			return ok();
		} catch (Exception e) {
			return errorAjax(e);
		}

	}

	public static Result generation(Long id) {
		// return ok(index.render("Your new application is ready."));
		// return ok("talking about my generation");
		return ok("blup");
	}

	public static Result setFitness() {
		Map<String, String[]> parameters = request().body().asFormUrlEncoded();
		String name = parameters.get("name")[0];
		try {
			RemoteOrderInterface genBotRMI = genBotRMIConnect();
			String[] nameA = name.split("-");
			System.out.println("setFitness(" + nameA[0] + ", " + name + ", " + parameters.get("fitness")[0] + ")");
			genBotRMI.setCocktailFitness(nameA[0], name, Double.parseDouble(parameters.get("fitness")[0]));
		} catch (Exception e) {
			return error(e);
		}
		return ok(parameters.get("fitness")[0]);
	}
}
