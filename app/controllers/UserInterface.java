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

public class UserInterface extends GenBotController {
	
	public static Result index() {
		Ingredient[] alleZutaten = IngredientArray.getInstance()
				.getAllIngredients();
		try {
			RemoteOrderInterface genBotRMI = genBotRMIConnect();
			/*for(CocktailWithName c: genBotRMI.getQueue().getLinkedList()) {
				System.out.println(c);
			}*/
			return ok(user.render(genBotRMI, alleZutaten));
		} catch (Exception e) {
			return error(e);
		}
	}

	public static Result pourType() {
		Map<String, String[]> parameters = request().body().asFormUrlEncoded();
		String name = parameters.get("name")[0];
		try {
			RemoteOrderInterface genBotRMI = genBotRMIConnect();
			for(CocktailWithName c: genBotRMI.getNamedPopulation(name)) {
				if(c.getCocktail().isQueued() || c.getCocktail().isPouring() || c.getCocktail().isPoured()) 
					continue;
				
				genBotRMI.queueCocktail(c.getEvolutionStackName(), c.getName());
				//genBotRMI.queueCocktail(c);
				break;
			}
			
			//genBotRMI.queueCocktail(nameA[0], name);
		} catch (Exception e) {
			return errorAjax(e);
		}
		ObjectNode result = Json.newObject();
		result.put("valid", true);
		result.put("message", "Cocktail queued");
		
		return ok(result);
	}
}
