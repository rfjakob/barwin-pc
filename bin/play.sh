#!/bin/bash

set -e

# Make sure we are in the right directory
cd "$(dirname "$(realpath "$0")")";
cd ../src/genBotWI

../../lib/play/play run
