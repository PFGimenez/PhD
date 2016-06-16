<?php
    include("entete.php");
    fwrite($fc,date("(h:i:s)\t").microtime()."\t"."[PHP][Index] Affichage de la page d index\n");
    if(isset($_SESSION['recommandateur'])){
        echo "recommandateur";
        header('Location: gest.php');
        exit();
    }
?>
<html>
    <head>
        <title>Démonstrateur du recommandeur en configuration</title>
        <link rel="icon" type="image/x-icon" href="./img/favicon.ico" />
        <link rel="shortcut icon" href="./img/favicon.ico" type="image/x-icon" />
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
    </head>
    <body>
        <center><img src="./img/logo_irit.jpg" align="center"></center><br>
        <h1><center><u><b><a href="index.php">Démonstrateur du recommandeur</a></b></u></center></h1><br><br><br>
        <?php
                include("cnnx.php");
        ?>
    </body>
</html>
<?php
    include("bottom.php"); 
?>
