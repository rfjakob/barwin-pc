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
import java.util.Enumeration;
import java.util.Properties;

import serialRMI.SerialRMIException;


public class RMIServer {
	static boolean rmiRegistry = true;
	static int rmiRegistryPort = Registry.REGISTRY_PORT;
	static String rmiServiceName = "genBot";
	static String rmiInterface = null;
	
	static String serialRMIAddress = "";
	static String serialPort = "";
	
	static RemoteOrderImpl rmiImpl;
	
	public static void main(String[] args) throws Exception, SerialRMIException {
		readProps();

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
		
		QueueManager queueManager;
		CocktailQueue queue = new CocktailQueue();	
		queueManager = new QueueManager(queue, serialRMIAddress, serialPort, 200);

		rmiImpl = new RemoteOrderImpl(queueManager);
		RemoteOrderInterface stub = (RemoteOrderInterface) UnicastRemoteObject.exportObject(rmiImpl, 0);
		// RemoteServer.setLog(System.out);
		System.out.println("Starting RMI service '" + rmiServiceName + "'");
		registry.rebind(rmiServiceName, stub);
		
		queueManager.start();
	}
	
	public static void readProps() throws Exception {
		Properties prop = new Properties();
	    String fileName = "../etc/genBot.config";
	    InputStream is = new FileInputStream(fileName);
	    prop.load(is);
	    is.close();
	    if(prop.containsKey("rmiInterface"))
	    	rmiInterface 	= prop.getProperty("rmiInterface");
	    if(prop.containsKey("rmiRegistry"))
	    	rmiRegistry 	= Boolean.parseBoolean(prop.getProperty("rmiRegistry"));
	    if(prop.containsKey("rmiRegistryPort"))
	    	rmiRegistryPort = Integer.parseInt(prop.getProperty("rmiRegistryPort"));
	    if(prop.containsKey("rmiServiceName"))
	    	rmiServiceName = prop.getProperty("rmiServiceName");
	    if(prop.containsKey("serialRMIAddress"))
	    	serialRMIAddress = prop.getProperty("serialRMIAddress");
	    if(prop.containsKey("serialPort"))
	    	serialPort = prop.getProperty("serialPort");
	}
}
