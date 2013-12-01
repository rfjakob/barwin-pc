package controllers;



import java.rmi.RemoteException;
import java.sql.SQLException;
import java.util.HashMap;
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
			ObjectNode result = Json.newObject();
			result.put("valid", true);
			result.put("message", "Cocktail queued");
			result.put("showTab", nameA[0]);
			result.put("stack", stack.render(genBotRMI.listLoadedEvolutionStacks(), genBotRMI,
					alleZutaten).toString());
			
			return ok(result);
		} catch (Exception e) {
			return errorAjax(e);
		}

	}


	public static Result sendResume() {
		try {
			RemoteOrderInterface genBotRMI = genBotRMIConnect();
			genBotRMI.sendToSerial("RESUME ");
			ObjectNode result = Json.newObject();
			result.put("valid", true);
			result.put("message", "Resume sent");
			return ok(result);
		} catch (Exception e) {
			return errorAjax(e);
		}
	}

	public static Result setFitness() {
		Map<String, String[]> parameters = request().body().asFormUrlEncoded();
		String name = parameters.get("name")[0];
		try {
			RemoteOrderInterface genBotRMI = genBotRMIConnect();
			String[] nameA = name.split("-");
			//System.out.println("setFitness(" + nameA[0] + ", " + name + ", " + parameters.get("fitness")[0] + ")");
			genBotRMI.setCocktailFitness(nameA[0], name, Double.parseDouble(parameters.get("fitness")[0]));
			ObjectNode result = Json.newObject();
			result.put("valid", true);
			result.put("message", "Fitness set");
			result.put("showTab", nameA[0]);
			result.put("stack", stack.render(genBotRMI.listLoadedEvolutionStacks(), genBotRMI,
					alleZutaten).toString());
			return ok(result);
		} catch (Exception e) {
			return errorAjax(e);
		}
		
	}
	
	public static HashMap<String, Double> getMinMaxMedianThirdQuartMean(String evolutionStackName, int generationNumber) throws Exception, FitnessNotSetException {
		RemoteOrderInterface genBotRMI;
		genBotRMI = genBotRMIConnect();
		
		HashMap<String, Double> returnMap = new HashMap<String, Double>();
		
		
		CocktailGenerationManager genMngr = genBotRMI.getOldGeneration(evolutionStackName, generationNumber);
			
		CocktailGeneration relevantCocktails = new CocktailGeneration(genMngr.getCocktailGeneration().getRankedPopulation());
		Cocktail[] rankedGen = relevantCocktails.rankCocktails();
		
		returnMap.put("min", rankedGen[0].getFitness());
		returnMap.put("max", rankedGen[0].getFitness());
		
		double sum = 0;
		for (int i = 0; i < rankedGen.length; i++) {
			if (rankedGen[i].getFitness() < returnMap.get("min")) {
				returnMap.put("min", rankedGen[i].getFitness());
			}
			
			if (rankedGen[i].getFitness() > returnMap.get("max")) {
				returnMap.put("min", rankedGen[i].getFitness());
			}
			
			sum += rankedGen[i].getFitness();
		}
		
		if (rankedGen.length % 2 != 0) {
			returnMap.put("median", rankedGen[(int) (Math.floor(rankedGen.length / 2) + 1)].getFitness());
		} else {
			returnMap.put("median", ((rankedGen[rankedGen.length / 2].getFitness() + rankedGen[(rankedGen.length / 2) + 1].getFitness()) / 2) );
		}
		
		if (rankedGen.length % 4 != 0) {
			returnMap.put("thirdQuart", rankedGen[(int) (Math.floor(rankedGen.length * 0.75) + 1)].getFitness());
		} else {
			returnMap.put("thirdQuart", ((rankedGen[(int) Math.round(rankedGen.length * 0.75)].getFitness() + rankedGen[(int) (Math.round(rankedGen.length * 0.75) + 1)].getFitness()) / 2) );
		}

		
		returnMap.put("mean", (sum / genMngr.getCocktailGeneration().getRankedPopulationSize()));
		
		return returnMap;
	}
}
