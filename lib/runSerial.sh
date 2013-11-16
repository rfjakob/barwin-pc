#!/bin/bash
java  -Djava.library.path=. -cp serialRMI.jar:/usr/share/arduino/lib/RXTXcomm.jar serialRMI.Serial
