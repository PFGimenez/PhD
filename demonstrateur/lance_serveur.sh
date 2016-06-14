#!/bin/sh
./wrapper.sh < p | nc -v -l -k 4242 | tee p
