#!/bin/sh
(cd ../.. ; ./run.sh Recom2 drc datasets/renault_medium 10 -e -s 10 -c datasets/renault_medium/contraintes_60.xml | tee experiments/exp7/medium_60_drc_s10)
(cd ../.. ; ./run.sh Recom2 jointree datasets/renault_medium 10 -e -s 10 -c datasets/renault_medium/contraintes_60.xml | tee experiments/exp7/medium_60_jointree_s10)
(cd ../.. ; ./run.sh Recom2 oracle datasets/renault_medium 10 -e -s 10 -c datasets/renault_medium/contraintes_60.xml | tee experiments/exp7/medium_60_oracle_s10)
