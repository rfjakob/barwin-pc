package serialRMI;

import gnu.io.CommPortIdentifier;
import gnu.io.SerialPort;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.IOException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
//import java.rmi.server.RemoteServer;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
//import java.util.List;

public class Serial implements SerialRMIInterface {
	// static String portName = "/dev/ttyACM0";
	static String portName = "/dev/ttyUSB0";
	static int portRate = 9600;
	static int rmiPort = 12121;
	static boolean connected = false;
	static InputStream in;
	static OutputStream out;
	static BufferedWriter file;

	public static void main(String[] args) throws Exception {
		/*
		 * try { connect(portName); } catch(Exception e) { e.printStackTrace();
		 * }
		 */

		try {
			// LocateRegistry.createRegistry(Registry.REGISTRY_PORT);
			Registry registry = LocateRegistry.createRegistry(rmiPort);
			Serial rmiImpl = new Serial();
			SerialRMIInterface stub = (SerialRMIInterface) UnicastRemoteObject
					.exportObject(rmiImpl, 0);
			// RemoteServer.setLog(System.out);
			registry.rebind("genBotSerial", stub);

		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@Override
	public void connect(String portName) throws RemoteException, Exception {
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

			// (new Thread(new SerialReader(in))).start();
			// (new Thread(new SerialWriter(out))).start();
			connected = true;

			try {
				// new SimpleDateFormat("yyyyMMddhhmm'.txt'").format(new Date()));
			    FileWriter fstream = new FileWriter("log/out.txt", true); //true tells to append data.
			    file = new BufferedWriter(fstream);
			    file.write("\nsue");
			    file.close();
			} catch (Exception e) {
				System.out.println("ERROR " + e.getMessage());
			}
		}
	}

	@Override
	public void write(String str) throws RemoteException, Exception {
		if(!connected)
			throw new Exception("Not connected!");
		// System.out.println("SENDING '" + str. + "'...");
		OutputStreamWriter writer = new OutputStreamWriter(out);
		try {
			writer.write(str);
			writer.flush();
			file.write(str);
			file.flush();
		} catch (IOException e) {
			System.out.println("Serial::write()");
			System.out.println("   - IOException occured: " + e.getMessage());
			System.out.println("   - Setting status to not connected");
			connected = false;
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
			System.out.println("Serial::read()");
			System.out.println("   - IOException occured: " + e.getMessage());
			System.out.println("   - Setting status to not connected");
			connected = false;
			//e.printStackTrace();
		}
		file.write(str);
		file.flush();
		return str;
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
