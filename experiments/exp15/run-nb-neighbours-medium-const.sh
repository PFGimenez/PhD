#!/bin/sh

#(cd ../.. ; ./run.sh Recom2 1 v-naif constant 20 datasets/renault_medium 10 -e -c datasets/renault_medium/contraintes.xml -s 10 ) | tee vnaif-20-medium-const-1
#(cd ../.. ; ./run.sh Recom2 1 v-naif constant 20 datasets/renault_medium 10 -e -c datasets/renault_medium/contraintes.xml -s 10 -f 2) | tee vnaif-20-medium-const-2
#(cd ../.. ; ./run.sh Recom2 1 v-naif constant 20 datasets/renault_medium 10 -e -c datasets/renault_medium/contraintes.xml -s 10 -f 10) | tee vnaif-20-medium-const-10
#(cd ../.. ; ./run.sh Recom2 1 v-naif constant 20 datasets/renault_medium 10 -e -c datasets/renault_medium/contraintes.xml -s 10 -f 20) | tee vnaif-20-medium-const-20
#(cd ../.. ; ./run.sh Recom2 1 v-naif constant 20 datasets/renault_medium 10 -e -c datasets/renault_medium/contraintes.xml -s 10 -f 50) | tee vnaif-20-medium-const-50
#(cd ../.. ; ./run.sh Recom2 1 v-naif constant 20 datasets/renault_medium 10 -e -c datasets/renault_medium/contraintes.xml -s 10 -f 100) | tee vnaif-20-medium-const-100
(cd ../.. ; ./run.sh Recom2 1 jointree hc datasets/renault_medium 10 -e -c datasets/renault_medium/contraintes.xml -s 10 ) | tee jointree-medium-const-1
(cd ../.. ; ./run.sh Recom2 1 jointree hc datasets/renault_medium 10 -e -c datasets/renault_medium/contraintes.xml -s 10 -f 2) | tee jointree-medium-const-2
(cd ../.. ; ./run.sh Recom2 1 jointree hc datasets/renault_medium 10 -e -c datasets/renault_medium/contraintes.xml -s 10 -f 10) | tee jointree-medium-const-10
(cd ../.. ; ./run.sh Recom2 1 jointree hc datasets/renault_medium 10 -e -c datasets/renault_medium/contraintes.xml -s 10 -f 20) | tee jointree-medium-const-20
(cd ../.. ; ./run.sh Recom2 1 jointree hc datasets/renault_medium 10 -e -c datasets/renault_medium/contraintes.xml -s 10 -f 50) | tee jointree-medium-const-50
(cd ../.. ; ./run.sh Recom2 1 jointree hc datasets/renault_medium 10 -e -c datasets/renault_medium/contraintes.xml -s 10 -f 100) | tee jointree-medium-const-100
notify-send 'An experiment just completed'
