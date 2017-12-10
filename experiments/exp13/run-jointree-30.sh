#!/bin/sh

(cd ../.. ; ./run.sh Recom2 cluster 1 jointree datasets/renault_medium 10 -e -c datasets/renault_medium/contraintes_30.xml -s 10 | tee experiments/exp13/jointree-30)
notify-send  'An experiment just completed'
