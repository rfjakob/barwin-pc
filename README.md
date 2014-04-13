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
Run make in the top-level directory:

	make

Run
---
1. Start the Arduino simulator (enter sudo password if asked):

		./arduino-sim.py

2. Start the serial port service

		./genBotWI/lib/runSerial.sh

3. Start the evolutionary algorithm

		./genBotWI/lib/runGenBot.sh

4. Start the web interface

		./genBotWI/run.sh

5. Go to web interface

	* Front-end: http://127.0.0.1:9000/interface
	* Back-end: http://127.0.0.1:9000


See Also
--------
There is a more detailed README.md in each subfolder!
