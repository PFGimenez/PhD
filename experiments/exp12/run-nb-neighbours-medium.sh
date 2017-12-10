#!/bin/sh

(cd ../.. ; ./run.sh Recom2 cluster 1 v-naif 10 datasets/renault_medium 10 -e | tee experiments/exp12/vnaif-10-medium)
(cd ../.. ; ./run.sh Recom2 cluster 1 v-naif 20 datasets/renault_medium 10 -e | tee experiments/exp12/vnaif-20-medium)
(cd ../.. ; ./run.sh Recom2 cluster 1 v-naif 25 datasets/renault_medium 10 -e | tee experiments/exp12/vnaif-25-medium)
(cd ../.. ; ./run.sh Recom2 cluster 1 v-naif 30 datasets/renault_medium 10 -e | tee experiments/exp12/vnaif-30-medium)
(cd ../.. ; ./run.sh Recom2 cluster 1 v-naif 35 datasets/renault_medium 10 -e | tee experiments/exp12/vnaif-35-medium)
(cd ../.. ; ./run.sh Recom2 cluster 1 v-naif 40 datasets/renault_medium 10 -e | tee experiments/exp12/vnaif-40-medium)
(cd ../.. ; ./run.sh Recom2 cluster 1 v-naif 45 datasets/renault_medium 10 -e | tee experiments/exp12/vnaif-45-medium)
(cd ../.. ; ./run.sh Recom2 cluster 1 v-naif 50 datasets/renault_medium 10 -e | tee experiments/exp12/vnaif-50-medium)
(cd ../.. ; ./run.sh Recom2 cluster 1 v-naif 100 datasets/renault_medium 10 -e | tee experiments/exp12/vnaif-100-medium)
notify-send  'An experiment just completed'
