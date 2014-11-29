#!/usr/bin/python
"""
Integration test for Barwin Arduino Simulator
"""

import serial
import unittest
import barwinsimu
import multiprocessing
from time import sleep

class BarwinSimulationTest(unittest.TestCase):
    def setUp(self):
        # start main loop
        barwin = barwinsimu.BarwinSimulation(bottlesnr=7)
        self.barwin_process = multiprocessing.Process(target=barwin.loop)
        self.barwin_process.start()

        # open serial to barwin
        self.serial = serial.Serial(barwinsimu.VSERIAL_FRIENDLY_NAME)

    def tearDown(self):
        self.barwin_process.terminate()

    @unittest.skip("TODO")
    def test_symlinks(self):
        pass

    def test_pour(self):
        self.serial.write("POUR 10 20 10 30 10 0 0\r\n")
        sleep(10)
        # TODO make this better by parsing messages from Barwinsimulation()

    @unittest.skip("TODO")
    def test_resume(self):
        pass

    @unittest.skip("TODO")
    def test_abort(self):
        pass

    @unittest.skip("TODO")
    def test_wrong_cmd(self):
        # TODO wrong number of bottles in POUR command
        # TODO invalid command
        pass


if __name__ == '__main__':
    # run tests...
    unittest.main()
