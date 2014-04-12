#!/usr/bin/python
#
# Barwin Arduino Simulator
#
# Creates a virtual serial port at /dev/pts/X (name shown on startup)
# that simulates a serial connection to Barwin's Arduino.
#
# This should make testing the PC software easier as the hardware is
# not required and all scenarios can be covered easily.

import sys, os, serial, time

# https://github.com/rfjakob/barwin-arduino/blob/master/lib/utils/utils.h#L52
def ERROR(msg):
	swrite("ERROR %s\r\n" %msg)

# https://github.com/rfjakob/barwin-arduino/blob/master/lib/utils/utils.h#L29
def DEBUG_MSG_LN(msg):
	swrite("DEBUG     %s\r\n" %msg)

def MSG(msg):
	swrite("%s\r\n" %msg)

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

		swrite("POURING %d 0\r\n" % n)
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
		if(vserial.inWaiting() == 0):
			time.sleep(0.1)
			continue
		
		c = sread(50)
		if c == "RESUME\r\n":
			return 0
		elif c == "ABORT\r\n":
			return 1
		else:
			ERROR("ERROR INVAL_CMD");

# https://github.com/rfjakob/barwin-arduino/blob/master/lib/ads1231/ads1231.cpp#L274
def wait_for_cup():
	MSG("WAITING_FOR_CUP")

# Escape \r\n
def escapern(s):
	return s.replace("\r", "\\r").replace("\n", "\\n")

# Write to serial
def swrite(msg):
	print 'TX: \033[91m%s\033[0m' % escapern(msg)
	vserial.write(msg)

# Read from serial
def sread(n):
	msg = vserial.read(n)
	print 'RX: \033[92m%s\033[0m' % escapern(msg)
	return msg

master_fd, slave_fd = os.openpty()
slave_fn = os.ttyname(slave_fd)

print "Virtual terminal: %s" % slave_fn

vserial = serial.Serial()
vserial.fd = master_fd
vserial._isOpen = True
vserial.timeout = 0.05

friendly_name = "/dev/ttyS99"
if (not os.path.islink(friendly_name)) or (os.readlink(friendly_name) != slave_fn):
	print "Creating %s symlink via sudo..." % friendly_name
	ret = os.system("sudo ln -sfT %s %s" % (slave_fn, friendly_name))
	if ret != 0:
		print "Failed"
		exit(1)

print "Symlinked as:     %s" % friendly_name

while True:
	if(vserial.inWaiting() == 0):
		swrite("READY 0 0\r\n")
		time.sleep(1)
		continue
	
	c = sread(50)
	swrite("DEBUG     Got: " + c + "\r\n")
	if c.startswith('POUR '):
		c = c[5:]
		parts = c.split(" ")
		pour_all(parts)
	else:
		ERROR("ERROR INVAL_CMD");
