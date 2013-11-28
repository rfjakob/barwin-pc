package controllers;



import genBot2.*;
import play.mvc.*;
import views.html.*;


public class Queue extends AbstractController {
	
	public static Result index() {
		Ingredient[] alleZutaten = IngredientArray.getInstance()
				.getAllIngredients();
		try {
			RemoteOrderInterface genBotRMI = genBotRMIConnect();

			//for (String name: genBotRMI.listLoadedEvolutionStacks())
			//	System.out.println(name);

			return ok(index.render(genBotRMI.listLoadedEvolutionStacks(), genBotRMI,
					alleZutaten));
		} catch (Exception e) {
			return error(e);
		}
	}


}
