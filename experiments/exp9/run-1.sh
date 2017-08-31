#!/bin/sh

(cd ../.. ; ./run.sh -ea Recom2 cluster 1 lextree-group false 1 datasets/renault_small 10 -e | tee 1cl-1var)
(cd ../.. ; ./run.sh -ea Recom2 cluster 2 lextree-group false 1 datasets/renault_small 10 -e | tee 2cl-1var)
(cd ../.. ; ./run.sh -ea Recom2 cluster 3 lextree-group false 1 datasets/renault_small 10 -e | tee 3cl-1var)
(cd ../.. ; ./run.sh -ea Recom2 cluster 4 lextree-group false 1 datasets/renault_small 10 -e | tee 4cl-1var)
(cd ../.. ; ./run.sh -ea Recom2 cluster 5 lextree-group false 1 datasets/renault_small 10 -e | tee 5cl-1var)


#(cd ../.. ; ./run.sh -ea Recom2 cluster 1 lextree-group false 2 datasets/renault_small 10 -e | tee 1cl-2var)
#(cd ../.. ; ./run.sh -ea Recom2 cluster 2 lextree-group false 2 datasets/renault_small 10 -e | tee 2cl-2var)
#(cd ../.. ; ./run.sh -ea Recom2 cluster 3 lextree-group false 2 datasets/renault_small 10 -e | tee 3cl-2var)
#(cd ../.. ; ./run.sh -ea Recom2 cluster 4 lextree-group false 2 datasets/renault_small 10 -e | tee 4cl-2var)
#(cd ../.. ; ./run.sh -ea Recom2 cluster 5 lextree-group false 2 datasets/renault_small 10 -e | tee 5cl-2var)


#(cd ../.. ; ./run.sh -ea Recom2 cluster 1 lextree-group false 3 datasets/renault_small 10 -e | tee 1cl-3var)
#(cd ../.. ; ./run.sh -ea Recom2 cluster 2 lextree-group false 3 datasets/renault_small 10 -e | tee 2cl-3var)
#(cd ../.. ; ./run.sh -ea Recom2 cluster 3 lextree-group false 3 datasets/renault_small 10 -e | tee 3cl-3var)
#(cd ../.. ; ./run.sh -ea Recom2 cluster 4 lextree-group false 3 datasets/renault_small 10 -e | tee 4cl-3var)
#(cd ../.. ; ./run.sh -ea Recom2 cluster 5 lextree-group false 3 datasets/renault_small 10 -e | tee 5cl-3var)
