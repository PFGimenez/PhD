#!/bin/sh
echo Experiment on recommendation precision of DRC with naive BN
(cd ../.. ; ./run.sh Recommandation drcnaif ../experiments/exp5/renault_small_header | tee experiments/exp5/naive_drc_small_result)
(cd ../.. ; ./run.sh Recommandation naif ../experiments/exp5/renault_small_header | tee experiments/exp5/naive_bn_small_result)

(cd ../.. ; ./run.sh Recommandation drcnaif ../experiments/exp5/renault_medium_header | tee experiments/exp5/naive_drc_medium_result)
(cd ../.. ; ./run.sh Recommandation naif ../experiments/exp5/renault_medium_header | tee experiments/exp5/naive_bn_medium_result)

(cd ../.. ; ./run.sh Recommandation drcnaif ../experiments/exp5/renault_big_header | tee experiments/exp5/naive_drc_big_result)
(cd ../.. ; ./run.sh Recommandation naif ../experiments/exp5/renault_big_header | tee experiments/exp5/naive_bn_big_result)
