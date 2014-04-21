PC part of Barwin
=================
Java app controlling Barwin's evolutionary algorithm and webinterface.
See http://barwin.suuf.cc/ for more info about Barwin.

This repo merges these three repos:
* https://github.com/petres/genBotWI
* https://github.com/speendo/genBot2
* https://github.com/petres/serialRMI


Dependencies
------------

Java 7 is required!

Install some packages:
* Fedora: sudo yum install java-1.7.0-openjdk-devel rxtx guava
* Debian: sudo apt-get install openjdk-7-jdk librxtx-java libguava-java


Maybe some symlinks in the lib folder have to be adjusted to your system specific folders:

RXTX Library:

* RXTXcomm.jar -> /usr/share/java/RXTXcomm.jar
* librxtxSerial.so -> /usr/lib/jni/librxtxSerial.so

Play Framework:

If your distro does not offer any ready package, download (see http://www.playframework.com/download, tested with v2.2.2) the Play Framework and extract it to /opt/play. If the exectuable is not located at `/opt/play/play` you can adapt the symlink in `barwin-pc/lib/play`.


Build
-----
Run make in the src/ or top directory:

	make


Run
---
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
