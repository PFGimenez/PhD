#!/bin/sh
echo Experiment of recommendation precision and time on the constrained dataset \'insurance\'
#echo Experiment with DRC
#(cd ../.. ; ./run.sh ConstrainedRecom drc experiments/exp1 0 10 -c 5 | tee drc-0)
#(cd ../.. ; ./run.sh ConstrainedRecom drc experiments/exp1 1 10 -c 5 | tee drc-1)
#(cd ../.. ; ./run.sh ConstrainedRecom drc experiments/exp1 2 10 -c 5 | tee drc-2)
echo Experiment with Bayesian network \(jointree\)
(cd ../.. ; ./run.sh ConstrainedRecom jointree experiments/exp1 0 10 -c 5 | tee experiments/exp1/jointree-0)
(cd ../.. ; ./run.sh ConstrainedRecom jointree experiments/exp1 1 10 -c 5 | tee experiments/exp1/jointree-1)
(cd ../.. ; ./run.sh ConstrainedRecom jointree experiments/exp1 2 10 -c 5 | tee experiments/exp1/jointree-2)
