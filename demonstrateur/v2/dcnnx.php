<?php
    include("entete.php");
    
    fwrite($fc,date("(h:i:s)\t").microtime()."\t"."[PHP][Dcnnx] Affichage de la page de deconnexion\n");

    include("f_x_dcnnx.php");

    if(!isset($_GET['dcnnx'])){
        if (!isset($_SESSION['recommandateur'])){
            header('Location: index.php');
            exit();
        }else{
            header('Location: gest.php');
            exit();
        }
    }else{ 
        vidersession();
        vider_cookie();
        session_unset();
        session_destroy();
        $_GET['bye'] = 1;include("gerer.php");
        header('Location: index.php');
        exit();
    }
    header('Location: index.php');
    include("bottom.php"); 
?>
