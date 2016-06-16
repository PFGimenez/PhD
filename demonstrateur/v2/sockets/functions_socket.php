<?php
    function init($algo,$dataset){
        $illegal = array("&#039;", "#039;");
        $dataset = str_replace($illegal, "", $dataset);
        $algo = str_replace($illegal, "", $algo);
        fwrite($GLOBALS['f'],"\n".date("(h:i:s)\t").microtime()."\t"."[PHP][Socket][Init][Write] Ecriture dans socket de 'exit'\n");
        fwrite($GLOBALS['f'],date("(h:i:s)\t").microtime()."\t"."[PHP][Socket][Init][Write] Ecriture dans socket de 'start'\n");
        fwrite($GLOBALS['f'],date("(h:i:s)\t").microtime()."\t"."[PHP][Socket][Init][Write] Ecriture dans socket de '".$algo."'\n");
        fwrite($GLOBALS['f'],date("(h:i:s)\t").microtime()."\t"."[PHP][Socket][Init][Write] Ecriture dans socket de '".$dataset."'\n");
        $nb_o = socket_write($GLOBALS['s'],"exit\nstart\n".$algo."\n".$dataset."\n");
        fwrite($GLOBALS['f'],date("(h:i:s)\t").microtime()."\t"."[PHP][Socket][Init][Write] --> Ecriture de ".$nb_o." octets effectuee\n");
        
        $chaine = "";
        $NB = 0;
        while(($chaine != "ready\n") && ($NB != $GLOBALS['nb_tentatives_cnnx'])){
            fwrite($GLOBALS['f'],date("(h:i:s)\t").microtime()."\t"."[PHP][Socket][Init][Read] Lecture du socket\n");
            $chaine = socket_read($GLOBALS['s'],1000,PHP_NORMAL_READ);
            fwrite($GLOBALS['f'],date("(h:i:s)\t").microtime()."\t"."[PHP][Socket][Init][Read=ready] --> Reception de ".$chaine);
            if($chaine == "error\n"){
                echo "erreur init";
                fwrite($GLOBALS['f'],date("(h:i:s)\t").microtime()."\t"."[PHP][Socket][Init][Read=ready] --> Erreur init -> ".$chaine);
                exit();
            }
            $NB++;
        }
        if($NB == $GLOBALS['nb_tentatives_cnnx']){
            echo "erreur init";
            fwrite($GLOBALS['f'],date("(h:i:s)\t").microtime()."\t"."[PHP][Socket][Init][Read=ready] --> Nb de tentatives de connexion maximal [".$GLOBALS['nb_tentatives_cnnx']."] atteint.\n");
            exit();
        }
        fwrite($GLOBALS['f'],date("(h:i:s)\t").microtime()."\t"."[PHP][Socket][Init] Algo pret\n");
    }

    function get_var(){
        # RECUPERATION DES VARIABLES
        fwrite($GLOBALS['f'],"\n".date("(h:i:s)\t").microtime()."\t"."[PHP][Socket][Get_var][Write] Ecriture dans socket de 'vars'\n");
        $nb_o = socket_write($GLOBALS['s'],"vars\n");
        fwrite($GLOBALS['f'],date("(h:i:s)\t").microtime()."\t"."[PHP][Socket][Get_var][Write] --> Ecriture de ".$nb_o." octets effectuee\n");

        fwrite($GLOBALS['f'],date("(h:i:s)\t").microtime()."\t"."[PHP][Socket][Get_var][Read] Lecture du socket\n");
        $chaine = socket_read($GLOBALS['s'],1000,PHP_NORMAL_READ);
        fwrite($GLOBALS['f'],date("(h:i:s)\t").microtime()."\t"."[PHP][Socket][Get_var][Read] --> Reception de ".$chaine);
        $var = substr($chaine,0,strlen($chaine)-1);

        return $var;
    }

    function get_possible(){
        # RECUPERATION DE LEURS VALEURS
        $data = array();
        #$data['Vars'] = $vars;
        fwrite($GLOBALS['f'],date("(h:i:s)\t").microtime()."\t"."[PHP][Socket][Get_possible][Write] Ecriture dans socket de 'possible-all'\n");
        $nb_o = socket_write($GLOBALS['s'],"possible-all\n");
        fwrite($GLOBALS['f'],date("(h:i:s)\t").microtime()."\t"."[PHP][Socket][Get_possible][Write] --> Ecriture de ".$nb_o." octets effectuee\n");

        fwrite($GLOBALS['f'],date("(h:i:s)\t").microtime()."\t"."[PHP][Socket][Get_possible][Read=vars] Lecture du socket\n");
        $chaine = socket_read($GLOBALS['s'],1000,PHP_NORMAL_READ);
        fwrite($GLOBALS['f'],date("(h:i:s)\t").microtime()."\t"."[PHP][Socket][Get_possible][Read] --> Reception de ".$chaine);
        fwrite($GLOBALS['f'],date("(h:i:s)\t").microtime()."\t"."[PHP][Socket][Get_possible][Read=values] Lecture du socket\n");
        $chaine2 = socket_read($GLOBALS['s'],1000,PHP_NORMAL_READ);
        fwrite($GLOBALS['f'],date("(h:i:s)\t").microtime()."\t"."[PHP][Socket][Get_possible][Read] --> Reception de ".$chaine2);
        $d1=explode(";",substr($chaine,0,strlen($chaine)-1));
        $d2=explode(";",substr($chaine2,0,strlen($chaine2)-1));

        $data['Vars'] = $d1;
        $data['Values'] = $d2;
        
        return $data;
    }

    function get_data(){
        $data = array();
        #$data['Vars'] = $vars;
        fwrite($GLOBALS['f'],date("(h:i:s)\t").microtime()."\t"."[PHP][Socket][Get_data][Write] Ecriture dans socket de 'value-all'\n");
        $nb_o = socket_write($GLOBALS['s'],"value-all\n");
        fwrite($GLOBALS['f'],date("(h:i:s)\t").microtime()."\t"."[PHP][Socket][Get_data][Write] --> Ecriture de ".$nb_o." octets effectuee\n");

        fwrite($GLOBALS['f'],date("(h:i:s)\t").microtime()."\t"."[PHP][Socket][Get_data][Read=vars] Lecture du socket\n");
        $chaine = socket_read($GLOBALS['s'],1000,PHP_NORMAL_READ);
        fwrite($GLOBALS['f'],date("(h:i:s)\t").microtime()."\t"."[PHP][Socket][Get_data][Read] --> Reception de ".$chaine);
        fwrite($GLOBALS['f'],date("(h:i:s)\t").microtime()."\t"."[PHP][Socket][Get_data][Read=values] Lecture du socket\n");
        $chaine2 = socket_read($GLOBALS['s'],1000,PHP_NORMAL_READ);
        fwrite($GLOBALS['f'],date("(h:i:s)\t").microtime()."\t"."[PHP][Socket][Get_data][Read] --> Reception de ".$chaine2);
        $d1=explode(",",substr($chaine,0,strlen($chaine)-1));
        $d2=explode(",",substr($chaine2,0,strlen($chaine2)-1));

        $data['Vars'] = $d1;
        foreach($d1 as $ind => $v){
            $data[$v] = $d2[$ind];
        }
        
        return $data;
    }

    function recom($vars,$var){
        # RECUPERATION DES VARIABLES ET VERIFICATION DE L EXISTENCE DE LA VARIABLE ET DE SON CONTENU VIDE
        #test d existence
        $chaine = implode(",",$vars);
        $tab = array();
        $pos = strpos($chaine,$var);
        if($pos !== false){
            #test de vide
            fwrite($GLOBALS['f'],date("(h:i:s)\t").microtime()."\t"."[PHP][Socket][Recom][Write] Ecriture dans socket de 'isset'\n");
            fwrite($GLOBALS['f'],date("(h:i:s)\t").microtime()."\t"."[PHP][Socket][Recom][Write] Ecriture dans socket de '".$var."'\n");
            $nb_o = socket_write($GLOBALS['s'],"isset\n".$var."\n");
            fwrite($GLOBALS['f'],date("(h:i:s)\t").microtime()."\t"."[PHP][Socket][Recom][Write] --> Ecriture de ".$nb_o." octets effectuee\n");

            fwrite($GLOBALS['f'],date("(h:i:s)\t").microtime()."\t"."[PHP][Socket][Recom][Read=var] Lecture du socket\n");
            $chaine = socket_read($GLOBALS['s'],1000,PHP_NORMAL_READ);
            fwrite($GLOBALS['f'],date("(h:i:s)\t").microtime()."\t"."[PHP][Socket][Recom][Read] --> Reception de ".$chaine);

            if($chaine == "false\n"){
                $tab['ok'] = true;
                fwrite($GLOBALS['f'],date("(h:i:s)\t").microtime()."\t"."[PHP][Socket][Recom] ".$var." est adequat pour recom\n");
                
                fwrite($GLOBALS['f'],date("(h:i:s)\t").microtime()."\t"."[PHP][Socket][Recom][Write] Ecriture dans socket de 'reco'\n");
                fwrite($GLOBALS['f'],date("(h:i:s)\t").microtime()."\t"."[PHP][Socket][Recom][Write] Ecriture dans socket de '".$var."'\n");
                $nb_o = socket_write($GLOBALS['s'],"reco\n".$var."\n");
                fwrite($GLOBALS['f'],date("(h:i:s)\t").microtime()."\t"."[PHP][Socket][Recom][Write] --> Ecriture de ".$nb_o." octets effectuee\n");

                fwrite($GLOBALS['f'],date("(h:i:s)\t").microtime()."\t"."[PHP][Socket][Recom][Read] Lecture du socket\n");
                $chaine = socket_read($GLOBALS['s'],1000,PHP_NORMAL_READ);
                fwrite($GLOBALS['f'],date("(h:i:s)\t").microtime()."\t"."[PHP][Socket][Recom][Read=reco1] --> Reception de ".$chaine);
                $tab['reco'] = $chaine;
                fwrite($GLOBALS['f'],date("(h:i:s)\t").microtime()."\t"."[PHP][Socket][Recom][Read] Lecture du socket\n");
                $chaine = socket_read($GLOBALS['s'],1000,PHP_NORMAL_READ);
                fwrite($GLOBALS['f'],date("(h:i:s)\t").microtime()."\t"."[PHP][Socket][Recom][Read=reco2] --> Reception de ".$chaine);
                $tab['others'] = $chaine;
                fwrite($GLOBALS['f'],date("(h:i:s)\t").microtime()."\t"."[PHP][Socket][Recom][Read] Lecture du socket\n");
                $chaine = socket_read($GLOBALS['s'],1000,PHP_NORMAL_READ);
                fwrite($GLOBALS['f'],date("(h:i:s)\t").microtime()."\t"."[PHP][Socket][Recom][Read=reco3] --> Reception de ".$chaine);
                $tab['forbid'] = $chaine;
            }else{
                $tab['ok'] = false;
                fwrite($GLOBALS['f'],date("(h:i:s)\t").microtime()."\t"."[PHP][Socket][Recom] ".$var." est deja affectee\n");
            }
        }else{
            $tab['ok'] = false;
            fwrite($GLOBALS['f'],date("(h:i:s)\t").microtime()."\t"."[PHP][Socket][Recom] ".$var." n existe pas\n");
        }

        return $tab;
    }

    function set($vars,$var,$val){
        # RECUPERATION DES VARIABLES ET VERIFICATION DE L EXISTENCE DE LA VARIABLE ET DE SON CONTENU VIDE
        #test d existence
        $chaine = implode(",",$vars);
        $data = array();

        $pos = strpos($chaine,$var);
        if($pos !== false){
            #test de vide
            fwrite($GLOBALS['f'],date("(h:i:s)\t").microtime()."\t"."[PHP][Socket][Set][Write] Ecriture dans socket de 'isset'\n");
            fwrite($GLOBALS['f'],date("(h:i:s)\t").microtime()."\t"."[PHP][Socket][Set][Write] Ecriture dans socket de '".$var."'\n");
            $nb_o = socket_write($GLOBALS['s'],"isset\n".$var."\n");
            fwrite($GLOBALS['f'],date("(h:i:s)\t").microtime()."\t"."[PHP][Socket][Set][Write] --> Ecriture de ".$nb_o." octets effectuee\n");

            fwrite($GLOBALS['f'],date("(h:i:s)\t").microtime()."\t"."[PHP][Socket][Set][Read=var] Lecture du socket\n");
            $chaine = socket_read($GLOBALS['s'],1000,PHP_NORMAL_READ);
            fwrite($GLOBALS['f'],date("(h:i:s)\t").microtime()."\t"."[PHP][Socket][Set][Read] --> Reception de ".$chaine);

            if($chaine == "false\n"){
                #test si valeur ok
                fwrite($GLOBALS['f'],date("(h:i:s)\t").microtime()."\t"."[PHP][Socket][Set][Write] Ecriture dans socket de 'set'\n");
                fwrite($GLOBALS['f'],date("(h:i:s)\t").microtime()."\t"."[PHP][Socket][Set][Write] Ecriture dans socket de '".$var."'\n");
                fwrite($GLOBALS['f'],date("(h:i:s)\t").microtime()."\t"."[PHP][Socket][Set][Write] Ecriture dans socket de '".$val   ."'\n");
                $nb_o = socket_write($GLOBALS['s'],"set\n".$var."\n".$val."\n");
                fwrite($GLOBALS['f'],date("(h:i:s)\t").microtime()."\t"."[PHP][Socket][Set][Write] --> Ecriture de ".$nb_o." octets effectuee\n");
                $data['ok'] = true;

                #$data['Vars'] = $vars;
                fwrite($GLOBALS['f'],date("(h:i:s)\t").microtime()."\t"."[PHP][Socket][Set][Write] Ecriture dans socket de 'modif'\n");
                $nb_o = socket_write($GLOBALS['s'],"modif\n");
                fwrite($GLOBALS['f'],date("(h:i:s)\t").microtime()."\t"."[PHP][Socket][Set][Write] --> Ecriture de ".$nb_o." octets effectuee\n");

                fwrite($GLOBALS['f'],date("(h:i:s)\t").microtime()."\t"."[PHP][Socket][Set][Read=vars] Lecture du socket\n");
                $chaine = socket_read($GLOBALS['s'],1000,PHP_NORMAL_READ);
                fwrite($GLOBALS['f'],date("(h:i:s)\t").microtime()."\t"."[PHP][Socket][Set][Read] --> Reception de ".$chaine);
                fwrite($GLOBALS['f'],date("(h:i:s)\t").microtime()."\t"."[PHP][Socket][Set][Read=values] Lecture du socket\n");
                $chaine2 = socket_read($GLOBALS['s'],1000,PHP_NORMAL_READ);
                fwrite($GLOBALS['f'],date("(h:i:s)\t").microtime()."\t"."[PHP][Socket][Set][Read] --> Reception de ".$chaine2);
                $d1=substr($chaine,0,strlen($chaine)-1);
                $d2=substr($chaine2,0,strlen($chaine2)-1);

                $data['Vars'] = $d1;
                $data['Values'] = $d2;

            }else{
                $data['ok'] = false;
                fwrite($GLOBALS['f'],date("(h:i:s)\t").microtime()."\t"."[PHP][Socket][Set] ".$var." est deja affectee\n");
            }
        }else{
            $data['ok'] = false;
            fwrite($GLOBALS['f'],date("(h:i:s)\t").microtime()."\t"."[PHP][Socket][Set] ".$var." n existe pas\n");
        }

        return $data;
    }

    function raz(){
        fwrite($GLOBALS['f'],"\n".date("(h:i:s)\t").microtime()."\t"."[PHP][Socket][Raz][Write] Ecriture dans socket de 'reinit-all'\n");
        $nb_o = socket_write($GLOBALS['s'],"reinit-all\n");
        fwrite($GLOBALS['f'],date("(h:i:s)\t").microtime()."\t"."[PHP][Socket][Raz][Write] --> Ecriture de ".$nb_o." octets effectuee\n");
    }

    function byebye(){
        fwrite($GLOBALS['f'],"\n".date("(h:i:s)\t").microtime()."\t"."[PHP][Socket][Byebye][Write] Ecriture dans socket de 'exit'\n");
        $nb_o = socket_write($GLOBALS['s'],"exit\n");
        fwrite($GLOBALS['f'],date("(h:i:s)\t").microtime()."\t"."[PHP][Socket][Byebye][Write] --> Ecriture de ".$nb_o." octets effectuee\n");
    }
?>
