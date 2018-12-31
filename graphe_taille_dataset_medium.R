library(ggplot2)

#--------------------------------------------------------------------------------------------
#Parametres globaux
dataset_name = "@@DATASET@@"
algo_name = "@@ALGO@@"
taille_img_x = 1024/2
taille_img_y = 720/2
#fin parametres globaux
#--------------------------------------------------------------------------------------------

#--------------------------------------------------------------------------------------------
#data

oracle = c(@@ORACLE_RESULT@@)
courbe1 = c(@@COURBE1_RESULT@@)
courbe1_2 = c(@@COURBE1_2_RESULT@@)
courbe1_4 = c(@@COURBE1_4_RESULT@@)
courbe1_8 = c(@@COURBE1_8_RESULT@@)
courbe1_16 = c(@@COURBE1_16_RESULT@@)
courbe1_32= c(@@COURBE1_32_RESULT@@)
courbe1_64 = c(@@COURBE1_64_RESULT@@)

tps_courbe1 = c(@@COURBE1_TIME@@)
tps_courbe1_2 = c(@@COURBE1_2_TIME@@)
tps_courbe1_4 = c(@@COURBE1_4_TIME@@)
tps_courbe1_8 = c(@@COURBE1_8_TIME@@)
tps_courbe1_16 = c(@@COURBE1_16_TIME@@)
tps_courbe1_32= c(@@COURBE1_32_TIME@@)
tps_courbe1_64 = c(@@COURBE1_64_TIME@@)

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
oracle = 100-100*oracle
courbe1 = 100-100*courbe1
courbe1_2 = 100-100*courbe1_2
courbe1_4 = 100-100*courbe1_4
courbe1_8 = 100-100*courbe1_8
courbe1_16 = 100-100*courbe1_16
courbe1_32 = 100-100*courbe1_32
courbe1_64 = 100-100*courbe1_64

nb_val = length(oracle)

min_val_erreur = 30000
min_val_erreur = min(min_val_erreur,100-100*oracle)
min_val_erreur = min(min_val_erreur,100-100*courbe1)
min_val_erreur = min(min_val_erreur,100-100*courbe1_2)
min_val_erreur = min(min_val_erreur,100-100*courbe1_4)
min_val_erreur = min(min_val_erreur,100-100*courbe1_8)
min_val_erreur = min(min_val_erreur,100-100*courbe1_16)
min_val_erreur = min(min_val_erreur,100-100*courbe1_32)
min_val_erreur = min(min_val_erreur,100-100*courbe1_64)
min_val_erreur = ceiling(min_val_erreur)

max_val_erreur = 0
max_val_erreur = max(max_val_erreur,100-100*oracle)
max_val_erreur = max(max_val_erreur,100-100*courbe1)
max_val_erreur = max(max_val_erreur,100-100*courbe1_2)
max_val_erreur = max(max_val_erreur,100-100*courbe1_4)
max_val_erreur = max(max_val_erreur,100-100*courbe1_8)
max_val_erreur = max(max_val_erreur,100-100*courbe1_16)
max_val_erreur = max(max_val_erreur,100-100*courbe1_32)
max_val_erreur = max(max_val_erreur,100-100*courbe1_64)
max_val_erreur = floor(max_val_erreur)

size = 0:(nb_val-1)
x_lim_erreur = c(0,(nb_val-1))
if(min_val_erreur-y_padding_erreur <= 0){
  min_val_erreur = min_val_erreur+y_padding_erreur+0.00001
}
y_lim_erreur = c((min_val_erreur-y_padding_erreur),(max_val_erreur+y_padding_erreur))
x_axp_erreur = c(0, (nb_val-1), x_pas_erreur)
y_axp_erreur = c((min_val_erreur-y_padding_erreur),(max_val_erreur+y_padding_erreur),y_pas_erreur)

min_val_time = 30000
min_val_time = min(min_val_erreur,100-100*rctemps)
min_val_time = min(min_val_erreur,100-100*dRCtemps)
min_val_time = min(min_val_erreur,100-100*Naiftemps)
min_val_time = min(min_val_erreur,100-100*jointreetemps)
min_val_time = min(min_val_erreur,100-100*wmvtemps)
min_val_time = min(min_val_erreur,100-100*lextreetemps)
min_val_time = min(min_val_erreur,100-100*vpoptemps)
min_val_time = ceiling(min_val_erreur)

max_val_time = 30000
max_val_time = max(max_val_time,rctemps)
max_val_time = max(max_val_time,dRCtemps)
max_val_time = max(max_val_time,Naiftemps)
max_val_time = max(max_val_time,jointreetemps)
max_val_time = max(max_val_time,wmvtemps)
max_val_time = max(max_val_time,lextreetemps)
max_val_time = max(max_val_time,vpoptemps)
max_val_time = floor(max_val_time)

x_lim_time = c(0,(nb_val-1))
if(min_val_time-y_padding_time <= 0){
  min_val_time = min_val_time+y_padding_time+0.00001
}

png(file=paste("Taux_erreur_sur_",dataset_name,"_",algo_name,".png",sep=""), bg="white", width=taille_img_x, height=taille_img_y, pointsize = 12)
(ggplot(NULL, aes(size)) + #scale_y_log10(breaks = round(seq(0, 100, by = 5),1)) + annotation_logticks(sides="l") +
  ylab("Error rate (%)") + xlab("Number of known choices") + theme_bw() #+ theme(legend.position="bottom")
+geom_line(aes(y=dRC, colour="Complete sample",shape="a")) + geom_point(aes(y=dRC), colour="coral3") 
+geom_line(aes(y=oracle, colour="1/2 sample",shape="b")) + geom_point(aes(y=oracle), colour="firebrick3") 
+geom_line(aes(y=oracleVrai, colour="Oracle",shape="b")) + geom_point(aes(y=oracleVrai), colour="black") 
+geom_line(aes(y=rc, colour="1/4 sample")) + geom_point(aes(y=rc), colour="darkorchid3") 
+geom_line(aes(y=Naif, colour="1/8 sample")) + geom_point(aes(y=Naif), colour="darkslategray4") 
+geom_line(aes(y=jointree, colour="1/16 sample")) + geom_point(aes(y=jointree), colour="springgreen4") 
+geom_line(aes(y=wmv, colour="1/32 sample")) + geom_point(aes(y=wmv), colour="indianred4") 
+geom_line(aes(y=lextree, colour="1/64 sample")) + geom_point(aes(y=lextree), colour="deepskyblue4") 
+ scale_colour_manual(name = 'Legend', guide = 'legend',
                      limits = c(NULL
                                 ,'1/64 sample' #lextree
                                 ,'1/32 sample' #wmv
                                 ,'1/16 sample' #jointree
                                 ,'1/8 sample' #Naif
                                 ,'1/4 sample' #rc
                                 ,'1/2 sample' #oracle
                                 ,'Complete sample' #dRC
                                 ,'Oracle' #oracleVrai
                      ),                      values =c(NULL
                                ,'Complete sample'='coral' #dRC
                                ,'Oracle'='black' #oracleVrai
                                ,'1/2 sample'='firebrick3' #oracle
                                ,'1/4 sample'='darkorchid1' #rc
                                ,'1/8 sample'='darkslategray1' #Naif
                                ,'1/16 sample'='springgreen' #jointree
                                ,'1/32 sample'='indianred1' #wmv
                                ,'1/64 sample'='deepskyblue4' #lextree
                      )))

dev.off()

png(file=paste("Temps_sur_",dataset_name,"_",algo_name,".png",sep=""), bg="white", width=taille_img_x, height=taille_img_y, pointsize = 12)
(ggplot(NULL, aes(size))
+ scale_y_log10(
  breaks = scales::trans_breaks("log10", function(x) 10^x),
  labels = scales::trans_format("log10", function(x) round(10^x,2)))
+ annotation_logticks(sides="l")
+ theme_bw() +
  ylab("Time (ms)") + xlab("Number of known choices") #+ theme(legend.position="bottom")
+geom_line(aes(y=dRCtemps, colour="Complete sample",shape="a")) + geom_point(aes(y=dRCtemps), colour="coral3") 
+geom_line(aes(y=rctemps, colour="1/2 sample",shape="b")) + geom_point(aes(y=rctemps), colour="firebrick3") 
+geom_line(aes(y=Naiftemps, colour="1/4 sample")) + geom_point(aes(y=Naiftemps), colour="darkorchid3") 
+geom_line(aes(y=jointreetemps, colour="1/8 sample")) + geom_point(aes(y=jointreetemps), colour="darkslategray4") 
+geom_line(aes(y=wmvtemps, colour="1/16 sample")) + geom_point(aes(y=wmvtemps), colour="springgreen4") 
+geom_line(aes(y=lextreetemps, colour="1/32 sample")) + geom_point(aes(y=lextreetemps), colour="indianred4") 
+geom_line(aes(y=vpoptemps, colour="1/64 sample")) + geom_point(aes(y=vpoptemps), colour="deepskyblue4") 
+ scale_colour_manual(name = 'Legend', guide = 'legend',
                      limits = c(NULL
                                 ,'1/64 sample' #lextree
                                 ,'1/32 sample' #wmv
                                 ,'1/16 sample' #jointree
                                 ,'1/8 sample' #Naif
                                 ,'1/4 sample' #rc
                                 ,'1/2 sample' #oracle
                                 ,'Complete sample' #dRC
                      ),
                      values =c(NULL
                                ,'Complete sample'='coral' #dRC
                                ,'1/2 sample'='firebrick3' #oracle
                                ,'1/4 sample'='darkorchid1' #rc
                                ,'1/8 sample'='darkslategray1' #Naif
                                ,'1/16 sample'='springgreen' #jointree
                                ,'1/32 sample'='indianred1' #wmv
                                ,'1/64 sample'='deepskyblue4' #lextree
                      )))
dev.off()
