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
import views.html.frontend.*;

public class User extends AbstractController {
	
	public static Result index() {
		try {
			RemoteOrderInterface genBotRMI = genBotRMIConnect();
			return ok(views.html.frontend.content.render(genBotRMI));
		} catch (Exception e) {
			return error(e);
		}
	}

	public static Result ajaxIndex() {
		Ingredient[] alleZutaten = IngredientArray.getInstance()
				.getAllIngredients();
		try {
			RemoteOrderInterface genBotRMI = genBotRMIConnect();
			ObjectNode result = Json.newObject();
			
			//genBotRMI.getCurrentlyPouringCocktail()
			
			result.put("valid", true);
			String s = genBotRMI.getStatusMessage();
			if(s != null && !s.isEmpty())
				result.put("message", s);
			result.put("html", userInner.render(genBotRMI, alleZutaten).toString());
			
			return ok(result);
		} catch (Exception e) {
			return errorAjax(e);
		}
	}
	
	public static Result pourType() {
		Map<String, String[]> parameters = request().body().asFormUrlEncoded();
		String name = parameters.get("name")[0].replace("_", " ");;
		try {
			RemoteOrderInterface genBotRMI = genBotRMIConnect();
			
			CocktailWithName t = null;
			for(CocktailWithName c: genBotRMI.getNamedPopulation(name)) {
				if(c.getCocktail().isQueued() || c.getCocktail().isPouring() || c.getCocktail().isPoured()) 
					continue;
				t = c;
				break;
			}
			ObjectNode result = Json.newObject();
			if(t == null) {
				result.put("valid", false);
				result.put("message", "No more cocktails");
			} else {
				genBotRMI.queueCocktail(t.getEvolutionStackName(), t.getName());
				result.put("valid", true);
				result.put("name", t.getEvolutionStackName());
				result.put("cocktailName", t.getName());
				result.put("message", "Cocktail queued");
			}
			return ok(result);
		} catch (Exception e) {
			return errorAjax(e);
		}

	}
}
