#!/bin/sh

(cd ../.. ; ./run.sh Recom2 1 v-naif constant 10 datasets/renault_big 10 -e | tee experiments/exp14/vnaif-10-big)
(cd ../.. ; ./run.sh Recom2 1 v-naif constant 20 datasets/renault_big 10 -e | tee experiments/exp14/vnaif-20-big)
(cd ../.. ; ./run.sh Recom2 1 v-naif constant 50 datasets/renault_big 10 -e | tee experiments/exp14/vnaif-50-big)
(cd ../.. ; ./run.sh Recom2 1 v-naif constant 200 datasets/renault_big 10 -e | tee experiments/exp14/vnaif-200-big)
(cd ../.. ; ./run.sh Recom2 1 v-naif constant 2000 datasets/renault_big 10 -e | tee experiments/exp14/vnaif-2000-big)
(cd ../.. ; ./run.sh Recom2 1 v-naif inverse 1000 datasets/renault_big 10 -e | tee experiments/exp14/vnaif-inverse-1000-big)
(cd ../.. ; ./run.sh Recom2 1 v-naif inverse 750 datasets/renault_big 10 -e | tee experiments/exp14/vnaif-inverse-750-big)
(cd ../.. ; ./run.sh Recom2 1 jointree mmhc datasets/renault_big 10 -e | tee experiments/exp14/jointree-big)
notify-send  'An experiment just completed'
