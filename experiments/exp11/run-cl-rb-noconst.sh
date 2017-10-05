#!/bin/sh

(cd ../.. ; ./run.sh Recom2 cluster 1 naif datasets/renault_small 10 -e | tee experiments/exp11/naif-cl1)
(cd ../.. ; ./run.sh Recom2 cluster 2 naif datasets/renault_small 10 -e | tee experiments/exp11/naif-cl2)
(cd ../.. ; ./run.sh Recom2 cluster 3 naif datasets/renault_small 10 -e | tee experiments/exp11/naif-cl3)
(cd ../.. ; ./run.sh Recom2 cluster 1 jointree datasets/renault_small 10 -e | tee experiments/exp11/jointree-cl1)
(cd ../.. ; ./run.sh Recom2 cluster 2 jointree datasets/renault_small 10 -e | tee experiments/exp11/jointree-cl2)
(cd ../.. ; ./run.sh Recom2 cluster 3 jointree datasets/renault_small 10 -e | tee experiments/exp11/jointree-cl3)
