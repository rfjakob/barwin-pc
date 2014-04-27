#!/bin/bash

# Make sure we are in the right directory
cd "$(dirname "$(realpath "$0")")";

java -cp ../lib/genBot.jar:../lib/serialRMI.jar genBot.RMIServer
