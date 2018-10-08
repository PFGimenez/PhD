library(ggplot2)

#--------------------------------------------------------------------------------------------
#Parametres globaux
dataset_name = "insurance"
taille_img_x = 1024/2
taille_img_y = 720/2
#fin parametres globaux
#--------------------------------------------------------------------------------------------

#--------------------------------------------------------------------------------------------
#data
#result

#naif : jointree 1
#jointree : jointree 3
# wmv : vnaif 1
#vpop : vnaif 3

Naif = c(0.12448744927634249, 0.1086794302718788, 0.12640429061274178, 0.13386710564047072, 0.1276868988908427, 0.12346760814283782, 0.11952775321249831, 0.11360067604490734, 0.10802456945759502, 0.10254076890301636, 0.09595378155011497, 0.09540854619234411, 0.0889061032733667, 0.083718426687407, 0.07991546544028134, 0.07276646591370214, 0.07260977275801433, 0.06830642465846071, 0.06319538739348032, 0.059993493439740295, 0.05471461720546463, 0.05679957419180306, 0.050285667726227515, 0.047237269849857974, 0.04749186994454213, 0.04269336811849046, 0.0429188483700798, 0.04017307121601515, 0.03822232645745976, 0.035671056472338696, 0.035079751995130526, 0.03459466704991208, 0.03209805045313134, 0.03324939091032057, 0.03088847335317192, 0.030715028675774383, 0.028756842891924792, 0.02753826734749087, 0.027561989584742323, 0.026175895374002434, 0.027268143919924253, 0.02500468666305965, 0.025375328215879886, 0.024427831394562425)
jointree = c(0.026228930542404976, 0.031088404842418503, 0.04130523758961179, 0.050237325916407415, 0.054607138779926956, 0.06053747085080482, 0.06113482483430272, 0.0615756246449344, 0.061946390842689034, 0.06283635878533748, 0.059197536250507235, 0.056176893412687674, 0.05525952400919789, 0.05283377593669687, 0.051821408832679564, 0.05137248302448262, 0.049275306911943734, 0.04571460509941837, 0.04322456445286081, 0.04152246719870147, 0.03967165568781279, 0.03892547118896253, 0.0356765910996889, 0.03462381502772893, 0.034112288516163936, 0.032576783579061276, 0.03305078655484918, 0.031075461991072636, 0.031723465372649806, 0.028946048221290408, 0.02954956289733532, 0.025674248275395644, 0.025522797308264573, 0.024984867306911945, 0.024198570066278912, 0.02381806580549168, 0.024155919450831866, 0.02235082666035439, 0.021573706479101853, 0.021481281347220344, 0.02175127018801569, 0.020706285472744487, 0.021079850331394562, 0.019855530569457595)
wmv = c(0.4494038685919113, 0.4967798196266739, 0.5515680150818342, 0.5970946590017584, 0.6461875509265521, 0.6927466897064791, 0.7319930931286351, 0.7683001784796429, 0.8021085089273637, 0.8324823104964155, 0.8623469918842148, 0.8914434409576627, 0.9202062371838226, 0.9479598675098065, 0.9764703082645746, 1.004198266265386, 1.0321642523332883, 1.0595404977005276, 1.0877324741647505, 1.1158403942918977, 1.1445429954686865, 1.1718358771134858, 1.200502870688489, 1.2280016795617477, 1.2562031648180711, 1.2846652703232788, 1.313240186324902, 1.3415959738942242, 1.370274200392263, 1.39903223312593, 1.4277950685107534, 1.4578981556878128, 1.4880198928040038, 1.5210458539158664, 1.5562808102935208, 1.592247956310023, 1.626759088867848, 1.6636773851616393, 1.7035288999053158, 1.7469154924252672, 1.794243814283782, 1.846449209116732, 1.9026610744623291, 1.9613170334099823)
vpop = c(0.051662238468821856, 0.14436689490058163, 0.19814453286893008, 0.24941927262275126, 0.28930117327201404, 0.31880757094548895, 0.33407550229947247, 0.34825433748140133, 0.3583803261869336, 0.3667831691464899, 0.37506521459488706, 0.3816593812390099, 0.3896884772758014, 0.392892363451914, 0.3983875854862708, 0.4045863766400649, 0.4111561037467875, 0.415931137292033, 0.42087792580819694, 0.42595397328554035, 0.43147279379142434, 0.4370440200865684, 0.4435824974976329, 0.44913306824022725, 0.45505308149600976, 0.46233789483295007, 0.4685006038820506, 0.4750492817530096, 0.48205439902610575, 0.4902890376031381, 0.4957995622886514, 0.5030517145948871, 0.5104261571080752, 0.5179762596374949, 0.5257936175436223, 0.5346573395103477, 0.54178703198972, 0.5501812920330041, 0.5586743982145272, 0.5675673376166644, 0.5782490376031381, 0.5904258407277154, 0.6053238941566347, 0.6241750090626268)


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


nb_val = length(Naif)
nb_val = length(jointree)
nb_val = length(wmv)
nb_val = length(vpop)

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

(ggplot(NULL, aes(size)) + scale_y_log10() + annotation_logticks(sides="l") +
   ylab("Temps de recommandation (ms)") + xlab("Nombre de variables configurées") + theme_bw() #+ theme(legend.position="bottom")
 +geom_line(aes(y=Naif, colour="Réseau bayésien (1 cluster)"), colour="turquoise2", linetype = "dotted") + geom_point(aes(y=Naif, shape="Réseau bayésien (1 cluster)"), colour="turquoise2", fill="turquoise2") 
 +geom_line(aes(y=wmv, colour="Voteur bayésien naïf (1 cluster)"), colour="deeppink2", linetype = "dotted") + geom_point(aes(y=wmv, shape="Voteur bayésien naïf (1 cluster)"), colour="deeppink2", fill="deeppink2") 
 +geom_line(aes(y=vpop, colour="Voteur bayésien naïf (3 clusters)"), colour="gold2", linetype = "dotted") + geom_point(aes(y=vpop, shape="Voteur bayésien naïf (3 clusters)"), colour="gold2", fill="gold2")
 +geom_line(aes(y=jointree, colour="Réseau bayésien (3 clusters)"), colour="springgreen4", linetype = "dotted") + geom_point(aes(y=jointree, shape="Réseau bayésien (3 clusters)"), colour="springgreen4", fill="springgreen4") 
 + theme(legend.position=c(0.75,0.43), legend.background = element_rect(fill=alpha('blue', 0)))
 + scale_colour_manual(name = 'Légende', guide = 'legend',
                       limits = c(NULL
                                  ,'Réseau bayésien (1 cluster)' #Naif
                                  ,'Réseau bayésien (3 clusters)' #jointree
                                  ,'Voteur bayésien naïf (1 cluster)' #wmvo
                                  ,'Voteur bayésien naïf (3 clusters)' #vpop
                       ),
                       values =c(NULL
                                 ,'Réseau bayésien (1 cluster)'='turquoise2' #Naif
                                 ,'Réseau bayésien (3 clusters)'='springgreen4' #jointree
                                 ,'Voteur bayésien naïf (1 cluster)'='deeppink2' #wmv
                                 ,'Voteur bayésien naïf (3 clusters)'='gold2' #vpop
                       ))
 + scale_shape_manual(name = 'Légende', guide = 'legend',
                      limits = c(NULL
                                 ,'Réseau bayésien (1 cluster)' #Naif
                                 ,'Réseau bayésien (3 clusters)' #jointree
                                 ,'Voteur bayésien naïf (1 cluster)' #wmv
                                 ,'Voteur bayésien naïf (3 clusters)' #vpop
                      ),
                      values =c(NULL
                                ,'Réseau bayésien (1 cluster)'=24 #Naif
                                ,'Réseau bayésien (3 clusters)'=21 #jointree
                                ,'Voteur bayésien naïf (1 cluster)'=25 #wmv
                                ,'Voteur bayésien naïf (3 clusters)'=22 #vpop
                      ))
 
 + guides(shape = guide_legend(override.aes = list(colour = c(NULL
                                                              ,'Réseau bayésien (1 cluster)'='turquoise2' #Naif
                                                              ,'Réseau bayésien (3 clusters)'='springgreen4' #jointree
                                                              ,'Voteur bayésien naïf (1 cluster)'='deeppink2' #wmv
                                                              ,'Voteur bayésien naïf (3 clusters)'='gold2' #vpop
 ),
 fill = c(NULL
          ,'Réseau bayésien (1 cluster)'='turquoise2' #Naif
          ,'Réseau bayésien (3 clusters)'='springgreen4' #jointree
          ,'Voteur bayésien naïf (1 cluster)'='deeppink2' #wmv
          ,'Voteur bayésien naïf (3 clusters)'='gold2' #vpop
 ))))
)
