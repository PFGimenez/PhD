#!/bin/sh

(cd ../.. ; ./run.sh Recom2 cluster 1 v-maj 20 datasets/renault_small 10 -e | tee experiments/exp11/vmaj-cl1) &
(cd ../.. ; ./run.sh Recom2 cluster 2 v-maj 20 datasets/renault_small 10 -e | tee experiments/exp11/vmaj-cl2) &
(cd ../.. ; ./run.sh Recom2 cluster 3 v-maj 20 datasets/renault_small 10 -e | tee experiments/exp11/vmaj-cl3) &
(cd ../.. ; ./run.sh Recom2 cluster 1 v-pop 20 datasets/renault_small 10 -e | tee experiments/exp11/vpop-cl1) &
(cd ../.. ; ./run.sh Recom2 cluster 2 v-pop 20 datasets/renault_small 10 -e | tee experiments/exp11/vpop-cl2) &
(cd ../.. ; ./run.sh Recom2 cluster 3 v-pop 20 datasets/renault_small 10 -e | tee experiments/exp11/vpop-cl3) &
(cd ../.. ; ./run.sh Recom2 cluster 1 v-naif 20 datasets/renault_small 10 -e | tee experiments/exp11/vnaif-cl1) &
(cd ../.. ; ./run.sh Recom2 cluster 2 v-naif 20 datasets/renault_small 10 -e | tee experiments/exp11/vnaif-cl2) &
(cd ../.. ; ./run.sh Recom2 cluster 3 v-naif 20 datasets/renault_small 10 -e | tee experiments/exp11/vnaif-cl3) &
