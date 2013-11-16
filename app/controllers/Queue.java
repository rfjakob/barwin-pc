package controllers;



import genBot2.*;
import play.mvc.*;
import views.html.*;


public class Queue extends GenBotController {
	
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


}
