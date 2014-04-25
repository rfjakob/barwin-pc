package genBot;

import java.io.FileInputStream;
import java.io.InputStream;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
//import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.Enumeration;
import java.util.Properties;

import serialRMI.SerialRMIException;
import serialRMI.SerialRMIInterface;

public class RMIServer {

	static int cocktailSize = 200;
	
	public static void main(String[] args) throws Exception, SerialRMIException {
	    String fileName = "../etc/genBot.config";
		Properties prop = readPropFile(fileName);
		
		QueueManager queueManager = new QueueManager(prop);

		RemoteOrderImpl rmiImpl = offerGenBotRMI(queueManager, prop);

		autoload(rmiImpl);

		SerialRMIInterface serial = connectSerialRMI(prop);
		queueManager.setSerial(serial);

		queueManager.start();
	}

	private static void autoload(RemoteOrderImpl rmiImpl) throws Exception {
		String[] cocktailTypes = rmiImpl.listPossibleEvolutionStacks();
		System.out.println("\nAuto Load Cocktail Types ... ");
		for (int i = 0; i < cocktailTypes.length; i++) {
			String cocktailType = cocktailTypes[i];
			System.out.println(" - " + cocktailType + " ");
			Properties props = EvolutionAlgorithmManager.loadProps(cocktailType);
			if(props.containsKey("autoLoad") && Integer.parseInt(props.getProperty("autoLoad")) == 1) {
				System.out.println("    AUTOLOAD");
				rmiImpl.loadEvolutionStack(cocktailType);
			} else {
				System.out.println("    SKIP");
			}
		}
		System.out.println();
	}


	private static SerialRMIInterface connectSerialRMI(Properties prop) throws Exception {
		String serialRMIAddress = "rmi://127.0.0.1:12121/serial";
		String serialPort = "auto";

	    if(prop.containsKey("serialRMIAddress"))
	    	serialRMIAddress = prop.getProperty("serialRMIAddress");
	    if(prop.containsKey("serialPort"))
	    	serialPort = prop.getProperty("serialPort");

	    System.out.println("Connecting to RMI: " + serialRMIAddress);
		SerialRMIInterface serial = (SerialRMIInterface) Naming.lookup(serialRMIAddress);

		System.out.println("Connecting to tty: " + serialPort);
		serial.connect(serialPort);

		return serial;
	}

	private static RemoteOrderImpl offerGenBotRMI(QueueManager queueManager, Properties prop) throws Exception {
		boolean rmiRegistry = true;
		int rmiRegistryPort = Registry.REGISTRY_PORT;
		String rmiServiceName = "genBot";
		String rmiInterface = null;
		
		if(prop.containsKey("rmiInterface"))
	    	rmiInterface 	= prop.getProperty("rmiInterface");
	    if(prop.containsKey("rmiRegistry"))
	    	rmiRegistry 	= Boolean.parseBoolean(prop.getProperty("rmiRegistry"));
	    if(prop.containsKey("rmiRegistryPort"))
	    	rmiRegistryPort = Integer.parseInt(prop.getProperty("rmiRegistryPort"));
	    if(prop.containsKey("rmiServiceName"))
	    	rmiServiceName = prop.getProperty("rmiServiceName");

		Registry registry;
		
		if(rmiRegistry) {
			System.out.println("Starting RMI registry on port " + rmiRegistryPort);
			registry = LocateRegistry.createRegistry(rmiRegistryPort);
			Enumeration<NetworkInterface> ifs = NetworkInterface.getNetworkInterfaces();
			String ipAddress = Inet4Address.getLocalHost().getHostAddress();
			if(rmiInterface != null) {
				System.out.println("Trying to obtain address of interface '" + rmiInterface + "'");
				System.out.print("Available interfaces: ");
				boolean found = false;
				for (Enumeration<NetworkInterface> e = ifs; e.hasMoreElements();) {
					NetworkInterface ni = e.nextElement();
					String name = ni.getDisplayName();
					System.out.print(name + " ");
					if(name.equals(rmiInterface)) {
						for (Enumeration<InetAddress> iae = ni.getInetAddresses(); iae.hasMoreElements();) {
							InetAddress ia = iae.nextElement();
							if (ia instanceof Inet4Address) {
								ipAddress = ia.getHostAddress();
								found = true;
								break;
							}
						}
					}
				}
				System.out.println();
				if(!found)
					System.out.println("Interface not found, using host address");
			} else {
				System.out.println("No interface specified in config file");
			}
			
			System.out.println("Setting java.rmi.server.hostname to: " + ipAddress);
			System.setProperty("java.rmi.server.hostname", ipAddress);
		} else {
			System.out.println("Using running RMI registry");
			registry = LocateRegistry.getRegistry();
		}

		RemoteOrderImpl rmiImpl = new RemoteOrderImpl(queueManager);
		RemoteOrderInterface stub = (RemoteOrderInterface) UnicastRemoteObject.exportObject(rmiImpl, 0);
		// RemoteServer.setLog(System.out);
		System.out.println("Starting RMI service '" + rmiServiceName + "'");
		registry.rebind(rmiServiceName, stub);

		return rmiImpl;
	}
	
	private static Properties readPropFile(String fileName) throws Exception {
		Properties prop = new Properties();
	    InputStream is = new FileInputStream(fileName);
	    prop.load(is);
	    is.close();
	    return prop;
	}
}
