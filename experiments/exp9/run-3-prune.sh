#!/bin/sh

(cd ../.. ; ./run.sh -ea Recom2 cluster 1 lextree-group true 3 datasets/renault_small 10 -e | tee experiments/exp9/1cl-3var-prune)
(cd ../.. ; ./run.sh -ea Recom2 cluster 2 lextree-group true 3 datasets/renault_small 10 -e | tee experiments/exp9/2cl-3var-prune)
(cd ../.. ; ./run.sh -ea Recom2 cluster 3 lextree-group true 3 datasets/renault_small 10 -e | tee experiments/exp9/3cl-3var-prune)
(cd ../.. ; ./run.sh -ea Recom2 cluster 4 lextree-group true 3 datasets/renault_small 10 -e | tee experiments/exp9/4cl-3var-prune)
(cd ../.. ; ./run.sh -ea Recom2 cluster 5 lextree-group true 3 datasets/renault_small 10 -e | tee experiments/exp9/5cl-3var-prune)
