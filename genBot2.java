package genBot2;

public class genBot2 {

	public static void main(String[] args) {
		CocktailGenerationManager manager = new CocktailGenerationManager(0, 10);
		
		System.out.println(manager.randomToString());
		
		System.out.println(manager.evolve(0.2, 2).toString());

	}

}
