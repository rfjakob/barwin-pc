package genBot2;

public class Ingredient {
	
	private final String name;
	private double pricePerLiter;
	private int arduinoOutputLine;

	public Ingredient(String name, double bottlePrice, double bottleSize, int arduinoOutputLine) {
		this.name = name;
		this.arduinoOutputLine = arduinoOutputLine;
		
		this.pricePerLiter = bottlePrice / bottleSize;
	}
	
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
}
