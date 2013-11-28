package controllers;



import java.util.Map;

import com.fasterxml.jackson.databind.node.ObjectNode;

import genBot2.*;
import play.libs.Json;
//import play.libs.Json;
//import play.*;
//import play.cache.Cache;
import play.mvc.*;
import views.html.*;

public class Admin extends AbstractController {

	static Ingredient[] alleZutaten = IngredientArray.getInstance()
			.getAllIngredients();
	
	public static Result index() {
		try {
			RemoteOrderInterface genBotRMI = genBotRMIConnect();

			//for (String name: genBotRMI.listEvolutionStacks())
			//	System.out.println(name);
			//genBotRMI.getNamedPopulation("adsf").;
			
			return ok(index.render(genBotRMI.listLoadedEvolutionStacks(), genBotRMI,
					alleZutaten));
		} catch (Exception e) {
			return error(e);
		}
	}

	public static Result stack() {
		try {
			RemoteOrderInterface genBotRMI = genBotRMIConnect();

			ObjectNode result = Json.newObject();
			result.put("valid", true);
			result.put("message", "Refreshed");
			result.put("stack", stack.render(genBotRMI.listLoadedEvolutionStacks(), genBotRMI,
					alleZutaten).toString());
			return ok(result);

		} catch (Exception e) {
			return errorAjax(e);
		}
	}

	public static Result stackOP() {
		Map<String, String[]> parameters = request().body().asFormUrlEncoded();
		
		try {
			RemoteOrderInterface genBotRMI = genBotRMIConnect();
			String name = parameters.get("name")[0];
			String action = parameters.get("action")[0];
			
			if(action.equals("load")) 
				genBotRMI.loadEvolutionStack(name);
			else if(action.equals("remove"))
				genBotRMI.removeEvolutionStack(name);
			else if(action.equals("delete"))
				genBotRMI.deleteEvolutionStack(name);
			else
				throw new Exception("Operation " + name + " not implemented");
			
			ObjectNode result = Json.newObject();
			result.put("valid", true);
			result.put("message", "Refreshed");
			result.put("stack", stack.render(genBotRMI.listLoadedEvolutionStacks(), genBotRMI,
					alleZutaten).toString());
			return ok(result);

		} catch (Exception e) {
			return errorAjax(e);
		}
	}

	public static Result generate() {
		Map<String, String[]> parameters = request().body().asFormUrlEncoded();

		Ingredient[] allIngredients = IngredientArray.getInstance().getAllIngredients();
		Ingredient[] selectedIngredients = new Ingredient[parameters.get("ingredients").length];
		for(int i = 0; i < parameters.get("ingredients").length; i++) {
			//System.out.println(parameters.get("ingredients")[i] + " " + allIngredients[Integer.parseInt(parameters.get("ingredients")[i])].getName());
			selectedIngredients[i] = allIngredients[Integer.parseInt(parameters.get("ingredients")[i])];
		}

		try {
			RemoteOrderInterface genBotRMI = genBotRMIConnect();
			String name = parameters.get("name")[0];
			genBotRMI.generateEvolutionStack(name, selectedIngredients);
			
			ObjectNode result = Json.newObject();
			result.put("valid", true);
			result.put("message", "Created");
			result.put("showTab", name);
			result.put("stack", stack.render(genBotRMI.listLoadedEvolutionStacks(), genBotRMI,
					alleZutaten).toString());
			return ok(result);
		} catch (Exception e) {
			return errorAjax(e);
		}
	}
	
	public static Result pour() {
		Map<String, String[]> parameters = request().body().asFormUrlEncoded();
		String name = parameters.get("name")[0];
		String[] nameA = name.split("-");
		try {
			RemoteOrderInterface genBotRMI = genBotRMIConnect();
			
			genBotRMI.queueCocktail(nameA[0], name);
		} catch (Exception e) {
			return errorAjax(e);
		}
		ObjectNode result = Json.newObject();
		result.put("valid", true);
		result.put("message", "Cocktail queued");
		
		return ok(result);
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
			//System.out.println("setFitness(" + nameA[0] + ", " + name + ", " + parameters.get("fitness")[0] + ")");
			genBotRMI.setCocktailFitness(nameA[0], name, Double.parseDouble(parameters.get("fitness")[0]));
		} catch (Exception e) {
			return errorAjax(e);
		}
		ObjectNode result = Json.newObject();
		result.put("valid", true);
		result.put("message", "Fitness set");
		return ok(result);
	}
}
