#!/bin/bash

ERROR_NB_PARAM=1
FILE_MOULE_NOT_EXIST=2

affUsage(){
	echo "$0 dataset [-{entete=e,hide=h,forcefic=f,algo=a}]"
    echo "$0 --clean pour nettoyer tous les fichiers .R du repertoire"
    echo "$0 --help pour afficher l aide"
}

if [ -n $1 ]; then
    if [ $1 == "--clean" ]; then
        echo "Les fichiers "`ls ./*.R`" ont bien ete suprimmes"
        rm -if ./*.R
        exit 0
    fi
    if [ $1 == "--help" ]; then
        affUsage
        exit 0
    fi
fi

#PARAMETRES
DEBUG="true";
EFF="false";
FORCE="false";
#FIN PARAMETRES

#-----------------------------------------------------------------------------------------------------------------------------------------------------------
#DEFINITION VARIABLES ENVIRONNEMENT
MOULE="new_data_moule"
CMD="java -jar"
LOGICIEL="recom.jar"
LISTE_ALGO=( rc drc oracle naif jointree v-majority lextree v-pop )
NAME_FIC=( AlgoRC AlgoDRC-10 Oracle AlgoRBNaif AlgoRBJayes AlgoVoisinsMajorityVoter AlgoLexTree AlgoVoisinsMostPopular )
REPLACE_PREF=( "" C_ )
REPLACE_HEAD=( RC DRC ORACLE NAIF JOIN_TREE WMV LEX_TREE VPOP )
NAME_VAR=( rc drc oracle naif jointree wmv lextree vpop )
REPLACE_BODY=( _RESULT _TIME )

if [ -z $1 ]; then
	affUsage
	exit $ERROR_NB_PARAM
fi;
DATASET="$1"
clean=()
if [ -n $2 ]; then
    if [ `echo $2 | grep 'e' | wc -l` -eq 1 ]; then
	    entete="-e"
    fi
    if [ `echo $2 | grep 'h' | wc -l` -eq 1 ]; then
        DEBUG="false"
    fi
    if [ `echo $2 | grep 'f' | wc -l` -eq 1 ]; then
        FORCE="true"
    fi
    if [ `echo $2 | grep 'a' | wc -l` -eq 1 ]; then
        shift
        shift
        A=0
        while [ -n "$1" ]; do
            index=0
            while [ $index -lt ${#LISTE_ALGO[@]} ] && [ ${LISTE_ALGO[$index]} != $1 ]; do
                let "index = $index+1"
            done;
            if [ $index -eq ${#LISTE_ALGO[@]} ]; then
                echo "L'algo $1 n existe pas"
            else
                NAME_FIC2[$A]=${NAME_FIC[$index]}
                REPLACE_HEAD2[$A]=${REPLACE_HEAD[$index]}
                LISTE_ALGO2[$A]="$1"
                let "A=$A+1"
            fi
            shift
        done;
        index=0
        e=0
        while [ $index -lt ${#LISTE_ALGO[@]} ]; do
            index2=0
            while [ $index2 -lt ${#LISTE_ALGO2[@]} ] && [ ${LISTE_ALGO2[$index2]} != ${LISTE_ALGO[$index]} ]; do
                let "index2 = $index2+1"
            done
            if [ $index2 -eq ${#LISTE_ALGO2[@]} ]; then #algo n existe pas on le rajoute a la commande
                clean[$e]=${NAME_VAR[$index]}
                let "e=$e+1"
            fi
            let "index = $index+1"
        done;
        LISTE_ALGO=( ${LISTE_ALGO2[*]} )
        NAME_FIC=( ${NAME_FIC2[*]} )
        REPLACE_HEAD=( ${REPLACE_HEAD2[*]} ) 
    fi; 
else
	entete=""
fi;

if [ $DEBUG ]; then
    echo -e "Liste algo:"${LISTE_ALGO[*]}"\n"
    echo -e "Liste name fic:"${NAME_FIC[*]}"\n"
    echo -e "Liste replace head:"${REPLACE_HEAD[*]}"\n"
fi;

let "maxhead = ${#REPLACE_HEAD[*]} - 1"
let "maxbody = ${#REPLACE_BODY[*]} - 1"
let "maxpref = ${#REPLACE_PREF[*]} - 1"
A=0
for i in `seq 0 $maxhead`; do
    for j in `seq 0 $maxbody`; do
        for h in `seq 0 $maxpref`; do
            pref=${REPLACE_PREF[$h]}
		    algo=${REPLACE_HEAD[$i]}
		    typ=${REPLACE_BODY[$j]}
		    REPLACE[$A]="@@"$pref""$algo""$typ"@@"
		    let "A = $A + 1"
        done;
	done;
done;
#echo ${REPLACE[*]} #affiche le tableau

echo -e "\n                                Execution du dataset:<<"$DATASET">> pour les algorithmes {"${LISTE_ALGO[*]}"}\n\n"
#FIN DEFINITION VARIABLES ENVIRONNEMENT

#-------------------------------------------------------------------------------------------------------------------------------------------------------------

#EXECUTION DU PROGRAMME ET ECRITURE DANS LE FICHIER
let "maxalg = ${#LISTE_ALGO[*]} - 1"
A=0
for i in `seq 0 $maxhead`; do
    ficOut="results/"${NAME_FIC[$i]}"_"$DATASET".data"

    if [ ! -e $ficOut ] || [ $FORCE == "true" ]; then #le fic n est pas deja present ou on le force a ce reexec
        echo "Lancement de "${LISTE_ALGO[$i]}" pour le dataset "$DATASET
        if [ $DEBUG == "true" ]; then
            echo "[DEBUG] $CMD $LOGICIEL ${LISTE_ALGO[$i]} $DATASET $entete -o >/dev/null"
        fi;
        if [ $DEBUG == "true" ]; then
            $CMD $LOGICIEL ${LISTE_ALGO[$i]} $DATASET $entete -o
        else
        	$CMD $LOGICIEL ${LISTE_ALGO[$i]} $DATASET $entete -o >/dev/null
        fi;
    fi;
	#FIN EXECUTION PROGRAMME ET ECRITURE DANS LE FICHIER

	#PARSE DU FICHIER ET RECUPERATION DES INFORMATIONS
    echo -e "Parse de $ficOut"
	result=`head -1 $ficOut`
    c_result=`head -2 $ficOut | tail -1`
	temps=`head -3 $ficOut | tail -1`
    c_temps=`head -4 $ficOut | tail -1`
    if [ $DEBUG == "true" ]; then
        echo -e "[DEBUG] Result : $result\n"
        echo -e "[DEBUG] CResult : $c_result\n"
        echo -e "[DEBUG] Temps : $temps\n"
        echo -e "[DEBUG] CTime : $c_temps\n"
    fi;
    
    if [ $EFF == "true" ]; then  
        #Supression du fichier
        echo -e "Suppression de $ficOut\n"
        rm $ficOut
    fi;

    RESULT[A]=$result;
	let "A=$A+1"
	RESULT[A]=$c_result;
	let "A=$A+1"
    RESULT[A]=$temps;
	let "A=$A+1"
    RESULT[A]=$c_temps;
	let "A=$A+1"
done;
#FIN PARSE DU FICHIER ET RECUPERATION DES INFORMATIONS

#-------------------------------------------------------------------------------------------------------------------------------------------------------------

#CREATION ET REMPLACEMENT DES INFORMATIONS DANS LE FICHIER R
if [ ! -e "$MOULE" ]; then
	exit FILE_MOULE_NOT_EXIST
fi;
cp $MOULE "data_"$DATASET".R" #copie du fichier

if [ $DEBUG == "true" ]; then
    echo -e "[DEBUG] sed -i -e s/**DATASET**/"$DATASET"/ data_"$DATASET".R"
fi;
sed -i -e s/@@DATASET@@/$DATASET/ data_$DATASET.R

let "max = ${#REPLACE[*]} - 1"
A=0
for i in `seq 0 $max`; do
    echo "Ecriture des valeurs pour "${REPLACE[$i]}
    a="sed -i -e s/"${REPLACE[$i]}"/"${RESULT[$i]}"/ data_"$DATASET".R"
    if [ $DEBUG == "true" ]; then
        echo -e "[DEBUG] "$a"\n";
    fi;
	$a
done;

let "maxclean = ${#clean[*]} - 1"
A=0
for i in `seq 0 $maxclean`; do
    grep -v "${clean[$i]}" ./data_$DATASET.R > a.out
    mv a.out data_$DATASET.R #on fait le menage
done;

echo "Exec de ./data_$DATASET.R"
if [ $DEBUG == "true" ]; then
    echo -e "[DEBUG] Rscript ./data_$DATASET.R >/dev/null"
fi;
Rscript ./data_$DATASET.R >/dev/null

if [ $DEBUG == "true" ]; then
    echo -e "[DEBUG] read -t 20 && eog ./*$DATASET*.png"
fi;
read -t 20 && eog ./*$DATASET*.png

#FIN CREATION ET REMPLACEMENT DES INFORMATIONS DANS LE FICHIER R

#-----------------------------------------------------------------------------------------------------------------------------------------------------------

./rangement.bash

exit 0
