package genBot2;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.LinkedList;
import java.util.Queue;

import serialRMI.SerialRMIException;
import serialRMI.SerialRMIInterface;

public class QueueManager extends Thread {
	private CocktailQueue queue;
	private ArduinoProtocol protocol;
	private SerialRMIInterface serial;
	private String statusClientMessage = null;
	private int statusClientCode = 0;

	Queue<ArduinoMessage> receivedMessages = new LinkedList<ArduinoMessage>();
	
	private int cocktailSizeMilliliter;
	
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

	public QueueManager(CocktailQueue queue, String server, String portName, int cocktailSizeMilliliter) throws MalformedURLException, RemoteException, NotBoundException, SerialRMIException {
		setDaemon(true);
		
		this.queue = queue;
		this.protocol = ArduinoProtocol.getInstance();
		
		this.cocktailSizeMilliliter = cocktailSizeMilliliter;
		
		if (!(server.equals("") || portName.equals(""))) {
			this.serial = (SerialRMIInterface) Naming.lookup(server);
			serial.connect(portName);
		} else {
			this.serial = null;
		}
		
		this.status = Status.unknown;
	}

	@Override
	public void run() {
		while (true) {
			try {
				if (serial == null)
					Thread.sleep(10000);
				//if (serial != null) {
				
				processSerialInput();
				//}
				if(serialIsReady()) {
					processQueue();
					//Thread.sleep(200);
				}
			} catch (SerialRMIException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
		}
	}
	
	public void processQueue() throws RemoteException, SerialRMIException {
		if (!queue.isEmpty()) {
			pourCocktail();
		}
	}
	
	private void processSerialInput() throws SerialRMIException {
		try {
			String[] sA;
			//String[] sA = {new String("READY")};
			if(serial != null) {
				sA = serial.readLines();
			} else {
				// Only for testing, always get read
				String[] sAT = {new String("READY 213 0 ")};
				sA = sAT;
			}
			
			if(sA.length == 0)
				return;

			ArduinoMessage[] messages = protocol.read(sA);
			
			for (ArduinoMessage message : messages) {
				receivedMessages.add(message);
				if(message.unknownMessage) {
					System.err.println("Unknown Message " + message.raw);
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
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void pourCocktail() throws RemoteException, SerialRMIException {
		CocktailWithName toBePoured  = queue.getAndRemoveFirstCocktail();
		Cocktail pourCocktail = toBePoured.getCocktail();
		String codedPourCocktail = codePour(pourCocktail);
		
		if (serial != null) {
			serial.writeLine(codedPourCocktail);
		}

		currentlyPouring = toBePoured;
	
		pourCocktail.setQueued(false);
		pourCocktail.setPouring(true);
		
		// ONLY FOR TESTING PURPOSES 
		if (serial == null) {
			simulatePourCocktail();		
		}
	}

	private void simulatePourCocktail() {
		Ingredient[] ings = IngredientArray.getInstance().getAllIngredients();
		try {
			statusClientCode = 1;
			statusClientMessage = "SIM: Place cup";
			Thread.sleep(3000);

			int[] milliLiters = new int[ings.length];
			for (int i = 0; i < milliLiters.length; i++) {
				milliLiters[ings[i].getArduinoOutputLine()] = (int) Math.round(currentlyPouring.getCocktail().getAmount(ings[i]) * cocktailSizeMilliliter);
			}
			
			for (int i = 0; i < milliLiters.length; i++) {
				if(milliLiters[i] > 0) {
					statusClientMessage = "Pouring ";
					// Get string of ingredient
					Ingredient ci = null;
					for(Ingredient ing : IngredientArray.getInstance().getAllIngredients()) {
						if(ing.getArduinoOutputLine() == i) {
							ci = ing;
							break;
						}
					}
					if(currentlyPouring != null && ci != null)
						statusClientMessage += ci.getName();
					
					statusClientCode = 2;
					Thread.sleep(3000);
				}
			}
			
			finishedPouring(milliLiters);
			statusClientCode = 3;
			statusClientMessage = "Take cup";
			Thread.sleep(3000);			
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	
	private void finishedPouring(int[] realValues) {
		if(currentlyPouring != null) {
			currentlyPouring.getCocktail().setPoured(true);		
			currentlyPouring.getCocktail().setPouring(false);
			
			/*
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
			milliLiters[ings[i].getArduinoOutputLine()] = (int) Math.round(pourCocktail.getAmount(ings[i]) * cocktailSizeMilliliter);
		}
		
		ArduinoMessage m = new ArduinoMessage("POUR", milliLiters);
		return m.raw;
	}
	
	public boolean serialIsReady() {
		if (status == Status.ready) {
			return true;
		} else {
			return false;
		}
	}

	public CocktailQueue getQueue() {
		return queue;
	}

	public CocktailWithName getCurrentlyPouringCocktail() {
		return currentlyPouring;
	}
	
	public int getCocktailSizeMilliliter() {
		return cocktailSizeMilliliter;
	}

	public void setCocktailSizeMilliliter(int cocktailSizeMilliliter) {
		this.cocktailSizeMilliliter = cocktailSizeMilliliter;
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
		if (serial != null) {
			serial.writeLine(s);
		}
	}
}
