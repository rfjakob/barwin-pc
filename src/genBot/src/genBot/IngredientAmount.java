package genBot;

import java.io.Serializable;

/*
 * This class consists of an Ingredient and an amount (as a double).
 * It is therefore the base for a cocktail which consists of multipe 
 * IngredientAmounts.
 */

public class IngredientAmount implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final Ingredient ingredient;
	private double amount;
	
	/*
	 * Constructor
	 * @param ingredient An ingredient
	 * @param amount a number that matches the amount
	 */
	public IngredientAmount(Ingredient ingredient, double amount) {
		this.ingredient = ingredient;
		this.amount = amount;
	}
	
	public Ingredient getIngredient() {
		return ingredient;
	}

	public double getAmount() {
		return amount;
	}
	
	/*
	 * Returns the price of this IngredientAmount
	 * @return the price of this IngredientAmount
	 */
	public double getAmountPrice() {
		return ingredient.getPricePer(amount);
	}
		
	public void setAmount(double amount) {
		this.amount = amount;
	}

	public String toString() {
		return ingredient.getName() + ": " + amount;
	}
}
