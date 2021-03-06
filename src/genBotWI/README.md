genBotWI
========
genBot Web Interface

Dependencies
------------
* http://www.playframework.com/download - Choose "classic", extract to /opt
* Fedora packages: yum install java-1.7.0-openjdk-devel rxtx jackson-databind
* Debian packages: apt-get install openjdk-7-jdk librxtx-java libjackson-json-java


Run the whole system
-----------------
1. start serialRMI Server (./runSerial.sh) in the lib folder, the rxtxSerial.so file should be in the lib folder, copy it there or adjust the runSerial.sh script.
There are some settings in the serialRMI.config file (set the rmiInterface to 'lo' if the genBotServer is started from the same computer). this server writes a log file with a timestamp in the (relative, has to exist) log folder. 
run the rmiServer after connected arduino.

2. start genBotServer 
the script ./runGenBot.sh in the lib folder starts the genetic algorithm and the queue for darwin. Adjust the ./genBot.config file so that the serialRMIAddress finds the serialRMI Server (serialRMIAddress), the serialPort has to be the serial port on which the arduino is connected, it is listed on the std output of the ./runSerial.sh.
Here also the rmiInterface should be lo, if the play framework is running on the same computer. 
to fully be functioning, some (relative) folders should exist, namely: dataBases, evolutionStackSettings and ingredients. in the last mentioned, there should eist some ingredients property file.

3. start the play framework
note you have it start it with sudo if you have done it once. simple call 'sudo rel/path/to/play "run 80"' in the genBotWI. the web interface will be loaded after a short time. after the first http requests some files will be compiled. use play compile, to do it only once. it connects to the genBotServer on each http request, so it is not necessary to start the play framework after the genBotServer has been restarted.

the framework provides three urls
localhost:port/interface   --- user interface
localhost:port/            --- admin interface
localhost:serial/	   --- serial inerface (note reading is disabled, only writing is working, for reading use the log file of the rmiServer)


4. load or create some evolution stacks in the admin interface

5. running, the user interface should show these evolution stacks

notes: the rmi address of the genBotServer is hardcoded in the AbstractController of the play framework, please donot change it in genBot.config



After compiling the genBotServer
-------------
1. copy the produced jar file to the lib folder
2. restart the genBotServer
3. restarting the play framework is only necessary if the changes are used through replied objects of the web server site

notes: the serialRMI server does not have to be restarted. after the reconnection the serialRMI will produce an error like 'ERROR already connected', simple ignore it.


Changing files of the web system
-------------
does not require any restart, the files will be automatically compiled by the play framework, changes css and js files under the public folder will be effective immediatly.

 

Protocol errors
-----------
if some protocol errors are occuring on import messages like enjoy, try the send messages over the serial interface to get the arduino back to the ready state and reload the user interface, if it doesnt react to the observed ready state of the arduino, in the brower (without any hash tag # in the url). 
if it blocks the rating, do the rating afterwards in the admin interface

Serial connection interruption
----------
should not happing. after it you have to:
1. stop the genBotServer (lots of exception are outputed by the genBotServer) 
2. restart the rmiServer
3. restart the genBotServer (note that the stacks have to be loaded again. note also that sometimes the serialPort will be different after such interrputions, so adjust if necessary the genBot.config)

