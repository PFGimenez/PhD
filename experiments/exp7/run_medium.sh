#!/bin/sh
(cd ../.. ; ./run.sh Recom2 drc datasets/renault_medium 10 -e | tee experiments/exp7/medium_noconst_drc)
(cd ../.. ; ./run.sh Recom2 jointree datasets/renault_medium 10 -e | tee experiments/exp7/medium_noconst_jointree)
(cd ../.. ; ./run.sh Recom2 oracle datasets/renault_medium 10 -e | tee experiments/exp7/medium_noconst_oracle)

(cd ../.. ; ./run.sh Recom2 drc datasets/renault_medium 10 -e -c datasets/renault_medium/contraintes.xml | tee experiments/exp7/medium_const_drc)
(cd ../.. ; ./run.sh Recom2 jointree datasets/renault_medium 10 -e -c datasets/renault_medium/contraintes.xml | tee experiments/exp7/medium_const_jointree)
(cd ../.. ; ./run.sh Recom2 oracle datasets/renault_medium 10 -e -c datasets/renault_medium/contraintes.xml | tee experiments/exp7/medium_const_oracle)

(cd ../.. ; ./run.sh Recom2 drc datasets/renault_medium 10 -e -c datasets/renault_medium/contraintes-harder.xml | tee experiments/exp7/medium_const-harder_drc)
(cd ../.. ; ./run.sh Recom2 jointree datasets/renault_medium 10 -e -c datasets/renault_medium/contraintes-harder.xml | tee experiments/exp7/medium_const-harder_jointree)
(cd ../.. ; ./run.sh Recom2 oracle datasets/renault_medium 10 -e -c datasets/renault_medium/contraintes-harder.xml | tee experiments/exp7/medium_const-harder_oracle)
