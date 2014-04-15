package genBot;

import java.net.MalformedURLException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.sql.SQLException;

import serialRMI.SerialRMIException;

public class CheckProblems {

	public static void main(String[] args) throws MalformedURLException, RemoteException, NotBoundException, SerialRMIException, SQLException, MaxAttemptsToMeetPriceConstraintException, NotEnoughRatedCocktailsException {
		CocktailQueue queue = new CocktailQueue();
		
		QueueManager mngr = new QueueManager(queue, "", "", 200);
		
		RemoteOrderImpl rmt = new RemoteOrderImpl(mngr);
		
		rmt.loadEvolutionStack("Risky Barwin");
		
		CocktailWithName[] cocktails = rmt.getNamedPopulation("Risky Barwin");
		
		for (int i = 0; i < cocktails.length; i++) {
			System.out.println(cocktails[i].toString());
		}
		System.out.println(rmt.canEvolve("Risky Barwin"));
//		rmt.evolve("Risky Barwin");
				
		rmt.generateEvolutionStack("Test", IngredientArray.getInstance().getAllIngredients(), 10, 1, 1, "testDB", true, "efficientCocktail", "normal", 0.05, 5, "test");
		
		Cocktail c = rmt.readGenerationManager("Test").getCocktailByName("Test-0-0");
		c.setFitness(new EfficientCocktail(), 0.2, 3.0);
		System.out.println(c.toString());

		c.setFitness(new EfficientCocktail(), 0.6, 9.0);
		System.out.println(c.toString());

	}

}
