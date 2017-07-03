#!/bin/sh
echo Experiment of recommendation precision and time on the constrained dataset \'insurance\'
echo Experiment with DRC
(cd ../.. ; ./run.sh ConstrainedRecom drc experiments/exp1 0 10 | tee drc-0)
(cd ../.. ; ./run.sh ConstrainedRecom drc experiments/exp1 1 10 | tee drc-1)
(cd ../.. ; ./run.sh ConstrainedRecom drc experiments/exp1 2 10 | tee drc-2)
echo Experiment with Bayesian network \(jointree\)
(cd ../.. ; ./run.sh ConstrainedRecom jointree 0 10 | tee jointree-1)
(cd ../.. ; ./run.sh ConstrainedRecom jointree 1 10 | tee jointree-2)
(cd ../.. ; ./run.sh ConstrainedRecom jointree 2 10 | tee jointree-3)
