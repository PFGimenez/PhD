#!/bin/sh

(cd ../.. ; ./run.sh Recom2 cluster 1 v-naif 35 datasets/renault_medium 10 -e -c datasets/renault_medium/contraintes_60.xml -s 10 | tee experiments/exp13/vnaif-60-35)
notify-send  'An experiment just completed'
