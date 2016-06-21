#!/bin/bash
    echo "$0 dataset nb_cut [-e|m] [liste cut ...]";
    
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

    if [ `echo -- $3 | grep 'm' | wc -l` -eq 1 ]; then
        while [ -n "$4" ]; do
            echo "Traitement pour $4"
            set $1 $4 $3 $4 $5 $6 $7 $8 $9
            
            if [ $2 -ne 1 ]; then
                src="../$1";
                echo $src
                dest="./"$1"_appl_"$2"";
                fic1="set0_exemples.csv";
                fic2="set1_exemples.csv";

                rm -Rf $dest;
                mkdir $dest;
                cd $dest
                cp $src/$fic1 f1;
                cp $src/$fic2 f2;

                nbL1=`cat f1 | wc -l`
                nbL2=`cat f2 | wc -l`

                if [ -n "$3" ] && [ `echo -- $3 | grep 'e' | wc -l` -eq 1 ]; then
                    entete=`head -1 f1`
                    let "nbL1=$nbL1-1"
                    let "nbL2=$nbL2-1"
                    
                    a="sed /".$entete."/d f1"
                    $a >/dev/null;
                    a="sed /".$entete."/d f2"
                    $a >/dev/null;
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

                if [ -n "$3" ] && [ `echo -- $3 | grep 'e' | wc -l` -eq 1 ]; then
                    chaine='1i\'$entete
                    a="sed -i "$chaine" f1";
                    $a >/dev/null;
                    a="sed -i "$chaine" f2";
                    $a >/dev/null;
                fi;

                mv f1 training.csv
                mv f2 testing.csv
                cd ..
            fi;

            set $1 $4 $3 $5 $6 $7 $8 $9
        done;
    else
        if [ $2 -ne 1 ]; then
            src="../$1";
            dest="./"$1"_appl_"$2"";
            fic1="set0_exemples.csv";
            fic2="set1_exemples.csv";

            rm -Rf $dest;
            mkdir $dest;
            cd $dest
            cp $src/$fic1 f1;
            cp $src/$fic2 f2;

            nbL1=`cat f1 | wc -l`
            nbL2=`cat f2 | wc -l`

            if [ -n "$3" ] && [ `echo -- $3 | grep 'e' | wc -l` -eq 1 ]; then
                entete=`head -1 f1`
                let "nbL1=$nbL1-1"
                let "nbL2=$nbL2-1"
                
                a="sed /".$entete."/d f1"
                $a >/dev/null;
                a="sed /".$entete."/d f2"
                $a >/dev/null;
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

            if [ -n "$3" ] && [ `echo -- $3 | grep 'e' | wc -l` -eq 1 ]; then
                chaine='1i\'$entete
                a="sed -i "$chaine" f1";
                $a >/dev/null;
                a="sed -i "$chaine" f2";
                $a >/dev/null;
            fi;

            mv f1 training.csv
            mv f2 testing.csv
            cd ..
        fi;
    fi

    exit 0;
