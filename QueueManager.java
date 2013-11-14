package genBot2;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

import serialRMI.SerialRMIInterface;
import models.ArduinoProtocolException;
import models.GenBotMessage;
import models.GenBotProtocol;

public class QueueManager extends Thread {

	private CocktailQueue queue;
	private GenBotProtocol protocol;
	private SerialRMIInterface serial;
	
	private int cocktailSizeMilliliter;
	
	private enum Status {
		unknown,
		ready,
		waitingForCup,
		error
	}
	
	private Status status;
	// TODO these two should make sense later :)
	private boolean specialCommand1;
	private boolean specialCommand2;

	public QueueManager(CocktailQueue queue, String server, String portName, int cocktailSizeMilliliter) throws Exception {
		// boooohhh!!!
		setDaemon(true);
		
		this.queue = queue;
		this.protocol = GenBotProtocol.getInstance();
		
		this.cocktailSizeMilliliter = cocktailSizeMilliliter;
		
		this.serial = (SerialRMIInterface) Naming.lookup(server);
		serial.connect(portName);
		
		this.status = Status.unknown;
		
		this.specialCommand1 = false;
		this.specialCommand2 = false;
	}

	@Override
	public void run() {
		while (true) {
			try {
				processQueue();
				Thread.sleep(200);
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ArduinoProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public void processQueue() throws RemoteException, ArduinoProtocolException, Exception {
		serialRead();
		if (!queue.isEmpty() & serialIsReady()) {
			if (anySpecialCommands()) {
				runSpecialCommands();
				yield();
			} else {
				System.out.println("jetzt schenk ich aus");
				pourCocktail();
			}
		}
	}
	
	private void serialRead() {
		GenBotMessage[] message;
		try {
			String s = serial.read();
			if(!s.isEmpty())
				message = protocol.read(s);
			else
				return;
			for (GenBotMessage me : message) {
				switch (me.command) {
				case "READY":
					status = Status.ready;
					break;
				case "WAITING_FOR_CUP":
					status = Status.waitingForCup;
					System.out.println("Wort ma am Becher!");
				case "ENJOY":
					System.out.println("Horray");
					for (int i = 0; i < me.args.length; i++) {
						System.out.print(me.args[i]);
					}
					System.out.println();
				default:
					status = Status.unknown;
					break;
				}
			}
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ArduinoProtocolException e) {
			// TODO Auto-generated catch block
			System.out.println(e.getMessage());
		}
	}

	public void pourCocktail() throws RemoteException, Exception {
		CocktailWithName toBePoured = queue.getAndRemoveFirstCocktail();
		
		Cocktail pourCocktail = toBePoured.getCocktail();
		
		String codedPourCocktail = codePour(pourCocktail);
		pourCocktail(codedPourCocktail);
		
		pourCocktail.setQueued(false);
		pourCocktail.setPouredTrue();
	}

	private String codePour(Cocktail pourCocktail) {
		Ingredient[] ings = IngredientArray.getInstance().getAllIngredients();
		
		int[] milliLiters = new int[ings.length];
		for (int i = 0; i < milliLiters.length; i++) {
			milliLiters[ings[i].getArduinoOutputLine()] = (int) Math.round(pourCocktail.getAmount(ings[i]) * cocktailSizeMilliliter);
		}
		
		GenBotMessage m = new GenBotMessage("POUR", milliLiters);
		return m.raw;
	}

	private void pourCocktail(String codedPourCocktail) throws RemoteException, Exception {
		serial.write(codedPourCocktail);
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
		if (status == Status.ready) {
			return true;
		} else {
			return false;
		}
	}

}
