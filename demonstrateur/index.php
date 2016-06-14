<?php
    error_reporting(E_ALL);
    ini_set("display_errors", 1);
    ini_set('max_execution_time', 30);

    global $s;
    global $f; 
    $f = fopen("trace_".date("Y_m_d").".txt", "a+");

    $time_start = microtime(true);
    include("./sockets/create_socket.php");
    include("./sockets/functions_socket.php");
/* -------------------------------------------------------   config   ---------------------------------------------------------------*/
    $a = fopen("./param", "r") or die ("Impossible de load le fichier de param\n");
    $algo = fgets($a);
    $algo = substr($algo, 0 , strlen($algo) - 1);
    $dataset = fgets($a);
    $dataset = substr($dataset, 0 , strlen($dataset) - 1);
    fclose($a);
/* ----------------------------------------------------------------------------------------------------------------------------------*/


/* -------------------------------------------------------   session   ---------------------------------------------------------------*/
    function vidersession(){
        foreach($_SESSION as $cle => $element){
            unset($_SESSION[$cle]);
        }
    }

    function vider_cookie(){
        foreach($_COOKIE as $cle => $element){
            setcookie($cle, '', time()-3600);
        }

    }
/* -----------------------------------------------------------------------------------------------------------------------------------*/
?>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
    </head>
    <body>
        <center><img src="./logo_irit.jpg" align="center"></center><br>
        <h1><center><u><b><a href="index.php">RECOMMANDEUR</a></b></u></center></h1><br><br><br>
        <?php
            session_start();
    
            if(isset($_GET['dccnx'])){
                vidersession();
                vider_cookie();
                session_unset();
                session_destroy();
            
                byebye();
            }
            if(isset($_GET['algo'])){
                $a = fopen("./param", "w") or die ("Impossible de load le fichier de param\n");
                fwrite($a,$_GET['algo']."\n");
                fwrite($a,$_GET['dataset']."\n");
                fclose($a);

                init($_GET['algo'],$_GET['dataset']);

                $_SESSION['recommandateur'] = "recom";
            }

            if((isset($_GET['dccnx'])) || (! isset($_SESSION['recommandateur']))){
                /* on n'as pas de session */
                $a = fopen("./data", "r") or die ("Impossible de load le fichier de data\n");
                $da = fgets($a);
                $al = fgets($a);
                fclose($a);
                $dataset = explode(",", $da);
                $algo = explode(",", $al);
                echo '<form method="get" action="index.php">Dataset:<select name="dataset">';
                foreach($dataset as $v){
                    if($v == "champi"){
                        echo '<option value="'.$v.'" selected>'.$v.'</option>';
                    }else{
                        echo '<option value="'.$v.'">'.$v.'</option>';
                    }
                }
                echo '</select><br>Algorithme:<select name="algo">';
                foreach($algo as $v){
                    if($v == "naif"){
                        echo '<option value="'.$v.'" selected>'.$v.'</option>';
                    }else{
                        echo '<option value="'.$v.'">'.$v.'</option>';
                    }
                }
                echo '</select><br><p id="info"></p><input type="submit" value="GO!" onclick="document.getElementById(`info`).innerHTML=`apprentissage en cours`;"></form>';
            }else{
                echo '<form method="get" action="index.php"><input type="hidden" value="1" name="raz"><input type="submit" value="Redémarrer la session" align="right"></form><form method="get" action="index.php"><input type="hidden" value="1" name="dccnx"><input type="submit" value="Déconnexion" align="right"></form>';

                
                if(isset($_POST['Valeur'])){
                    echo "modif de ".$_POST['var']." -> ", $_POST['Valeur'];
                    $data = get_data();
                    $ok = set($data['Vars'], $_POST['var'], $_POST['Valeur']);
                }
                if(isset($_GET['raz'])){
                    echo "raz";
                    raz();
                }


                echo '<table border="1" width="100%"><tr><td width="45%"><form method="post" action="index.php"><input type="hidden" name="act" value="valeur"><table border="1" width="100%"><tr><th>Variables</th><th>Valeurs choisies</th></tr>';
                $data3 = get_data();
                //echo "data3[]:<br>\n";
                //print_r($data3);
                foreach($data3 as $key => $value){
                    if($key != "Vars"){
                        if($value == "null"){
                            echo '<tr><td><input type="radio" name="Variable" value="'.$key.'">'.$key.'</td><td> </td></tr>';
                        }else{
                            echo '<tr><td bgcolor="green"><input type="radio" name="Variable" value="'.$key.'" disabled>'.$key.'</td><td  bgcolor="green">"'.$value.'"</td></tr>';
                        }
                    }
                }
                if(isset($_POST['Variable'])){
                    $var = $_POST['Variable'];
                }else{
                    $var = "";
                }
                echo '</table></td><td width="10%"><input type="submit" value="Afficher Valeurs >>"></form>';
                if(isset($_POST['Variable'])){
                    echo '<form method="post" action="index.php"><input type="hidden" name="act2"><input type="hidden" name="var" value="'.$var.'"><input type="submit" value="<< Affecter">';
                }
                echo '</td><td width="45%"><table border="1" width="100%" valign="top"><tr><th>Valeurs possibles ["'.$var.'"]</th></tr>';

                if(isset($_POST['Variable'])){
                    $data2 = recom($data3['Vars'],$var);
                    foreach($data2 as $key => $value){
                        if(preg_match("#[a-zA-Z0-9?]+#isU",$value)){
                            if($key == "reco"){
                                echo '<tr><td bgcolor="yellow"><input type="radio" name="Valeur" value="'.$value.'" checked="checked">'.$value.'</td></tr>';
                            }else{
                                if($key == "others"){
                                    $pos = strpos(",",$value);
                                    if($pos === true){
                                        echo '<tr><td><input type="radio" name="Valeur" value="'.$value.'">'.$value.'</td></tr>';
                                    }else{
                                        $value = explode(",",$value);
                                        foreach($value as $value2){
                                            echo '<tr><td><input type="radio" name="Valeur" value="'.$value2.'">'.$value2.'</td></tr>';
                                        }
                                    }
                                }else{
                                    if($key == "forbid"){
                                        $pos = strpos(",",$value);
                                        if($pos === true){
                                            echo '<tr><td bgcolor="#BEBEBE"><input type="radio" name="Valeur" value="'.$value.'" disabled>'.$value.'</td></tr>';
                                        }else{
                                            $value = explode(",",$value);
                                            foreach($value as $value2){
                                                echo '<tr><td bgcolor="#BEBEBE"><input type="radio" name="Valeur" value="'.$value2.'" disabled>'.$value2.'</td></tr>';
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                    //echo "<br><br>\n\ndata2[]:<br>\n";
                    //print_r($data2);
                    //echo "<br>\n";
                }

                echo "</table></td></tr></table>";
            }

            include("./sockets/destroy_socket.php");
            $time_end = microtime(true);
            $time = $time_end - $time_start;
            echo "Realised in ".$time." seconds\n";
            fwrite($f,date("(h:i:s)\t").microtime()."\t"."[PHP] Realised in ".$time." seconds\n");
            fwrite($f,date("(h:i:s)\t").microtime()."\t"."--------------------------------------  END ---------------------------------------------\n\n\n");
            fclose($f);  
        ?>
    </body>
</html>
