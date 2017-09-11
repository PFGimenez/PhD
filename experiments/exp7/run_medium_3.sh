#!/bin/sh
(cd ../.. ; ./run.sh Recom2 drc datasets/renault_medium 10 -e -s 10 -c datasets/renault_medium/contraintes_30.xml | tee experiments/exp7/medium_30_drc_s10)
(cd ../.. ; ./run.sh Recom2 jointree datasets/renault_medium 10 -e -s 10 -c datasets/renault_medium/contraintes_30.xml | tee experiments/exp7/medium_30_jointree_s10)
(cd ../.. ; ./run.sh Recom2 oracle datasets/renault_medium 10 -e -s 10 -c datasets/renault_medium/contraintes_30.xml | tee experiments/exp7/medium_30_oracle_s10)
