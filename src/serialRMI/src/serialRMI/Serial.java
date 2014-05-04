package serialRMI;

import gnu.io.CommPortIdentifier;
import gnu.io.NoSuchPortException;
import gnu.io.PortInUseException;
import gnu.io.SerialPort;
import gnu.io.UnsupportedCommOperationException;

import java.io.BufferedWriter;
import java.io.FileInputStream;
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
import java.rmi.server.UnicastRemoteObject;

import java.text.SimpleDateFormat;

import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.Properties;

public class Serial implements SerialRMIInterface {
	static int portRate = 9600;
	static boolean rmiRegistry = true;
	static int rmiRegistryPort = Registry.REGISTRY_PORT;
	static String rmiServiceName = "serial";
	static String rmiInterface = null;
	static boolean connected = false;
	static InputStream in;
	static OutputStream out;
	static BufferedWriter file;
	static String logging = "";
	static String logFolder = "log/";
	static SerialPort serialPort;

	private String readBuffer = "";
	
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

			System.out.println("Starting RMI service '" + rmiServiceName + "'");
			registry.rebind(rmiServiceName, stub);
			
			System.out.print("Available serial ports:");
			for(String port: stub.getSerialPorts())
				System.out.print(" " + port);
			System.out.println();
			
			if(!logging.isEmpty()) {
				try {
					//String fileName = new SimpleDateFormat("yyyyMMdd-HHmmss").format(new Date());
					String fileName = "serialRMI.log";
					String filePath = logFolder + "/" + fileName;
					System.out.println("Opening file '" + filePath + "' for logging (appending)");
				    FileWriter fstream = new FileWriter(filePath, true);
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
	    String fileName = "../etc/serialRMI.config";
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
	    	rmiServiceName 	= prop.getProperty("rmiServiceName");
	    if(prop.containsKey("logging"))
	    	logging 		= prop.getProperty("logging");
	   	if(prop.containsKey("logFolder"))
	    	logFolder	 	= prop.getProperty("logFolder");
	}

	@Override
	public void connect(String portName) throws RemoteException, SerialRMIException {
		System.out.println("Connecting to serial port " + portName);
		CommPortIdentifier portIdentifier;
		try {
			portIdentifier = CommPortIdentifier
					.getPortIdentifier(portName);
		} catch (NoSuchPortException e) {
			System.out.println("serialRMI: NoSuchPortException");
			throw new SerialRMIException(e);
		}
		if (portIdentifier.isCurrentlyOwned()) {
			System.out.println("Warning: Port is currently in use");
		} else {
			try {
				serialPort = (SerialPort) portIdentifier.open("blup",
						2000);
			} catch (PortInUseException e) {
				throw new SerialRMIException(e);
			}
			try {
				serialPort.setSerialPortParams(portRate, SerialPort.DATABITS_8,
						SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
			} catch (UnsupportedCommOperationException e) {
				throw new SerialRMIException(e);
			}
			// serialPort.setDTR(false);

			try {
				in = serialPort.getInputStream();
				out = serialPort.getOutputStream();
			} catch (IOException e) {
				e.printStackTrace();
			}

			log("Connected to port " + portName, 2);
			connected = true;
		}
	}

	@Override
	public void write(String str) throws RemoteException, SerialRMIException {
		if(!connected)
			throw new SerialRMIException("Not connected!");
		
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
		}
	}

	@Override
	public void writeLine(String str) throws RemoteException, SerialRMIException {
		write(str + "\r\n");
		log(str, 4);
	}

	@Override
	public String read() throws SerialRMIException {
		if(!connected)
			throw new SerialRMIException("Not connected!");
		
		String str = "";
		try {
			//InputStreamReader reader = new InputStreamReader(in);
			while (in.available() > 0) {
				str += (char) in.read();
			}
		} catch (IOException e) {
			System.err.println("Serial::read()");
			System.err.println("   - IOException occured: " + e.getMessage());
			System.err.println("   - Setting status to not connected");
			disconnect();
		}
		
		if(str.length() > 0)
			log(str, 0);
		
		return str;
	}
	
	public String[] readLines() throws RemoteException, SerialRMIException {
		// Remove \r 
		String read = read().replaceAll("\\r", "");
		if(read.isEmpty())
			return new String[0];
		
		// Combine buffered and new readed string
		String str = readBuffer + read; 
		int i = str.lastIndexOf("\n");
		
		// No newline in string
		if(i == -1) {
			readBuffer = str;
			return new String[0];
		}
		
		// Newline found but not at the end
		if(i != str.length() - 1) {
			readBuffer = str.substring(i + 1);
			str = str.substring(0, i);
		} else {
			readBuffer = "";
		}
		
		String[] sA = str.split("\\n");
		for (int j = 0; j < sA.length; j++)
			log(sA[j], 3);
		
		return sA;
	}

	private static void log(String str, int i) {
		if(file == null)
			return;

		if(logging.isEmpty())
			return;

		if(logging.equals("line") && (i == 0 || i == 1)  || logging.equals("raw") && (i == 3 || i == 4))
			return;
			
		String output = "";
		switch(i) {
			case 0:
				output += "R  ";
			break;
			case 3:
				output += "RL ";
			break;
			case 1:
				output += "W  ";
			break;
			case 4:
				output += "WL ";
			break;
			case 2:
				output += "-- ";
			break;
		}
		output += new SimpleDateFormat("yyyy-MM-dd hh:mm:ss:SSS").format(new Date()) + " ";
		
		if(i != 2) {
			str = "'" + str.replaceAll("\\n", "\\\\n").replaceAll("\\r", "\\\\r") + "'";
		}
		output +=  str + "\n";

		// Print the log to the console, too.
		System.out.print(output);

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
		if (serialPort != null) {
			log("Disconnect", 2);
	        try {
	            in.close();
	            out.close();
	        } catch (IOException ex) {}
	        serialPort.close();
	    }
	}
	
	@Override
	public boolean isConnected() throws RemoteException {
		return connected;
	}

	@Override
	@SuppressWarnings("unchecked")
	public String[] getSerialPorts() throws RemoteException {
		ArrayList<String> portList = new ArrayList<String>();
		java.util.Enumeration<CommPortIdentifier> portEnum = CommPortIdentifier.getPortIdentifiers();
		while (portEnum.hasMoreElements()) {
			CommPortIdentifier portIdentifier = portEnum.nextElement();
			if(portIdentifier.getPortType() == CommPortIdentifier.PORT_SERIAL)
				portList.add(portIdentifier.getName());
		}
		String[] portListA = new String[portList.size()];
		return portList.toArray(portListA);
	}
}
