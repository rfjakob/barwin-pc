#!/usr/bin/python
"""
Barwin Arduino Simulator

Creates a virtual serial port at /dev/pts/X (name shown on startup)
that simulates a serial connection to Barwin's Arduino.

This should make testing the PC software easier as the hardware is
not required and all scenarios can be covered easily.
"""
import os
import re
import time
import serial
import argparse
from argparse import ArgumentDefaultsHelpFormatter as better_formatter

VSERIAL_FRIENDLY_NAME = "/dev/ttyS99"

class BarwinSimulation(object):
    def __init__(self, bottlesnr):
        self.bottlesnr = bottlesnr
        self.cocktails_poured = 0

        # starte symlink dance and create serial device
        intermediate_symlink = self.create_symlinks()
        self._vserial = self._create_vserial(intermediate_symlink)

    @classmethod
    def create_symlinks(cls):
        """Symlink dance:

        /dev/ttyS99 -> intermediate_symlink -> /dev/pts/X
        ^ root only    ^ writeable by normal user

        The intermediate symlink is needed so we don't need root rights for
        every run."""
        intermediate_symlink = os.path.dirname(
            os.path.realpath(__file__)) + "/../../var/ttyS99"

        # Create a symlink /dev/ttyS99 pointing to intermediate_symlink using
        # sudo if it does not exist yet
        if (not os.path.islink(VSERIAL_FRIENDLY_NAME)) or \
                (os.readlink(VSERIAL_FRIENDLY_NAME) != intermediate_symlink):
            print "Creating %s symlink via sudo..." % VSERIAL_FRIENDLY_NAME
            ret = os.system("sudo ln -sfT %s %s" %
                            (intermediate_symlink, VSERIAL_FRIENDLY_NAME))
            if ret != 0:
                raise RuntimeError("Failed to create symlink")
        print "Symlink %s ok\n" % VSERIAL_FRIENDLY_NAME

        return intermediate_symlink

    def _create_vserial(self, intermediate_symlink):
        """Create serial device for simulated Arduino."""
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
        print "Symlinked as:     %s" % VSERIAL_FRIENDLY_NAME
        return vserial

    def ERROR(self, msg):
        """https://github.com/rfjakob/barwin-arduino/blob/master/lib/utils/utils.h#L52"""
        self.swrite("ERROR %s\r\n" % msg)

    def DEBUG_MSG_LN(self, msg):
        """https://github.com/rfjakob/barwin-arduino/blob/master/lib/utils/utils.h#L29"""
        self.swrite("DEBUG     %s\r\n" % msg)

    def MSG(self, msg):
        """https://github.com/rfjakob/barwin-arduino/blob/master/lib/utils/utils.h#L50"""
        self.swrite("%s\r\n" % msg)

    def mysleep(self, secs):
        time.sleep(secs)

    def pour_cocktail(self, parts):
        """https://github.com/rfjakob/barwin-arduino/blob/master/lib/bottle/bottle.cpp#L165

        Testcase: POUR 0 1 2 3 4 5 6
                RESUME
                ABORT
        """
        bottle = 0
        self.cocktails_poured += 1

        # Cup is already on the table in 1 in 2 cases
        if self.cocktails_poured % 2 == 0:
            self.MSG("WAITING_FOR_CUP")
            self.mysleep(0.5)

        for part in parts:
            part = int(part)
            if part == 0:
                continue

            self.MSG("POURING %d 0" % bottle)
            self.mysleep(1)

            # Simulate empty bottle in one of 13 cocktails poured
            if bottle == 1 and self.cocktails_poured % 13 == 0:

                # https://github.com/rfjakob/barwin-arduino/blob/master/lib/errors/errors.cpp#L18
                self.ERROR("BOTTLE_EMPTY")

                if self.wait_for_resume() == 1:
                    # Got ABORT
                    return

                self.MSG("POURING %d 0" % bottle)
                self.mysleep(0.5)

            # Simulate temporarily removed cup in of of 3 cocktails poureds
            if bottle == 2 and self.cocktails_poured % 3 == 0:
                self.MSG("WAITING_FOR_CUP")
                self.mysleep(0.5)

            # Simulate permanently removed cup in one of 10 cocktails poureds
            if bottle == 5 and self.cocktails_poured % 10 == 0:
                self.MSG("WAITING_FOR_CUP")
                self.mysleep(1)
                self.ERROR("CUP_TO")
                return 1

            bottle += 1

        # https://github.com/rfjakob/barwin-arduino/blob/master/src/sketch.ino#L298
        self.MSG("ENJOY 34 12 18 20 26 33 8")

    def wait_for_resume(self):
        while True:
            if not self.savailable:
                time.sleep(0.1)
                continue

            c = self.sread(50)
            if c == "RESUME\r\n":
                return 0
            elif c == "ABORT\r\n":
                return 1
            else:
                self.ERROR("ERROR INVAL_CMD")

    def wait_for_cup(self):
        """https://github.com/rfjakob/barwin-arduino/blob/master/lib/ads1231/ads1231.cpp#L274"""
        self.MSG("WAITING_FOR_CUP")

    @staticmethod
    def escapern(s):
        """Escape \r\n"""
        s = s.replace("\r", "\\r").replace("\n", "\\n")
        # Fainter color for final \r\n
        s = re.sub("\\\\r\\\\n$", '\033[2m\\\\r\\\\n', s)
        return s

    def swrite(self, msg):
        """Write to serial"""
        print 'TX: \033[31m%s\033[0m' % self.escapern(msg)
        self._vserial.write(msg)

    def sread(self, n):
        """Read from serial"""
        msg = self._vserial.read(n)
        print 'RX: \033[32m%s\033[0m' % self.escapern(msg)
        return msg

    @property
    def savailable(self):
        return self._vserial.inWaiting() > 0

    def dancing_bottles(self):
        self.DEBUG_MSG_LN(":D-<")
        time.sleep(0.5)
        self.DEBUG_MSG_LN(":D|-<")
        time.sleep(0.5)
        self.DEBUG_MSG_LN(":D/-<")
        time.sleep(0.5)

    def loop(self):
        """This is the Arduino main loop."""
        i = 0
        while True:
            i += 1

            if i < 10:
                self.swrite("READY 0 0\r\n")
            else:
                self.swrite("READY 15 1\r\n")

            if not self.savailable:
                time.sleep(1)
                continue

            cmd = self.sread(50)
            if cmd.startswith('POUR '):
                cmd = cmd[5:]
                parts = cmd.split(" ")

                if len(parts) != self.bottlesnr:
                    self.DEBUG_MSG_LN("Got %d values instead of %d" %
                                    (len(parts), self.bottlesnr))
                    self.ERROR("INVAL_CMD")
                    continue

                self.pour_cocktail(parts)
            elif cmd == 'DANCE\r\n':
                self.dancing_bottles()
            else:
                self.ERROR("INVAL_CMD")
                self.DEBUG_MSG_LN("got '%s' (len=%d)" % (cmd, len(cmd)))


if __name__ == '__main__':
    parser = argparse.ArgumentParser(formatter_class=better_formatter,
                                     description="Simulation for Barwin via"
                                     "virtual serial interface")

    parser.add_argument('--bottlesnr', metavar='N', type=int, default=7,
                        help="Numbers of available bottles")
    parser.add_argument('--symlink-only', action='store_true',
                        help="create some symlinks using sudo")

    args = parser.parse_args()

    if args.symlink_only:
        BarwinSimulation.create_symlinks()
    else:
        barwin = BarwinSimulation(bottlesnr=args.bottlesnr)
        barwin.loop()

