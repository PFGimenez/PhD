#!/bin/bash

#set -xv

ERROR_NB_PARAM=1
FILE_MOULE_NOT_EXIST=2
DATASET_NOT_EXIST=3

affUsage(){
	echo "$0 dataset algo [-{entete=e,verbose=v,forcefic=f,partie=p}] [liste part ... ]"
    echo "$0 --clean pour nettoyer tous les fichiers .R du repertoire"
    echo "$0 --help pour afficher l aide"
}

isInTab(){
    #$1 tab $2 val
    let "nb=${#tab[@]}-1"
    if [ $nb -eq -1 ]; then
        return false;
    else
        trouve=false
        for i in `seq 0 $nb`; do
            if [ $1 == ${tab[$a]} ]; then
                return true;
            fi;
        done;
    fi;
    
    return false;
}

if [ -n $1 ]; then
    if [ "$1" == "--help" ]; then    
        affUsage
        exit 0
    fi

    if [ "$1" == "--clean" ]; then
        rm -i ./*.R
    fi
else
    affUsage
    exit $ERROR_NB_PARAM
fi

if [ -z $1 -o -z $2 ]; then
    affUsage
    exit $ERROR_NB_PARAM
fi

DATASET_BASE=$1
ALGO_BASE=$2
MOULE="genere_data/new_data_moule2"
CMD="java -jar"
LOGICIEL="recom.jar"
LISTE_ALGO=( rc drc oracle naif jointree v-majority lextree v-pop v-naif )
NAME_FIC=( AlgoRC AlgoDRC-10 Oracle AlgoRBNaif AlgoRBJayes AlgoVoisinsMajorityVoter AlgoLexTree AlgoVoisinsMostPopular AlgoVoisinsNaive )
FIC_BASE_ORACLE="Oracle"
let "maxalg=${#LISTE_ALGO[@]}-1"
for i in `seq 0 $maxalg`; do
    if [ ${LISTE_ALGO[$i]} == $ALGO_BASE ]; then
        FIC_BASE=${NAME_FIC[$i]}
    fi
done

ENTETE=false
DEBUG=true
FORCE="false"
EFF=false

NBCUT=( "1" "2" "4" "8" "16" "32" "64" )
CLEAN=()

if [ -n $3 ]; then
    if [ `echo -- $3 | grep "e" | wc -l` -eq 1 ]; then 
        ENTETE=true
    fi;
    if [ `echo -- $3 | grep "v" | wc -l` -eq 1 ]; then  
        DEBUG=false
    fi;
    if [ `echo -- $3 | grep "f" | wc -l` -eq 1 ]; then
        FORCE="true"
    fi;
    if [ `echo -- $3 | grep 'p' | wc -l` -eq 1 ]; then
        shift
        shift
        shift
        A=0
        NBCUT2[$A]="1"
        let "A=$A+1"
        while [ -n "$1" ]; do
            index=0
            while [ $index -lt ${#NBCUT[@]} ] && [ ${NBCUT[$index]} != $1 ]; do
                let "index = $index+1"
            done;
            if [ $index -eq ${#NBCUT[@]} ]; then
                echo "L'algo $1 n existe pas"
            else
                NBCUT2[$A]="$1"
                let "A=$A+1"
            fi
            shift
        done;
        index=0
        e=0
        while [ $index -lt ${#NBCUT[@]} ]; do
            index2=0
            while [ $index2 -lt ${#NBCUT2[@]} ] && [ ${NBCUT2[$index2]} != ${NBCUT[$index]} ]; do
                let "index2 = $index2+1"
            done
            if [ $index2 -eq ${#NBCUT2[@]} ]; then #algo n existe pas on le rajoute a la commande
                clean[$e]="1_"${NBCUT[$index]}
                let "e=$e+1"
            fi
            let "index = $index+1"
        done;
        NBCUT2[$A] = "1";
        NBCUT=( ${NBCUT2[*]} )
    fi;
fi

REPLACE=()
A=4
REPLACE[0]="@@ORACLE_RESULT@@"
REPLACE[1]="@@ORACLE_TIME@@"
REPLACE[2]="@@1_RESULT@@"
REPLACE[3]="@@1_TIME@@"
REPLACE[4]="@@ORACLE_RESULT@@"
for i in ${NBCUT[@]}; do
    if [ $i -ne 1 ]; then
        REPLACE[$A]="@@1_"$i"_RESULT@@"
        let "A=$A+1"
        REPLACE[$A]="@@1_"$i"_TIME@@"
        let "A=$A+1"
    fi;
done;
if $DEBUG ; then
    echo "cut: "${NBCUT[*]}
    echo "clean: "${clean[*]}
    echo "replace: "${REPLACE[*]}
fi

A=0
ALGO="oracle"
DATASET=$DATASET_BASE
FIC="./results/data/"$FIC_BASE_ORACLE"_"$DATASET".data"
if $DEBUG ; then
    echo "ALGO:"$ALGO
    echo "DATASET:"$DATASET
    echo "FIC:"$FIC
fi;

if [ ! -e $FIC ] || [ $FORCE == "true" ]; then #le fic n est pas deja present ou on le force a ce reexec
    echo "Lancement de "$ALGO" pour le dataset "$DATASET
    if $DEBUG ; then
        echo "[DEBUG] $CMD $LOGICIEL $ALGO $DATASET $entete -o results/data >/dev/null"
    fi;
    if $DEBUG ; then
        $CMD $LOGICIEL $ALGO $DATASET $entete -o results/data
    else
    	$CMD $LOGICIEL $ALGO $DATASET $entete -o results/data >/dev/null
    fi;
fi;

if [ ! -e $FIC ]; then
    exit 3;
fi;
echo -e "Parse de $FIC"
result=`head -1 $FIC`
c_result=`head -2 $FIC | tail -1`
temps=`head -3 $FIC | tail -1`
c_temps=`head -4 $FIC | tail -1`
if [ $DEBUG == "true" ]; then
    echo -e "[DEBUG] Result : $result\n"
    echo -e "[DEBUG] CResult : $c_result\n"
    echo -e "[DEBUG] Temps : $temps\n"
    echo -e "[DEBUG] CTime : $c_temps\n"
fi;

if [ $EFF == "true" ]; then  
    #Supression du fichier
    echo -e "Suppression de $FIC\n"
    rm $FIC
fi;
#FIN EXECUTION PROGRAMME ET ECRITURE DANS LE FICHIER*

echo "Ecriture result -> ["$A"]"
RESULT[A]=$result;
let "A=$A+1"
echo "Ecriture result -> ["$A"]"
RESULT[A]=$temps;
let "A=$A+1"

echo "A"$A
let "nbMax=${#NBCUT[@]}-1"
for j in `seq 0 $nbMax`; do
    i=${NBCUT[$j]}
    result=""
    temps=""
    echo "i:"$i"-j:"$j
    if [ "$i" != "1" ]; then
        DATASET=$DATASET_BASE"_appl_"$i
        ALGO=$ALGO_BASE
        FIC="./results/data/"$FIC_BASE"_"$DATASET".data"
    else
        DATASET=$DATASET_BASE
        ALGO=$ALGO_BASE
        FIC="./results/data/"$FIC_BASE"_"$DATASET".data"
    fi

    if $DEBUG ; then
        echo "ALGO:"$ALGO
        echo "DATASET:"$DATASET
        echo "FIC:"$FIC
    fi;

    if [ ! -e $FIC ] || [ $FORCE == "true" ]; then #le fic n est pas deja present ou on le force a ce reexec
        echo "Lancement de "$ALGO" pour le dataset "$DATASET
        if $DEBUG ; then
            echo "[DEBUG] $CMD $LOGICIEL $ALGO $DATASET $entete -o results/data >/dev/null"
        fi;
        if $DEBUG ; then
            $CMD $LOGICIEL $ALGO $DATASET $entete -o results/data
        else
        	$CMD $LOGICIEL $ALGO $DATASET $entete -o results/data >/dev/null
        fi;
    fi;

    if [ ! -e $FIC ]; then
        exit 3;
    fi;
    echo -e "Parse de $FIC"
    echo ''
	result_tab=`head -1 $FIC`
    c_result=`head -2 $FIC | tail -1`
	temps_tab=`head -3 $FIC | tail -1`
    c_temps=`head -4 $FIC | tail -1`
    if [ $DEBUG == "true" ]; then
        echo -e "[DEBUG] Result : $result_tab\n"
        #echo -e "[DEBUG] CResult : $c_result\n"
        echo -e "[DEBUG] Temps : $temps_tab\n"
        #echo -e "[DEBUG] CTime : $c_temps\n"
    fi;

    if [ $EFF == "true" ]; then  
        #Supression du fichier
        echo -e "Suppression de $FIC\n"
        rm $FIC
    fi;
	#FIN EXECUTION PROGRAMME ET ECRITURE DANS LE FICHIER*

    echo "Ecriture result -> ["$A"]"
    RESULT[A]=$result_tab;
	let "A=$A+1"
    echo "Ecriture temps -> ["$A"]"
    RESULT[A]=$temps_tab;
	let "A=$A+1"
done     

#CREATION ET REMPLACEMENT DES INFORMATIONS DANS LE FICHIER R
if [ ! -e "$MOULE" ]; then
	exit FILE_MOULE_NOT_EXIST
fi;
cp $MOULE "data_"$DATASET_BASE"_"$ALGO_BASE".R" #copie du fichier

if [ $DEBUG == "true" ]; then
    echo -e "[DEBUG] sed -i -e s/**DATASET**/"$DATASET_BASE"/ data_"$DATASET_BASE".R"
fi;
sed -i -e s/@@DATASET@@/$DATASET_BASE/ data_$DATASET_BASE"_"$ALGO_BASE.R
sed -i -e s/@@ALGO@@/$ALGO_BASE/ data_$DATASET_BASE"_"$ALGO_BASE.R

let "max = ${#REPLACE[*]} - 1"
A=0
for i in `seq 0 $max`; do
    echo "Ecriture des valeurs pour "${REPLACE[$i]}
    a="sed -i -e s/"${REPLACE[$i]}"/"${RESULT[$i]}"/ data_"$DATASET_BASE"_"$ALGO_BASE".R"
    if [ $DEBUG == "true" ]; then
        echo -e "[DEBUG] "$a"\n";
    fi;
	$a
done;

let "maxclean = ${#clean[*]} - 1"
A=0
for i in `seq 0 $maxclean`; do
    grep -v "${clean[$i]}" ./data_$DATASET_BASE"_"$ALGO_BASE.R > a.out
    mv a.out data_$DATASET_BASE"_"$ALGO_BASE.R #on fait le menage
done;

echo "Exec de ./data_"$DATASET_BASE"_"$ALGO_BASE".R"
if [ $DEBUG == "true" ]; then
    echo -e "[DEBUG] Rscript ./data_"$DATASET_BASE"_"$ALGO_BASE".R >/dev/null"
fi;
Rscript ./data_$DATASET_BASE"_"$ALGO_BASE.R #>/dev/null

if [ $DEBUG == "true" ]; then
    echo -e "[DEBUG] read -t 20 && eog ./*"$DATASET_BASE"_"$ALGO_BASE"*.png"
fi;
read -t 20 && eog ./*$DATASET_BASE"_"$ALGO_BASE*.png

#FIN CREATION ET REMPLACEMENT DES INFORMATIONS DANS LE FICHIER R

#-----------------------------------------------------------------------------------------------------------------------------------------------------------

./genere_data/rangement.bash

exit 0
