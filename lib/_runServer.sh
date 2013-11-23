#!/bin/bash
#rmiregistry &
#java -Djava.rmi.server.codebase=file:./genBotSerial.jar -Djava.security.policy=./serial.policy -Djava.library.path=. -cp genBotSerial.jar:/usr/share/arduino/lib/RXTXcomm.jar:/usr/lib/rx genBotSerial.Serial > serial.txt &
#java -Djava.rmi.server.codebase=file:./genBot2.jar -Djava.security.policy=./server.policy -cp sqlite-jdbc-3.7.15-M1.jar:genBot2.jar genBot2.RMIServer > server.txt &
java -cp ./serialRMI.jar:genBot2.jar:guava.jar:sqlite-jdbc-3.7.15-M1.jar genB2.RMIServer
