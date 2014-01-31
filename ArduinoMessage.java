package genBot2;

public class ArduinoMessage {
	public ArduinoMessage() {
	}
	
	public ArduinoMessage(String sc) throws ArduinoProtocolException {
		raw = sc;
		String[] a = raw.split(" ");
		command = a[0];
		if(!ArduinoProtocol.commands.containsKey(command))
			throw new ArduinoProtocolException("Command '" + raw + "' not implemented!"); 

		switch (a[0]) {
			case "ERROR": 
			case "DEBUG":
				debug = raw.substring(command.length() + 1);
				break;
			default:
				args = new int[a.length - 1];
				for (int i = 1; i < a.length; i++)
					args[i-1] = Integer.parseInt(a[i]);
		}
	}

	public ArduinoMessage(String command, int[] args) {
		this.command = command;
		this.args = args;
		
		raw = command;
		
		for (int i = 0; i < args.length; i++)
			raw += " " + args[i];
		
	}
	public boolean unknownMessage = false;
	public String command;
	public String raw;
	public String debug;
	public int[] args;
}