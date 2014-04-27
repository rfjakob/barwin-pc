package genBot;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.Properties;

/*
 * Class to store ingredients. Only the name, the price per liter
 * and the output line for Arduino are stored. the specific amount
 * of an ingredient to mix a cocktail is stored in class IngredientAmount.
 * @see IngredientAmount
 */

public class Ingredient implements Serializable, Comparable<Ingredient> {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final String name;
	private double pricePerLiter;
	private int arduinoOutputLine;

	/*
	 * Constructor
	 * @param name Name of the ingredient
	 * @param bottlePrice Price of a bottle of this ingredient
	 * @param bottleSize Size of the bottle (bottlePrice and bottleSize are used to 
	 * generate the pricePerLiter
	 * @param arduinoOutput Arduino Output
	 */
	public Ingredient(String name, double bottlePrice, double bottleSize, int arduinoOutputLine) {
		this.name = name;
		this.arduinoOutputLine = arduinoOutputLine;
		
		this.pricePerLiter = bottlePrice / bottleSize;
	}
	
	/*
	 * @param amount of this ingredient
	 * @return the price of this amount of the ingredient
	 */
	public double getPricePer(double liter) {
		return pricePerLiter * liter;
	}
	
	public int getArduinoOutputLine() {
		return arduinoOutputLine;
	}
	
	public String getName() {
		return name;
	}
	
	public void setPricePerLiter(float pricePerLiter) {
		this.pricePerLiter = pricePerLiter;
	}
	
	public void setArduinoOutputLine(int arduinoOutputLine) {
		this.arduinoOutputLine = arduinoOutputLine;
	}
	
	public boolean equals(Ingredient ingredient) {
		if (this.getName().equals(ingredient.getName()) & this.getPricePer(1) == ingredient.getPricePer(1) & this.getArduinoOutputLine() == ingredient.getArduinoOutputLine()) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public int compareTo(Ingredient o) {
		if (this.getArduinoOutputLine() == o.getArduinoOutputLine()) {
			return 0;
		} else if (this.getArduinoOutputLine() > o.getArduinoOutputLine()) {
			return 1;
		} else {
			return -1;
		}
	}
}
