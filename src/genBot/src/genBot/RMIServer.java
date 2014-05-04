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
import java.util.Scanner;

import serialRMI.SerialRMIException;
import serialRMI.SerialRMIInterface;

public class RMIServer {
	
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
		System.out.println("--------------------------------");
	    System.out.println("--- AUTOLOADING GENERATIONS ----");
	    System.out.println("--------------------------------");

		String[] cocktailTypes = rmiImpl.listPossibleEvolutionStacks();
		for(String cocktailType: cocktailTypes) {
			System.out.print("- " + String.format("%-30s", cocktailType));
			Properties props = EvolutionAlgorithmManager.loadProps(cocktailType);
			if(Boolean.parseBoolean(props.getProperty("autoLoad"))) {
				try {
					rmiImpl.loadEvolutionStack(cocktailType);
					System.out.println("    AUTOLOAD");
				} catch (GeneratingRandomCocktailsException e)
				{
					System.out.println("    ERROR");
					e.printStackTrace();
				}
			} else {
				System.out.println("    SKIP");
			}
		}
		System.out.println("--------------------------------\n\n\n");
	}


	private static SerialRMIInterface connectSerialRMI(Properties prop) throws Exception {
		String serialRMIAddress = "rmi://127.0.0.1:12121/serial";
		String serialPort = "auto";

	    if(prop.containsKey("serialRMIAddress"))
	    	serialRMIAddress = prop.getProperty("serialRMIAddress");
	    if(prop.containsKey("serialPort"))
	    	serialPort = prop.getProperty("serialPort");

	    System.out.println("--------------------------------");
	    System.out.println("--- SERIAL RMI CONNECTION ------");
	    System.out.println("--------------------------------");
	    System.out.println("- CONFIG VAULES ----------------");
	    System.out.println("- RMI Server: " + serialRMIAddress);
	    System.out.println("- Serial Port: " + serialPort);
	    System.out.println("--------------------------------");
	    System.out.println("- RMI SERVER -------------------");
	    System.out.print(  "- Connecting ... ");
		SerialRMIInterface serial = (SerialRMIInterface) Naming.lookup(serialRMIAddress);
		System.out.println("Done");
		System.out.println("--------------------------------");
		System.out.println("- SERIAL PORT ------------------");
		if(serial.isConnected()) {
			System.out.println("- Already connected!");
		} else {
			String[] availablePorts = serial.getSerialPorts();
			if(availablePorts.length > 0) {
				System.out.println("- Available ports: ");
				System.out.println("-----");
				int i = 0;
				boolean exist = false; 
				for(String tName: availablePorts) {
					i++;
					System.out.println("- " + i + ": " + tName);
					if (serialPort.equals(tName)) 
						exist = true;
				}
				System.out.println("-----");
				if(!exist && !serialPort.equals("select")) 
					System.out.println("- Serial port " + serialPort + " not available");
				
				if(!exist) {
					System.out.print("- Use number to specify: ");
					Scanner scanner = new Scanner(System.in);
					if(scanner.hasNextInt()) {
						serialPort = availablePorts[scanner.nextInt() - 1];
					}
					scanner.close();
					System.out.println();
					System.out.println("-----");
				}
				

			} else {
				System.out.println("- No serial port available");
				System.out.println("------------------------------------");
				throw new SerialRMIException("ERROR NO SERIAL PORT");
			}

			System.out.print(  "- Connecting to " + serialPort + " ... ");
			serial.connect(serialPort);
			System.out.println("Done");
		}
		System.out.println("--------------------------------\n\n\n");

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

	    System.out.println("--------------------------------");
	    System.out.println("--- OFFER GENBOT RMI CONNECTION ");
	    System.out.println("--------------------------------");
	    System.out.println("- CONFIG VAULES ----------------");
	    System.out.println("- Interface: " + rmiInterface);
	    System.out.println("- Registry: " + rmiRegistry);
	    System.out.println("- Registry Port: " + rmiRegistryPort);
	    System.out.println("- Service Name: " + rmiServiceName);
	    System.out.println("--------------------------------");

		Registry registry;
		
		if(rmiRegistry) {
			System.out.println("- Starting RMI registry on port " + rmiRegistryPort);
			registry = LocateRegistry.createRegistry(rmiRegistryPort);
			Enumeration<NetworkInterface> ifs = NetworkInterface.getNetworkInterfaces();
			String ipAddress = Inet4Address.getLocalHost().getHostAddress();
			if(rmiInterface != null) {
				System.out.println("- Trying to obtain address of interface '" + rmiInterface + "'");
				System.out.print("- Available interfaces: ");
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
					System.out.println("- Interface not found, using host address");
			} else {
				System.out.println("- No interface specified in config file");
			}
			
			System.out.println("- Setting java.rmi.server.hostname to: " + ipAddress);
			System.setProperty("- java.rmi.server.hostname", ipAddress);
		} else {
			System.out.println("- Using running RMI registry");
			registry = LocateRegistry.getRegistry();
		}

		RemoteOrderImpl rmiImpl = new RemoteOrderImpl(queueManager);
		RemoteOrderInterface stub = (RemoteOrderInterface) UnicastRemoteObject.exportObject(rmiImpl, 0);
		// RemoteServer.setLog(System.out);
		System.out.println("- Starting RMI service '" + rmiServiceName + "'");
		registry.rebind(rmiServiceName, stub);

		System.out.println("--------------------------------\n\n\n");

		return rmiImpl;
	}
	
	private static Properties readPropFile(String fileName) throws Exception {
		Properties prop = new Properties();
	    InputStream is = new FileInputStream(fileName);
	    prop.load(is);
	    is.close();

		if(prop.containsKey("ingredientsPath"))
	    	GenBotConfig.ingredientsPath = prop.getProperty("ingredientsPath");
	    if(prop.containsKey("storePath"))
	    	GenBotConfig.storePath = prop.getProperty("storePath"); 
	    if(prop.containsKey("stackPath"))
	    	GenBotConfig.stackPath = prop.getProperty("stackPath");

		if(prop.containsKey("cocktailSize"))
	    	GenBotConfig.cocktailSize = Integer.parseInt(prop.getProperty("cocktailSize"));

	    return prop;
	}
}
