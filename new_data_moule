library(ggplot2)

#--------------------------------------------------------------------------------------------
#Parametres globaux
dataset_name = "@@DATASET@@"
taille_img_x = 1024
taille_img_y = 720
genere_img = TRUE #genere les deux images ou non
aff_graph = "erreur" #time / erreur --> erreur a faire plus tard
log_echec = "" # "" || "y"
#fin parametres globaux
#--------------------------------------------------------------------------------------------

#--------------------------------------------------------------------------------------------
#data
#result
rc = c(@@RC_RESULT@@)
drc = c(@@DRC_RESULT@@)
oracle = c(@@ORACLE_RESULT@@)
naif = c(@@NAIF_RESULT@@)
jointree = c(@@JOIN_TREE_RESULT@@)
wmv = c(@@WMV_RESULT@@)
lextree = c(@@LEX_TREE_RESULT@@)
vpop = c(@@VPOP_RESULT@@)

#result conf
c_rc = @@C_RC_RESULT@@
c_drc = @@C_DRC_RESULT@@
c_oracle = @@C_ORACLE_RESULT@@
c_naif = @@C_NAIF_RESULT@@
c_jointree = @@C_JOIN_TREE_RESULT@@
c_wmv = @@C_WMV_RESULT@@
c_lextree = @@C_LEX_TREE_RESULT@@
c_vpop = @@C_VPOP_RESULT@@

#temps
rctemps = c(@@RC_TIME@@)
drctemps = c(@@DRC_TIME@@)
naiftemps = c(@@NAIF_TIME@@)
jointreetemps = c(@@JOIN_TREE_TIME@@)
wmvtemps = c(@@WMV_TIME@@)
lextreetemps = c(@@LEX_TREE_TIME@@)
vpoptemps = c(@@VPOP_TIME@@)

#temps conf
c_rctemps = c(@@C_RC_TIME@@)
c_drctemps = c(@@C_DRC_TIME@@)
c_naiftemps = c(@@C_NAIF_TIME@@)
c_jointreetemps = c(@@C_JOIN_TREE_TIME@@)
c_wmvtemps = c(@@C_WMV_TIME@@)
c_lextreetemps = c(@@C_LEX_TREE_TIME@@)
c_vpoptemps = c(@@C_VPOP_TIME@@)
#fin data
#--------------------------------------------------------------------------------------------


#--------------------------------------------------------------------------------------------
#parametres graphes
# -- manuels
x_pas_erreur = 4
y_pas_erreur = 3
x_legend_erreur = 0
y_legend_erreur = 50
y_padding_erreur = 1

x_pas_time = 4
y_pas_time = 3
x_legend_time = 0
y_legend_time = 0.013
y_padding_time = 0.01

# -- auto calcul

#MAJ VERS ERROR
drc = 100-100*drc
oracle = 100-100*oracle
naif = 100-100*naif
rc = 100-100*rc
jointree = 100-100*jointree
wmv = 100-100*wmv
vpop = 100-100*vpop
lextree = 100-100*lextree

c_rc = 100*c_rc
c_drc = 100*c_drc
c_oracle = 100*c_oracle
c_naif = 100*c_naif
c_jointree = 100*c_jointree
c_wmv = 100*c_wmv
c_lextree = 100*c_lextree
c_vpop = 100*c_vpop
#FIN MAJ VERS ERROR

nb_val = length(rc)
min_val_erreur = ceiling(min(100-100*rc,100-100*drc,100-100*oracle,100-100*naif,100-100*jointree,100-100*wmv,100-100*lextree,100-100*vpop))
max_val_erreur = floor(max(100-100*rc,100-100*drc,100-100*oracle,100-100*naif,100-100*jointree,100-100*wmv,100-100*lextree,100-100*vpop))
size = 0:(nb_val-1)
x_lim_erreur = c(0,(nb_val-1))
if(min_val_erreur-y_padding_erreur <= 0){
  min_val_erreur = min_val_erreur+y_padding_erreur+0.00001
}
y_lim_erreur = c((min_val_erreur-y_padding_erreur),(max_val_erreur+y_padding_erreur))
x_axp_erreur = c(0, (nb_val-1), x_pas_erreur)
y_axp_erreur = c((min_val_erreur-y_padding_erreur),(max_val_erreur+y_padding_erreur),y_pas_erreur)

nb_val = length(rc)
min_val_time = min(rctemps,drctemps,naiftemps,jointreetemps,wmvtemps,lextreetemps,vpoptemps)
max_val_time = max(rctemps,drctemps,naiftemps,jointreetemps,wmvtemps,lextreetemps,vpoptemps)
x_lim_time = c(0,(nb_val-1))
if(min_val_time-y_padding_time <= 0){
  min_val_time = min_val_time+y_padding_time+0.00001
}
size = 0:(nb_val-1)
y_lim_time = c((min_val_time-y_padding_time),(max_val_time+y_padding_time))
x_axp_time = c(0, (nb_val-1), x_pas_erreur)
y_axp_time = c((min_val_time-y_padding_time),(max_val_time+y_padding_time),y_pas_time)
#fin parametres graphes
#--------------------------------------------------------------------------------------------

if(genere_img == TRUE){
  png(file=paste("Taux_erreur_sur_",dataset_name,"_plot.png",sep=""), bg="white", width=taille_img_x, height=taille_img_y, pointsize = 12)
}
if(genere_img == TRUE || aff_graph == "erreur"){
  plot(size, 100-100*drc, col="coral3", log=log_echec, main=paste("Error rate on dataset '",dataset_name,"'"), xlab="Number of known choices", ylab="Error rate (%)", type="o", xlim=x_lim_erreur, ylim=y_lim_erreur, pch=15, lwd=2.5, xaxp= x_axp_erreur, yaxp= y_axp_erreur, cex.main=2, cex.lab=1.5)
  points(size, oracle, col="black", type="o", pch=15, lty=1, lwd=2.5)
  points(size, rc, col="darkorchid3", type="o", pch=15, lty=1, lwd=2.5)
  points(size, naif, col="darkslategray4", type="o", pch=15, lty=1, lwd=2.5)
  points(size, jointree, col="springgreen4", type="o", pch=15, lty=1, lwd=2.5)
  points(size, wmv, col="indianred4", type="o", pch=15, lty=1, lwd=2.5)
  points(size, lextree, col="deepskyblue", type="o", pch=15, lty=1, lwd=2.5)
  points(size, vpop, col="goldenrod4", type="o", pch=15, lty=1, lwd=2.5)
  legend(legend=c("DRC","Oracle","RC","Naive Bayes","Join Tree","Weighted Majority Voter","Lex tree", "Most Popular Choice"), x=x_legend_erreur, y=y_legend_erreur,
         lty=c(1,1,1,1,1,1,1), #type de trait que des 1 pour trait plein (autant que de courbe)
         lwd=c(2.5,2.5,2.5,2.5,2.5,2.5,2.5), #epaisseur du trait 1 par courbe
         col=c("coral3","ivory4","darkorchid3","darkslategray4","springgreen4","indianred4","deepskyblue","goldenrod4"),
         pch=c(15,15,15,15,15,15,15)) #type d'icone des points (carree = 15) 1 par courbe)
  #title(paste("Error rate on dataset '",dataset_name,"'"))
}
if(genere_img == TRUE){
  dev.off()
}

if(genere_img == TRUE){
  png(file=paste("Temps_sur_",dataset_name,"_plot.png",sep=""), bg="white", width=taille_img_x, height=taille_img_y, pointsize = 12)
}
if(genere_img == TRUE || aff_graph == "time"){
  plot(size, drctemps, col="coral3", log="y", main=paste("Recommandation time on dataset '",dataset_name,"'"), xlab="Number of known choices", ylab="Time (ms)", type="o", xlim=x_lim_time, ylim=y_lim_time, pch=15, lwd=2.5, xaxp= x_axp_time, yaxp= y_axp_time, cex.main=2, cex.lab=1.5)
  points(size, rctemps, col="darkorchid3", type="o", pch=15, lty=1, lwd=2.5)
  points(size, naiftemps, col="darkslategray4", type="o", pch=15, lty=1, lwd=2.5)
  points(size, jointreetemps, col="springgreen4", type="o", pch=15, lty=1, lwd=2.5)
  points(size, wmvtemps, col="indianred4", type="o", pch=15, lty=1, lwd=2.5)
  points(size, lextreetemps, col="deepskyblue", type="o", pch=15, lty=1, lwd=2.5)
  points(size, vpoptemps, col="goldenrod4", type="o", pch=15, lty=1, lwd=2.5)
  legend(legend=c("DRC","RC","Naive Bayes","Join Tree", "Weighted Majority Voter","Lex tree", "Most Popular Choice"), x=x_legend_time, y=y_legend_time,
         lty=c(1,1,1,1,1,1,1), #type de trait que des 1 pour trait plein (autant que de courbe)
         lwd=c(2.5,2.5,2.5,2.5),
         col=c("coral3","darkorchid3","darkslategray4","springgreen4","indianred4","deepskyblue", "goldenrod4"),
         pch=c(15,15,15,15,15,15,15))#type d'icone des points (carree = 15) 1 par courbe)
  #title(paste("Recommandation time on dataset '",dataset_name,"'"))
}
if(genere_img == TRUE){
  dev.off()
}

if(genere_img == TRUE){
  png(file=paste("Taux_erreur_sur_",dataset_name,"_inter_conf_plot.png",sep=""), bg="white", width=taille_img_x, height=taille_img_y, pointsize = 12)
}
if(genere_img == TRUE || aff_graph == "erreur"){
  ggplot(NULL, aes(size)) +
    ggtitle(paste("Error rate on dataset '",dataset_name,"'")) + ylab("Error rate (%)") + xlab("Number of known choices") +
    geom_ribbon(aes(ymin=drc+c_drc,ymax=drc-c_drc), fill="coral", alpha=0.4) +
    geom_line(aes(y=drc), colour="coral3") + geom_point(aes(y=drc), colour="coral3") +
    geom_ribbon(aes(ymin=oracle+c_oracle,ymax=oracle-c_oracle), fill="grey56", alpha=0.4) +
    geom_line(aes(y=oracle), colour="black") + geom_point(aes(y=oracle), colour="black") +
    geom_ribbon(aes(ymin=rc+c_rc,ymax=rc-c_rc), fill="darkorchid1", alpha=0.4) +
    geom_line(aes(y=rc), colour="darkorchid3") + geom_point(aes(y=rc), colour="darkorchid3") +
    geom_ribbon(aes(ymin=naif+c_naif,ymax=naif-c_naif), fill="darkslategray1", alpha=0.4) +
    geom_line(aes(y=naif), colour="darkslategray4") + geom_point(aes(y=naif), colour="darkslategray4") +
    geom_ribbon(aes(ymin=jointree+c_jointree,ymax=jointree-c_jointree), fill="springgreen", alpha=0.4) +
    geom_line(aes(y=jointree), colour="springgreen4") + geom_point(aes(y=jointree), colour="springgreen4") +
    geom_ribbon(aes(ymin=wmv+c_wmv,ymax=wmv-c_wmv), fill="indianred1", alpha=0.4) +
    geom_line(aes(y=wmv), colour="indianred4") + geom_point(aes(y=wmv), colour="indianred4") +
    geom_ribbon(aes(ymin=lextree+c_lextree,ymax=lextree-c_lextree), fill="deepskyblue", alpha=0.4) +
    geom_line(aes(y=lextree), colour="deepskyblue4") + geom_point(aes(y=lextree), colour="deepskyblue4") +
    geom_ribbon(aes(ymin=vpop+c_vpop,ymax=vpop-c_vpop), fill="goldenrod", alpha=0.4) +
    geom_line(aes(y=vpop), colour="goldenrod4") + geom_point(aes(y=vpop), colour="goldenrod4")
}
if(genere_img == TRUE){
  dev.off()
}

if(genere_img == TRUE){
  png(file=paste("Temps_sur_",dataset_name,"_inter_conf_plot.png",sep=""), bg="white", width=taille_img_x, height=taille_img_y, pointsize = 12)
}
if(genere_img == TRUE || aff_graph == "time"){
  ggplot(NULL, aes(size)) + scale_y_log10() +
    ggtitle(paste("Recommendation time on dataset '",dataset_name,"'")) + ylab("Time (ms)") + xlab("Number of known choices") +
    geom_ribbon(aes(ymin=drctemps+c_drctemps,ymax=drctemps-c_drctemps), fill="coral", alpha=0.4) +
    geom_line(aes(y=drctemps), colour="coral3") + geom_point(aes(y=drctemps), colour="coral3") +
    geom_ribbon(aes(ymin=rctemps+c_rctemps,ymax=rctemps-c_rctemps), fill="darkorchid1", alpha=0.4) +
    geom_line(aes(y=rctemps), colour="darkorchid3") + geom_point(aes(y=rctemps), colour="darkorchid3") +
    geom_ribbon(aes(ymin=naiftemps+c_naiftemps,ymax=naiftemps-c_naiftemps), fill="darkslategray1", alpha=0.4) +
    geom_line(aes(y=naiftemps), colour="darkslategray4") + geom_point(aes(y=naiftemps), colour="darkslategray4") +
    geom_ribbon(aes(ymin=jointreetemps+c_jointreetemps,ymax=jointreetemps-c_jointreetemps), fill="springgreen", alpha=0.4) +
    geom_line(aes(y=jointreetemps), colour="springgreen4") + geom_point(aes(y=jointreetemps), colour="springgreen4") +
    geom_ribbon(aes(ymin=wmvtemps+c_wmvtemps,ymax=wmvtemps-c_wmvtemps), fill="indianred1", alpha=0.4) +
    geom_line(aes(y=wmvtemps), colour="indianred4") + geom_point(aes(y=wmvtemps), colour="indianred4") +
    geom_ribbon(aes(ymin=lextreetemps+c_lextreetemps,ymax=lextreetemps-c_lextreetemps), fill="deepskyblue", alpha=0.4) +
    geom_line(aes(y=lextreetemps), colour="deepskyblue4") + geom_point(aes(y=lextreetemps), colour="deepskyblue4") +
    geom_ribbon(aes(ymin=vpoptemps+c_vpoptemps,ymax=vpoptemps-c_vpoptemps), fill="goldenrod", alpha=0.4) +
    geom_line(aes(y=vpoptemps), colour="goldenrod4") + geom_point(aes(y=vpoptemps), colour="goldenrod4")
}
if(genere_img == TRUE){
  dev.off()
}
