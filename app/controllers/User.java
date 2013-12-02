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

public class User extends AbstractController {
	
	public static Result index() {
		return ok(user.render());
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
				result.put("valid", true);
				result.put("message", "Cocktail queued");
				genBotRMI.queueCocktail(t.getEvolutionStackName(), t.getName());
			}
			return ok(result);
		} catch (Exception e) {
			return errorAjax(e);
		}

	}
}
