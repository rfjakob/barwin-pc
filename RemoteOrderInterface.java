package genBot2;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.sql.SQLException;

public interface RemoteOrderInterface extends Remote {

	public CocktailWithName[] getNamedPopulation(String evolutionStackName) throws RemoteException;
	
	public void setCocktailFitness(String evolutionStackName, String name, double fitnessInput) throws RemoteException, NotEnoughRatedCocktailsException, SQLException;
	
	public boolean canEvolve(String evolutionStackName) throws RemoteException;
	
	public void evolve(String evolutionStackName) throws RemoteException, SQLException, NotEnoughRatedCocktailsException;
	
	public void generateEvolutionStack(String evolutionStackName, CheckFitness fitnessCheck, Recombination recombination, boolean dbReset, String propPath) throws RemoteException, SQLException;
	
	public void generateEvolutionStack(String evolutionStackName, Ingredient[] allowedIngredients, int populationSize, int truncation, int elitism, String dbDriverPath, boolean dbReset, CheckFitness fitnessCheck, Recombination recombination, String propPath)  throws RemoteException, SQLException;
}
