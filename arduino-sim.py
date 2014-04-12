#!/usr/bin/python
#
# Barwin Arduino Simulator
#
# Creates a virtual serial port at /dev/pts/X (name shown on startup)
# that simulates a serial connection to Barwin's Arduino.
#
# This should make testing the PC software easier as the hardware is
# not required and all scenarios can be covered easily.

import os, serial, time

# https://github.com/rfjakob/barwin-arduino/blob/master/lib/utils/utils.h#L52
def ERROR(msg):
	s.write("ERROR %s\r\n" %msg)

# https://github.com/rfjakob/barwin-arduino/blob/master/lib/utils/utils.h#L29
def DEBUG_MSG_LN(msg):
	s.write("DEBUG     %s\r\n" %msg)

def MSG(msg):
	s.write("%s\r\n" %msg)

# https://github.com/rfjakob/barwin-arduino/blob/master/lib/bottle/bottle.cpp#L165
#
# Testcase: POUR 0 1 2 3 4 5 6
#           RESUME
#           ABORT
def pour_all(parts):
	n = 0
	for part in parts:
		if part == 0:
			continue

		s.write("POURING %d 0\r\n" % n)
		time.sleep(1)
		
		if n == 1:
			# https://github.com/rfjakob/barwin-arduino/blob/master/lib/errors/errors.cpp#L18
			ERROR("BOTTLE_EMPTY")
			if wait_for_resume() == 1:
				return
		
		if n == 2:
			MSG("WAITING_FOR_CUP")
			time.sleep(0.5)
		
		if n == 3:
			MSG("WAITING_FOR_CUP")
			time.sleep(1)
			ERROR("CUP_TO")
			return 1
		
		n += 1

def wait_for_resume():
	while True:
		if(s.inWaiting() == 0):
			time.sleep(0.1)
			continue
		
		c = s.read(50)
		if c == "RESUME":
			return 0
		elif c == "ABORT":
			return 1
		else:
			ERROR("ERROR INVAL_CMD");

# https://github.com/rfjakob/barwin-arduino/blob/master/lib/ads1231/ads1231.cpp#L274
def wait_for_cup():
	MSG("WAITING_FOR_CUP")
	

master_fd, slave_fd = os.openpty()
slave_fn = os.ttyname(slave_fd)

print "Created virtual terminal: %s" % slave_fn

s = serial.Serial()
s.fd = master_fd
s._isOpen = True
s.timeout = 0.05

while True:
	if(s.inWaiting() == 0):
		s.write("READY 0 0\r\n")
		time.sleep(1)
		continue
	
	c = s.read(50)
	s.write("DEBUG     Got: " + c + "\r\n")
	if c.startswith('POUR '):
		c = c[5:]
		parts = c.split(" ")
		pour_all(parts)
	else:
		ERROR("ERROR INVAL_CMD");
