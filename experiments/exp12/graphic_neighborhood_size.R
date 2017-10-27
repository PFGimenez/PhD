library(ggplot2)

results = matrix(c(2,92.61694108677742,
                   4,93.01412269591629,
                   6,93.16921412146625,
                   8,93.28142100020904,
                   10,93.36134918780665,
                   12,93.39224450647419,
                   14,93.42944185531768,
                   16,93.46110571425058,
                   18,93.44389041230649,
                   20,93.47540056318631,
                   25,93.48616012690137,
                   30,93.49984014362481,
                   35,93.50660329795998,
                   40,93.50168464026167,
                   45,93.4887731638036,
                   50,93.48354708999913,
                   60,93.45910750956064,
                   70, 93.4526517713316,
                   80,93.42790477478697,
                   90,93.42421578151323,
                   100,93.40500227487918,
                   200,93.17935885296903,
                   500,92.61663367067128,
                   1000,91.79291221425673
                   ),
            ncol=2,byrow=TRUE)

index = results[,1]
precision = 100 - results[,2]

(ggplot(NULL, aes(index))  + scale_x_log10() + annotation_logticks(sides="b") +
   ylab("Error rate (%)") + xlab("Neighborhood size") + theme_bw() #+ theme(legend.position="bottom")
 +geom_line(aes(y=precision), colour="springgreen4", linetype = "solid") + geom_point(aes(y=precision), colour="springgreen4", fill="springgreen4") 
)