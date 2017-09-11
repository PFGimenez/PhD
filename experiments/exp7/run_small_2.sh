#!/bin/sh
(cd ../.. ; ./run.sh Recom2 drc datasets/renault_small 10 -e -s 10 -c datasets/renault_small/contraintes_grosse.xml | tee experiments/exp7/small_grosse_drc)
(cd ../.. ; ./run.sh Recom2 jointree datasets/renault_small 10 -e -s 10 -c datasets/renault_small/contraintes_grosse.xml | tee experiments/exp7/small_grosse_jointree)
(cd ../.. ; ./run.sh Recom2 oracle datasets/renault_small 10 -e -s 10 -c datasets/renault_small/contraintes_grosse.xml | tee experiments/exp7/small_grosse_oracle)

#(cd ../.. ; ./run.sh Recom2 drc datasets/renault_small 10 -e -s 10 -c datasets/renault_small/contraintes_30.xml | tee experiments/exp7/small_30_drc)
#(cd ../.. ; ./run.sh Recom2 jointree datasets/renault_small 10 -e -s 10 -c datasets/renault_small/contraintes_30.xml | tee experiments/exp7/small_30_jointree)
#(cd ../.. ; ./run.sh Recom2 oracle datasets/renault_small 10 -e -s 10 -c datasets/renault_small/contraintes_30.xml | tee experiments/exp7/small_30_oracle)

#(cd ../.. ; ./run.sh Recom2 drc datasets/renault_small 10 -e -s 10 -c datasets/renault_small/contraintes_60.xml | tee experiments/exp7/small_60_drc)
#(cd ../.. ; ./run.sh Recom2 jointree datasets/renault_small 10 -e -s 10 -c datasets/renault_small/contraintes_60.xml | tee experiments/exp7/small_60_jointree)
#(cd ../.. ; ./run.sh Recom2 oracle datasets/renault_small 10 -e -s 10 -c datasets/renault_small/contraintes_60.xml | tee experiments/exp7/small_60_oracle)
