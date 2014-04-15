#!/bin/bash

set -e

# Make sure we are in the right directory
cd "$(dirname "$(realpath "$0")")";

# Create the /dev/ttyS99 symlink here - may ask for the sudo password.
# If we don't do it now, arduino-sim may ask for it later.
# This will delay the startup, and genBot crashes immediately as
# the serial line is not ready.
./arduino-sim.py symlinkonly

gnome-terminal -e 'bash -c "./arduino-sim.py ; exec bash"' -t arduino-sim
sleep 1
gnome-terminal -e 'bash -c "./serialRMI.sh ; exec bash"' -t serialRMI
# genBot crashes horribly if serialRMI is not ready
sleep 3
gnome-terminal -e 'bash -c "./genBot.sh ; exec bash"' -t genBot
gnome-terminal -e 'bash -c "./play.sh ; exec bash"' -t play
sleep 10
xdg-open http://127.0.0.1:9000/interface
