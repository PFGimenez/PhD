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
        <title>Interactive recommender demonstrator</title>
        <link rel="icon" type="image/x-icon" href="./img/favicon.ico" />
        <link rel="shortcut icon" href="./img/favicon.ico" type="image/x-icon" />
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
    </head>
    <body>
        <center><img src="./img/logo_irit.jpg" align="center"></center><br>
        <h1><center><b>Interactive recommender demonstrator</b></center></h1><br><br><br>
        <?php
                include("cnnx.php");
        ?>
<table border="0" width="100%">
            <tr>
                <td><b>Alarm:</b> classic Bayesian network dataset</td>
                <td><b>DRC:</b> experimental Bayesian inference</td>
            </tr>
            <tr>
                <td><b>Champi:</b> dataset on mushrooms</td>
                <td><b>RC:</b> Bayesian inference</td>
            </tr>
            <tr>
                <td><b>Child:</b> classic Bayesian network dataset</td>
                <td><b>Jointree:</b> fast Bayesian inference</td>
            </tr>
            <tr>
                <td><b>Congress:</b> dataset on american congress</td>
                <td><b>v-maj:</b> weighted majority voter (based on neighbourhood)</td>
            </tr>
            <tr>
                <td><b>Hailfinder:</b> classic Bayesian network dataset</td>
                <td><b>v-pop:</b> most popular choice (based on neighbourhood)</td>
            </tr>
            <tr>
                <td><b>Insurance:</b> classic Bayesian network dataset</td>
                <td><b>v-naif:</b> naïve Bayes voter (based on neighbourhood)</td>
            </tr>
            <tr>
                <td><b>Renault_small_header:</b> Renault "small" dataset (without constraints)</td>
                <td><b>naif:</b> naïve Bayesian network</td>
            </tr>
          <tr>
                <td><b>Renault_medium_header:</b> Renault "medium" dataset (without constraints)</td>
            </tr>
            <tr>
                <td><b>Renault_medium_header:</b> Renault "big" dataset (without constraints)</td>
            </tr>
<tr>
                <td><b>Renault_small_header_contraintes:</b> Renault "small" dataset (with constraints)</td>
            </tr>
            <tr>
                <td><b>Renault_medium_header_contraintes:</b> Renault "medium" dataset (with constraints)</td>
            </tr>
            <tr>
                <td><b>Renault_medium_header_contraintes:</b> Renault "big" dataset (with constraints)</td>
            </tr>

        </table>
    </body>
</html>
<?php
    include("bottom.php"); 
?>
