#!/bin/sh
echo Experiment on recommendation precision with different BN-learning algorithms
echo Trimester 1
(cd ../.. ; ./run.sh Recommandation drc ../experiments/exp4/renault_small_header_part1 | tee experiments/exp4/drc_trim1_small_result)
(cd ../.. ; ./run.sh Recommandation jointree ../experiments/exp4/renault_small_header_part1 | tee experiments/exp4/jointree_trim1_small_result)

echo Trimester 2
(cd ../.. ; ./run.sh Recommandation drc ../experiments/exp4/renault_small_header_part2 | tee experiments/exp4/drc_trim2_small_result)
(cd ../.. ; ./run.sh Recommandation jointree ../experiments/exp4/renault_small_header_part2 | tee experiments/exp4/jointree_trim2_small_result)

echo Trimester 3
(cd ../.. ; ./run.sh Recommandation drc ../experiments/exp4/renault_small_header_part3 | tee experiments/exp4/drc_trim3_small_result)
(cd ../.. ; ./run.sh Recommandation jointree ../experiments/exp4/renault_small_header_part3 | tee experiments/exp4/jointree_trim3_small_result)

echo Trimester 4
(cd ../.. ; ./run.sh Recommandation drc ../experiments/exp4/renault_small_header_part4 | tee experiments/exp4/drc_trim4_small_result)
(cd ../.. ; ./run.sh Recommandation jointree ../experiments/exp4/renault_small_header_part4 | tee experiments/exp4/jointree_trim4_small_result)
