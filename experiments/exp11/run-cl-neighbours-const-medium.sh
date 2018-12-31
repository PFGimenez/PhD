#!/bin/sh

(cd ../.. ; ./run.sh Recom2 1 v-maj constant 20 datasets/renault_medium 10 -e -c datasets/renault_medium/contraintes.xml -s 10 | tee experiments/exp11/vmaj-cl1-const-medium-valid)
(cd ../.. ; ./run.sh Recom2 2 v-maj constant 20 datasets/renault_medium 10 -e -c datasets/renault_medium/contraintes.xml -s 10 | tee experiments/exp11/vmaj-cl2-const-medium-valid)
(cd ../.. ; ./run.sh Recom2 3 v-maj constant 20 datasets/renault_medium 10 -e -c datasets/renault_medium/contraintes.xml -s 10 | tee experiments/exp11/vmaj-cl3-const-medium-valid)
(cd ../.. ; ./run.sh Recom2 1 v-pop constant 20 datasets/renault_medium 10 -e -c datasets/renault_medium/contraintes.xml -s 10 | tee experiments/exp11/vpop-cl1-const-medium-valid)
(cd ../.. ; ./run.sh Recom2 2 v-pop constant 20 datasets/renault_medium 10 -e -c datasets/renault_medium/contraintes.xml -s 10 | tee experiments/exp11/vpop-cl2-const-medium-valid)
(cd ../.. ; ./run.sh Recom2 3 v-pop constant 20 datasets/renault_medium 10 -e -c datasets/renault_medium/contraintes.xml -s 10 | tee experiments/exp11/vpop-cl3-const-medium-valid)
(cd ../.. ; ./run.sh Recom2 1 v-naif constant 20 datasets/renault_medium 10 -e -c datasets/renault_medium/contraintes.xml -s 10 | tee experiments/exp11/vnaif-cl1-const-medium-valid)
(cd ../.. ; ./run.sh Recom2 2 v-naif constant 20 datasets/renault_medium 10 -e -c datasets/renault_medium/contraintes.xml -s 10 | tee experiments/exp11/vnaif-cl2-const-medium-valid)
(cd ../.. ; ./run.sh Recom2 3 v-naif constant 20 datasets/renault_medium 10 -e -c datasets/renault_medium/contraintes.xml -s 10 | tee experiments/exp11/vnaif-cl3-const-medium-valid)
notify-send  'An experiment just completed'
