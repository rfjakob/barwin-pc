package genBot;

import java.util.HashMap;
import java.io.File;
import java.util.ArrayList;
/*
 * EvolutionStackContainer is a singleton to store the different EvolutionStacks
 * THIS CLASS SHOULD EXTEND HASHMAP
 * COULD ALSO BE INTEGRATED WITH EvolutionAlgorithmManager
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
	
	private void add(String name, EvolutionAlgorithmManager evoAlgMngr) {
		evolutionMap.put(name, evoAlgMngr);
	}

	public void load(String name) throws Exception {
		EvolutionAlgorithmManager evoAlgMngr = new EvolutionAlgorithmManager(name);
		add(name, evoAlgMngr);
	}

	public void create(String name) throws Exception {
		EvolutionAlgorithmManager evoAlgMngr = new EvolutionAlgorithmManager(name);
		add(name, evoAlgMngr);
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

	/*** GET LIST OF PROPERTY FILES***/
	public String[] getListOfAvailableStacks() {		
		String files;
		//System.out.println(GenBotConfig.storePath);
		File folder = new File(GenBotConfig.stackPath);
		File[] listOfFiles = folder.listFiles();
		
		ArrayList<String> fileNames = new ArrayList<String>();
		
		for (int i = 0; i < listOfFiles.length; i++) {
			if (listOfFiles[i].isFile()) {
				files = listOfFiles[i].getName();
				
				if (files.endsWith(".properties") || files.endsWith(".PROPERTIES")) {
					String files1 = files.substring(0, files.lastIndexOf('.'));
					fileNames.add(files1);
				}
			}
		}
		
		return fileNames.toArray(new String[fileNames.size()]);
	}
}
