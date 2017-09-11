#!/bin/sh
(cd ../.. ; ./run.sh Recom2 old-drc datasets/renault_small 10 -e -s 10 | tee experiments/exp7/small_noconst_old-drc)
(cd ../.. ; ./run.sh Recom2 old-drc datasets/renault_small 10 -e -s 10 -c datasets/renault_small/contraintes.xml | tee experiments/exp7/small_const_old-drc)

