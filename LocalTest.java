package genBot2;

import java.net.MalformedURLException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.sql.SQLException;

public class LocalTest {

	public static void main(String[] args) {
		Ingredient[] alleZutaten = IngredientArray.getInstance().getAllIngredients();
		Ingredient[] erlaubteZutaten = {alleZutaten[2], alleZutaten[3], alleZutaten[4]};
		
		RemoteOrderInterface remoteOrderImpl = new RemoteOrderImpl();
		
		CocktailQueue queue = new CocktailQueue();
		
		QueueManager queueManager;
		try {
			queueManager = new QueueManager(queue, "rmi://192.168.1.151:12121/serial", "/dev/ttyACM1", 250);
			queueManager.start();
		} catch (Exception e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		
		
		try {
			remoteOrderImpl.generateEvolutionStack("testStack", erlaubteZutaten, 10, 3, 2, "datenbank", true, "EfficientCocktail", "MutationAndIntermediateRecombination", 0.001, "eigenschaften");
			String[] evolutionStacks = remoteOrderImpl.listEvolutionStacks();
			System.out.println(evolutionStacks.length);
			System.out.println(evolutionStacks[0]);
			
			CocktailWithName[] namedCocktails = remoteOrderImpl.getNamedPopulation(evolutionStacks[0]);
			
			for (int i = 0; i < namedCocktails.length; i++) {
				System.out.println(namedCocktails[i].getCocktail().toString());
			}
			
			System.out.println(namedCocktails[1].getName());
			
			remoteOrderImpl.setCocktailFitness(evolutionStacks[0], namedCocktails[1].getName(), 10);
			
			System.out.println(remoteOrderImpl.canEvolve("testStack"));
			
			// now test the queue
			queue.addCocktail("testStack", remoteOrderImpl.getNamedPopulation("testStack")[1].getName());
			queue.addCocktail("testStack", remoteOrderImpl.getNamedPopulation("testStack")[0].getName());
			queue.addCocktail("testStack", remoteOrderImpl.getNamedPopulation("testStack")[5].getName());

			try {
				Thread.sleep(50000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			queue.addCocktail("testStack", remoteOrderImpl.getNamedPopulation("testStack")[3].getName());
			System.out.println("Now finished!");
			
		} catch (RemoteException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (NotEnoughRatedCocktailsException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
