#!/bin/sh

(cd ../.. ; ./run.sh Recom2 1 v-naif constant 10 datasets/renault_big 10 -e -s 10 -c datasets/renault_big/contraintes.xml | tee experiments/exp14/vnaif-10-big-const)
(cd ../.. ; ./run.sh Recom2 1 v-naif constant 20 datasets/renault_big 10 -e -s 10 -c datasets/renault_big/contraintes.xml | tee experiments/exp14/vnaif-20-big-const)
(cd ../.. ; ./run.sh Recom2 1 v-naif constant 50 datasets/renault_big 10 -e -s 10 -c datasets/renault_big/contraintes.xml | tee experiments/exp14/vnaif-50-big-const)
(cd ../.. ; ./run.sh Recom2 1 v-naif constant 200 datasets/renault_big 10 -e -s 10 -c datasets/renault_big/contraintes.xml | tee experiments/exp14/vnaif-200-big-const)
(cd ../.. ; ./run.sh Recom2 1 v-naif constant 2000 datasets/renault_big 10 -e -s 10 -c datasets/renault_big/contraintes.xml | tee experiments/exp14/vnaif-2000-big-const)
(cd ../.. ; ./run.sh Recom2 1 v-naif inverse 1000 datasets/renault_big 10 -e -s 10 -c datasets/renault_big/contraintes.xml | tee experiments/exp14/vnaif-inverse-1000-big-const)
(cd ../.. ; ./run.sh Recom2 1 v-naif inverse 750 datasets/renault_big 10 -e -s 10 -c datasets/renault_big/contraintes.xml | tee experiments/exp14/vnaif-inverse-750-big-const)
(cd ../.. ; ./run.sh Recom2 1 jointree mmhc datasets/renault_big 10 -e -s 10 -c datasets/renault_big/contraintes.xml | tee experiments/exp14/jointree-big-const)
notify-send  'An experiment just completed'
