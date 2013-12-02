package genBot2;

import java.sql.SQLException;
import java.util.HashMap;

/*
 * EvolutionStackContainer is a singleton to store the different EvolutionStacks
 */

public class EvolutionStackContainer {
	
	private static EvolutionStackContainer evolutionStackContainer = null;
	
	private static HashMap<String, EvolutionAlgorithmManager> evolutionMap;

	/*
	 * Private constructor (because it's a singleton).
	 * Nothing special - evolutionMap is initialized.
	 */
	public EvolutionStackContainer() {
		evolutionMap = new HashMap<String, EvolutionAlgorithmManager>();
	}
	
	/*
	 * Returns the singleton IngredientArray.
	 * If there is no IngredientArray yet, it will be constructed now.
	 * @return IngredientArray an instance of the IngredientArray
	 */
	public static EvolutionStackContainer getInstance() {
		if (evolutionStackContainer == null) {
			evolutionStackContainer = new EvolutionStackContainer();
		}
		return evolutionStackContainer;
	}
	
	public EvolutionAlgorithmManager getEvolutionAlgorithmManager(String name) {
		if (evolutionMap.containsKey(name)) {
			return evolutionMap.get(name);
		} else {
			throw new IllegalArgumentException("Evolution Stack " + name + " does not exist.");
		}
	}
	
	private void addEvolutionAlgorithmManager(String name, EvolutionAlgorithmManager evoAlgMngr) {
		evolutionMap.put(name, evoAlgMngr);
	}

	public void addEvolutionAlgorithmManager(String name, Ingredient[] allowedIngredients, int populationSize, int truncation, int elitism, String dbDriverPath, boolean resetDbTable, CheckFitness fitnessCheck, Recombination recombination, double stdDeviation, double maxPricePerLiter, String propPath) throws SQLException {
		EvolutionAlgorithmManager evoAlgMngr = new EvolutionAlgorithmManager(name, allowedIngredients, populationSize, truncation, elitism, dbDriverPath, resetDbTable, fitnessCheck, recombination, stdDeviation, maxPricePerLiter, propPath);

		addEvolutionAlgorithmManager(name, evoAlgMngr);
	}
	
	public void addEvolutionAlgorithmManager(String name, CheckFitness fitnessCheck, Recombination recombination, boolean reset, String propPath) throws SQLException {
		EvolutionAlgorithmManager evoAlgMngr = new EvolutionAlgorithmManager(fitnessCheck, recombination, reset, propPath);
		
		addEvolutionAlgorithmManager(name, evoAlgMngr);
	}
		
	public String[] listEvolutionStacks() {
		String[] keys = (String[])(evolutionMap.keySet().toArray(new String[evolutionMap.size()]));

		return keys;
	}
	
	public boolean containsEvolutionStack(String name) {
		return evolutionMap.containsKey(name);
	}

	public void remove(String evolutionStackName) {
		evolutionMap.remove(evolutionStackName);
	}
}
