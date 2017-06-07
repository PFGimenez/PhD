#!/bin/sh
echo Experiment on the mean rank of LP-tree learnt from Renault datasets
echo Dataset = small
(cd ../.. ; ./run.sh MeanRank renault_small_header | tee small_result)
echo Dataset = medium
(cd ../.. ; ./run.sh MeanRank renault_medium_header | tee medium_result)
echo Dataset = big
(cd ../.. ; ./run.sh MeanRank renault_big_header | tee big_result)
