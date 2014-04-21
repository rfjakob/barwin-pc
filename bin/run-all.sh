#!/bin/bash

set -e

# Make sure we are in the right directory
cd "$(dirname "$(realpath "$0")")";

# Determining simulation mode using symlink name
if [ "$0" = "./run-barwin.sh" ]; then
    # exporting variable, used also in tmux!
    export SIMULATION=0

    # set correct tty path for serial connection to Arduino
    # uses Bash's autocompletion to decide between ttyACM0 and ttyACM1
    TTY_PATH=$(echo /dev/ttyACM?|sed -e 's/[\/]/\\&/g')
else
    TTY_PATH=$(echo /dev/ttyS99|sed -e 's/[\/]/\\&/g')
    export SIMULATION=1
fi
# changing tty path in config file, because there is no command line option
sed -i "s/serialPort=.*/serialPort=$TTY_PATH/" ../etc/genBot.config

# Rebuild
cd ..
make

cd bin

# Create the /dev/ttyS99 symlink here - may ask for the sudo password.
# If we don't do it now, arduino-sim may ask for it later.
# This will delay the startup, and genBot crashes immediately as
# the serial line is not ready.
if [ $SIMULATION = "1" ]; then
    ./arduino-sim.py symlinkonly
fi

if [ "$1" = "gnome-terminal" ]; then
    if [ $SIMULATION = "1" ]; then
        gnome-terminal -e 'bash -c "./arduino-sim.py"' -t arduino-sim
    fi
    sleep 1
    gnome-terminal -e 'bash -c "./serialRMI.sh"' -t serialRMI
    # genBot crashes horribly if serialRMI is not ready
    sleep 3
    gnome-terminal -e 'bash -c "./genBot.sh"' -t genBot
    gnome-terminal -e 'bash -c "./play.sh"' -t play
    sleep 10
    xdg-open http://127.0.0.1:9000/interface

else
    # use tmux
    if ! type tmux; then
        echo
        echo "tmux not installed!"
        echo "try running $0 gnome-terminal'"
        exit 1
    fi
    if type zenity; then
        zenity --info --text="Use [CTRL]+[B] [~] to kill all tmux panes" &
    fi

    # tmux occupies the main shell, so we need to run this first in a subshell
    # and sleep until all processes in the tmux window are ready
    (sleep 20; xdg-open http://127.0.0.1:9000/interface) &

    export BIN_DIR=`pwd`
    tmux -f ../etc/tmux_barwin_simulation.conf a
fi


