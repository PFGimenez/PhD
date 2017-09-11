#!/bin/sh
echo Experiment on recommendation precision with new dataset on old BN 

echo Trimester 2
(cd ../.. ; ./run.sh Recom2 drc experiments/exp8/renault_small_T2 10 -s 10 -e -rb experiments/exp8/ -x 1 experiments/exp8/renault_small_T1 | tee experiments/exp8/drc_trim2_small_result_cumul)
(cd ../.. ; ./run.sh Recom2 jointree experiments/exp8/renault_small_T2 10 -s 10 -e -rb experiments/exp8/ -x 1 experiments/exp8/renault_small_T1 | tee experiments/exp8/jointree_trim2_small_result_cumul)
(cd ../.. ; ./run.sh Recom2 oracle experiments/exp8/renault_small_T2 10 -s 10 -e -rb experiments/exp8/ -x 1 experiments/exp8/renault_small_T1 | tee experiments/exp8/oracle_trim2_small_result_cumul)

echo Trimester 3
(cd ../.. ; ./run.sh Recom2 drc experiments/exp8/renault_small_T3 10 -s 10 -e -rb experiments/exp8/ -x 2 experiments/exp8/renault_small_T1/ experiments/exp8/renault_small_T2/ | tee experiments/exp8/drc_trim3_small_result_cumul)
(cd ../.. ; ./run.sh Recom2 jointree experiments/exp8/renault_small_T3 10 -s 10 -e -rb experiments/exp8/ -x 2 experiments/exp8/renault_small_T1/ experiments/exp8/renault_small_T2/ | tee experiments/exp8/jointree_trim3_small_result_cumul)
(cd ../.. ; ./run.sh Recom2 oracle experiments/exp8/renault_small_T3 10 -s 10 -e -rb experiments/exp8/ -x 2 experiments/exp8/renault_small_T1/ experiments/exp8/renault_small_T2/ | tee experiments/exp8/oracle_trim3_small_result_cumul)

echo Trimester 4
(cd ../.. ; ./run.sh Recom2 drc experiments/exp8/renault_small_T4 10 -s 10 -e -rb experiments/exp8/ -x 3 experiments/exp8/renault_small_T1/ experiments/exp8/renault_small_T2 experiments/exp8/renault_small_T3 | tee experiments/exp8/drc_trim4_small_result_cumul)
(cd ../.. ; ./run.sh Recom2 jointree experiments/exp8/renault_small_T4 10 -s 10 -e -rb experiments/exp8/ -x 3 experiments/exp8/renault_small_T1/ experiments/exp8/renault_small_T2 experiments/exp8/renault_small_T3 | tee experiments/exp8/jointree_trim4_small_result_cumul)
(cd ../.. ; ./run.sh Recom2 oracle experiments/exp8/renault_small_T4 10 -s 10 -e -rb experiments/exp8/ -x 3 experiments/exp8/renault_small_T1/ experiments/exp8/renault_small_T2 experiments/exp8/renault_small_T3 | tee experiments/exp8/oracle_trim4_small_result_cumul)

