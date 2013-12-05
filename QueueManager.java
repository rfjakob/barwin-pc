package genBot2;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;











import serialRMI.SerialRMIException;
import serialRMI.SerialRMIInterface; 
public class QueueManager extends Thread {

	private CocktailQueue queue;
	private GenBotProtocol protocol;
	private SerialRMIInterface serial;
	private String statusMessage = null;
	private int statusCode = 0;
	
	private int cocktailSizeMilliliter;
	
	private CocktailWithName currentlyPouring;
	
	private enum Status {
		unknown,
		ready,
		waitingForCup,
		error,
		waitingForWaitingForCup, 
		waitingForReady,
		waitingForEnjoy
	}
	
	private Status status;

	public QueueManager(CocktailQueue queue, String server, String portName, int cocktailSizeMilliliter) throws MalformedURLException, RemoteException, NotBoundException, SerialRMIException {
		setDaemon(true);
		
		this.queue = queue;
		this.protocol = GenBotProtocol.getInstance();
		
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
				//System.out.println("reading ...");
				sA = serial.readLines();
			} else {
				String[] sAT = {new String("READY 213 0 ")};
				sA = sAT;
			}
			
			if(sA.length == 0)
				return;

			GenBotMessage[] message = protocol.read(sA);
			
			for (GenBotMessage me : message) {
				System.out.println("GOT COMMANT " + me.raw);
				switch (me.command) {
				case "READY":
					// THIS IF IS ONLY NEEDED FOR TESTING
					/*if(currentlyPouring != null) {
						System.out.println("NO ENJOY RECEIVED");
						currentlyPouring.getCocktail().setPoured(true);		
						currentlyPouring.getCocktail().setPouring(false);
						//currentlyPouring = null;
					}*/
						
					System.out.println("weight: " + me.args[0] + " cup: " + me.args[1]);
					//if(status != Status.waitingForWaitingForCup)
					status = Status.ready;
					statusCode = 0;
					statusMessage = null;
					break;
				case "WAITING_FOR_CUP":
					status = Status.waitingForCup;
					statusMessage = "Waiting for cup";
					statusCode = 1;
					break;
				case "ENJOY":
					finishedPouring(me.args);
					status = Status.waitingForReady;
					statusMessage = "Take cup";
					statusCode = 3;
					break;
				case "ERROR":
					statusMessage = me.raw;
					System.out.println("ERROR" + me.raw);
					status = Status.error;
					break;
				case "POURING":
					statusMessage = "Pouring ";
					
					Ingredient ci = null;
					for(Ingredient i : IngredientArray.getInstance().getAllIngredients()) {
						if(i.getArduinoOutputLine() == me.args[0]) {
							ci = i;
							break;
							//statusMessage += i.getName();
						}
					}
					if(currentlyPouring != null && ci != null)
						statusMessage += ci.getName();
					
					statusCode = 2;
					break;
				default:
					status = Status.unknown;
					break;
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ArduinoProtocolException e) {
			// TODO Auto-generated catch block
			System.out.print("ArduinoProtocolException: ");
			System.out.println(e.getMessage());
		}
	}

	public void pourCocktail() throws RemoteException, SerialRMIException {
		CocktailWithName toBePoured  = queue.getAndRemoveFirstCocktail();
		
		Cocktail pourCocktail = toBePoured.getCocktail();
		
		String codedPourCocktail = codePour(pourCocktail);
		//System.out.println("WRITING POUR");
		if (serial != null) {
			serial.writeLine(codedPourCocktail);
		}

		currentlyPouring = toBePoured;
	
		pourCocktail.setQueued(false);
		pourCocktail.setPouring(true);
		status = Status.waitingForWaitingForCup;
		
		if (serial == null) {
			// COMMENT THIS
			try {
				statusCode = 1;
				Thread.sleep(3000);
				statusCode = 2;
				Thread.sleep(10000);
				statusCode = 3;
				Thread.sleep(3000);
				statusCode = 0;
				
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	private void finishedPouring(int[] realValues) {
		if(currentlyPouring != null) {
			currentlyPouring.getCocktail().setPoured(true);		
			currentlyPouring.getCocktail().setPouring(false);
			int mandatoryLength = IngredientArray.getInstance().getAllIngredients().length;
			
			if (realValues.length == mandatoryLength) {
				double[] realDoubles = new double[mandatoryLength];
				for (int i = 0; i < mandatoryLength; i++) {
					realDoubles[i] = realValues[i];
					
					currentlyPouring.getCocktail().changeAmounts(realDoubles);
				}
			}
			currentlyPouring = null;
		}
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
		return statusMessage;
	}
	
	public int getStatusCode() {
		return statusCode;
	}

	public void sendToSerial(String s) throws RemoteException, SerialRMIException {
		if (serial != null) {
			serial.writeLine(s);
		}
	}
}
