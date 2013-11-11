serialRMI
============
This programm creates a RMI service, which allows you to communicate with a serial port. 
The RXTX Libary (http://rxtx.qbang.org/) is used as connector to the serial port. 

Till now the RMI service implements following functions:
* reading
* writing
* list ports
* connecting

In addition the communication can be logged to a file.


configuration file
============
Example:
``` 
rmiRegistry=true
rmiRegistryPort=12121
rmiInterface=eth0
logging=true
``` 
