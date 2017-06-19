#!/bin/sh
echo Experiment on recommendation precision of DRC with naive BN with constraints
(cd ../.. ; ./run.sh Recommandation drcnaif ../experiments/exp6/csp0 -c experiments/exp6/randomCSP-0.xml -e | tee experiments/exp6/naive_drc_csp0_result)
(cd ../.. ; ./run.sh Recommandation naif ../experiments/exp6/csp0 -c experiments/exp6/randomCSP-0.xml -e | tee experiments/exp6/naive_bn_csp0_result)

(cd ../.. ; ./run.sh Recommandation drcnaif ../experiments/exp6/csp1 -c experiments/exp6/randomCSP-1.xml -e | tee experiments/exp6/naive_drc_csp1_result)
(cd ../.. ; ./run.sh Recommandation naif ../experiments/exp6/csp1 -c experiments/exp6/randomCSP-1.xml -e | tee experiments/exp6/naive_bn_csp1_result)

(cd ../.. ; ./run.sh Recommandation drcnaif ../experiments/exp6/csp2 -c experiments/exp6/randomCSP-2.xml -e | tee experiments/exp6/naive_drc_csp2_result)
(cd ../.. ; ./run.sh Recommandation naif ../experiments/exp6/csp2 -c experiments/exp6/randomCSP-2.xml -e | tee experiments/exp6/naive_bn_csp2_result)
