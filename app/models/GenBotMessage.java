package models;

public class GenBotMessage {
	
	public GenBotMessage(String sc) throws ArduinoProtocolException {
		raw = sc;
		String[] a = raw.split(" ");
		command = a[0];
		if(!GenBotProtocol.commands.containsKey(command))
			throw new ArduinoProtocolException("Command not implemented '" + raw + "'"); 

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

	public GenBotMessage(String command, int[] args) {
		this.command = command;
		this.args = args;
		
		raw = command;
		
		for (int i = 0; i < args.length; i++)
			raw += " " + args[i];
	}
	public String command;
	public String raw;
	public String debug;
	public int[] args;
}