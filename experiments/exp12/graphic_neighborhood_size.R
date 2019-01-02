library(ggplot2)
# tail ... | grep "Taux succès" | cut -f3 -d ' ' | tee /dev/tty | xclip -selection clipboard

# medium, small, medium-reduced, small-reduced, vmaj-small
results = matrix(c(2,92.61694108677742,89.91089227702304,92.5865068922691,89.85244081019886,89.88981898503643,
                   4,93.01412269591629,90.79027921342784,92.98030692424038,90.73967254873007,90.48125553750738,
                   6,93.16921412146625,90.99401333924001,93.11772192368703,90.93671551978736,90.74259512207128,
                   8,93.28142100020904,91.18428824571767,93.22024519508626,91.11922253396338,90.87903315121086,
                   10,93.36134918780665,91.2811177151014,93.29402506056097,91.22105114195708,90.96694108092144,
                   12,93.39224450647419,91.34210720614294,93.32153880206091,91.27919496948218,91.02654619511715,
                   14,93.42944185531768,91.36879491533766,93.34935995966701,91.29965298287064,91.05754085449891,
                   16,93.46110571425058,91.39817446839929,93.37195504346863,91.32726360996259,91.07692213034062,
                   18,93.44389041230649,91.44116706044497,93.34490242612792,91.36518015357353,91.11899180448907,
                   20,93.47540056318631,91.45454936995472,93.36211772807201,91.38240795432172,91.1292208111833,
                   25,93.48616012690137,91.47892978440638,93.3627325602843,91.39332914943887,91.14398749753889,
                   30,93.49984014362481,91.50392547745619,93.35965839922285,91.41909394073636,91.17452069797204,
                   35,93.50660329795998,91.50530985430203,93.36150289585972,91.42063213723173,91.16436860110258,
                   40,93.50168464026167,91.52546022839141,93.36242514417815,91.43286079936995,91.18951811380192,
                   45,93.4887731638036,91.50946298483953,93.35258782878152,91.41294115475488,91.16906010041346,
                   50,93.48354708999913,91.52569095786572,93.34520984223406,91.42355471057294,91.18813373695609,
                   60,93.45910750956064,91.51769233608978,93.30924215781513,91.40948021264029,91.17213649340421,
                   70, 93.4526517713316, 91.50554058377634,93.28972123507495,91.39025275644812,91.15667761862571,
                   80,93.42790477478697,91.48954334022446,93.25682771171748,91.3704100216578,91.13083591750345,
                   90,93.42421578151323,91.48369819354204,93.21993777898011,91.35833517916913,91.11668450974601,
                   100,93.40500227487918,91.47554575211656,93.16906041341318,91.34156883736956,91.10460966725734,
                   200,93.17935885296903,91.37648589781453,92.75604687480786,91.17859691868478,90.944483412089,
                   500,92.61663367067128,91.09999507777121,91.79460300284053,90.8119677840126,90.53786116853712,
                   1000,91.79291221425673,90.80473826048434,90.492695793318,90.39750073833432,89.99572381374286
                   ),
            ncol=6,byrow=TRUE)

index = results[,1]
precision = 100 - results[,2]
precision2 = 100 - results[,3]
precision3 = 100 - results[,4]
precision4 = 100 - results[,5]
precision5 = 100 - results[,6]

(ggplot(NULL, aes(index)) + scale_x_log10() + annotation_logticks(sides="b") +
    ylab("Error rate (%)") + xlab("Neighborhood size") + theme_bw() #+ theme(legend.position="bottom")
#  +geom_line(aes(y=precision), colour="springgreen4", linetype = "solid") + geom_point(aes(y=precision), colour="springgreen4", fill="springgreen4") 
  +geom_line(aes(y=precision, colour="Renault-44"), colour="springgreen4", linetype = "dotted") + geom_point(aes(y=precision, shape="Renault-44"), colour="springgreen4", fill="springgreen4") 
  +geom_line(aes(y=precision2, colour="Renault-48"), colour="red", linetype = "dotted") + geom_point(aes(y=precision2, shape="Renault-48"), colour="red", fill="red") 
#  +geom_line(aes(y=precision3, colour="Renault-44 reduced"), colour="blue", linetype = "dotted") + geom_point(aes(y=precision3, shape="Renault-44 reduced"), colour="blue", fill="blue") 
#  +geom_line(aes(y=precision4, colour="Renault-48 reduced"), colour="black", linetype = "dotted") + geom_point(aes(y=precision4, shape="Renault-48 reduced"), colour="black", fill="black") 
#  +geom_line(aes(y=precision5, colour="Renault-48 reduced"), colour="black", linetype = "dotted") + geom_point(aes(y=precision5, shape="Renault-48 reduced"), colour="black", fill="black") 
  #  +geom_line(aes(y=drc, colour="DRC"), colour="blue", linetype = "dotted") + geom_point(aes(y=drc, shape="DRC"), colour="blue", fill="blue") 
#  +geom_line(aes(y=oracle, colour="Oracle"), colour="black", linetype = "dotted") + geom_point(aes(y=oracle, shape="Oracle"), colour="black", fill="black") 
+ theme(legend.position=c(0.56,0.8), legend.background = element_rect(fill=alpha('blue', 0)))
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
