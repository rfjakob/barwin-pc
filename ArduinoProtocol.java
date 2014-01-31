package genBot2;


import java.util.HashMap;
import java.util.Map;

public class ArduinoProtocol {
	static ArduinoProtocol instance;
	static final Integer cBottles = 7;
	
	static public final Map<String, Integer> commands =  new HashMap<String, Integer>() {
		private static final long serialVersionUID = 1L;
	{
		put("READY"				,  2);
		put("POUR"				,  cBottles);
	    put("DEBUG"				, -1);
	    put("ENJOY"				,  cBottles);
	    put("WAITING_FOR_CUP"	,  0);
	    put("ERROR"				, -1);
	    put("START_POURING"		,  1);
	    put("BOTTLE_REFILL"		,  1);
	    put("POURING"			,  2);
	    put("TURN_BOTTLE"		,  2);
	}};
	
	public static ArduinoProtocol getInstance() {
		if(instance == null)
			instance = new ArduinoProtocol();
		return instance;
	}

	public ArduinoMessage[] read(String[] sA) {
		ArduinoMessage[] ma = new ArduinoMessage[sA.length];
		for (int i = 0; i < sA.length; i++)
			try {
				ma[i] = new ArduinoMessage(sA[i]);
			} catch (ArduinoProtocolException e) {
				ma[i] = new ArduinoMessage();
				ma[i].raw = sA[i];
				ma[i].unknownMessage = true;
			}
		return ma;
	}


}
