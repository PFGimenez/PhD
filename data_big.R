library(ggplot2)

#--------------------------------------------------------------------------------------------
#Parametres globaux
dataset_name = "big"
taille_img_x = 1024/2
taille_img_y = 720/2
#fin parametres globaux
#--------------------------------------------------------------------------------------------

#--------------------------------------------------------------------------------------------
#data
#result
oracle = c()
Naif = c()
jointree = c()
wmv = c()
vpop = c()
vnaif = c()

#result conf
c_oracle = c()
c_Naif = c()
c_jointree = c()
c_wmv = c()
c_vpop = c()
c_vnaif = c()

#temps
Naiftemps = c()
jointreetemps = c()
wmvtemps = c()
vpoptemps = c()
vnaiftemps = c()

#temps conf
c_Naiftemps = c()
c_jointreetemps = c()
c_wmvtemps = c()
c_vpoptemps = c()
c_vnaiftemps = c()
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
oracle = 100-100*oracle
Naif = 100-100*Naif
jointree = 100-100*jointree
wmv = 100-100*wmv
vpop = 100-100*vpop
vnaif = 100-100*vnaif

c_oracle = 100*c_oracle
c_Naif = 100*c_Naif
c_jointree = 100*c_jointree
c_wmv = 100*c_wmv
c_vpop = 100*c_vpop
c_vnaif = 100*c_vnaif
#FIN MAJ VERS ERROR

nb_val = length(oracle)
nb_val = length(Naif)
nb_val = length(jointree)
nb_val = length(wmv)
nb_val = length(vpop)
nb_val = length(vnaif)

min_val_erreur = 30000
min_val_erreur = min(min_val_erreur,100-100*oracle)
min_val_erreur = min(min_val_erreur,100-100*Naif)
min_val_erreur = min(min_val_erreur,100-100*jointree)
min_val_erreur = min(min_val_erreur,100-100*wmv)
min_val_erreur = min(min_val_erreur,100-100*vpop)
min_val_erreur = min(min_val_erreur,100-100*vnaif)
min_val_erreur = ceiling(min_val_erreur)

max_val_erreur = 30000
max_val_erreur = max(max_val_erreur,100-100*oracle)
max_val_erreur = max(max_val_erreur,100-100*Naif)
max_val_erreur = max(max_val_erreur,100-100*jointree)
max_val_erreur = max(max_val_erreur,100-100*wmv)
max_val_erreur = max(max_val_erreur,100-100*vpop)
max_val_erreur = max(max_val_erreur,100-100*vnaif)
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
min_val_time = min(min_val_erreur,100-100*Naiftemps)
min_val_time = min(min_val_erreur,100-100*jointreetemps)
min_val_time = min(min_val_erreur,100-100*wmvtemps)
min_val_time = min(min_val_erreur,100-100*vpoptemps)
min_val_time = min(min_val_erreur,100-100*vnaiftemps)
min_val_time = ceiling(min_val_erreur)

max_val_time = 30000
max_val_time = max(max_val_time,Naiftemps)
max_val_time = max(max_val_time,jointreetemps)
max_val_time = max(max_val_time,wmvtemps)
max_val_time = max(max_val_time,vpoptemps)
max_val_time = max(max_val_time,vnaiftemps)
max_val_time = floor(max_val_time)


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

  png(file=paste("Taux_erreur_sur_",dataset_name,".png",sep=""), bg="white", width=taille_img_x, height=taille_img_y, pointsize = 12)
  (ggplot(NULL, aes(size)) + #scale_y_log10(breaks = round(seq(0, 100, by = 5),1)) + annotation_logticks(sides="l") +
    ylab("Error rate (%)") + xlab("Number of assigned variables") + theme_bw() #+ theme(legend.position="bottom")
    +geom_line(aes(y=Naif, colour="Naive Bayes"), colour="turquoise2", linetype = "dotted") + geom_point(aes(y=Naif, shape="Naive Bayes"), colour="turquoise2", fill="turquoise2") 
    +geom_line(aes(y=wmv, colour="Weighted Majority Voter"), colour="deeppink2", linetype = "dotted") + geom_point(aes(y=wmv, shape="Weighted Majority Voter"), colour="deeppink2", fill="deeppink2") 
    +geom_line(aes(y=vpop, colour="Most Popular Choice"), colour="gold2", linetype = "dotted") + geom_point(aes(y=vpop, shape="Most Popular Choice"), colour="gold2", fill="gold2")
    +geom_line(aes(y=vnaif, colour="Naive Bayes Voter"), colour="firebrick3", linetype = "dotted") + geom_point(aes(y=vnaif, shape="Naive Bayes Voter"), colour="firebrick3", fill="firebrick3")
    +geom_line(aes(y=jointree, colour="Bayesian network"), colour="springgreen4", linetype = "dotted") + geom_point(aes(y=jointree, shape="Bayesian network"), colour="springgreen4", fill="springgreen4") 
    +geom_line(aes(y=oracle, colour="Oracle"), colour="black", linetype = "dotted") + geom_point(aes(y=oracle, shape="Oracle"), colour="black", fill="black") 
   + scale_colour_manual(name = 'Legend', guide = 'legend',
                      limits = c(NULL
                                  ,'Bayesian network' #jointree
                                  ,'Naive Bayes' #Naif
                                  ,'Weighted Majority Voter' #wmv
                                  ,'Most Popular Choice' #vpop
                                  ,'Naive Bayes Voter' #vnaif
                                  ,'Oracle' #oracle
                      ),
                      values =c(NULL
                                  ,'Bayesian network'='springgreen4' #jointree
                                  ,'Naive Bayes'='turquoise2' #Naif
                                  ,'Weighted Majority Voter'='deeppink2' #wmv
                                  ,'Most Popular Choice'='gold2' #vpop
                                  ,'Naive Bayes Voter'='firebrick3' #vnaif
                                  ,'Oracle'='black' #oracle
                                  ))
    + scale_shape_manual(name = 'Legend', guide = 'legend',
                      limits = c(NULL
                                  ,'Bayesian network' #jointree
                                  ,'Naive Bayes' #Naif
                                  ,'Weighted Majority Voter' #wmv
                                  ,'Most Popular Choice' #vpop
                                  ,'Naive Bayes Voter' #vnaif
                                  ,'Oracle' #oracle
                      ),
                      values =c(NULL
                                  ,'Bayesian network'=21 #jointree
                                  ,'Naive Bayes'=24 #Naif
                                  ,'Weighted Majority Voter'=25 #wmv
                                  ,'Most Popular Choice'=22 #vpop
                                  ,'Naive Bayes Voter'=23 #vnaif
                                  ,'Oracle'=4 #oracle
                                  ))
  
    + guides(shape = guide_legend(override.aes = list(colour = c(NULL
                                  ,'Bayesian network'='springgreen4' #jointree
                                  ,'Naive Bayes'='turquoise2' #Naif
                                  ,'Weighted Majority Voter'='deeppink2' #wmv
                                  ,'Most Popular Choice'='gold2' #vpop
                                  ,'Naive Bayes Voter'='firebrick3' #vnaif
                                  ,'Oracle'='black' #oracle
                                  ),
                                  fill = c(NULL
                                  ,'Bayesian network'='springgreen4' #jointree
                                  ,'Naive Bayes'='turquoise2' #Naif
                                  ,'Weighted Majority Voter'='deeppink2' #wmv
                                  ,'Most Popular Choice'='gold2' #vpop
                                  ,'Naive Bayes Voter'='firebrick3' #vnaif
                                  ,'Oracle'='black' #oracle
                                  ))))
)
  dev.off()

  png(file=paste("Temps_sur_",dataset_name,".png",sep=""), bg="white", width=taille_img_x, height=taille_img_y, pointsize = 12)
  (ggplot(NULL, aes(size))
  + scale_y_log10(
   breaks = scales::trans_breaks("log10", function(x) 10^x),
   labels = scales::trans_format("log10", function(x) round(10^x,2)))
   + annotation_logticks(sides="l")
   + theme_bw() +
    ylab("Time (ms)") + xlab("Number of assigned variables") #+ theme(legend.position="bottom")
    +geom_line(aes(y=jointreetemps, colour="Bayesian network"), colour="springgreen4", linetype = "dotted") + geom_point(aes(y=jointreetemps, shape="Bayesian network"), colour="springgreen4", fill="springgreen4") 
    +geom_line(aes(y=Naiftemps, colour="Naive Bayes"), colour="turquoise2", linetype = "dotted") + geom_point(aes(y=Naiftemps, shape="Naive Bayes"), colour="turquoise2", fill="turquoise2") 
    +geom_line(aes(y=wmvtemps, colour="Weighted Majority Voter"), colour="deeppink2", linetype = "dotted") + geom_point(aes(y=wmvtemps, shape="Weighted Majority Voter"), colour="deeppink2", fill="deeppink2") 
    +geom_line(aes(y=vpoptemps, colour="Most Popular Choice"), colour="gold2", linetype = "dotted") + geom_point(aes(y=vpoptemps, shape="Most Popular Choice"), colour="gold2", fill="gold2")
    +geom_line(aes(y=vnaiftemps, colour="Naive Bayes Voter"), colour="firebrick3", linetype = "dotted") + geom_point(aes(y=vnaiftemps, shape="Naive Bayes Voter"), colour="firebrick3", fill="firebrick3")
   + scale_colour_manual(name = 'Legend', guide = 'legend',
                      limits = c(NULL
                                  ,'Bayesian network' #jointree
                                  ,'Naive Bayes' #Naif
                                  ,'Weighted Majority Voter' #wmv
                                  ,'Most Popular Choice' #vpop
                                  ,'Naive Bayes Voter' #vnaif
                      ),
                      values =c(NULL
                                  ,'Bayesian network'='springgreen4' #jointree
                                  ,'Naive Bayes'='turquoise2' #Naif
                                  ,'Weighted Majority Voter'='deeppink2' #wmv
                                  ,'Most Popular Choice'='gold2' #vpop
                                  ,'Naive Bayes Voter'='firebrick3' #vnaif
                                  ))
    + scale_shape_manual(name = 'Legend', guide = 'legend',
                      limits = c(NULL
                                  ,'Bayesian network' #jointree
                                  ,'Naive Bayes' #Naif
                                  ,'Weighted Majority Voter' #wmv
                                  ,'Most Popular Choice' #vpop
                                  ,'Naive Bayes Voter' #vnaif
                      ),
                      values =c(NULL
                                  ,'Bayesian network'=21 #jointree
                                  ,'Naive Bayes'=24 #Naif
                                  ,'Weighted Majority Voter'=25 #wmv
                                  ,'Most Popular Choice'=22 #vpop
                                  ,'Naive Bayes Voter'=23 #vnaif
                                  ))
  
    + guides(shape = guide_legend(override.aes = list(colour = c(NULL
                                  ,'Bayesian network'='springgreen4' #jointree
                                  ,'Naive Bayes'='turquoise2' #Naif
                                  ,'Weighted Majority Voter'='deeppink2' #wmv
                                  ,'Most Popular Choice'='gold2' #vpop
                                  ,'Naive Bayes Voter'='firebrick3' #vnaif
                                  ),
                                  fill = c(NULL
                                  ,'Bayesian network'='springgreen4' #jointree
                                  ,'Naive Bayes'='turquoise2' #Naif
                                  ,'Weighted Majority Voter'='deeppink2' #wmv
                                  ,'Most Popular Choice'='gold2' #vpop
                                  ,'Naive Bayes Voter'='firebrick3' #vnaif
                                  ))))
  )
  dev.off()
