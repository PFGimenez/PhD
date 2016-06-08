#!/bin/bash

cd ..

if [ ! -d "./result/Rscript" ]; then
    echo "Creation du repertoire Rscript"
    mkdir ./result/Rscript 2>/dev/null
fi;
echo "Deplacement des fichiers .R"
mv *.R ./result/Rscript 2>/dev/null

if [ ! -d "./result/img" ]; then
    echo "Creation du repertoire img"
    mkdir ./result/img 2>/dev/null
fi;
echo "Deplacement des fichiers .png"
mv *.png ./result/img 2>/dev/null

if [ ! -d "./result/data" ]; then
    echo "Creation du repertoire data"
    mkdir ./result/data 2>/dev/null
fi;
echo "Deplacement des fichiers .data"
mv *.data ./result/data 2>/dev/null

