library(ggplot2)

results = matrix(c(2,986892,2526180.094274525,
                   4,1015475, 2240192.44529634,
                   6,964914.4932906434,2445243.6586526628,
                   8,1111862.132537843,2540669.311709367,
                   10,1028205.6994269764,2757396.609591886,
                   12,926182.1555663834,2971264.324928628,
                   14,893471.1746738316,3033164.07017252,
                   16,938531.1394547053,2978603.271048681,
                   18,1192592.676304674,2317306.6108055227,
                   20,1153651.3162573935, 2705492.3655154803,
                   25,1068628.5126501727, 2661228.4106353982,
                   30,1130136.3507894445,2668497.130086046,
                   35,1136854.8436327977,2912170.044017031,
                   40,1254630.6057572889,3013268.09243484,
                   45,1368308.7154771714,2926938.917735713,
                   50,1201938.2669539982,3122400.001615106,
                   60,1306453.487766376,3433712.878661677,
                   70, 1324224.627348659,3288484.5544713833,
                   80,1320461.8101259791, 3507345.120493084,
                   90,1384108.9532896597,3510462.6219105325,
                   100,1422389.9799610812,3647727.520040393,
                   200,2058402.739597039,4920816.5588106355,
                   500,4195950.1699826615, 6959979.17960059,
                   1000,6738947.915202341,1.1425094038083438E7
),
ncol=3,byrow=TRUE)

index = results[,1]
temps = results[,2]/1000000.
temps2 = results[,3]/1000000.


(ggplot(NULL, aes(index))  + scale_y_log10(breaks = round(seq(0, 100, by = 1),1)) + scale_x_log10() + annotation_logticks(sides="lb") +
    ylab("Recommendation time (ms)") + xlab("Neighborhood size") + theme_bw() #+ theme(legend.position="bottom")
  #  +geom_line(aes(y=precision), colour="springgreen4", linetype = "solid") + geom_point(aes(y=precision), colour="springgreen4", fill="springgreen4") 
  +geom_line(aes(y=temps, colour="Renault-44"), colour="springgreen4", linetype = "dotted") + geom_point(aes(y=temps, shape="Renault-44"), colour="springgreen4", fill="springgreen4") 
  +geom_line(aes(y=temps2, colour="Renault-48"), colour="red", linetype = "dotted") + geom_point(aes(y=temps2, shape="Renault-48"), colour="red", fill="red") 
  #  +geom_line(aes(y=precision3, colour="Renault-44 reduced"), colour="blue", linetype = "dotted") + geom_point(aes(y=precision3, shape="Renault-44 reduced"), colour="blue", fill="blue") 
  #  +geom_line(aes(y=precision4, colour="Renault-48 reduced"), colour="black", linetype = "dotted") + geom_point(aes(y=precision4, shape="Renault-48 reduced"), colour="black", fill="black") 
  #  +geom_line(aes(y=precision5, colour="Renault-48 reduced"), colour="black", linetype = "dotted") + geom_point(aes(y=precision5, shape="Renault-48 reduced"), colour="black", fill="black") 
  #  +geom_line(aes(y=drc, colour="DRC"), colour="blue", linetype = "dotted") + geom_point(aes(y=drc, shape="DRC"), colour="blue", fill="blue") 
  #  +geom_line(aes(y=oracle, colour="Oracle"), colour="black", linetype = "dotted") + geom_point(aes(y=oracle, shape="Oracle"), colour="black", fill="black") 
  + theme(legend.position=c(0.86,0.2), legend.background = element_rect(fill=alpha('blue', 0)))
  + scale_colour_manual(name = 'Legend', guide = 'legend',
                        limits = c(NULL
                                   ,'Renault-44' #jointree
                                   ,'Renault-48'
                        ),
                        values =c(NULL
                                  ,'Renault-44'='springgreen4' #jointree
                                  ,'Renault-48'='red'
                        ))
  + scale_shape_manual(name = 'Legend', guide = 'legend',
                       limits = c(NULL
                                  ,'Renault-44' #jointree
                                  ,'Renault-48'
                       ),
                       values =c(NULL
                                 ,'Renault-44'=4 #jointree
                                 ,'Renault-48'=24
                       ))
  
  + guides(shape = guide_legend(override.aes = list(colour = c(NULL
                                                               ,'Renault-44'='springgreen4' #jointree
                                                               ,'Renault-48'='red'
  ),
  fill = c(NULL
           ,'Renault-44'='springgreen4' #jointree
           ,'Renault-48'='red'
           #            ,'Renault-44 reduced'='blue'
           #            ,'Renault-48 reduced'='black'
  ))))
)
