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

tightness = 0
oracle = c(0.7614635465981334, 0.7969701068578385, 0.811713783308535, 0.8409982415798729, 0.857770864331124, 0.8748140132557825, 0.8911808467469228, 0.898552684972271, 0.9148518869200595, 0.9180982010009469, 0.9305424049776816, 0.9390639794400109, 0.9455566076017855, 0.949479237116191, 0.953198972000541, 0.9570539699715948, 0.9584742323819829, 0.9592858109022048, 0.9638847558501284, 0.9642905451102394, 0.9672663330177195, 0.9703773840119031, 0.9724739618558096, 0.9734208034627351, 0.9736913296361422, 0.9762613282835114, 0.9770729068037333, 0.9778844853239551, 0.9745029081563641, 0.976058433653456, 0.9786284323008251, 0.9769376437170296, 0.9803192208846205, 0.9807250101447315, 0.9787636953875287, 0.9798458000811578, 0.9817394832950088, 0.9797781685378061, 0.9806573786013797, 0.9802515893412688, 0.983497903422156, 0.9811307994048424, 0.982821587988638, 0.980927904774787)
Naif = c(0.761598809684837, 0.7965643175977276, 0.8111050994183687, 0.8403219261463547, 0.8545921817935885, 0.8717705938049506, 0.8863790071689436, 0.8930068984174219, 0.9076829433247666, 0.9092384688218585, 0.9206005681049642, 0.9290545110239415, 0.9352089814689571, 0.9399431895035845, 0.9412958203706209, 0.9467739753821183, 0.9483295008792101, 0.9483971324225619, 0.9518463411335046, 0.9520492357635602, 0.9566481807114838, 0.9568510753415393, 0.9616529149195184, 0.9619910726362776, 0.9611118625727039, 0.9626673880697958, 0.9649668605437576, 0.964087650480184, 0.9627350196131476, 0.9643581766535912, 0.9667929122142568, 0.9628026511564994, 0.9671987014743676, 0.9672663330177195, 0.9638847558501284, 0.9689571216015149, 0.9669958068443122, 0.9648992290004058, 0.9676721222778304, 0.9684160692547004, 0.9689571216015149, 0.9654402813472204, 0.9682808061679967, 0.9676044907344785)
jointree = c(0.5353036656296497, 0.6356012444203977, 0.6984985797375897, 0.7468551332341404, 0.7737724874881645, 0.8076558907074259, 0.8276071959962127, 0.8440416610307048, 0.8625727039091032, 0.8689300689841742, 0.8863113756255918, 0.8953740024347355, 0.9031516299201948, 0.911673204382524, 0.915054781550115, 0.9227647774922224, 0.9265521439199242, 0.9311510888678479, 0.9339916136886244, 0.9352766130123089, 0.9428513458677127, 0.9436629243879345, 0.9479237116190992, 0.9513052887866901, 0.9500879210063574, 0.9543487082375219, 0.9544163397808738, 0.9566481807114838, 0.9573921276883538, 0.9587447585553902, 0.9623968618963885, 0.9573921276883538, 0.9654402813472204, 0.9650344920871095, 0.9630055457865548, 0.9661842283240903, 0.9653050182605167, 0.9621263357229812, 0.966319491410794, 0.9665223860408495, 0.9699715947517923, 0.9663871229541459, 0.9696334370350331, 0.9689571216015149)
wmv = c(0.7492898687948059, 0.7817530096036791, 0.801636683349114, 0.8333558771811173, 0.8495198160422021, 0.8679155958338969, 0.884350060868389, 0.8898282158798864, 0.9061950493710267, 0.9113350466657649, 0.9237116190991479, 0.930001352630867, 0.9368997700527526, 0.9407547680238063, 0.9444068713648045, 0.9480589747058028, 0.9479913431624509, 0.9514405518733937, 0.9554984444745029, 0.9527255511970784, 0.9559718652779656, 0.960706073312593, 0.9635465981333694, 0.9634789665900176, 0.9655079128905721, 0.9646963343703503, 0.9656431759772758, 0.9660489652373867, 0.9653050182605167, 0.9672663330177195, 0.9688894900581632, 0.9661842283240903, 0.9710536994454213, 0.9705126470986067, 0.9686865954281076, 0.9701068578384959, 0.9717300148789395, 0.967063438387664, 0.9705126470986067, 0.9713242256188286, 0.9743000135263087, 0.9702421209251995, 0.9722034356824023, 0.9715271202488841)
vpop = c(0.5384823481671852, 0.637359664547545, 0.6985662112809414, 0.747396185580955, 0.7716082781009063, 0.8066414175571487, 0.8240227241985663, 0.8403219261463547, 0.8610848099553632, 0.8670363857703233, 0.8861761125388882, 0.8945624239145137, 0.9002434735560666, 0.9077505748681185, 0.9137697822264305, 0.9212092519951305, 0.9237792506424997, 0.9283105640470716, 0.9330447720816989, 0.9336534559718653, 0.9381847693764371, 0.9425131881509536, 0.9462329230353037, 0.9494116055728392, 0.9500202894630055, 0.9515758149600974, 0.9513052887866901, 0.9541458136074665, 0.9544163397808738, 0.957730285405113, 0.9590152847287975, 0.9563776545380765, 0.9622615988096849, 0.9617205464628703, 0.9613823887461111, 0.9641552820235357, 0.9628026511564994, 0.9602326525091303, 0.9643581766535912, 0.965575544433924, 0.9692276477749222, 0.9648315974570539, 0.9671987014743676, 0.9668605437576085)


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

nb_val = length(oracle)
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

(ggplot(NULL, aes(size)) +scale_y_log10(breaks = round(seq(0, 100, by = 5),1)) + annotation_logticks(sides="l") +
    ylab("Taux d'erreur (%)") + xlab("Nombre de variables configurées") + theme_bw() #+ theme(legend.position="bottom")
  +geom_line(aes(y=Naif, colour="Réseau bayésien (1 cluster)"), colour="turquoise2", linetype = "dotted") + geom_point(aes(y=Naif, shape="Réseau bayésien (1 cluster)"), colour="turquoise2", fill="turquoise2") 
  +geom_line(aes(y=wmv, colour="Voteur bayésien naïf (1 cluster)"), colour="deeppink2", linetype = "dotted") + geom_point(aes(y=wmv, shape="Voteur bayésien naïf (1 cluster)"), colour="deeppink2", fill="deeppink2") 
  +geom_line(aes(y=vpop, colour="Voteur bayésien naïf (3 clusters)"), colour="gold2", linetype = "dotted") + geom_point(aes(y=vpop, shape="Voteur bayésien naïf (3 clusters)"), colour="gold2", fill="gold2")
  +geom_line(aes(y=jointree, colour="Réseau bayésien (3 clusters)"), colour="springgreen4", linetype = "dotted") + geom_point(aes(y=jointree, shape="Réseau bayésien (3 clusters)"), colour="springgreen4", fill="springgreen4") 
 +geom_line(aes(y=oracle, colour="Oracle"), colour="black", linetype = "dotted") + geom_point(aes(y=oracle, shape="Oracle"), colour="black", fill="black") 
 + theme(legend.position=c(0.75,0.70), legend.background = element_rect(fill=alpha('blue', 0)))
 + scale_colour_manual(name = 'Légende', guide = 'legend',
                        limits = c(NULL
                                   ,'Réseau bayésien (1 cluster)' #Naif
                                   ,'Réseau bayésien (3 clusters)' #jointree
                                   ,'Voteur bayésien naïf (1 cluster)' #wmv
                                   ,'Voteur bayésien naïf (3 clusters)' #vpop
                                   ,'Oracle' #oracle
                        ),
                        values =c(NULL
                                  ,'Réseau bayésien (1 cluster)'='turquoise2' #Naif
                                  ,'Réseau bayésien (3 clusters)'='springgreen4' #jointree
                                  ,'Voteur bayésien naïf (1 cluster)'='deeppink2' #wmv
                                  ,'Voteur bayésien naïf (3 clusters)'='gold2' #vpop
                                  ,'Oracle'='black' #oracle
                        ))
  + scale_shape_manual(name = 'Légende', guide = 'legend',
                       limits = c(NULL
                                  ,'Réseau bayésien (1 cluster)' #Naif
                                  ,'Réseau bayésien (3 clusters)' #jointree
                                  ,'Voteur bayésien naïf (1 cluster)' #wmv
                                  ,'Voteur bayésien naïf (3 clusters)' #vpop
                                  ,'Oracle' #oracle
                       ),
                       values =c(NULL
                                 ,'Réseau bayésien (1 cluster)'=24 #Naif
                                 ,'Réseau bayésien (3 clusters)'=21 #jointree
                                 ,'Voteur bayésien naïf (1 cluster)'=25 #wmv
                                 ,'Voteur bayésien naïf (3 clusters)'=22 #vpop
                                 ,'Oracle'=4 #oracle
                       ))
  
  + guides(shape = guide_legend(override.aes = list(colour = c(NULL
                                                               ,'Réseau bayésien (1 cluster)'='turquoise2' #Naif
                                                               ,'Réseau bayésien (3 clusters)'='springgreen4' #jointree
                                                               ,'Voteur bayésien naïf (1 cluster)'='deeppink2' #wmv
                                                               ,'Voteur bayésien naïf (3 clusters)'='gold2' #vpop
                                                               ,'Oracle'='black' #oracle
  ),
  fill = c(NULL
           ,'Réseau bayésien (1 cluster)'='turquoise2' #Naif
           ,'Réseau bayésien (3 clusters)'='springgreen4' #jointree
           ,'Voteur bayésien naïf (1 cluster)'='deeppink2' #wmv
           ,'Voteur bayésien naïf (3 clusters)'='gold2' #vpop
           ,'Oracle'='black' #oracle
  ))))
)
