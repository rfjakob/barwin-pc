package genBot2;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Random;
import java.util.Scanner;

public class genBot2 {

	public static void main(String[] args) {
		
		// Set a recombination
		CocktailGenerationManager manager = new CocktailGenerationManager(0, 10, null, null);
		
		System.out.println("First Generation:");
		System.out.println(manager.toString());
		System.out.println("Press <Enter> to save");
		try {
			  System.in.read();
			} catch (IOException e) {
			  e.printStackTrace();
			}
		
		try {
			DataBaseDriver drv = new DataBaseDriver("testDB.db", 10);
			drv.reset();
			
			drv.insert(0, manager.getCocktailGeneration());
			
			System.out.println("Saved generation to database, yay!");
			
			System.out.println("Press <Enter> to restore");
			try {
				  System.in.read();
				} catch (IOException e) {
				  e.printStackTrace();
				}
			
			CocktailGeneration restoredGen =  drv.select(0);
			
			System.out.println("The generation has been restored");
			
			System.out.println(restoredGen.toString());
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
