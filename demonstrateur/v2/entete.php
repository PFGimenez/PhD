<?php
    error_reporting(E_ERROR|E_PARSE|E_CORE_ERROR|E_COMPILE_ERROR|E_RECOVERABLE_ERROR|E_NOTICE);
    ini_set("display_errors", 1);
    ini_set('max_execution_time', 30);
    if(session_id() == ""){session_start();}

    $trace_cnnx = "high"; // level : {"none","low","high"}
    $trace_socket = "high"; // level : {"none","low","high"}
    $nb_tentatives_cnnx = 3;

    global $trace_cnnx, $trace_socket, $nb_tentatives_cnnx;

    global $s;

    global $f; /* fic trace socket */
    global $fc; /* fic trace cnnx */
    $f = fopen("traces/trace_socket_".date("Y_m_d").".txt", "a+");
    $fc = fopen("traces/trace_cnnx_".date("Y_m_d").".txt", "a+");
?>
