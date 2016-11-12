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
        <title>Interactive recommender demonstrator</title>
        <link rel="icon" type="image/x-icon" href="./img/favicon.ico" />
        <link rel="shortcut icon" href="./img/favicon.ico" type="image/x-icon" />
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
        <script type="text/javascript" src="gest.js"></script>
    </head>
    <body>
        <center><img src="./img/logo_irit.jpg" align="center"></center><br>
        <h1><center><b>Interactive recommender demonstrator</b></center></h1><br><br><br>
        <form method="get" action="dcnnx.php"><input type="hidden" value="1" name="dcnnx"><p align="right"><input type="submit" value="Change parameters" align="right"></p></form>
        <br>
        <?php
            $a = fopen("./param", "r") or die ("Impossible de load le fichier de param\n");
            $al = fgets($a);
            $da = fgets($a);
            $var = fgets($a);
            $val = fgets($a);
            $da = substr($da,0,strlen($da)-1);
            $al = substr($al,0,strlen($al)-1);
            $var = substr($var,0,strlen($var)-1);
            $val = substr($val,0,strlen($val)-1);
            fclose($a);
            $variable = explode(";",$var);
            $valeur = explode(";",$val);
            echo '<br>Recommender algorithm <b>'.$al.'</b> on dataset <b>'.$da.'</b>.&nbsp;&nbsp;&nbsp;&nbsp;<input type="button" onclick="raz();" value="Clear the session"><br><table border="0"><tr><td>There are <td><p id="nb_val">'.sizeof($variable).'</p></td><td> variables.</td></tr><table><br>'."\n";
         ?>
        <table border="0" width="50%">
            <tr>
                <th colspan="4">Color key</th>
            </tr>
            <tr>
                <td><font color="#04B404">Affected value</font></td>
                <td><font color="#013ADF">Recommended value</font></td>
                <td><font color="#B40404">Impossible value</font></td>
                <td><font color="#000000">Unaffected possible value</font></td>
            </tr>
        </table><br><br>
        <table border="1" width="100%">
            <tr>
                <th>Variables</th>
                <th>Values</th>
            </tr>
            <?php
                $i = 0;
                foreach($valeur as $v){
                    $valeur[$i] = explode(",",$v);
                    $i++;
                }
                foreach($variable as $key => $value){
                    echo '<tr><td><table border="0">';
                    echo '<tr><td><img name="img_'.$key.'" id="img_'.$value.'" width="20" height="20" src="./img/vide.png" onclick="getImg(`'.$value.'`);"></td><td style="font-weight:bold;"><input type="radio" name="r_'.$value.'" id="var_'.$key.'" value="'.$value.'" onclick="getButton(`'.$value.'`);"></td><td style="font-weight:bold;">'.$value.'</td><td style="font-weight:bold;">[</td><td id="'.$value.'_nbval">'.sizeof($valeur[$key]).'</td><td style="font-weight:bold;">values ]</td>';
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

