#!/bin/sh
cd ..
while true; do
    read com
    if [ "$com" = "start" ]; then
        read algo
        read dataset
        java -jar interactive_recom.jar $algo $dataset
    elif [ "$com" = "ping" ]; then
        echo pong
    elif [ "$com" != "exit" ]; then
        echo error
        1>&2 echo Erreur : $com
    fi
done
