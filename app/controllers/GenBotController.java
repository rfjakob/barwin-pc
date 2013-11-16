package controllers;

import genBot2.RemoteOrderInterface;

import java.rmi.Naming;

import com.fasterxml.jackson.databind.node.ObjectNode;

import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.error;

public class GenBotController extends Controller {
	private static String genBotRMIService = "rmi://127.0.0.1/genBot";
	protected static Result error(Exception e) {
		e.printStackTrace();
		System.out.println("EXCEPION " + e.getMessage() + " (" + e.getClass() + ")");
		return ok(error.render(e));
	}
	protected static Result errorAjax(Exception e) {
		e.printStackTrace();
		ObjectNode result = Json.newObject();
		result.put("valid", false);
		result.put("message", "EXCEPION " + e.getMessage() + " (" + e.getClass() + ")");
		return ok(result);
	}
	protected static RemoteOrderInterface genBotRMIConnect() throws Exception {
		return (RemoteOrderInterface) Naming.lookup(genBotRMIService);
	}
}
