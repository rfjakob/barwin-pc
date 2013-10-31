package genBotSerial;

import gnu.io.CommPortIdentifier;
import gnu.io.SerialPort;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.IOException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.RemoteServer;
import java.rmi.server.UnicastRemoteObject;

public class Serial {
	static String portName = "/dev/ttyACM0";
	static int portRate = 9600;
 
    public static void main(String[] args) throws Exception{
        /*try {
            connect(portName);
        } catch(Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }*/

		try {
			LocateRegistry.createRegistry( Registry.REGISTRY_PORT );
			
			SerialRMIImpl rmiImpl = new SerialRMIImpl();
			SerialRMIInterface stub = (SerialRMIInterface) UnicastRemoteObject.exportObject(rmiImpl, 0);
			RemoteServer.setLog(System.out);
			
			Registry registry = LocateRegistry.getRegistry();
		    registry.rebind( "genBotSerial", stub );

		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    public static void connect (String portName) throws Exception {
    	CommPortIdentifier portIdentifier = CommPortIdentifier.getPortIdentifier(portName);
        if(portIdentifier.isCurrentlyOwned()) {
            System.out.println("Error: Port is currently in use");
        } else {
        	SerialPort serialPort = (SerialPort) portIdentifier.open("blup",2000);
        	serialPort.setSerialPortParams(portRate,
                    SerialPort.DATABITS_8,
                    SerialPort.STOPBITS_1,
                    SerialPort.PARITY_NONE);
        	//serialPort.setDTR(false);
        	
            InputStream in = serialPort.getInputStream();
            OutputStream out = serialPort.getOutputStream();
            
            (new Thread(new SerialReader(in))).start();
            (new Thread(new SerialWriter(out))).start();
        }            
    }

	public static class SerialReader implements Runnable {
	    InputStream in;
	    
	    public SerialReader(InputStream in) {
	        this.in = in;
	    }
	    
	    public void run () {
	        byte[] buffer = new byte[1024];
	        int len = -1;
	        try {
	            while (( len = this.in.read(buffer)) > -1) {
	                System.out.print(new String(buffer,0,len));
	            }
	        } catch(IOException e) {
	            e.printStackTrace();
	        }            
	    }
	}
	
	public static class SerialWriter implements Runnable {
	    OutputStream out;
	    
	    public SerialWriter(OutputStream out) {
	        this.out = out;
	    }
	    
	    public void run () {
	        try {                
	            int c = 0;
	            while((c = System.in.read()) > -1) {
	                this.out.write(c);
	            }                
	        } catch(IOException e) {
	            e.printStackTrace();
	        }            
	    }
	}
}
