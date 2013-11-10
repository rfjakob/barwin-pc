package genBot2;

import java.util.Iterator;
import java.util.LinkedList;

public class CocktailQueue {
	
	private LinkedList<CocktailWithName> queue;
	
	public CocktailQueue(SerialRemoteInterface arduino) {
		this.queue = new LinkedList<CocktailWithName>();
	}
	
	public void addCocktail(CocktailWithName cocktail) {
		queue.addLast(cocktail);
	}
	
	public void addCocktail(String evolutionAlgorithmManager, String cocktailName) {
		CocktailWithName[] cocktails = EvolutionStackContainer.getInstance().getEvolutionAlgorithmManager(evolutionAlgorithmManager).getGenManager().getRatedNamedCocktailGeneration();
		boolean cocktailFound = false;
		
		for (int i = 0; i < cocktails.length; i++) {
			if (cocktails[i].getName().equals(cocktailName)) {
				queue.addLast(cocktails[i]);
				cocktailFound = true;
			}
		}
		
		if (cocktailFound == false) {
			throw new IllegalArgumentException(cocktailName + "not found in " + evolutionAlgorithmManager + "!");
		}
	}
	
	public void pourAndRemoveFirstCocktail() {
		queue.getFirst();
		queue.removeFirst();
	}
	
	public void deleteCocktail(CocktailWithName cocktail) {
		Iterator<CocktailWithName> queueIt = queue.iterator();

		while (queueIt.hasNext()) {
			CocktailWithName curCocktail = queueIt.next();
			
			if (curCocktail.getName().equals(cocktail.getName())) {
				queue.remove(curCocktail);
				break;
			}
		}
	}
}
