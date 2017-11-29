#!/bin/sh

(cd ../../.. ; ./run.sh -ea Recom2 1 lextree-group false 1 datasets/renault_small 10 -e) | tee 1cl-1var
(cd ../../.. ; ./run.sh -ea Recom2 2 lextree-group false 1 datasets/renault_small 10 -e) | tee 2cl-1var
(cd ../../.. ; ./run.sh -ea Recom2 3 lextree-group false 1 datasets/renault_small 10 -e) | tee 3cl-1var
(cd ../../.. ; ./run.sh -ea Recom2 4 lextree-group false 1 datasets/renault_small 10 -e) | tee 4cl-1var
(cd ../../.. ; ./run.sh -ea Recom2 5 lextree-group false 1 datasets/renault_small 10 -e) | tee 5cl-1var
