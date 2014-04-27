package genBot;

import java.net.MalformedURLException;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Properties;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

import serialRMI.SerialRMIException;
import serialRMI.SerialRMIInterface;

public class QueueManager extends Thread {
	private CocktailQueue queue;
	private ArduinoProtocol protocol;
	private SerialRMIInterface serial;
	private String statusClientMessage = null;
	private int statusClientCode = 0;

	Queue<ArduinoMessage> receivedMessages = new LinkedList<ArduinoMessage>();
	
	private CocktailWithName currentlyPouring;
	
	private enum Status {
		unknown,
		ready,
		waitingForCup,
		error,
		waitingForReady,
		waitingForEnjoy
	}
	
	private Status status;

	public QueueManager(Properties prop)
		throws MalformedURLException, RemoteException, NotBoundException, SerialRMIException {
		//setDaemon(true);

		this.queue = new CocktailQueue();	;
		this.protocol = ArduinoProtocol.getInstance();
	}

	public void setSerial(SerialRMIInterface serial) {
		this.serial = serial;
		this.status = Status.unknown;
	}

	@Override
	public void run() {
		System.out.println("RUNNING");
		while (true) {
			try {
				Thread.sleep(100);

				processSerialInput();
				
				if(status == Status.ready) 
					processQueue();
				
			} catch (Exception e) {
				e.printStackTrace();
				try { Thread.sleep(2000); } 
				catch(InterruptedException ie) {}
			}
		}
	}
	
	public void processQueue() throws RemoteException, SerialRMIException {
		if (!queue.isEmpty()) {
			pourCocktail();
		}
	}
	
	private void processSerialInput() throws SerialRMIException, RemoteException {
		String[] sA = serial.readLines();
		
		if(sA.length == 0)
			return;

		ArduinoMessage[] messages = protocol.read(sA);
		
		for (ArduinoMessage message : messages) {
			receivedMessages.add(message);
			if(message.unknownMessage) {
				System.err.println("Unknown Message: '" + message.raw + "'");
				continue;
			}
			//System.out.println("GOT COMMAND " + me.raw);
			switch (message.command) {
				case "READY":						
					//System.out.println("weight: " + message.args[0] + " cup: " + message.args[1]);
					status = Status.ready;
					statusClientCode = 0;
					statusClientMessage = null;
					break;
				case "WAITING_FOR_CUP":
					status = Status.waitingForCup;
					statusClientMessage = "Waiting for cup";
					statusClientCode = 1;
					break;
				case "ENJOY":
					finishedPouring(message.args);
					status = Status.waitingForReady;
					statusClientMessage = "Take cup";
					statusClientCode = 3;
					break;
				case "ERROR":
					statusClientMessage = message.raw;
					//System.out.println("ERROR" + message.raw);
					status = Status.error;
					break;
				case "POURING":
					statusClientMessage = "Pouring ";
					// Get string of ingredient
					Ingredient ci = null;
					for(Ingredient i : IngredientArray.getInstance().getAllIngredients()) {
						if(i.getArduinoOutputLine() == message.args[0]) {
							ci = i;
							break;
						}
					}
					if(currentlyPouring != null && ci != null)
						statusClientMessage += ci.getName();
					
					statusClientCode = 2;
					break;
				default:
					status = Status.unknown;
					break;
			}
		}
	}

	public void pourCocktail() throws RemoteException, SerialRMIException {
		CocktailWithName toBePoured  = queue.getAndRemoveFirstCocktail();
		Cocktail pourCocktail = toBePoured.getCocktail();
		
		sendToSerial(codePour(pourCocktail));

		currentlyPouring = toBePoured;
	
		pourCocktail.setQueued(false);
		pourCocktail.setPouring(true);
	}

	
	private void finishedPouring(int[] realValues) {
		if(currentlyPouring != null) {
			currentlyPouring.getCocktail().setPoured(true);		
			currentlyPouring.getCocktail().setPouring(false);
			
			/* REPLACE REAL VALUES
			int mandatoryLength = IngredientArray.getInstance().getAllIngredients().length; 
			if (realValues.length == mandatoryLength) {
				double[] realDoubles = new double[mandatoryLength];
				for (int i = 0; i < mandatoryLength; i++) {
					realDoubles[i] = realValues[i];
					
					currentlyPouring.getCocktail().changeAmounts(realDoubles);
				}
			}*/
			currentlyPouring = null;
		}
	}
	
	private String codePour(Cocktail pourCocktail) {
		Ingredient[] ings = IngredientArray.getInstance().getAllIngredients();
		
		int[] milliLiters = new int[ings.length];
		for (int i = 0; i < milliLiters.length; i++) {
			milliLiters[ings[i].getArduinoOutputLine()] = (int) Math.round(pourCocktail.getAmount(ings[i]) * GenBotConfig.cocktailSize);
		}
		
		ArduinoMessage m = new ArduinoMessage("POUR", milliLiters);
		return m.raw;
	}
	
	public CocktailQueue getQueue() {
		return queue;
	}

	public CocktailWithName getCurrentlyPouringCocktail() {
		return currentlyPouring;
	}

	public String getStatusMessage() {
		return statusClientMessage;
	}
	
	public int getStatusCode() {
		return statusClientCode;
	}

	public ArduinoMessage[] getReceivedMessagesAndClearQueue() {
		ArduinoMessage[] ama = (ArduinoMessage[]) receivedMessages.toArray();
		receivedMessages.clear();
		return ama;
	}

	public void sendToSerial(String s) throws RemoteException, SerialRMIException {
		serial.writeLine(s);
	}
}
