#!/bin/sh

(cd ../.. ; ./run.sh Recom2 1 v-naif constant 20 datasets/renault_medium 10 -e ) | tee vnaif-20-medium-1
(cd ../.. ; ./run.sh Recom2 1 v-naif constant 20 datasets/renault_medium 10 -e -f 2) | tee vnaif-20-medium-2
(cd ../.. ; ./run.sh Recom2 1 v-naif constant 20 datasets/renault_medium 10 -e -f 10) | tee vnaif-20-medium-10
(cd ../.. ; ./run.sh Recom2 1 v-naif constant 20 datasets/renault_medium 10 -e -f 20) | tee vnaif-20-medium-20
(cd ../.. ; ./run.sh Recom2 1 v-naif constant 20 datasets/renault_medium 10 -e -f 50) | tee vnaif-20-medium-50
(cd ../.. ; ./run.sh Recom2 1 v-naif constant 20 datasets/renault_medium 10 -e -f 100) | tee vnaif-20-medium-100
(cd ../.. ; ./run.sh Recom2 1 jointree hc datasets/renault_medium 10 -e ) | tee jointree-medium-1
(cd ../.. ; ./run.sh Recom2 1 jointree hc datasets/renault_medium 10 -e -f 2) | tee jointree-medium-2
(cd ../.. ; ./run.sh Recom2 1 jointree hc datasets/renault_medium 10 -e -f 10) | tee jointree-medium-10
(cd ../.. ; ./run.sh Recom2 1 jointree hc datasets/renault_medium 10 -e -f 20) | tee jointree-medium-20
(cd ../.. ; ./run.sh Recom2 1 jointree hc datasets/renault_medium 10 -e -f 50) | tee jointree-medium-50
(cd ../.. ; ./run.sh Recom2 1 jointree hc datasets/renault_medium 10 -e -f 100) | tee jointree-medium-100
notify-send 'An experiment just completed'
