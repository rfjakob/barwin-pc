package genBot2;

public class TestAllFunctions {

	public static void main(String[] args) {
		Ingredient[] alleZutaten = IngredientArray.getInstance().getAllIngredients();
		Ingredient[] erlaubteZutaten1 = {alleZutaten[2], alleZutaten[3], alleZutaten[4]};
		Ingredient[] erlaubteZutaten2 = {alleZutaten[0], alleZutaten[3], alleZutaten[4], alleZutaten[5], alleZutaten[6]};
//		Ingredient[] erlaubteZutaten3 = alleZutaten;
	}
}

/*		CocktailQueue queue = new CocktailQueue();
		
		QueueManager queueManager;
		
		try {
			queueManager = new QueueManager(queue, "rmi://127.0.0.1:12121/serial", "/dev/ttyACM0", 250);
			queueManager.start();
			RemoteOrderInterface remoteOrderImpl = new RemoteOrderImpl(queueManager);
			remoteOrderImpl.generateEvolutionStack("testStack1", erlaubteZutaten1, 15, 3, 2, "datenbank", true, "EfficientCocktail", "MutationAndIntermediateRecombination", 0.001, "eigenschaften");
			remoteOrderImpl.generateEvolutionStack("testStack2", erlaubteZutaten2, 15, 3, 2, "datenbank", true, "EfficientCocktail", "MutationAndIntermediateRecombination", 0.001, "eigenschaften");
			remoteOrderImpl.generateEvolutionStack("testStack3", erlaubteZutaten3, 15, 3, 2, "datenbank", true, "EfficientCocktail", "MutationAndIntermediateRecombination", 0.001, "eigenschaften");
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
				queue.addCocktail("testStack", remoteOrderImpl.getNamedPopulation("testStack")[3].getName());
				Thread.sleep(50000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (RemoteException | SerialRMIException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (NotEnoughRatedCocktailsException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

			
			System.out.println("Now finished!");
	*/