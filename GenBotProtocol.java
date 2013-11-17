package genBot2;

import java.util.Map;

import com.google.common.collect.ImmutableMap;

public class GenBotProtocol {
	static GenBotProtocol instance;
	static final Integer cBottles = 4;
	
	static public final Map<String, Integer> commands = ImmutableMap.<String, Integer>builder()
			.put("READY"			,  0)
			.put("POUR"				,  cBottles)
		    .put("DEBUG"			, -1)
		    .put("ENJOY"			,  cBottles)
		    .put("WAITING_FOR_CUP"	,  0)
		    .put("ERROR"			, -1)
		    .put("START_POURING"	,  1)
		    .put("BOTTLE_REFILL"	,  1)
		    .put("POURING"			, -1)
		    .build();

	public static GenBotProtocol getInstance() {
		if(instance == null)
			instance = new GenBotProtocol();
		return instance;
	}

	public GenBotMessage[] read(String s) throws ArduinoProtocolException{
		String[] sca = s.split("\\r\\n");
		GenBotMessage[] ma = new GenBotMessage[sca.length];
		for (int i = 0; i < sca.length; i++) {
			ma[i] = new GenBotMessage(sca[i]);
		}
		return ma;
	}


}
