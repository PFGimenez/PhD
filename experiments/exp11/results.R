library(ggplot2)

#--------------------------------------------------------------------------------------------
#Parametres globaux
dataset_name = "insurance"
taille_img_x = 1024/2
taille_img_y = 720/2

dataset = "medium"

if(dataset == "small")
{
# small
oracle = c(93.20032548237842, 93.20032548237842, 93.20032548237842)
Naif = c( 87.77049185371136, 87.79464153868872, 88.90614232624533)
jointree = c( 91.50969371431384, 90.51086582004332, 89.96188349084466)
wmv = c(91.1292208111833, 90.34389459047057, 89.92835080724552)
vpop = c(91.13891144910416, 90.21583973223075, 89.64847595491239)
vnaif = c( 91.45454936995472, 90.5104043610947, 89.94365586237448)
} else if(dataset == "medium")
{
# medium
oracle = c(94.43699814320672,94.43699814320672,94.43699814320672)
Naif = c(91.4115625345843, 88.94977435657809, 87.3478290274584)
jointree = c(93.40454115071996, 91.4476839270563, 90.44135730359184)
wmv = c(93.42237128487636,91.69100377506979,90.75154015469178)
vpop = c(93.33875410400502, 91.12090060622457, 90.10120138214282)
vnaif = c(93.47540056318631, 91.28521451495887, 90.26490045866483)
} else if(dataset == "big")
{
# big
  oracle = c(96.87101975402364,96.87101975402364,96.87101975402364)
  Naif = c(91.52130962461192,92.32068414000733,91.55777459844732)
  jointree = c(93,93,93.50942931018261)
  wmv = c(94.84273668979792,94.44532038242804,93.75242099526021)
  vpop = c(94.84844650776503,94.46270937350968,93.57347010942736)
  vnaif = c(94.87407580432195,94.41657663970724,93.69084579922853)
} else if(dataset == "medium-const")
{
  # medium
  Naif = c(76.25687591863783, 71.76736243206682, 71.3768263015747)
  jointree = c(80.85768795436287, 76.49561555460944, 75.94610846841302)
  wmv = c(79.85911623854328, 77.28943192427545, 77.30123202490671)
  vpop = c(79.60725489066971, 75.45881151274457, 75.20345733508415)
  vnaif = c( 80.09785587451489, 76.15737747011507, 75.97631672602904)
} else if(dataset == "medium-const-valid")
{
  # medium
  Naif = c(77.48366796015038, 76.23567939965251, 74.11114653466467)
  jointree = c(81.71952846010039, 80.42551024065588, 78.65233081742261)
  wmv = c(81.21480570390479, 79.21157230900992, 77.7273987771957)
  vpop = c(81.23357599515556, 78.89370355767761, 80)
  vnaif = c(80,80,80)
}


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
oracle = 100-oracle
Naif = 100-Naif
jointree = 100-jointree
wmv = 100-wmv
vpop = 100-vpop
vnaif = 100-vnaif
drc = 100-drc

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

#fin parametres graphes
#--------------------------------------------------------------------------------------------

size = 1:3
(ggplot(NULL, aes(size))  + scale_x_continuous(breaks=size)  + #scale_y_log10(breaks = round(seq(0, 100, by = 5),1)) + annotation_logticks(sides="l") +
    ylab("Error rate (%)") + xlab("#clusters") + theme_bw() #+ theme(legend.position="bottom")
  +geom_line(aes(y=Naif, colour="Naive Bayesian network"), colour="turquoise2", linetype = "solid") + geom_point(aes(y=Naif, shape="Naive Bayesian network"), colour="turquoise2", fill="turquoise2") 
  +geom_line(aes(y=wmv, colour="Weighted Majority Voter"), colour="deeppink2", linetype = "solid") + geom_point(aes(y=wmv, shape="Weighted Majority Voter"), colour="deeppink2", fill="deeppink2") 
  +geom_line(aes(y=vpop, colour="Most Popular Choice"), colour="gold2", linetype = "solid") + geom_point(aes(y=vpop, shape="Most Popular Choice"), colour="gold2", fill="gold2")
  +geom_line(aes(y=vnaif, colour="Naive Bayes voter"), colour="firebrick3", linetype = "solid") + geom_point(aes(y=vnaif, shape="Naive Bayes voter"), colour="firebrick3", fill="firebrick3")
  +geom_line(aes(y=jointree, colour="Bayesian network"), colour="springgreen4", linetype = "solid") + geom_point(aes(y=jointree, shape="Bayesian network"), colour="springgreen4", fill="springgreen4") 
# +geom_line(aes(y=drc, colour="DRC"), colour="blue", linetype = "solid") + geom_point(aes(y=drc, shape="DRC"), colour="blue", fill="blue") 
  + theme(legend.position=c(0.20,0.78), legend.background = element_rect(fill=alpha('blue', 0)))
  + scale_colour_manual(name = 'Legend', guide = 'legend',
                        limits = c(NULL
                                   ,'Bayesian network' #jointree
                                   ,'Naive Bayesian network' #Naif
                                   ,'Weighted Majority Voter' #wmv
                                   ,'Most Popular Choice' #vpop
                                   ,'Naive Bayes voter' #vnaif
                        ),
                        values =c(NULL
                                  ,'Bayesian network'='springgreen4' #jointree
                                  ,'Naive Bayesian network'='turquoise2' #Naif
                                  ,'Weighted Majority Voter'='deeppink2' #wmv
                                  ,'Most Popular Choice'='gold2' #vpop
                                  ,'Naive Bayes voter'='firebrick3' #vnaif
                        ))
  + scale_shape_manual(name = 'Legend', guide = 'legend',
                       limits = c(NULL
                                  ,'Bayesian network' #jointree
                                  ,'Naive Bayesian network' #Naif
                                  ,'Weighted Majority Voter' #wmv
                                  ,'Most Popular Choice' #vpop
                                  ,'Naive Bayes voter' #vnaif
                       ),
                       values =c(NULL
                                 ,'Bayesian network'=21 #jointree
                                 ,'Naive Bayesian network'=24 #Naif
                                 ,'Weighted Majority Voter'=25 #wmv
                                 ,'Most Popular Choice'=22 #vpop
                                 ,'Naive Bayes voter'=23 #vnaif
                       ))
  
  + guides(shape = guide_legend(override.aes = list(colour = c(NULL
                                                               ,'Bayesian network'='springgreen4' #jointree
                                                               ,'Naive Bayesian network'='turquoise2' #Naif
                                                               ,'Weighted Majority Voter'='deeppink2' #wmv
                                                               ,'Most Popular Choice'='gold2' #vpop
                                                               ,'Naive Bayes voter'='firebrick3' #vnaif
  ),
  fill = c(NULL
           ,'Bayesian network'='springgreen4' #jointree
           ,'Naive Bayesian network'='turquoise2' #Naif
           ,'Weighted Majority Voter'='deeppink2' #wmv
           ,'Most Popular Choice'='gold2' #vpop
           ,'Naive Bayes voter'='firebrick3' #vnaif
  ))))
)

