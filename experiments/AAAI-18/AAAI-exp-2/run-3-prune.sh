#!/bin/sh

(cd ../../.. ; ./run.sh -ea Recom2 1 lextree-group true 3 datasets/renault_small 10 -e ) | tee 1cl-3var-prune
(cd ../../.. ; ./run.sh -ea Recom2 2 lextree-group true 3 datasets/renault_small 10 -e ) | tee 2cl-3var-prune
(cd ../../.. ; ./run.sh -ea Recom2 3 lextree-group true 3 datasets/renault_small 10 -e ) | tee 3cl-3var-prune
(cd ../../.. ; ./run.sh -ea Recom2 4 lextree-group true 3 datasets/renault_small 10 -e ) | tee 4cl-3var-prune
(cd ../../.. ; ./run.sh -ea Recom2 5 lextree-group true 3 datasets/renault_small 10 -e ) | tee 5cl-3var-prune
