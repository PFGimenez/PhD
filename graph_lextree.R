#nb="1.0E-5"

path = paste("~/code/datasets/lptree-relearning3","/",sep="")

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

var1 = read.csv(file = paste(path, "vars-results-10-false.csv",sep=""), header = FALSE);
var2 = read.csv(file = paste(path, "vars-results-13-false.csv",sep=""), header = FALSE);
var3 = read.csv(file = paste(path, "vars-results-15-false.csv",sep=""), header = FALSE);
var4 = read.csv(file = paste(path, "vars-results-18-false.csv",sep=""), header = FALSE);
var5 = read.csv(file = paste(path, "vars-results-20-false.csv",sep=""), header = FALSE);
var6 = read.csv(file = paste(path, "vars-results-25-false.csv",sep=""), header = FALSE);

var1=var1[order(var1$V1),]
var2=var2[order(var2$V1),]
var3=var3[order(var3$V1),]
var4=var4[order(var4$V1),]
var5=var5[order(var5$V1),]
var6=var6[order(var6$V1),]

plot(var1, log="xy", type="o", col="green", ylab="Mean KL-divergence", xlab="Size of example sample (log scale)", main="LP-tree learning w.r.t. variable number")
points(var2, type="o", col="springgreen4")
points(var3, type="o", col="magenta4")
points(var4, type="o", col="magenta")
points(var5, type="o", col="orange")
points(var6, type="o", col="red")


var1 = read.csv(file = paste(path, "split-results-0.2-false.csv",sep=""), header = FALSE);
var2 = read.csv(file = paste(path, "split-results-0.5-false.csv",sep=""), header = FALSE);
var3 = read.csv(file = paste(path, "split-results-0.7-false.csv",sep=""), header = FALSE);
var4 = read.csv(file = paste(path, "split-results-0.8-false.csv",sep=""), header = FALSE);
var5 = read.csv(file = paste(path, "split-results-0.5-false.csv",sep=""), header = FALSE);
var6 = read.csv(file = paste(path, "split-results-0.5-false.csv",sep=""), header = FALSE);

var1=var1[order(var1$V1),]
var2=var2[order(var2$V1),]
var3=var3[order(var3$V1),]
var4=var4[order(var4$V1),]
var5=var5[order(var5$V1),]
var6=var6[order(var6$V1),]

plot(var1, log="xy", type="o", col="green", ylab="Mean KL-divergence", xlab="Size of example sample (log scale)", main="LP-tree learning w.r.t. split coeff")
points(var2, type="o", col="springgreen4")
points(var3, type="o", col="magenta4")
points(var4, type="o", col="magenta")
points(var5, type="o", col="orange")
points(var6, type="o", col="red")

var1 = read.csv(file = paste(path, "moyenne-results-19-false.csv",sep=""), header = FALSE);
var2 = read.csv(file = paste(path, "moyenne-results-19-true.csv",sep=""), header = FALSE);
var3$V1 = var1$V1
var3$V2 = var2$V2 - var1$V2

var1=var1[order(var1$V1),]
var2=var2[order(var2$V1),]
var3=var3[order(var3$V1),]
plot(var3, log="x", type="o", col="green1", ylab="Distance de KL moyenne", xlab="Nb exemples d'apprentissage", main="Apprentissage de lextree (algo glouton), test sur 500 arbres")
points(var2, type="o", col="red")


var1 = read.csv(file = paste(path, "moyenne-results-10.csv",sep=""), header = FALSE);
var2 = read.csv(file = paste(path, "moyenne-results-15.csv",sep=""), header = FALSE);
var3 = read.csv(file = paste(path, "moyenne-results-18.csv",sep=""), header = FALSE);
var4 = read.csv(file = paste(path, "moyenne-results-20.csv",sep=""), header = FALSE);
var5 = read.csv(file = paste(path, "moyenne-results-22.csv",sep=""), header = FALSE);
var6 = read.csv(file = paste(path, "moyenne-results-25.csv",sep=""), header = FALSE);
var7 = read.csv(file = paste(path, "moyenne-results-28.csv",sep=""), header = FALSE);

var1=var1[order(var1$V1),]
var2=var2[order(var2$V1),]
var3=var3[order(var3$V1),]
var4=var4[order(var4$V1),]
var5=var5[order(var5$V1),]
var6=var6[order(var6$V1),]
var7=var7[order(var7$V1),]

plot(var1, log="x", type="o", col="green1", ylab="Distance de KL moyenne", xlab="Nb exemples d'apprentissage", main="Apprentissage de lextree (algo glouton), test sur 500 arbres")
points(var2, type="o", col="green2")
points(var3, type="o", col="green3")
points(var4, type="o", col="green4")
points(var5, type="o", col="orange")
points(var6, type="o", col="red")
points(var7, type="o", col="black")



var1 = read.csv(file = paste(path, "prune-results-10-0.1.csv",sep=""), header = FALSE);
var2 = read.csv(file = paste(path, "prune-results-13-0.8.csv",sep=""), header = FALSE);
var3 = read.csv(file = paste(path, "prune-results-15-0.8.csv",sep=""), header = FALSE);
var4 = read.csv(file = paste(path, "prune-results-18-0.8.csv",sep=""), header = FALSE);
var5 = read.csv(file = paste(path, "prune-results-20-0.2.csv",sep=""), header = FALSE);
var6 = read.csv(file = paste(path, "prune-results-25-0.8.csv",sep=""), header = FALSE);

var1=var1[order(var1$V1),]
var2=var2[order(var2$V1),]
var3=var3[order(var3$V1),]
var4=var4[order(var4$V1),]
var5=var5[order(var5$V1),]
var6=var6[order(var6$V1),]

plot(var1, log="x", ylim=c(0,1), type="o", col="green", ylab="Compression ratio of LP-tree", xlab="Size of example sample (log scale)", main="Compression ratio due to pruning")
points(var2, type="o", col="springgreen4")
points(var3, type="o", col="magenta4")
points(var4, type="o", col="magenta")
points(var5, type="o", col="orange")
points(var6, type="o", col="red")



var1 = read.csv(file = paste(path, "struct-lp-results.csv",sep=""), header = FALSE);
var2 = read.csv(file = paste(path, "struct-prune-results.csv",sep=""), header = FALSE);
var3 = read.csv(file = paste(path, "struct-lin-results.csv",sep=""), header = FALSE);

var1=var1[order(var1$V1),]
var2=var2[order(var2$V1),]
var3=var3[order(var3$V1),]

plot(var1, log="x", ylim=c(0,1), type="o", col="green", ylab="Overall well-learnt LP-tree ratio", xlab="Size of example sample (log scale)", main="LP-tree, pruned LP-tree and linear LP-tree performance")
points(var2, type="o", col="red")
points(var3, type="o", col="black")



var1 = read.csv(file = paste(path, "kl-lp-results.csv",sep=""), header = FALSE);
var2 = read.csv(file = paste(path, "kl-prune-results.csv",sep=""), header = FALSE);
var3 = read.csv(file = paste(path, "kl-lin-results.csv",sep=""), header = FALSE);

var1=var1[order(var1$V1),]
var2=var2[order(var2$V1),]
var3=var3[order(var3$V1),]

plot(var2, log="xy", type="o", col="red", ylab="Mean KL-divergence", xlab="Size of example sample (log scale)", main="LP-tree, pruned LP-tree and linear LP-tree performance")
points(var1, type="o", col="green")
points(var3, type="o", col="black")


var1 = read.csv(file = paste(path, "temps-lp-results.csv",sep=""), header = FALSE);
var2 = read.csv(file = paste(path, "temps-prune-results.csv",sep=""), header = FALSE);
var3 = read.csv(file = paste(path, "temps-lin-results.csv",sep=""), header = FALSE);

var1=var1[order(var1$V1),]
var2=var2[order(var2$V1),]
var3=var3[order(var3$V1),]

plot(var1, ylim=c(0.4, 50000), log="xy", type="o", col="green", ylab="Mean learning time (ms)", xlab="Size of example sample (log scale)", main="LP-tree, pruned LP-tree and linear LP-tree performance")
points(var2, type="o", col="red")
points(var3, type="o", col="black")