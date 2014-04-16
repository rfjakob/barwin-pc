PC part of Barwin
=================
Java app controlling Barwin's evolutionary algorithm and webinterface.
See http://barwin.suuf.cc/ for more info about Barwin.

This repo merges these three repos:
* https://github.com/petres/genBotWI
* https://github.com/speendo/genBot2
* https://github.com/petres/serialRMI

Build
-----
Run make in the src/ or top directory:

	make

Run
---

Java 7 is required! 

Maybe some symlinks in the lib folder have to be adjusted to your system specific folders:

RXTX Library:

* RXTXcomm.jar -> /usr/share/java/RXTXcomm.jar
* librxtxSerial.so -> /usr/lib/jni/librxtxSerial.so

Play Framework (link to folder):

* play -> /opt/play/
 

You can start the whole system using the run-all wrapper script:

		./run-all.sh

If that does not work, start the services in this order
(Don't forget to build first!):

1. Start the Arduino simulator (enter sudo password if asked):

		bin/arduino-sim.py

2. Start the serial port service

		bin/serialRMI.sh

3. Start the evolutionary algorithm

		bin/genBot.sh

4. Start the web interface

		bin/play.sh

5. Go to web interface

	* Front-end: http://127.0.0.1:9000/interface
	* Back-end: http://127.0.0.1:9000


See Also
--------
There is a more detailed README.md in each subfolder!
