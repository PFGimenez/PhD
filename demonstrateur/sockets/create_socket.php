<?php
    fwrite($f,date("(h:i:s)\t").microtime()."\t"."--------------------------------------  BEGIN ---------------------------------------------\n");
    fwrite($f,date("(h:i:s)\t").microtime()."\t"."[PHP] Creation du socket [domain: AF_INET, type: SOCK_STREAM, protocol: SOL_TCP]\n");
    $s = socket_create(AF_INET,SOCK_STREAM,SOL_TCP);
    if(socket_connect($s, "127.0.0.1", 4242)){
        fwrite($f,date("(h:i:s)\t").microtime()."\t"."[PHP][Socket] Connection au socket acceptee\n");
    }else{
        fwrite($f,date("(h:i:s)\t").microtime()."\t"."[PHP][Socket] Connection au socket refusee\n");
        fwrite($f,date("(h:i:s)\t").microtime()."\t"."[PHP] Fermeture du script\n");
        fwrite($f,date("(h:i:s)\t").microtime()."\t"."--------------------------------------  END ---------------------------------------------\n\n\n");
        exit();
    }
?>
