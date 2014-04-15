#!/bin/bash

set -e

# Make sure we are in the right directory
cd "$(dirname "$(realpath "$0")")";
cd ../src/genBotWI

/opt/play/play run 80 -Djava.security.manager -Djava.security.policy=:../genBot2/bin/genBot2/my.policy
