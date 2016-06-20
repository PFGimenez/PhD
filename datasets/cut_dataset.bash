#!/bin/bash
    echo "$0 dataset nb_cut [-e]";
    
    if [ -z $1 ] || [ -z $2 ]; then
        echo "Les parametres dataset et nb_cut sont obligatoires";
        exit 1;
    fi;

    if [ ! -d $1 ]; then
        echo "Le dataset $1 n existe pas";
        exit 2;
    fi;

    if [ ! -e $1/set0_exemples.csv ] || [ ! -e $1/set1_exemples.csv ]; then
        echo "Les fichiers set{0,1}_examples.csv n existe pas";
        exit 3;
    fi;

    if [ $2 -lt 2 ]; then
        echo "nb_cut >= 2";
        exit 4;
    fi;

    src="../$1";
    dest="./"$1"_appl_"$2"";
    fic1="set0_exemples.csv";
    fic2="set1_exemples.csv";

    rm -Rf $dest;
    mkdir $dest;
    cd $dest
    pwd
    cp $src/$fic1 f1;
    cp $src/$fic2 f2;

    nbL1=`cat f1 | wc -l`
    nbL2=`cat f2 | wc -l`

    if [ -n "$3" ] && [ "$3" == "-e" ]; then
        entete=`head -1 f1`
        let "nbL1=$nbL1-1"
        let "nbL2=$nbL2-1"
        
        a=( `tail -$nbL1 f1` )
        rm f1;
        for lig in ${a[*]}; do
            echo $mot>>f1
        done

        a=( `tail -$nbL2 f2` )
        rm f2;
        for lig in ${a[*]}; do
            echo $mot>>f2
        done
    fi;

    let "cut=$nbL1/$2"
    let "rest=$nbL1-$cut"
    echo $nbL1" - "$nbL2" -- "$cut" -- "$rest

    split f1 -l $cut
    cat xaa >f1
    rm xaa
    cat x* >>f2
    rm x*

    shuf f1 >f11
    shuf f2 >f22
    mv f11 f1
    mv f22 f2

    if [ -n "$3" ] && [ "$3" == "-e" ]; then
        a=( `cat f1` )
        rm f1; echo $entete>f1;
        for lig in ${a[*]}; do
            echo $lig>>f1
        done

        a=( `cat f2` )
        rm f2; echo $entete>f2;
        for lig in ${a[*]}; do
            echo $lig>>f1
        done
    fi;
    
    wc -l *

    exit 0;
