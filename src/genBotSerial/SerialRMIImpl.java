package genBotSerial;

import java.rmi.RemoteException;

public class SerialRMIImpl implements SerialRMIInterface {
	
	public SerialRMIImpl() {
	}

	@Override
	public String write() throws RemoteException {
		//return cocktailGeneration.getPopulation();
		return "Hallo";
	}

	public String read() throws RemoteException {
		//return cocktailGeneration.getPopulation();
		return "Hallo";
	}
}
