package genBot2;

public class IngredientAmount {
	
	private final Ingredient ingredient;
	private double amount;
	
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
	
	public double getAmountPrice() {
		return ingredient.getPricePer(amount);
	}
	
	public String toString() {
		return ingredient.getName() + ": " + amount;
	}
	
	public void setAmount(double amount) {
		this.amount = amount;
	}
}
