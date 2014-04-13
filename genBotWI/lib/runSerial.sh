#!/bin/bash

# Make sure we are in the right directory
cd "$(dirname "$(realpath "$0")")"

if [ -f /usr/share/arduino/lib/RXTXcomm.jar ]
then
	RXTXJAR=/usr/share/arduino/lib/RXTXcomm.jar
elif [ -f /usr/share/java/RXTXcomm.jar ]
then
	RXTXJAR=/usr/share/java/RXTXcomm.jar
else
	echo "Could not find RXTX library RXTXcomm.jar"
	exit 1
fi

echo "Using RXTX library at $RXTXJAR"

java  -Djava.library.path=. -cp serialRMI.jar:$RXTXJAR serialRMI.Serial
