#!/bin/sh
echo Experiment of recommendation precision and time on the constrained dataset \'insurance\'
echo Experiment with DRC
(cd ../.. ; ./run.sh ConstrainedRecom drc experiments/exp1 | tee drc-result)
echo Experiment with Bayesian network \(jointree\)
(cd ../.. ; ./run.sh ConstrainedRecom jointree | tee jointree-result)
