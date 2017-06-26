#!/bin/sh
(cd ../.. ; ./run.sh Recom2 drc datasets/renault_small 10 -e | tee experiments/exp7/small_noconst_drc)
(cd ../.. ; ./run.sh Recom2 jointree datasets/renault_small 10 -e | tee experiments/exp7/small_noconst_jointree)
(cd ../.. ; ./run.sh Recom2 oracle datasets/renault_small 10 -e | tee experiments/exp7/small_noconst_oracle)

(cd ../.. ; ./run.sh Recom2 drc datasets/renault_small 10 -e -c datasets/renault_small/contraintes.xml | tee experiments/exp7/small_const_drc)
(cd ../.. ; ./run.sh Recom2 jointree datasets/renault_small 10 -e -c datasets/renault_small/contraintes.xml | tee experiments/exp7/small_const_jointree)
(cd ../.. ; ./run.sh Recom2 oracle datasets/renault_small 10 -e -c datasets/renault_small/contraintes.xml | tee experiments/exp7/small_const_oracle)

(cd ../.. ; ./run.sh Recom2 drc datasets/renault_small 10 -e -c datasets/renault_small/contraintes-harder.xml | tee experiments/exp7/small_const-harder_drc)
(cd ../.. ; ./run.sh Recom2 jointree datasets/renault_small 10 -e -c datasets/renault_small/contraintes-harder.xml | tee experiments/exp7/small_const-harder_jointree)
(cd ../.. ; ./run.sh Recom2 oracle datasets/renault_small 10 -e -c datasets/renault_small/contraintes-harder.xml | tee experiments/exp7/small_const-harder_oracle)
