package genBot2;

import java.util.Map;

import com.google.common.collect.ImmutableMap;

public class GenBotProtocol {
	static GenBotProtocol instance;
	static final Integer cBottles = 7;
	
	static public final Map<String, Integer> commands = ImmutableMap.<String, Integer>builder()
			.put("READY"			,  2)
			.put("POUR"				,  cBottles)
		    .put("DEBUG"			, -1)
		    .put("ENJOY"			,  cBottles)
		    .put("WAITING_FOR_CUP"	,  0)
		    .put("ERROR"			, -1)
		    .put("START_POURING"	,  1)
		    .put("BOTTLE_REFILL"	,  1)
		    .put("POURING"			,  2)
		    .put("TURN_BOTTLE"		,  2)
		    .build();

	public static GenBotProtocol getInstance() {
		if(instance == null)
			instance = new GenBotProtocol();
		return instance;
	}

	public GenBotMessage[] read(String[] sA) throws ArduinoProtocolException{
		GenBotMessage[] ma = new GenBotMessage[sA.length];
		for (int i = 0; i < sA.length; i++)
			ma[i] = new GenBotMessage(sA[i]);
		return ma;
	}


}
