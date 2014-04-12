package controllers;



import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import serialRMI.*;
import play.libs.Json;
//import play.*;
//import play.cache.Cache;
import play.mvc.*;
import views.html.*;

import java.rmi.*;

import com.fasterxml.jackson.databind.node.ObjectNode;

public class Serial extends AbstractController {
	private static String serialRMIServer = "rmi://127.0.0.1:12121/serial";
	private static SerialRMIInterface genBotSerialRMIConnect() throws Exception {
		return (SerialRMIInterface) Naming.lookup(session("serialRMIServer"));
	}
	
	public static Result index() {
		if(session("serialRMIServer") == null)
			session("serialRMIServer", serialRMIServer);
		try {
			try {
				SerialRMIInterface serialRMI = genBotSerialRMIConnect();
				if(serialRMI.isConnected()) {
					return ok(serial.render());
				} else {
					return ok(serialConnect.render(serialRMI.getSerialPorts(), session("serialRMIServer")));
				}
			} catch (java.rmi.ConnectException e) {
				return ok(serialConnect.render(null,session("serialRMIServer")));
			}
		} catch (Exception e) {
			return error(e);
		}
	}
	
	public static Result setRMIServer() {
		Map<String, String[]> parameters = request().body().asFormUrlEncoded();
		if(parameters.containsKey("rmiServer"))
			session("serialRMIServer", parameters.get("rmiServer")[0]);
		return redirect("/serial");
	}
	
	public static Result connect() {
		Map<String, String[]> parameters = request().body().asFormUrlEncoded();
		try {
			SerialRMIInterface serialRMI = genBotSerialRMIConnect();
			serialRMI.connect(parameters.get("port")[0]);
			return redirect("/serial");
		} catch (Exception e) {
			return error(e);
		} 
	}

	public static Result write() {
		Map<String, String[]> parameters = request().body().asFormUrlEncoded();
		try {
			SerialRMIInterface serialRMI = genBotSerialRMIConnect();
			ObjectNode result = Json.newObject();
			String text = parameters.get("text")[0] + "\r\n";
			serialRMI.write(text);
			result.put("valid", true);
			SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
			result.put("timestamp", sdf.format(new Date().getTime()));
			result.put("string", text.replaceAll("\\r\\n", "\\\\r\\\\n"));
			return ok(result);
		} catch (Exception e) {
			return error(e);
		}
	}

	public static Result read() {
		try {
			SerialRMIInterface serialRMI = genBotSerialRMIConnect();
			ObjectNode result = Json.newObject();
			String str = serialRMI.read();
			result.put("valid", true);
			SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
			result.put("timestamp", sdf.format(new Date().getTime()));
			if(str.length() > 0) {
				result.put("string", str.replaceAll("\\r\\n", "\\\\r\\\\n"));
				
				/*GenBotProtocol.getInstance().read(str);
				for (GenBotMessage m: GenBotProtocol.getInstance().read(str)) {
					System.out.println("Command: " + m.command);
				}*/
			} else {
				result.put("string", "");
			}
			
			return ok(result);
		} catch (Exception e) {
			return errorAjax(e);
		}
	}
}
