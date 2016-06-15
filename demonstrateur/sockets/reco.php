<?php
    $time_start = microtime(true);
    include("functions_socket.php");

    init("lextree","renault_small_header");

    $data = get_data();
    echo "data[]:<br>\n";
    print_r($data);

    $var = $data['Vars'][0];
    $data2 = recom($data['Vars'],$var);
    echo "<br><br>\n\ndata2[]:<br>\n";
    print_r($data2);
    echo "<br>\n";

    byebye();
    $time_end = microtime(true);
    $time = $time_end - $time_start;
?>

