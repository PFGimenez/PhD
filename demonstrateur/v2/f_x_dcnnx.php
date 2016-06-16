<?php
    include("entete.php");

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
    
    include("bottom.php"); 
?>
