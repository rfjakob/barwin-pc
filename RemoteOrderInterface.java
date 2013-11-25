package genBot2;

import java.io.FileNotFoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.sql.SQLException;
import java.util.Properties;

public interface RemoteOrderInterface extends Remote {

	public CocktailWithName[] getNamedPopulation(String evolutionStackName) throws RemoteException;
	
	public void setCocktailFitness(String evolutionStackName, String name, double fitnessInput) throws RemoteException, NotEnoughRatedCocktailsException, SQLException;
	
	public boolean canEvolve(String evolutionStackName) throws RemoteException;
	
	public void evolve(String evolutionStackName) throws RemoteException, SQLException, NotEnoughRatedCocktailsException;
	
	public void generateEvolutionStack(String evolutionStackName, Ingredient[] allowedIngredients) throws RemoteException, SQLException;
	
	public void generateEvolutionStack(String evolutionStackName, String fitnessCheckName, String recombinationName, boolean resetDbTable, String propPath, double stdDeviation) throws RemoteException, SQLException;
	
	public void generateEvolutionStack(String evolutionStackName, Ingredient[] allowedIngredients, int populationSize, int truncation, int elitism, String dbDriverPath, boolean resetDbTable, String fitnessCheckName, String recombinationName, double stdDeviation, String propPath)  throws RemoteException, SQLException;
	
	public void loadEvolutionStack(String evolutionStackName) throws RemoteException, SQLException;
	
	public String[] listPossibleEvolutionStacks() throws RemoteException;
	
	public String[] listLoadedEvolutionStacks() throws RemoteException;
	
	public boolean containsEvolutionStack(String evolutionStackName) throws RemoteException;
	
	public Properties getProps(String evolutionStackName) throws RemoteException, FileNotFoundException;
	
	public void setProps(String evolutionStackName, int populationSize, int truncation, int elitism, double stdDeviation, String dbDriverPath, String booleanAllowedIngredientsString) throws RemoteException;
	
	public void queueCocktail(CocktailWithName cocktail) throws RemoteException;
	
	public void queueCocktail(String evolutionStackName, String cocktailName) throws RemoteException;
	
	public void deleteCocktailFromQueue(String cocktailName) throws RemoteException;
	
	public void reorderQueue(String cocktailNameList[]) throws RemoteException;
	
	public CocktailQueue getQueue() throws RemoteException;
	
	public CocktailWithName getCurrentlyPouringCocktail() throws RemoteException;

	public CocktailGenerationManager getOldGeneration(String evolutionStackName, int generationNumber) throws RemoteException, SQLException;
	
	public CocktailGenerationManager readGenerationManager(String evolutionStackName) throws RemoteException;
}
