#!/bin/sh
echo Experiment on recommendation precision with new dataset on old BN 

echo Trimester 1
(cd ../.. ; ./run.sh Recom2 jointree experiments/exp8/renault_small_T1 10 -e -rb experiments/exp8/renault_small_T1 | tee experiments/exp8/drc_trim1_small_result)
(cd ../.. ; ./run.sh Recom2 jointree experiments/exp8/renault_small_T1 10 -e -rb experiments/exp8/renault_small_T1 | tee experiments/exp8/jointree_trim1_small_result)
(cd ../.. ; ./run.sh Recom2 oracle experiments/exp8/renault_small_T1 10 -e -rb experiments/exp8/renault_small_T1 | tee experiments/exp8/oracle_trim1_small_result)

echo Trimester 2
(cd ../.. ; ./run.sh Recom2 drc experiments/exp8/renault_small_T2 10 -e -rb experiments/exp8/renault_small_T1 | tee experiments/exp8/drc_trim2_small_result)
(cd ../.. ; ./run.sh Recom2 jointree experiments/exp8/renault_small_T2 10 -e -rb experiments/exp8/renault_small_T1 | tee experiments/exp8/jointree_trim2_small_result)
(cd ../.. ; ./run.sh Recom2 oracle experiments/exp8/renault_small_T2 10 -e -rb experiments/exp8/renault_small_T1 | tee experiments/exp8/oracle_trim2_small_result)

echo Trimester 3
(cd ../.. ; ./run.sh Recom2 drc experiments/exp8/renault_small_T3 10 -e -rb experiments/exp8/renault_small_T1 | tee experiments/exp8/drc_trim3_small_result)
(cd ../.. ; ./run.sh Recom2 jointree experiments/exp8/renault_small_T3 10 -e -rb experiments/exp8/renault_small_T1 | tee experiments/exp8/jointree_trim3_small_result)
(cd ../.. ; ./run.sh Recom2 oracle experiments/exp8/renault_small_T3 10 -e -rb experiments/exp8/renault_small_T1 | tee experiments/exp8/oracle_trim3_small_result)

echo Trimester 4
(cd ../.. ; ./run.sh Recom2 drc experiments/exp8/renault_small_T4 10 -e -rb experiments/exp8/renault_small_T1 | tee experiments/exp8/drc_trim4_small_result)
(cd ../.. ; ./run.sh Recom2 jointree experiments/exp8/renault_small_T4 10 -e -rb experiments/exp8/renault_small_T1 | tee experiments/exp8/jointree_trim4_small_result)
(cd ../.. ; ./run.sh Recom2 oracle experiments/exp8/renault_small_T4 10 -e -rb experiments/exp8/renault_small_T1 | tee experiments/exp8/oracle_trim4_small_result)

