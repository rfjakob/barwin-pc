package serialRMI;

import gnu.io.CommPortIdentifier;
import gnu.io.SerialPort;

import java.io.BufferedWriter;
import java.io.FileInputStream;
//import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
//import java.rmi.server.RemoteServer;
import java.rmi.server.UnicastRemoteObject;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
//import java.util.List;
import java.util.Properties;

public class Serial implements SerialRMIInterface {
	//static String portName = "/dev/ttyUSB0";
	static int portRate = 9600;
	static boolean rmiRegistry = true;
	static int rmiRegistryPort = Registry.REGISTRY_PORT;
	static String rmiServiceName = "serial";
	static String rmiInterface = null;
	static boolean connected = false;
	static InputStream in;
	static OutputStream out;
	static BufferedWriter file;
	static boolean logging = false;

	public static void main(String[] args) throws Exception {	    
		try {
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
			
			Serial rmiImpl = new Serial();
			SerialRMIInterface stub = (SerialRMIInterface) UnicastRemoteObject
					.exportObject(rmiImpl, 0);
			// RemoteServer.setLog(System.out);
			System.out.println("Starting RMI service '" + rmiServiceName + "'");
			registry.rebind(rmiServiceName, stub);
			
			if(logging) {
				try {
					String fileName = new SimpleDateFormat("yyyy-MM-dd-hh:mm:ss").format(new Date());
					System.out.println("Opening file '" + "log/" + fileName + ".txt' for logging");
				    FileWriter fstream = new FileWriter("log/" + fileName + ".txt", true);
				    file = new BufferedWriter(fstream);
				} catch (Exception e) {
					System.out.println("Error while trying to open log file " + e.getMessage());
				}
			}

		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void readProps() throws Exception {
		Properties prop = new Properties();
	    String fileName = "serialRMI.config";
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
	    if(prop.containsKey("logging"))
	    	logging = Boolean.parseBoolean(prop.getProperty("logging"));
	}

	@Override
	public void connect(String portName) throws RemoteException, Exception {
		System.out.println("Connecting to serial port " + portName);
		CommPortIdentifier portIdentifier = CommPortIdentifier
				.getPortIdentifier(portName);
		if (portIdentifier.isCurrentlyOwned()) {
			System.out.println("Error: Port is currently in use");
		} else {
			SerialPort serialPort = (SerialPort) portIdentifier.open("blup",
					2000);
			serialPort.setSerialPortParams(portRate, SerialPort.DATABITS_8,
					SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
			// serialPort.setDTR(false);

			in = serialPort.getInputStream();
			out = serialPort.getOutputStream();

			log("Connected to port " + portName, 2);
			// (new Thread(new SerialReader(in))).start();
			// (new Thread(new SerialWriter(out))).start();
			connected = true;
		}
	}

	@Override
	public void write(String str) throws RemoteException, Exception {
		if(!connected)
			throw new Exception("Not connected!");
		
		OutputStreamWriter writer = new OutputStreamWriter(out);
		try {
			writer.write(str);
			writer.flush();
			log(str, 1);
		} catch (IOException e) {
			System.err.println("Serial::write()");
			System.err.println("   - IOException occured: " + e.getMessage());
			System.err.println("   - Setting status to not connected");
			disconnect();
			//e.printStackTrace();
		}
	}

	@Override
	public String read() throws RemoteException, Exception {
		if(!connected)
			throw new Exception("Not connected!");
		String str = "";
		try {
			InputStreamReader reader = new InputStreamReader(in);
			int available = in.available();
			while (available-- > 0) {
				int c = reader.read();
				str += (char) c;
			}
		} catch (IOException e) {
			System.err.println("Serial::read()");
			System.err.println("   - IOException occured: " + e.getMessage());
			System.err.println("   - Setting status to not connected");
			disconnect();
			//e.printStackTrace();
		}
		if(str.length() > 0)
			log(str, 0);
		return str;
	}

	private static void log(String str, int i) {
		if(!logging)
			return;
		String output = "";
		switch(i) {
			case 0:
				output += "R ";
			break;
			case 1:
				output += "W ";
			break;
			case 2:
				output += "--- ";
			break;
		}
		output += new SimpleDateFormat("yyyy-MM-dd hh:mm:ss:SSS").format(new Date()) + " ";
		
		if(i == 0 || i == 1)
			str = "'" + str.replaceAll("\\r\\n", "\\\\r\\\\n") + "'";
		output +=  str + "\n";
		try {
			file.write(output);
			file.flush();
		} catch (IOException e) {
			System.err.println("Error while writing to log file");
			System.err.println("   - IOException occured: " + e.getMessage());
		}
	}

	public void disconnect() {
		connected = false;
	}
	
	@Override
	public boolean isConnected() throws RemoteException {
		return connected;
	}

	@Override
	public String[] getSerialPorts() throws RemoteException {
		//System.out.println("getSerialPorts()");
		ArrayList<String> portList = new ArrayList<String>();
		java.util.Enumeration<CommPortIdentifier> portEnum = CommPortIdentifier.getPortIdentifiers();
		while (portEnum.hasMoreElements()) {
			CommPortIdentifier portIdentifier = portEnum.nextElement();
			if(portIdentifier.getPortType() == CommPortIdentifier.PORT_SERIAL)
				portList.add(portIdentifier.getName());
			//System.out.println(portIdentifier.getName());
		}
		String[] portListA = new String[portList.size()];
		return portList.toArray(portListA);
	}

	// public static class SerialReader implements Runnable {
	// InputStream in;
	//
	// public SerialReader(InputStream in) {
	// this.in = in;
	// }
	//
	// public void run () {
	// byte[] buffer = new byte[1024];
	// int len = -1;
	// try {
	// while (( len = this.in.read(buffer)) > -1) {
	// System.out.print(new String(buffer,0,len));
	// }
	// } catch(IOException e) {
	// e.printStackTrace();
	// }
	// }
	// }
	//
	// public static class SerialWriter implements Runnable {
	// OutputStream out;
	//
	// public SerialWriter(OutputStream out) {
	// this.out = out;
	// }
	//
	// public void run () {
	// try {
	// int c = 0;
	// while((c = System.in.read()) > -1) {
	// this.out.write(c);
	// }
	// } catch(IOException e) {
	// e.printStackTrace();
	// }
	// }
	// }

}
