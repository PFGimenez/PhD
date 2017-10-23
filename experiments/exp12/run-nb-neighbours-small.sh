#!/bin/sh

(cd ../.. ; ./run.sh Recom2 cluster 1 v-naif 10 datasets/renault_small 10 -e | tee experiments/exp12/vnaif-10-small)
(cd ../.. ; ./run.sh Recom2 cluster 1 v-naif 20 datasets/renault_small 10 -e | tee experiments/exp12/vnaif-20-small)
(cd ../.. ; ./run.sh Recom2 cluster 1 v-naif 25 datasets/renault_small 10 -e | tee experiments/exp12/vnaif-25-small)
(cd ../.. ; ./run.sh Recom2 cluster 1 v-naif 30 datasets/renault_small 10 -e | tee experiments/exp12/vnaif-30-small)
(cd ../.. ; ./run.sh Recom2 cluster 1 v-naif 35 datasets/renault_small 10 -e | tee experiments/exp12/vnaif-35-small)
(cd ../.. ; ./run.sh Recom2 cluster 1 v-naif 40 datasets/renault_small 10 -e | tee experiments/exp12/vnaif-40-small)
(cd ../.. ; ./run.sh Recom2 cluster 1 v-naif 45 datasets/renault_small 10 -e | tee experiments/exp12/vnaif-45-small)
(cd ../.. ; ./run.sh Recom2 cluster 1 v-naif 50 datasets/renault_small 10 -e | tee experiments/exp12/vnaif-50-small)
(cd ../.. ; ./run.sh Recom2 cluster 1 v-naif 100 datasets/renault_small 10 -e | tee experiments/exp12/vnaif-100-small)
