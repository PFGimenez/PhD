<?php
    fwrite($f,date("(h:i:s)\t").microtime()."\t"."[PHP][Socket] Deconnexion du socket\n");
    socket_shutdown($s);

    fwrite($f,date("(h:i:s)\t").microtime()."\t"."[PHP] Fermeture du socket\n");
    socket_close($s);
    fwrite($f,date("(h:i:s)\t").microtime()."\t"."--------------------------------------  END ---------------------------------------------\n\n\n");
?>
