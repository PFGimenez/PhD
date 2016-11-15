<?php
    include("entete.php");

    fwrite($fc,date("(h:i:s)\t").microtime()."\t"."[PHP][Cnnx] Affichage de la page de connexion\n");

    $a = fopen("./data", "r") or die ("Impossible de load le fichier de data\n");
    $da = fgets($a);
    $al = fgets($a);
    $da = substr($da,0,strlen($da)-1);
    $al = substr($al,0,strlen($al)-1);
    fclose($a);
    $dataset = explode(",", $da);
    $algo = explode(",", $al);
    echo '<form method="post" action="gest.php"><fieldset><legend>Demonstrator parameters</legend>Dataset: <select name="dataset">';
    foreach($dataset as $v){
        if($v == "renault_medium_header_contraintes"){
            echo '<option value="'.$v.'" selected>'.$v.'</option>';
        }else{
            echo '<option value="'.$v.'">'.$v.'</option>';
        }
    }
    echo '</select><br>Recommender: <select name="algo">';
    foreach($algo as $v){
        if($v == "v-naif"){
            echo '<option value="'.$v.'" selected>'.$v.'</option>';
        }else{
            echo '<option value="'.$v.'">'.$v.'</option>';
        }
    }
    echo '</select></fieldset><br><p id="info"></p><p align="right"><input type="submit"value="GO!" onclick="document.getElementById(`info`).innerHTML=`Learning, please wait…`;"></p></form>';

    include("bottom.php"); 
?>
