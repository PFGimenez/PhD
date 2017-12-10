#!/bin/sh

(cd ../.. ; ./run.sh Recom2 1 v-pop constant 20 datasets/renault_medium 10 -e -c datasets/renault_medium/contraintes.xml -s 10 | tee experiments/exp14/vpop-20-medium-const)
(cd ../.. ; ./run.sh Recom2 1 v-maj constant 20 datasets/renault_medium 10 -e -c datasets/renault_medium/contraintes.xml -s 10 | tee experiments/exp14/vmaj-20-medium-const)
notify-send  'An experiment just completed'
