#!/bin/sh
echo Experiment on recommendation precision with different BN-learning algorithms
echo Dataset = small
(cd ../.. ; ./run.sh Recommandation drc ../experiments/exp3/renault_small_header -bn BN_hc_ | tee experiments/exp3/drc_hc_small_result)
(cd ../.. ; ./run.sh Recommandation jointree ../experiments/exp3/renault_small_header -bn BN_hc_ | tee experiments/exp3/jointree_hc_small_result)

(cd ../.. ; ./run.sh Recommandation drc ../experiments/exp3/renault_small_header -bn BN_mmhc_ | tee experiments/exp3/drc_mmhc_small_result)
(cd ../.. ; ./run.sh Recommandation jointree ../experiments/exp3/renault_small_header -bn BN_mmhc_ | tee experiments/exp3/jointree_mmhc_small_result)

(cd ../.. ; ./run.sh Recommandation drc ../experiments/exp3/renault_small_header -bn BN_tabu_ | tee experiments/exp3/drc_tabu_small_result)
(cd ../.. ; ./run.sh Recommandation jointree ../experiments/exp3/renault_small_header -bn BN_tabu_ | tee experiments/exp3/jointree_tabu_small_result)

echo Dataset = medium
(cd ../.. ; ./run.sh Recommandation drc ../experiments/exp3/renault_medium_header -bn BN_hc_ | tee experiments/exp3/drc_hc_medium_result)
(cd ../.. ; ./run.sh Recommandation jointree ../experiments/exp3/renault_medium_header -bn BN_hc_ | tee experiments/exp3/jointree_hc_medium_result)

(cd ../.. ; ./run.sh Recommandation drc ../experiments/exp3/renault_medium_header -bn BN_mmhc_ | tee experiments/exp3/drc_mmhc_medium_result)
(cd ../.. ; ./run.sh Recommandation jointree ../experiments/exp3/renault_medium_header -bn BN_mmhc_ | tee experiments/exp3/jointree_mmhc_medium_result)

(cd ../.. ; ./run.sh Recommandation drc ../experiments/exp3/renault_medium_header -bn BN_tabu_ | tee experiments/exp3/drc_tabu_medium_result)
(cd ../.. ; ./run.sh Recommandation jointree ../experiments/exp3/renault_medium_header -bn BN_tabu_ | tee experiments/exp3/jointree_tabu_medium_result)

echo Dataset = big
(cd ../.. ; ./run.sh Recommandation drc ../experiments/exp3/renault_big_header -bn BN_hc_ | tee experiments/exp3/drc_hc_big_result)
(cd ../.. ; ./run.sh Recommandation jointree ../experiments/exp3/renault_big_header -bn BN_hc_ | tee experiments/exp3/jointree_hc_big_result)

(cd ../.. ; ./run.sh Recommandation drc ../experiments/exp3/renault_big_header -bn BN_mmhc_ | tee experiments/exp3/drc_mmhc_big_result)
(cd ../.. ; ./run.sh Recommandation jointree ../experiments/exp3/renault_big_header -bn BN_mmhc_ | tee experiments/exp3/jointree_mmhc_big_result)

(cd ../.. ; ./run.sh Recommandation drc ../experiments/exp3/renault_big_header -bn BN_tabu_ | tee experiments/exp3/drc_tabu_big_result)
(cd ../.. ; ./run.sh Recommandation jointree ../experiments/exp3/renault_big_header -bn BN_tabu_ | tee experiments/exp3/jointree_tabu_big_result)
