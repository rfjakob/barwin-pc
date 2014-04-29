package genBot;

public class BestRating implements CheckFitness {
	
	public BestRating() {
	}

	@Override
	public void setFitness(Cocktail c) {
		c.setFitness(c.getRating());
	}

}
