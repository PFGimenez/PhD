#!/bin/sh

(cd ../.. ; ./run.sh Recom2 1 naif datasets/renault_medium 10 -e -c datasets/renault_medium/contraintes.xml -s 10 | tee experiments/exp11/naif-cl1-const-medium-valid)
(cd ../.. ; ./run.sh Recom2 2 naif datasets/renault_medium 10 -e -c datasets/renault_medium/contraintes.xml -s 10 | tee experiments/exp11/naif-cl2-const-medium-valid)
(cd ../.. ; ./run.sh Recom2 3 naif datasets/renault_medium 10 -e -c datasets/renault_medium/contraintes.xml -s 10 | tee experiments/exp11/naif-cl3-const-medium-valid)
(cd ../.. ; ./run.sh Recom2 1 jointree hc datasets/renault_medium 10 -e -c datasets/renault_medium/contraintes.xml -s 10 | tee experiments/exp11/jointree-cl1-const-medium-valid)
(cd ../.. ; ./run.sh Recom2 2 jointree hc datasets/renault_medium 10 -e -c datasets/renault_medium/contraintes.xml -s 10 | tee experiments/exp11/jointree-cl2-const-medium-valid)
(cd ../.. ; ./run.sh Recom2 3 jointree hc datasets/renault_medium 10 -e -c datasets/renault_medium/contraintes.xml -s 10 | tee experiments/exp11/jointree-cl3-const-medium-valid)
notify-send  'An experiment just completed'
