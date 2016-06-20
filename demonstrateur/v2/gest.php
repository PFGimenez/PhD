<?php
    include("entete.php");
    
    fwrite($fc,date("(h:i:s)\t").microtime()."\t"."[PHP][Gest] Affichage de la page de gestion\n");

    /* gestion de la connexion */
    if(isset($_POST['algo'])){
        $a = fopen("./param", "w") or die ("Impossible de load le fichier de param\n");
        fwrite($a,$_POST['algo']."\n");
        fwrite($a,$_POST['dataset']."\n");
        fclose($a);

        fwrite($fc,date("(h:i:s)\t").microtime()."\t"."[PHP][Gest] Init de la gestion pour l algo: ".$_POST['algo']." et le dataset: ".$_POST['dataset']."\n");

        $_GET['init'] = 1;include("gerer.php");
        $_SESSION['recommandateur'] = "recom";
    }

    if(!isset($_SESSION['recommandateur'])){
        header('Location: index.php');
        exit();
    }
?>  
      
<html>
    <head>
        <title>Démonstrateur du recommandeur en configuration</title>
        <link rel="icon" type="image/x-icon" href="./img/favicon.ico" />
        <link rel="shortcut icon" href="./img/favicon.ico" type="image/x-icon" />
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
        <script type="text/javascript" src="gest.js"></script>
    </head>
    <body>
        <center><img src="./img/logo_irit.jpg" align="center"></center><br>
        <h1><center><u><b><a href="index.php">Démonstrateur du recommandeur</a></b></u></center></h1><br><br><br>
        <form method="get" action="dcnnx.php"><input type="hidden" value="1" name="dcnnx"><p align="right"><input type="submit" value="Déconnexion" align="right"></p></form>
        <br>
        <?php
            $a = fopen("./param", "r") or die ("Impossible de load le fichier de param\n");
            $da = fgets($a);
            $al = fgets($a);
            $var = fgets($a);
            $val = fgets($a);
            $da = substr($da,0,strlen($da)-1);
            $al = substr($al,0,strlen($al)-1);
            $var = substr($var,0,strlen($var)-1);
            $val = substr($val,0,strlen($val)-1);
            fclose($a);
            $variable = explode(";",$var);
            $valeur = explode(";",$val);
            echo '<br>Initialisé pour l algorithme '.$al.' et le dataset '.$da.'.&nbsp;&nbsp;&nbsp;&nbsp;<input type="button" onclick="raz();" value="Reinitialiser"><br><table border="0"><tr><td>Pour un total de <td><p id="nb_val">'.sizeof($variable).'</p></td><td> variables</td><td>.</td></tr><table><br>'."\n";
         ?>
        <table border="1" width="100%">
            <tr>
                <th colspan="3">Legende des couleurs</th>
            </tr>
            <tr>
                <th>Colonne</th>
                <th>Couleur</th>
                <th>Signification</th>
            </tr>
            <tr>
                <td>Valeurs</td>
                <td><font color="#04B404">val</font></td>
                <td>Valeur affectée à la variable</td>
            </tr>
            <tr>
                <td>Valeurs</td>
                <td><font color="#013ADF">val</font></td>
                <td>Valeur recommendée</td>
            </tr>
            <tr>
                <td>Valeurs</td>
                <td><font color="#B40404">val</font></td>
                <td>Valeur impossible</td>
            </tr>
            <tr>
                <td>Valeurs</td>
                <td><font color="#000000">val</font></td>
                <td>Valeur possibles ou non affectee</td>
            </tr>   
        </table><br><br>
        <table border="1" width="100%">
            <tr>
                <th>Variables</th>
                <th>Valeurs</th>
            </tr>
            <?php
                $i = 0;
                foreach($valeur as $v){
                    $valeur[$i] = explode(",",$v);
                    $i++;
                }
                foreach($variable as $key => $value){
                    echo '<tr><td><table border="0">';
                    echo '<tr><td style="font-weight:bold;"><input type="radio" name="r_'.$value.'" id="var_'.$key.'" value="'.$value.'" onclick="getButton(`'.$value.'`);"></td><td style="font-weight:bold;">'.$value.'</td><td style="font-weight:bold;">[</td><td id="'.$value.'_nbval">'.sizeof($valeur[$key]).'</td><td style="font-weight:bold;">valeurs ]</td>';
                    echo '</tr></table></td><td style="font-weight:bold;"><table border="0"><tr>';
                    foreach($valeur[$key] as $key2 => $v){
                        echo '<td style="font-weight:bold;"><input type="radio" id="radio_'.$value.'_'.$key2.'" name="radio_'.$value.'_'.$v.'" onclick="getValue(`'.$value.'`,'.$key2.',`'.$v.'`);" disabled="disabled"></td><td style="font-weight:bold;"><p style="font-weight:bold;" id="p_'.$value.'_'.$key2.'" name="p_'.$value.'_'.$v.'">'.$v."</p></td>";
                    }
                    
                    echo "</tr></table></td></tr>";
                }
            ?>
        </table>
        <iframe name="comm_php" id="comm_php" width="100%" height="40" src="" style="visibility=hidden" onload="traiteData();"></iframe>
    </body>
</html>

<?php
    include("bottom.php"); 
?>

