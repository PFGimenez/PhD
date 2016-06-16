<?php
    include("entete.php");

    include("./sockets/create_socket.php");
    include("./sockets/functions_socket.php");

    if(isset($_GET['init'])){
        fwrite($fc,date("(h:i:s)\t").microtime()."\t"."[PHP][Gerer] Init()\n");

        $a = fopen("./param", "r") or die ("Impossible de load le fichier de param\n");
        $da = fgets($a);
        $al = fgets($a);
        $da = substr($da,0,strlen($da)-1);
        $al = substr($al,0,strlen($al)-1);
        fclose($a);

        init($da,$al);
        $v = get_possible();

        $a = fopen("./param", "w") or die ("Impossible de load le fichier de param\n");
        fwrite($a,$da."\n");
        fwrite($a,$al."\n");
        fwrite($a,implode(";",$v['Vars'])."\n");
        fwrite($a,implode(";",$v['Values'])."\n");
        fclose($a);
    }

    if(isset($_GET['reco'])){
        fwrite($fc,date("(h:i:s)\t").microtime()."\t"."[PHP][Gerer] Reco(".$_GET['var'].")\n");

        $a = fopen("./param", "r") or die ("Impossible de load le fichier de param\n");
        $da = fgets($a);
        $al = fgets($a);
        $vars = fgets($a);
        $val = fgets($a);
        $da = substr($da,0,strlen($da)-1);
        $al = substr($al,0,strlen($al)-1);
        $vars = substr($vars,0,strlen($vars)-1);
        $val = substr($val,0,strlen($val)-1);
        fclose($a);
        $vars = explode(";",$vars);
        $var = $_GET['var'];
        $data2 = recom($vars,$var);
        echo "var:".$_GET['var']."\n";
        
        foreach($data2 as $key => $v){
            if($key != "ok"){       
                echo $key.":".$v;
            }
        }
        echo "\n";
    }

    if(isset($_GET['set'])){
        fwrite($fc,date("(h:i:s)\t").microtime()."\t"."[PHP][Gerer] Reco(".$_GET['var'].")\n");

        $a = fopen("./param", "r") or die ("Impossible de load le fichier de param\n");
        $da = fgets($a);
        $al = fgets($a);
        $vars = fgets($a);
        $val = fgets($a);
        $da = substr($da,0,strlen($da)-1);
        $al = substr($al,0,strlen($al)-1);
        $vars = substr($vars,0,strlen($vars)-1);
        $val = substr($val,0,strlen($val)-1);
        fclose($a);
        $vars = explode(";",$vars);

        $var = $_GET['var'];
        $val = $_GET['val'];

        $data = set($vars, $var, $val);
        //echo "set --> ".$data['ok'];
        if($data['ok'] && count($data) != 0){
            foreach($data as $key => $v){
                if($key != 'ok'){
                    echo $key."-".$v."\n";
                }
            }
        }
    }

    if(isset($_GET['raz'])){
        echo "raz --> 1";
        raz();
    }

    if(isset($_GET['bye'])){
        fwrite($fc,date("(h:i:s)\t").microtime()."\t"."[PHP][Gerer] ByeBye()\n");

        $a = fopen("./param", "w") or die ("Impossible de load le fichier de param\n");
        fclose($a);
        byebye();
    }

    include("./sockets/destroy_socket.php");

    include("bottom.php"); 
?>
