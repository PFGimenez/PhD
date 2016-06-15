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
    
    $val = $data2['reco'];
    $ok = set($data['Vars'],$var,$val);
    if($ok){
        echo "<br>\nset ok<br>\n";
    }else{
        echo "<br>\nset ko<br>\n";
    }
    echo "<br>\n";

    $data2 = get_data();
    echo "data2[]:<br>\n";
    print_r($data2);

    echo "<br>\n<br>\nraz<br>\n";
    raz();
    echo "<br>\n";

    $var = $data['Vars'][0];
    $data3 = get_data();
    echo "data3[]:<br>\n";
    print_r($data3);

    byebye();
    $time_end = microtime(true);
    $time = $time_end - $time_start;
?>

