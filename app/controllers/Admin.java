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

			//for (Ingredient i: genBotRMI.getIngredients())
			//	System.out.println(i.getName());
			//genBotRMI.getNamedPopulation("adsf").;
			
			for(String s: genBotRMI.listLoadedEvolutionStacks()) {
				System.out.println(s);
				for(Ingredient i: genBotRMI.getAllowedIngredients(s)) {
					System.out.println("  -- " + i.getName());
				}
			}
			
			return ok(index.render(genBotRMI.listLoadedEvolutionStacks(), genBotRMI));
		} catch (Exception e) {
			return error(e);
		}
	}

	public static Result stack() {
		try {
			RemoteOrderInterface genBotRMI = genBotRMIConnect();
			return getStack(genBotRMI, "Refreshed", "");

		} catch (Exception e) {
			return errorAjax(e);
		}
	}

	public static Result getStatus(String rrr) {
		try {
			RemoteOrderInterface genBotRMI = genBotRMIConnect();
			ObjectNode result = Json.newObject();
			result.put("valid", true);
			result.put("statusCode", genBotRMI.getStatusCode());
			result.put("statusMessage", genBotRMI.getStatusMessage());
			if(genBotRMI.getCurrentlyPouringCocktail() != null)
				result.put("statusCocktail", genBotRMI.getCurrentlyPouringCocktail().getName());
			return ok(result);

		} catch (Exception e) {
			return errorAjax(e);
		}
	}

	public static Result stackOP() {
		Map<String, String[]> parameters = request().body().asFormUrlEncoded();
		
		try {
			RemoteOrderInterface genBotRMI = genBotRMIConnect();
			String name = parameters.get("name")[0].replace("_", " ");
			String action = parameters.get("action")[0];
			System.out.println(action);
			if(action.equals("load")) 
				genBotRMI.loadEvolutionStack(name);
			else if(action.equals("remove"))
				genBotRMI.removeEvolutionStack(name);
			else if(action.equals("delete")) 
				genBotRMI.deleteEvolutionStack(name);
			else
				throw new Exception("Operation " + name + " not implemented");
			
			return getStack(genBotRMI, "Done", "");

		} catch (Exception e) {
			return errorAjax(e);
		}
	}

	
	public static Result queueOP() {
		Map<String, String[]> parameters = request().body().asFormUrlEncoded();
		
		try {
			RemoteOrderInterface genBotRMI = genBotRMIConnect();
			String name = parameters.get("name")[0].replace("_", " ");
			String action = parameters.get("action")[0];
			System.out.println(action);
			if(action.equals("delete")) 
				genBotRMI.deleteCocktailFromQueue(name);
			else
				throw new Exception("Operation " + name + " not implemented");
			
			return getStack(genBotRMI, "Deleted", "");

		} catch (Exception e) {
			return errorAjax(e);
		}
	}
	
	public static Result generate() {
		Map<String, String[]> parameters = request().body().asFormUrlEncoded();
		try {
			RemoteOrderInterface genBotRMI = genBotRMIConnect();
			Ingredient[] allIngredients = genBotRMI.getIngredients();
			Ingredient[] selectedIngredients = new Ingredient[parameters.get("ingredients").length];
			for(int i = 0; i < parameters.get("ingredients").length; i++) {
				selectedIngredients[i] = allIngredients[Integer.parseInt(parameters.get("ingredients")[i])];
			}
			String name = parameters.get("name")[0];
			double maxPrice = Double.parseDouble(parameters.get("maxPrice")[0]);
			genBotRMI.generateEvolutionStack(name, selectedIngredients, maxPrice);

			return getStack(genBotRMI, "Created", name);
		} catch (Exception e) {
			return errorAjax(e);
		}
	}

	public static Result saveStackSettings() {
		Map<String, String[]> parameters = request().body().asFormUrlEncoded();

		try {
			RemoteOrderInterface genBotRMI = genBotRMIConnect();
			String name = parameters.get("name")[0];
			double mutationRate = Double.parseDouble(parameters.get("mutationRate")[0]);
			double maxPrice = Double.parseDouble(parameters.get("maxPrice")[0]);
			genBotRMI.setMaxPricePerLiter(name, maxPrice);
			genBotRMI.setMutationStdDeviation(name, mutationRate);
		
			return getStack(genBotRMI, "Settings saved", name);
		} catch (Exception e) {
			return errorAjax(e);
		}
	}
	
	public static Result saveSettings() {
		Map<String, String[]> parameters = request().body().asFormUrlEncoded();

		try {
			RemoteOrderInterface genBotRMI = genBotRMIConnect();
			int cocktailSize = Integer.parseInt(parameters.get("cocktailSize")[0]);
			genBotRMI.setCocktailSize(cocktailSize);
			return getStack(genBotRMI, "Settings saved", "");
		} catch (Exception e) {
			return errorAjax(e);
		}
	}
	
	public static Result pour() {
		Map<String, String[]> parameters = request().body().asFormUrlEncoded();
		String name = parameters.get("name")[0].replace("_", " ");;
		String[] nameA = name.split("-");
		try {
			RemoteOrderInterface genBotRMI = genBotRMIConnect();
			genBotRMI.queueCocktail(nameA[0], name);
			return getStack(genBotRMI, "Cocktail queued", nameA[0]);
		} catch (Exception e) {
			return errorAjax(e);
		}
	}

	public static Result evolve() {
		Map<String, String[]> parameters = request().body().asFormUrlEncoded();
		String name = parameters.get("name")[0].replace("_", " ");;
		try {
			RemoteOrderInterface genBotRMI = genBotRMIConnect();
			genBotRMI.evolve(name);
			return getStack(genBotRMI, "Do the evolution!", name);
		} catch (Exception e) {
			return errorAjax(e);
		}
	}

	public static Result sendResume() {
		try {
			RemoteOrderInterface genBotRMI = genBotRMIConnect();
			genBotRMI.sendToSerial("RESUME");
			ObjectNode result = Json.newObject();
			result.put("valid", true);
			result.put("message", "Resume sent");
			return ok(result);
		} catch (Exception e) {
			return errorAjax(e);
		}
	}

	public static Result sendAbort() {
		try {
			RemoteOrderInterface genBotRMI = genBotRMIConnect();
			genBotRMI.sendToSerial("ABORT");
			ObjectNode result = Json.newObject();
			result.put("valid", true);
			result.put("message", "Abort sent");
			return ok(result);
		} catch (Exception e) {
			return errorAjax(e);
		}
	}

	public static Result setFitness() {
		Map<String, String[]> parameters = request().body().asFormUrlEncoded();
		String name = parameters.get("name")[0].replace("_", " ");;
		try {
			RemoteOrderInterface genBotRMI = genBotRMIConnect();
			String[] nameA = name.split("-");
			//System.out.println("setFitness(" + nameA[0] + ", " + name + ", " + parameters.get("fitness")[0] + ")");
			genBotRMI.setCocktailFitness(nameA[0], name, Double.parseDouble(parameters.get("fitness")[0]));
			return getStack(genBotRMI, "Fitness set", nameA[0]);
		} catch (Exception e) {
			return errorAjax(e);
		}
		
	}
	
	public static Result getStack(RemoteOrderInterface genBotRMI, String message, String name) throws Exception {
		ObjectNode result = Json.newObject();
		result.put("valid", true);
		if(!message.isEmpty())
			result.put("message", message);
		if(!name.isEmpty())
			result.put("showTab", name.replace(" ", "_"));
		result.put("stack", stack.render(genBotRMI.listLoadedEvolutionStacks(), genBotRMI).toString());
		return ok(result);
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
