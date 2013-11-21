serialRMI
============
This programm creates a RMI service, which allows you to communicate with a serial port. 
The RXTX Libary (http://rxtx.qbang.org/) is used as connector to the serial port. 

Till now the RMI service implements following functions:
* String read()
* void write(String str)
* void writeLine(String str)
* String[] readLine()
* String[] listPorts()
* void connect(String port)

The read are implemented so that they are not blocking functions. 
In addition the communication can be logged to a file.


configuration file
============
Example:
``` 
rmiRegistry=true
rmiRegistryPort=12121
rmiInterface=eth0
logging=line
``` 
Description of the configuration file options:
* rmiRegistry: if true it starts the registry service, if it is set to false, a own service will be started. 
* rmiRegistryPort: the port which is used, if it should create its own registry service
* rmiInterface: the ip address of the interface will be used to set the address of the backlink of the rmi service
* logging: options are 'line' and 'raw', in the first case the logging file will be filled with the output of the readLine and writeLine function, in case of raw it will be filled with the output of teh read und write functions, without defining this parameter no logging file will be produced
