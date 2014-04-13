#!/bin/bash

# Make sure we are in the right directory
cd "$(dirname "$(realpath "$0")")";

java -cp ./serialRMI.jar:genBot2.jar:sqlite-jdbc-3.7.15-M1.jar genBot2.RMIServer
