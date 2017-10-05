#!/bin/sh

(cd ../.. ; ./run.sh Recom2 cluster 1 v-maj 20 datasets/renault_medium 10 -e -c datasets/renault_medium/contraintes.xml -s 10 | tee experiments/exp11/vmaj-cl1-const-medium)
(cd ../.. ; ./run.sh Recom2 cluster 2 v-maj 20 datasets/renault_medium 10 -e -c datasets/renault_medium/contraintes.xml -s 10 | tee experiments/exp11/vmaj-cl2-const-medium)
(cd ../.. ; ./run.sh Recom2 cluster 3 v-maj 20 datasets/renault_medium 10 -e -c datasets/renault_medium/contraintes.xml -s 10 | tee experiments/exp11/vmaj-cl3-const-medium)
(cd ../.. ; ./run.sh Recom2 cluster 1 v-pop 20 datasets/renault_medium 10 -e -c datasets/renault_medium/contraintes.xml -s 10 | tee experiments/exp11/vpop-cl1-const-medium)
(cd ../.. ; ./run.sh Recom2 cluster 2 v-pop 20 datasets/renault_medium 10 -e -c datasets/renault_medium/contraintes.xml -s 10 | tee experiments/exp11/vpop-cl2-const-medium)
(cd ../.. ; ./run.sh Recom2 cluster 3 v-pop 20 datasets/renault_medium 10 -e -c datasets/renault_medium/contraintes.xml -s 10 | tee experiments/exp11/vpop-cl3-const-medium)
(cd ../.. ; ./run.sh Recom2 cluster 1 v-naif 20 datasets/renault_medium 10 -e -c datasets/renault_medium/contraintes.xml -s 10 | tee experiments/exp11/vnaif-cl1-const-medium)
(cd ../.. ; ./run.sh Recom2 cluster 2 v-naif 20 datasets/renault_medium 10 -e -c datasets/renault_medium/contraintes.xml -s 10 | tee experiments/exp11/vnaif-cl2-const-medium)
(cd ../.. ; ./run.sh Recom2 cluster 3 v-naif 20 datasets/renault_medium 10 -e -c datasets/renault_medium/contraintes.xml -s 10 | tee experiments/exp11/vnaif-cl3-const-medium)
