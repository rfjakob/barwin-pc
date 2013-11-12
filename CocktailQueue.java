package genBot2;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class CocktailQueue {
	
	private LinkedList<CocktailWithName> queue;
	final Lock lock;
		
	public CocktailQueue() {
		this.queue = new LinkedList<CocktailWithName>();
		this.lock = new ReentrantLock();
	}
	
	public void addCocktail(CocktailWithName cocktail) {
		cocktail.getCocktail().setQueued(true);
		
		lock.lock();
		
		queue.addLast(cocktail);
		
		lock.unlock();
	}
	
	public void addCocktail(String evolutionAlgorithmManager, String cocktailName) {
		CocktailWithName[] cocktails = EvolutionStackContainer.getInstance().getEvolutionAlgorithmManager(evolutionAlgorithmManager).getGenManager().getNamedCocktailGeneration();
		boolean cocktailFound = false;
		
		for (int i = 0; i < cocktails.length; i++) {
			if (cocktails[i].getName().equals(cocktailName)) {
				addCocktail(cocktails[i]);
				cocktailFound = true;
			}
		}
		
		if (cocktailFound == false) {
			throw new IllegalArgumentException(cocktailName + "not found in " + evolutionAlgorithmManager + "!");
		}
	}
	
	public CocktailWithName getAndRemoveFirstCocktail() {
		CocktailWithName retCocktail =  queue.getFirst();
		
		lock.lock();
	
		queue.removeFirst();
		
		lock.unlock();
		
		return retCocktail;
	}
	
	public void deleteCocktail(CocktailWithName cocktail) {
		Iterator<CocktailWithName> queueIt = queue.iterator();

		while (queueIt.hasNext()) {
			CocktailWithName curCocktail = queueIt.next();
			
			if (curCocktail.getName().equals(cocktail.getName())) {
				lock.lock();
				
				curCocktail.getCocktail().setQueued(false);
				queueIt.remove();
				
				lock.unlock();
			}
		}
	}
	
	public void deleteCocktail(String evolutionAlgorithmManager, String cocktailName) {
		CocktailWithName[] cocktails = EvolutionStackContainer.getInstance().getEvolutionAlgorithmManager(evolutionAlgorithmManager).getGenManager().getRatedNamedCocktailGeneration();
		boolean cocktailFound = false;
		
		for (int i = 0; i < cocktails.length; i++) {
			if (cocktails[i].getName().equals(cocktailName)) {
				deleteCocktail(cocktails[i]);
				cocktailFound = true;
			}
		}
		
		if (cocktailFound == false) {
			throw new IllegalArgumentException(cocktailName + "not found in " + evolutionAlgorithmManager + "!");
		}
	}
	
	public boolean isEmpty() {
		return queue.isEmpty();
	}
}
