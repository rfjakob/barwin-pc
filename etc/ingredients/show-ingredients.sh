#!/bin/bash

# Make sure we are in the right directory
cd "$(dirname "$(realpath "$0")")";

grep name *.properties | sed "s/\\.properties:name=/ /"
