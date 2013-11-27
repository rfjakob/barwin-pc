package genBot2;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.sql.SQLException;

public class RMISimpleClient {

	public static void main(String[] args) {
		Ingredient[] alleZutaten = IngredientArray.getInstance().getAllIngredients();
		Ingredient[] erlaubteZutaten = {alleZutaten[2], alleZutaten[3], alleZutaten[4]};
		
		try {
			Registry registry = LocateRegistry.getRegistry();
			
			RemoteOrderInterface remoteOrderImpl = (RemoteOrderInterface) registry.lookup("rmiImpl");
			
			remoteOrderImpl.generateEvolutionStack("testStack", erlaubteZutaten, 10, 3, 2, "datenbank", true, "EfficientCocktail", "MutationAndIntermediateRecombination", 0.001, "eigenschaften");
			
			String[] evolutionStacks = remoteOrderImpl.listLoadedEvolutionStacks();
			System.out.println(evolutionStacks.length);
			System.out.println(evolutionStacks[0]);
			
			System.out.println(remoteOrderImpl.listLoadedEvolutionStacks()[0]);

			//CocktailWithName[] namedCocktails = remoteOrderImpl.getNamedPopulation(evolutionStacks[0]);
			
//			remoteOrderImpl.setCocktailFitness(evolutionStacks[0], cocktails[0].getName(), 10);
			
			System.out.println(remoteOrderImpl.canEvolve(evolutionStacks[0]));
			
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NotBoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


	}

}
