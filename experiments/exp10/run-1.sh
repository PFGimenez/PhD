#!/bin/sh

(cd ../.. ; ./run.sh -ea EvaluationLextree test1) &
(cd ../.. ; ./run.sh -ea EvaluationLextree test2) &
(cd ../.. ; ./run.sh -ea EvaluationLextree test3) &
