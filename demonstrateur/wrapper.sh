#!/bin/sh
cd ../../code
while true; do
    read com
    if [ "$com" = "start" ]; then
        read algo
        read dataset
        java -jar interactive_recom.jar $algo $dataset
    else
        1>&2 echo Erreur : $com
    fi
done
