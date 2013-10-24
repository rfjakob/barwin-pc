package genBot2;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Random;
import java.util.Scanner;

public class genBot2_old2 {

	public static void main(String[] args) {

		// Set a recombination
		CocktailGenerationManager manager;
		try {
			manager = new CocktailGenerationManager(10, 0, 0, null, false, null, null, null);

			System.out.println("First Generation:");
			System.out.println(manager.toString());
			System.out.println("Press <Enter> to save");
			System.in.read();

			DataBaseDriver drv = new DataBaseDriver("testDB.db", true);
			drv.reset();

			drv.insert(0, manager.getCocktailGeneration());

			System.out.println("Saved generation to database, yay!");

			System.out.println("Press <Enter> to restore");
			System.in.read();

			CocktailGeneration restoredGen =  drv.select(0);

			System.out.println("The generation has been restored");

			System.out.println(restoredGen.toString());
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}