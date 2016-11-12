#!/bin/sh
echo "Server ready. You can access it at http://127.0.0.1"
./wrapper.sh < p | nc -l -k 4242 > p #| tee p
