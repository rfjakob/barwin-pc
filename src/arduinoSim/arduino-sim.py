#!/usr/bin/python
#
# Barwin Arduino Simulator
#
# Creates a virtual serial port at /dev/pts/X (name shown on startup)
# that simulates a serial connection to Barwin's Arduino.
#
# This should make testing the PC software easier as the hardware is
# not required and all scenarios can be covered easily.

import sys
import os
import serial
import time
import re

# https://github.com/rfjakob/barwin-arduino/blob/master/lib/utils/utils.h#L52


def ERROR(msg):
    swrite("ERROR %s\r\n" % msg)

# https://github.com/rfjakob/barwin-arduino/blob/master/lib/utils/utils.h#L29


def DEBUG_MSG_LN(msg):
    swrite("DEBUG     %s\r\n" % msg)

# https://github.com/rfjakob/barwin-arduino/blob/master/lib/utils/utils.h#L50


def MSG(msg):
    swrite("%s\r\n" % msg)


def mysleep(secs):
    # Selftest should run quickly
    if selftest == False:
        time.sleep(secs)

# https://github.com/rfjakob/barwin-arduino/blob/master/lib/bottle/bottle.cpp#L165
#
# Testcase: POUR 0 1 2 3 4 5 6
#           RESUME
#           ABORT


def pour_cocktail(parts):
    bottle = 0
    global cocktails_poured
    cocktails_poured += 1

    # Cup is already on the table in 1 in 2 cases
    if cocktails_poured % 2 == 0:
        MSG("WAITING_FOR_CUP")
        mysleep(0.5)

    for part in parts:
        part = int(part)
        if part == 0:
            continue

        MSG("POURING %d 0" % bottle)
        mysleep(1)

        # Simulate empty bottle in one of 13 cocktails poured
        if bottle == 1 and cocktails_poured % 13 == 0:

            # https://github.com/rfjakob/barwin-arduino/blob/master/lib/errors/errors.cpp#L18
            ERROR("BOTTLE_EMPTY")

            if selftest:
                testserial.write("RESUME\r\n")

            if wait_for_resume() == 1:
                # Got ABORT
                return

            MSG("POURING %d 0" % bottle)
            mysleep(0.5)

        # Simulate temporarily removed cup in of of 3 cocktails poureds
        if bottle == 2 and cocktails_poured % 3 == 0:
            MSG("WAITING_FOR_CUP")
            mysleep(0.5)

        # Simulate permanently removed cup in one of 10 cocktails poureds
        if bottle == 5 and cocktails_poured % 10 == 0:
            MSG("WAITING_FOR_CUP")
            mysleep(1)
            ERROR("CUP_TO")
            return 1

        bottle += 1

    # https://github.com/rfjakob/barwin-arduino/blob/master/src/sketch.ino#L298
    MSG("ENJOY 34 12 18 20 26 33 8")


def wait_for_resume():
    while True:
        if(vserial.inWaiting() == 0):
            time.sleep(0.1)
            continue

        c = sread(50)
        if c == "RESUME\r\n":
            return 0
        elif c == "ABORT\r\n":
            return 1
        else:
            ERROR("ERROR INVAL_CMD")

# https://github.com/rfjakob/barwin-arduino/blob/master/lib/ads1231/ads1231.cpp#L274


def wait_for_cup():
    MSG("WAITING_FOR_CUP")

# Escape \r\n


def escapern(s):
    s = s.replace("\r", "\\r").replace("\n", "\\n")
    # Fainter color for final \r\n
    s = re.sub("\\\\r\\\\n$", '\033[2m\\\\r\\\\n', s)
    return s

# Write to serial


def swrite(msg):
    print 'TX: \033[31m%s\033[0m' % escapern(msg)
    vserial.write(msg)

# Read from serial


def sread(n):
    msg = vserial.read(n)
    print 'RX: \033[32m%s\033[0m' % escapern(msg)
    return msg


def dancing_bottles():
    DEBUG_MSG_LN(":D-<")
    time.sleep(0.5)
    DEBUG_MSG_LN(":D|-<")
    time.sleep(0.5)
    DEBUG_MSG_LN(":D/-<")
    time.sleep(0.5)

### main() ####

selftest = False
symlinkonly = False
bottles_nr = 7

if len(sys.argv) == 2:
    if sys.argv[1] == "selftest":
        print "Enabling selftest mode"
        selftest = True
    elif sys.argv[1] == "symlinkonly":
        symlinkonly = True
    else:
        print "Command line options: selftest, symlinkonly"
        exit(3)

# Symlink dance:
#
# /dev/ttyS99 -> intermediate_symlink -> /dev/pts/X
# ^ root only    ^ writeable by normal user
#
# The intermediate symlink is needed so we don't need root rights for
# every run.
friendly_name = "/dev/ttyS99"
intermediate_symlink = os.path.dirname(
    os.path.realpath(__file__)) + "/../../var/ttyS99"

# Create a symlink /dev/ttyS99 pointing to intermediate_symlink using sudo
# if it does not exist yet
if (not os.path.islink(friendly_name)) or (os.readlink(friendly_name) != intermediate_symlink):
    print "Creating %s symlink via sudo..." % friendly_name
    ret = os.system("sudo ln -sfT %s %s" %
                    (intermediate_symlink, friendly_name))
    if ret != 0:
        print "Failed"
        exit(1)

if symlinkonly:
    print "Symlink %s ok" % friendly_name
    exit(0)

master_fd, slave_fd = os.openpty()
slave_fn = os.ttyname(slave_fd)

print "Virtual terminal: %s" % slave_fn

vserial = serial.Serial()
vserial.fd = master_fd
vserial._isOpen = True
vserial.timeout = 0.05

ret = os.system("ln -sfT %s %s" % (slave_fn, intermediate_symlink))
if ret != 0:
    print "Failed"
    exit(1)
print "Symlinked as:     %s" % friendly_name

if selftest:
    testserial = serial.Serial("/dev/ttyS99")

cocktails_poured = -1

i = 0
while True:
    i += 1

    if i < 10:
        swrite("READY 0 0\r\n")
    else:
        swrite("READY 15 1\r\n")

    if selftest:
        testserial.write("POUR 10 20 10 30 10 0 0\r\n")
        time.sleep(0.1)  # Needs some time to get to the other end of the vtty

    if(vserial.inWaiting() == 0):
        time.sleep(1)
        continue

    c = sread(50)
    if c.startswith('POUR '):
        c = c[5:]
        parts = c.split(" ")

        if len(parts) != bottles_nr:
            DEBUG_MSG_LN("Got %d values instead of %d" %
                         (len(parts), bottles_nr))
            ERROR("INVAL_CMD")
            continue

        pour_cocktail(parts)
    elif c == 'DANCE\r\n':
        dancing_bottles()
    else:
        ERROR("INVAL_CMD")

    if selftest and cocktails_poured == 1:
        print "Selftest seems to have passed. To be sure, check the output above for sanity."
        exit(0)
