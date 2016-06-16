<?php
    $time_start = microtime(true);
    include("functions_socket.php");

    init("lextree","renault_small_header");

    $data = get_data();
    echo "data[]:<br>\n";
    print_r($data);
    
    $var = "v1";
    $val = "v";
    $ok = set($data['Vars'],$var,$val);
    if($ok){
        echo "<br>\nset ok<br>\n";
    }else{
        echo "<br>\nset ko<br>\n";
    }
    echo "<br>\n";

    byebye();
    $time_end = microtime(true);
    $time = $time_end - $time_start;
?>

