package models;

import java.util.Map;

import com.google.common.collect.ImmutableMap;

public class GenBotProtocol {
	static GenBotProtocol instance;
	static final Integer cBottles = 1;
	
	static final Map<String, Integer> commands = ImmutableMap.<String, Integer>builder()
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
	
	public class Message {
		
		public Message(String sc) throws Exception {
			raw = sc;
			String[] a = raw.split(" ");
			command = a[0];
			if(!commands.containsKey(command))
				throw new Exception("Command not implemented '" + raw + "'"); 

			switch (a[0]) {
				case "DEBUG":
					debug = raw.substring(command.length() + 1);
					break;
				default:
					args = new int[a.length - 1];
					for (int i = 1; i < a.length; i++)
						args[i-1] = Integer.parseInt(a[i]);
			}
		}

		public Message(String command, int[] args) {
			this.command = command;
			this.args = args;
			
			raw = command;
			
			for (int i = 0; i < args.length; i++)
				raw = " " + args[i];
		}
		public String command;
		public String raw;
		public String debug;
		public int[] args;
	}

	public static GenBotProtocol getInstance() {
		if(instance == null)
			instance = new GenBotProtocol();
		return instance;
	}

	public Message[] read(String s) throws Exception{
		String[] sca = s.split("\\r\\n");
		Message[] ma = new Message[sca.length];
		for (int i = 0; i < sca.length; i++) {
			ma[i] = new Message(sca[i]);
		}
		return ma;
	}

}
