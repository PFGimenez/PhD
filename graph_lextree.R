#nb="1.0E-5"

path = "~/code/expe-lextree2/"
library(ggplot2)
#data10 = read.csv(file = paste(path, "final-results-10.csv",sep=""), header = FALSE);
#data25 = read.csv(file = paste(path, "final-results-25.csv",sep=""), header = FALSE);
#data100 = read.csv(file = paste(path, "final-results-100.csv",sep=""), header = FALSE);
#data250 = read.csv(file = paste(path, "final-results-250.csv",sep=""), header = FALSE);
#data1000 = read.csv(file = paste(path, "final-results-1000.csv",sep=""), header = FALSE);
#data2500 = read.csv(file = paste(path, "final-results-2500.csv",sep=""), header = FALSE);
#data10000 = read.csv(file = paste(path, "final-results-10000.csv",sep=""), header = FALSE);
#data25000 = read.csv(file = paste(path, "final-results-25000.csv",sep=""), header = FALSE);
#data100000 = read.csv(file = paste(path, "final-results-100000.csv",sep=""), header = FALSE);
#plot(data10, ylim=c(0,1), type="o", col="green1", ylab="Taux d'arbre bien appris (KL < 1)", xlab="Nombre de nœuds", main="Apprentissage de lextree (algo glouton), test sur 1800 arbres")
#points(data25, type="o", col="green2")
#points(data100, type="o", col="green3")
#points(data250, type="o", col="green4")
#points(data1000, type="o", col="red1")
#points(data2500, type="o", col="red2")
#points(data10000, type="o", col="red3")
#points(data25000, type="o", col="red4")
#points(data100000, type="o", col="black")

#eps1 = read.csv(file = paste(path, "eps-results-0.01.csv",sep=""), header = FALSE);
#eps2 = read.csv(file = paste(path, "eps-results-0.1.csv",sep=""), header = FALSE);
#eps3 = read.csv(file = paste(path, "eps-results-1.0.csv",sep=""), header = FALSE);
#eps4 = read.csv(file = paste(path, "eps-results-10.0.csv",sep=""), header = FALSE);
#plot(eps1, ylim=c(0,1), type="o", col="green1", ylab="Taux d'arbre bien appris (KL < epsilon)", xlab="Nb exemples d'apprentissage", main="Apprentissage de lextree (algo glouton), test sur 1800 arbres")
#points(eps2, type="o", col="green2")
#points(eps3, type="o", col="green3")
#points(eps4, type="o", col="green4")

#var1 = read.csv(file = paste(path, "vars-results-19-0.12-false.csv",sep=""), header = FALSE);
#var2 = read.csv(file = paste(path, "vars-results-19-0.12-true.csv",sep=""), header = FALSE);
#var1=var1[order(var1$V1),]
#var2=var2[order(var2$V1),]
#plot(var1, log="x", ylim=c(0,1), type="o", col="green1", ylab="Taux d'arbre bien appris (KL < 1)", xlab="Nb exemples d'apprentissage", main="Apprentissage de lextree (algo glouton), test sur 500 arbres")
#points(var2, type="o", col="red")

# var1 = read.csv(file = paste(path, "vars-results-10-false.csv",sep=""), header = FALSE);
# var2 = read.csv(file = paste(path, "vars-results-13-false.csv",sep=""), header = FALSE);
# var3 = read.csv(file = paste(path, "vars-results-15-false.csv",sep=""), header = FALSE);
# var4 = read.csv(file = paste(path, "vars-results-18-false.csv",sep=""), header = FALSE);
# var5 = read.csv(file = paste(path, "vars-results-20-false.csv",sep=""), header = FALSE);
# var6 = read.csv(file = paste(path, "vars-results-25-false.csv",sep=""), header = FALSE);
# 
# var1=var1[order(var1$V1),]
# var2=var2[order(var2$V1),]
# var3=var3[order(var3$V1),]
# var4=var4[order(var4$V1),]
# var5=var5[order(var5$V1),]
# var6=var6[order(var6$V1),]
# 
# plot(var1, log="xy", type="o", col="green", ylab="Mean KL-divergence", xlab="Size of example sample (log scale)", main="LP-tree learning w.r.t. variable number")
# points(var2, type="o", col="springgreen4")
# points(var3, type="o", col="magenta4")
# points(var4, type="o", col="magenta")
# points(var5, type="o", col="orange")
# points(var6, type="o", col="red")
# 
# 
# var1 = read.csv(file = paste(path, "split-results-0.2-false.csv",sep=""), header = FALSE);
# var2 = read.csv(file = paste(path, "split-results-0.5-false.csv",sep=""), header = FALSE);
# var3 = read.csv(file = paste(path, "split-results-0.7-false.csv",sep=""), header = FALSE);
# var4 = read.csv(file = paste(path, "split-results-0.8-false.csv",sep=""), header = FALSE);
# var5 = read.csv(file = paste(path, "split-results-0.5-false.csv",sep=""), header = FALSE);
# var6 = read.csv(file = paste(path, "split-results-0.5-false.csv",sep=""), header = FALSE);
# 
# var1=var1[order(var1$V1),]
# var2=var2[order(var2$V1),]
# var3=var3[order(var3$V1),]
# var4=var4[order(var4$V1),]
# var5=var5[order(var5$V1),]
# var6=var6[order(var6$V1),]
# 
# plot(var1, log="xy", type="o", col="green", ylab="Mean KL-divergence", xlab="Size of example sample (log scale)", main="LP-tree learning w.r.t. split coeff")
# points(var2, type="o", col="springgreen4")
# points(var3, type="o", col="magenta4")
# points(var4, type="o", col="magenta")
# points(var5, type="o", col="orange")
# points(var6, type="o", col="red")
# 
# var1 = read.csv(file = paste(path, "moyenne-results-19-false.csv",sep=""), header = FALSE);
# var2 = read.csv(file = paste(path, "moyenne-results-19-true.csv",sep=""), header = FALSE);
# var3$V1 = var1$V1
# var3$V2 = var2$V2 - var1$V2
# 
# var1=var1[order(var1$V1),]
# var2=var2[order(var2$V1),]
# var3=var3[order(var3$V1),]
# plot(var3, log="x", type="o", col="green1", ylab="Distance de KL moyenne", xlab="Nb exemples d'apprentissage", main="Apprentissage de lextree (algo glouton), test sur 500 arbres")
# points(var2, type="o", col="red")
# 
# 
# var1 = read.csv(file = paste(path, "moyenne-results-10.csv",sep=""), header = FALSE);
# var2 = read.csv(file = paste(path, "moyenne-results-15.csv",sep=""), header = FALSE);
# var3 = read.csv(file = paste(path, "moyenne-results-18.csv",sep=""), header = FALSE);
# var4 = read.csv(file = paste(path, "moyenne-results-20.csv",sep=""), header = FALSE);
# var5 = read.csv(file = paste(path, "moyenne-results-22.csv",sep=""), header = FALSE);
# var6 = read.csv(file = paste(path, "moyenne-results-25.csv",sep=""), header = FALSE);
# var7 = read.csv(file = paste(path, "moyenne-results-28.csv",sep=""), header = FALSE);
# 
# var1=var1[order(var1$V1),]
# var2=var2[order(var2$V1),]
# var3=var3[order(var3$V1),]
# var4=var4[order(var4$V1),]
# var5=var5[order(var5$V1),]
# var6=var6[order(var6$V1),]
# var7=var7[order(var7$V1),]
# 
# plot(var1, log="x", type="o", col="green1", ylab="Distance de KL moyenne", xlab="Nb exemples d'apprentissage", main="Apprentissage de lextree (algo glouton), test sur 500 arbres")
# points(var2, type="o", col="green2")
# points(var3, type="o", col="green3")
# points(var4, type="o", col="green4")
# points(var5, type="o", col="orange")
# points(var6, type="o", col="red")
# points(var7, type="o", col="black")
# 
# 
# 
# var1 = read.csv(file = paste(path, "prune-results-10-0.1.csv",sep=""), header = FALSE);
# var2 = read.csv(file = paste(path, "prune-results-13-0.8.csv",sep=""), header = FALSE);
# var3 = read.csv(file = paste(path, "prune-results-15-0.8.csv",sep=""), header = FALSE);
# var4 = read.csv(file = paste(path, "prune-results-18-0.8.csv",sep=""), header = FALSE);
# var5 = read.csv(file = paste(path, "prune-results-20-0.2.csv",sep=""), header = FALSE);
# var6 = read.csv(file = paste(path, "prune-results-25-0.8.csv",sep=""), header = FALSE);
# 
# var1=var1[order(var1$V1),]
# var2=var2[order(var2$V1),]
# var3=var3[order(var3$V1),]
# var4=var4[order(var4$V1),]
# var5=var5[order(var5$V1),]
# var6=var6[order(var6$V1),]
# 
# plot(var1, log="x", ylim=c(0,1), type="o", col="green", ylab="Compression ratio of LP-tree", xlab="Size of example sample (log scale)", main="Compression ratio due to pruning")
# points(var2, type="o", col="springgreen4")
# points(var3, type="o", col="magenta4")
# points(var4, type="o", col="magenta")
# points(var5, type="o", col="orange")
# points(var6, type="o", col="red")
# 
# 
# 
# var1 = read.csv(file = paste(path, "struct-lp-results.csv",sep=""), header = FALSE);
# var2 = read.csv(file = paste(path, "struct-prune-results.csv",sep=""), header = FALSE);
# var3 = read.csv(file = paste(path, "struct-lin-results.csv",sep=""), header = FALSE);
# 
# var1=var1[order(var1$V1),]
# var2=var2[order(var2$V1),]
# var3=var3[order(var3$V1),]
# 
# plot(var1, log="x", ylim=c(0,1), type="o", col="green", ylab="Overall well-learnt LP-tree ratio", xlab="Size of example sample (log scale)", main="LP-tree, LP-tree élagué and LP-tree linéaire heuristique precision")
# points(var2, type="o", col="red")
# points(var3, type="o", col="black")



var1 = read.csv(file = paste(path, "kl-lp-results.csv",sep=""), header = FALSE);
var2 = read.csv(file = paste(path, "kl-prune-results.csv",sep=""), header = FALSE);
var3 = read.csv(file = paste(path, "kl-lin-results.csv",sep=""), header = FALSE);
var4 = read.csv(file = paste(path, "kl-exact-lin-results.csv",sep=""), header = FALSE);
var5 = read.csv(file = paste(path, "kl-k-results.csv",sep=""), header = FALSE);


var1=var1[order(var1$V1),]
var2=var2[order(var2$V1),]
var3=var3[order(var3$V1),]
var4=var4[order(var4$V1),]
var5=var5[order(var5$V1),]
#var5 = var4/2


#plot(var2, log="xy", type="o", col="red", ylab="Mean KL-divergence", xlab="Sample size")
#points(var1, type="o", col="green")
#points(var3, type="o", col="black")


(ggplot(NULL, aes(var1$V1)) + scale_y_log10() + scale_x_log10() + annotation_logticks(sides="lb") +
#    ylab("Mean KL-divergence") + xlab("Sample size") + theme_bw() + theme(legend.position=c(0.765, 0.74), legend.background = element_rect(fill=alpha('white', 0))) +theme(plot.margin=unit(c(1,5,1,5),"mm"))
  ylab("Ranking loss") + xlab("Taille de l'échantillon") + theme_bw() + theme(legend.position=c(0.28, 0.28), legend.background = element_rect(fill=alpha('white', 0))) +theme(plot.margin=unit(c(1,5,1,1),"mm"))
  +geom_line(aes(y=var5$V2, colour="2-LP-tree"), colour="darkorange", linetype="solid") + geom_point(aes(y=var5$V2, shape="2-LP-tree"), colour="darkorange", fill="darkorange") 
  +geom_line(aes(y=var1$V2, colour="LP-tree non élagué"), colour="springgreen4", linetype="solid") + geom_point(aes(y=var1$V2, shape="LP-tree non élagué"), colour="springgreen4", fill="springgreen4") 
  +geom_line(aes(y=var2$V2, colour="LP-tree élagué"), colour="firebrick3", linetype="solid") + geom_point(aes(y=var2$V2, shape="LP-tree élagué"), colour="firebrick3", fill="firebrick3") 
  +geom_line(aes(y=var3$V2, colour="LP-tree linéaire heuristique"), colour="black", linetype="solid") + geom_point(aes(y=var3$V2, shape="LP-tree linéaire heuristique"), colour="black", fill="black") 
  +geom_line(aes(y=var4$V2, colour="LP-tree linéaire exact"), colour="blue", linetype="solid") + geom_point(aes(y=var4$V2, shape="LP-tree linéaire exact"), colour="blue", fill="blue") 
  + scale_colour_manual(name = 'Légende', guide = 'legend',
                        limits = c(NULL
                                   ,'2-LP-tree'
                                   ,'LP-tree non élagué' #courbe1
                                   ,'LP-tree élagué' #1_2
                                   ,'LP-tree linéaire heuristique' #1_4
                                   ,'LP-tree linéaire exact'
                        ),
                        values =c(NULL
                                  ,'2-LP-tree'='darkorange'
                                  ,'LP-tree non élagué'='springgreen4' #courbe1
                                  ,'LP-tree élagué'='firebrick3' #1_2
                                  ,'LP-tree linéaire heuristique'='black' #1_4
                                  ,'LP-tree linéaire exact'='blue'
                        ))
  
  + scale_shape_manual(name = 'Légende', guide = 'legend',
                       limits = c(NULL
                                  ,'2-LP-tree'
                                  ,'LP-tree non élagué' #courbe1
                                  ,'LP-tree élagué' #1_2
                                  ,'LP-tree linéaire heuristique' #1_4
                                  ,'LP-tree linéaire exact'
                       ),
                       values =c(NULL
                                 ,'2-LP-tree'=1
                                 ,'LP-tree non élagué'=0 #courbe1
                                 ,'LP-tree élagué'=3 #1_2
                                 ,'LP-tree linéaire heuristique'=4 #1_4
                                 ,'LP-tree linéaire exact'=2
                       ))
  
  + guides(shape = guide_legend(override.aes = list(colour = c(NULL
                                                               ,'2-LP-tree'='darkorange'
                                                               ,'LP-tree non élagué'='springgreen4' #courbe1
                                                               ,'LP-tree élagué'='firebrick3' #1_2
                                                               ,'LP-tree linéaire heuristique'='black' #1_4
                                                               ,'LP-tree linéaire exact'='blue'
  ),
  fill = c(NULL
           ,'2-LP-tree'='darkorange'
           ,'LP-tree non élagué'='springgreen4' #courbe1
           ,'LP-tree élagué'='firebrick3' #1_2
           ,'LP-tree linéaire heuristique'='black' #1_4
           ,'LP-tree linéaire exact'='blue'
  )
  )))
)


var1 = read.csv(file = paste(path, "temps-lp-results.csv",sep=""), header = FALSE);
var2 = read.csv(file = paste(path, "temps-prune-results.csv",sep=""), header = FALSE);
var3 = read.csv(file = paste(path, "temps-lin-results.csv",sep=""), header = FALSE);
var4 = read.csv(file = paste(path, "temps-exact-lin-results.csv",sep=""), header = FALSE);
var5 = read.csv(file = paste(path, "temps-k-results.csv",sep=""), header = FALSE);

var1=var1[order(var1$V1),]
var2=var2[order(var2$V1),]
var3=var3[order(var3$V1),]
var4=var4[order(var4$V1),]
var5=var5[order(var5$V1),]
#plot(var1, log="xy", ylim=c(0.5, 200000), type="o", col="green", ylab="Mean learning time (ms)", xlab="Sample size")
#points(var2, type="o", col="red")
#points(var3, type="o", col="black")


(ggplot(NULL, aes(var1$V1)) + scale_y_log10() + scale_x_log10() + annotation_logticks(sides="lb") +
    ylab("Temps moyen d'apprentissage (ms)") + xlab("Taille de l'échantillon") + theme_bw() + theme(legend.position=c(0.3, 0.72), legend.background = element_rect(fill=alpha('white', 0))) +theme(plot.margin=unit(c(1,1,1,5),"mm"))
  +geom_line(aes(y=var5$V2, colour="2-LP-tree"), colour="darkorange", linetype="solid") + geom_point(aes(y=var5$V2, shape="2-LP-tree"), colour="darkorange", fill="darkorange") 
  +geom_line(aes(y=var1$V2, colour="LP-tree non élagué"), colour="springgreen4", linetype="solid") + geom_point(aes(y=var1$V2, shape="LP-tree non élagué"), colour="springgreen4", fill="springgreen4") 
  +geom_line(aes(y=var2$V2, colour="LP-tree élagué"), colour="firebrick3", linetype="solid") + geom_point(aes(y=var2$V2, shape="LP-tree élagué"), colour="firebrick3", fill="firebrick3") 
  +geom_line(aes(y=var3$V2, colour="LP-tree linéaire heuristique"), colour="black", linetype="solid") + geom_point(aes(y=var3$V2, shape="LP-tree linéaire heuristique"), colour="black", fill="black") 
  +geom_line(aes(y=var4$V2, colour="LP-tree linéaire exact"), colour="blue", linetype="solid") + geom_point(aes(y=var4$V2, shape="LP-tree linéaire exact"), colour="blue", fill="blue") 
  + scale_colour_manual(name = 'Légende', guide = 'legend',
                        limits = c(NULL
                                   ,'2-LP-tree'
                                   ,'LP-tree non élagué' #courbe1
                                   ,'LP-tree élagué' #1_2
                                   ,'LP-tree linéaire heuristique' #1_4
                                   ,'LP-tree linéaire exact'
                        ),
                        values =c(NULL
                                  ,'2-LP-tree'='darkorange'
                                  ,'LP-tree non élagué'='springgreen4' #courbe1
                                  ,'LP-tree élagué'='firebrick3' #1_2
                                  ,'LP-tree linéaire heuristique'='black' #1_4
                                  ,'LP-tree linéaire exact'='blue'
                        ))
  
  + scale_shape_manual(name = 'Légende', guide = 'legend',
                       limits = c(NULL
                                  ,'2-LP-tree'
                                  ,'LP-tree non élagué' #courbe1
                                  ,'LP-tree élagué' #1_2
                                  ,'LP-tree linéaire heuristique' #1_4
                                  ,'LP-tree linéaire exact'
                       ),
                       values =c(NULL
                                 ,'2-LP-tree'=1
                                 ,'LP-tree non élagué'=0 #courbe1
                                 ,'LP-tree élagué'=3 #1_2
                                 ,'LP-tree linéaire heuristique'=4 #1_4
                                 ,'LP-tree linéaire exact'=2
                       ))
  
  + guides(shape = guide_legend(override.aes = list(colour = c(NULL
                                                               ,'2-LP-tree'='darkorange'
                                                               ,'LP-tree non élagué'='springgreen4' #courbe1
                                                               ,'LP-tree élagué'='firebrick3' #1_2
                                                               ,'LP-tree linéaire heuristique'='black' #1_4
                                                               ,'LP-tree linéaire exact'='blue'
  ),
  fill = c(NULL
           ,'2-LP-tree'='darkorange'
           ,'LP-tree non élagué'='springgreen4' #courbe1
           ,'LP-tree élagué'='firebrick3' #1_2
           ,'LP-tree linéaire heuristique'='black' #1_4
           ,'LP-tree linéaire exact'='blue'
  )
  )))
)

var1 = read.csv(file = paste(path, "taille-lp-results.csv",sep=""), header = FALSE);
var2 = read.csv(file = paste(path, "taille-prune-results.csv",sep=""), header = FALSE);
#var3 = read.csv(file = paste(path, "taille-lin-results.csv",sep=""), header = FALSE);
#var4 = read.csv(file = paste(path, "taille-lin2-results.csv",sep=""), header = FALSE);
var5 = read.csv(file = paste(path, "taille-k-results.csv",sep=""), header = FALSE);

var1=var1[order(var1$V1),]
var2=var2[order(var2$V1),]
var5=var5[order(var5$V1),]
#var4=var4[order(var4$V1),]

#plot(var1, log="xy", type="o", ylim = c(5, 5000), col="green", ylab="Mean size", xlab="Sample size")
#points(var2, type="o", col="red")
#points(var3, type="o", col="black")

(ggplot(NULL, aes(var1$V1)) + scale_y_log10() + scale_x_log10() + annotation_logticks(sides="lb") +
    ylab("Taille moyenne") + xlab("Taille de l'échantillon") + theme_bw() + theme(legend.position=c(0.3, 0.72), legend.background = element_rect(fill=alpha('white', 0))) +theme(plot.margin=unit(c(1,5,1,5),"mm"))
  +geom_line(aes(y=var5$V2, colour="2-LP-tree"), colour="darkorange", linetype="solid") + geom_point(aes(y=var5$V2, shape="2-LP-tree"), colour="darkorange", fill="darkorange") 
  +geom_line(aes(y=var1$V2, colour="LP-tree non élagué"), colour="springgreen4", linetype="solid") + geom_point(aes(y=var1$V2, shape="LP-tree non élagué"), colour="springgreen4", fill="springgreen4") 
  +geom_line(aes(y=var2$V2, colour="LP-tree élagué"), colour="firebrick3", linetype="solid") + geom_point(aes(y=var2$V2, shape="LP-tree élagué"), colour="firebrick3", fill="firebrick3") 
#  +geom_line(aes(y=var3$V2, colour="LP-tree linéaire heuristique"), colour="black", linetype="solid") + geom_point(aes(y=var3$V2, shape="LP-tree linéaire heuristique"), colour="black", fill="black") 
#  +geom_line(aes(y=var4$V2, colour="LP-tree linéaire exact"), colour="blue", linetype="solid") + geom_point(aes(y=var4$V2, shape="LP-tree linéaire exact"), colour="blue", fill="blue") 
  + scale_colour_manual(name = 'Légende', guide = 'legend',
                        limits = c(NULL
                                   ,'2-LP-tree'
                                   ,'LP-tree non élagué' #courbe1
                                   ,'LP-tree élagué' #1_2
#                                   ,'LP-tree linéaire heuristique' #1_4
#                                   ,'LP-tree linéaire exact'
                        ),
                        values =c(NULL
                                  ,'2-LP-tree'='darkorange'
                                  ,'LP-tree non élagué'='springgreen4' #courbe1
                                  ,'LP-tree élagué'='firebrick3' #1_2
#                                  ,'LP-tree linéaire heuristique'='black' #1_4
#                                  ,'LP-tree linéaire exact'='blue'
                        ))
  
  + scale_shape_manual(name = 'Légende', guide = 'legend',
                       limits = c(NULL
                                  ,'2-LP-tree'
                                  ,'LP-tree non élagué' #courbe1
                                  ,'LP-tree élagué' #1_2
#                                  ,'LP-tree linéaire heuristique' #1_4
#                                  ,'LP-tree linéaire exact'
                       ),
                       values =c(NULL
                                 ,'2-LP-tree'=1
                                 ,'LP-tree non élagué'=0 #courbe1
                                 ,'LP-tree élagué'=3 #1_2
#                                 ,'LP-tree linéaire heuristique'=4 #1_4
#                                 ,'LP-tree linéaire exact'=2
                       ))
  
  + guides(shape = guide_legend(override.aes = list(colour = c(NULL
                                                               ,'2-LP-tree'='darkorange'
                                                               ,'LP-tree non élagué'='springgreen4' #courbe1
                                                               ,'LP-tree élagué'='firebrick3' #1_2
#                                                               ,'LP-tree linéaire heuristique'='black' #1_4
#                                                               ,'LP-tree linéaire exact'='blue'
  ),
  fill = c(NULL
           ,'2-LP-tree'='darkorange'
           ,'LP-tree non élagué'='springgreen4' #courbe1
           ,'LP-tree élagué'='firebrick3' #1_2
#           ,'LP-tree linéaire heuristique'='black' #1_4
#           ,'LP-tree linéaire exact'='blue'
  )
  )))
)

