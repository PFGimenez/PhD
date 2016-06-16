function getButton(variable){
    //alert("vous voulez les reco pour la variable "+variable);
    //on active tous les bouttons de valeurs
    for(i=0; i<document.getElementById(variable+"_nbval").innerHTML; i++){
        document.getElementById("radio_"+variable+"_"+i).disabled = false;
    }
    document.getElementById("comm_php").src="gerer.php?reco=1&var="+variable;
}

function getValue(variable,num,value,send=1){
    //alert("vous voulez affecter la valeur "+value+" a la variable "+variable);
    document.getElementById("p_"+variable+"_"+num).style.color = "#00FF00";
    document.getElementsByName("r_"+variable)[0].disabled = true;
    document.getElementsByName("r_"+variable)[0].checked = true;
    document.getElementById("radio_"+variable+"_"+num).disabled = true;
    document.getElementById("radio_"+variable+"_"+num).checked = true;

    //on desactive tous les bouttons de valeur
    for(i=0; i<document.getElementById(variable+"_nbval").innerHTML; i++){
        document.getElementById("radio_"+variable+"_"+i).disabled = true;
        if(i != num){
            document.getElementById("radio_"+variable+"_"+i).checked = false;
            document.getElementById("p_"+variable+"_"+i).style.color = "#000000";
        }
    }
    if(send != 0){
        document.getElementById("comm_php").src="gerer.php?set=1&var="+variable+"&val="+value;
    }
}

function getIframeContent(){
    IE  = window.ActiveXObject ? true : false;
    MOZ = window.sidebar       ? true : false;
 
    if(IE){
        edoc = window.frames['comm_php'].document;
        return edoc.body.innerHTML;
    }
  
    if(MOZ) {
        edoc = document.getElementById('comm_php').contentDocument;
        return document.getElementById("comm_php").contentDocument.body.innerHTML;
    }
}

function getNum(variable,valeur){
    for(i=0; i<document.getElementById(variable+"_nbval").innerHTML; i++){
        if(document.getElementById("p_"+variable+"_"+i).innerHTML == valeur){
            return i;
        }
    }
}

function traiteData(){
    content = getIframeContent();
    if(content.match(/reco/) != null){ // si on a recu le retour de recom
        //alert(getIframeContent());
        variable = content.match(/var:([^\n]+)\n/)[1];
        reco = content.match(/reco:([^\n]+)\n/)[1];
        others = content.match(/others:([^\n]*)\n/)[1];
        others = others.split(/,/);
        forbid = content.match(/forbid:([^\n]*)\n/)[1];
        forbid = forbid.split(/,/);
        //alert("Traitement de la variable "+variable);
        //alert("reco:"+reco+"/");
        document.getElementsByName("radio_"+variable+"_"+reco)[0].checked = true;
        document.getElementsByName("p_"+variable+"_"+reco)[0].style.color = "#0000FF";
        //for (var i = 0; i < others.length; i++) {
            //alert("others["+i+"]:"+others[i]+"/");
        //    if(others[i] != ""){
        //        
        //    }
        //}
        for (var i = 0; i < forbid.length; i++) {
            //alert("forbid["+i+"]:"+forbid[i]+"/");
            if(forbid[i] != ""){
                document.getElementsByName("radio_"+variable+"_"+forbid[i])[0].checked = false;
                document.getElementsByName("radio_"+variable+"_"+forbid[i])[0].disabled = true;
                document.getElementsByName("p_"+variable+"_"+forbid[i])[0].style.color = "#FF0000";
            }
        }
    }else{
        if(content.match(/Vars/) != null){ // si on a recu le retour de set
            //alert(getIframeContent());
            variable = content.match(/Vars-([^\n]+)\n/)[1];
            values = content.match(/Values-([^\n]+)\n/)[1];
           
            variable = variable.split(/,/);
            values = values.split(/,/);
            for (var i = 0; i < variable.length; i++) {
                //alert("Modif de "+variable[i]+" vers "+values[i]+".");
                getValue(variable[i],getNum(variable[i],values[i]),values[i],0)
                document.getElementsByName("r_"+variable[i])[0].disabled = true;
                document.getElementsByName("r_"+variable[i])[0].checked = true;
            }
        }
    }
}

function raz(){
    //alert("vous voulez reinitialiser toutes les variables");

    for(i=0; i<document.getElementById("nb_val").innerHTML; i++){
        var nom = document.getElementById("var_"+i).value;
        document.getElementById("var_"+i).checked = false;
        for(j=0; j<document.getElementById(nom+"_nbval").innerHTML; j++){
            document.getElementById("radio_"+nom+"_"+j).disabled = "true";
            document.getElementById("radio_"+nom+"_"+j).checked = false;
            document.getElementById("p_"+nom+"_"+j).style.color = "#000000";
        }
    }
    document.getElementById("comm_php").src="gerer.php?raz=1";
}
