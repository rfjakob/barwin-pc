package genBot2;

public class QueueManager extends Thread {

	private CocktailQueue queue;
	
	// TODO these two should make sense later :)
	private boolean specialCommand1;
	private boolean specialCommand2;

	public QueueManager(CocktailQueue queue) {
		// boooohhh!!!
		setDaemon(true);
		
		this.queue = queue;
		
		this.specialCommand1 = false;
		this.specialCommand2 = false;
	}

	@Override
	public void run() {
		while (true) {
			processQueue();
		}
	}
	
	public void processQueue() {
		if (!queue.isEmpty() & serialIsReady()) {
			if (anySpecialCommands()) {
				runSpecialCommands();
			} else {
				pourCocktail();
			}
		}
	}
	public void pourCocktail() {
		CocktailWithName toBePoured = queue.getAndRemoveFirstCocktail();
		
		Cocktail pourCocktail = toBePoured.getCocktail();
		
		String codedPourCocktail = codePour(pourCocktail);
		pourCocktail(codedPourCocktail);
		
		pourCocktail.setQueued(false);
		pourCocktail.setPouredTrue();
	}

	private String codePour(Cocktail pourCocktail) {
		// TODO please implement me
		return null;
	}

	private void pourCocktail(String codedPourCocktail) {
		// TODO code me please!!!
		
	}

	private boolean anySpecialCommands() {
		// TODO remember - these should make sense :)
		return (specialCommand1 | specialCommand2);
	}

	private void runSpecialCommands() {
		// TODO implement
		if (specialCommand1) {
			send("specialCommand1");
		} else if (specialCommand2) {
			send("specialCommand2");
		}
	}
	
	private void send(String command) {
		// TODO send the command
	}

	public boolean serialIsReady() {
		// TODO To be implemented
		return true;
	}

}
