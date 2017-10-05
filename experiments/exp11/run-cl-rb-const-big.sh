#!/bin/sh

(cd ../.. ; ./run.sh Recom2 cluster 1 naif datasets/renault_big 10 -e -c datasets/renault_big/contraintes.xml -s 10 | tee experiments/exp11/naif-cl1-const-big)
(cd ../.. ; ./run.sh Recom2 cluster 2 naif datasets/renault_big 10 -e -c datasets/renault_big/contraintes.xml -s 10 | tee experiments/exp11/naif-cl2-const-big)
(cd ../.. ; ./run.sh Recom2 cluster 3 naif datasets/renault_big 10 -e -c datasets/renault_big/contraintes.xml -s 10 | tee experiments/exp11/naif-cl3-const-big)
(cd ../.. ; ./run.sh Recom2 cluster 1 jointree datasets/renault_big 10 -e -c datasets/renault_big/contraintes.xml -s 10 | tee experiments/exp11/jointree-cl1-const-big)
(cd ../.. ; ./run.sh Recom2 cluster 2 jointree datasets/renault_big 10 -e -c datasets/renault_big/contraintes.xml -s 10 | tee experiments/exp11/jointree-cl2-const-big)
(cd ../.. ; ./run.sh Recom2 cluster 3 jointree datasets/renault_big 10 -e -c datasets/renault_big/contraintes.xml -s 10 | tee experiments/exp11/jointree-cl3-const-big)
