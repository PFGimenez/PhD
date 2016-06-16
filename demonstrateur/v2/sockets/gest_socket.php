<?php
    error_reporting(E_ALL);
    ini_set("display_errors", 1);
    ini_set('max_execution_time', 30);

    global $s;
    global $f; 
    $f = fopen("trace_".date("Y_m_d").".txt", "a+");

    include("create_socket.php");
    include("set.php");
    include("destroy_socket.php");
    
    echo "Realised in ".$time." seconds\n";
    fwrite($f,date("(h:i:s)\t").microtime()."\t"."[PHP] Realised in ".$time." seconds\n");
    fwrite($f,date("(h:i:s)\t").microtime()."\t"."--------------------------------------  END ---------------------------------------------\n\n\n");
    fclose($f);
?>
