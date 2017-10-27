library(ggplot2)

results = matrix(c(2,986892,
                   4,1015475,
                   6,964914.4932906434,
                   8,1111862.132537843,
                   10,1028205.6994269764,
                   12,926182.1555663834,
                   14,893471.1746738316,
                   16,938531.1394547053,
                   18,1192592.676304674,
                   20,1153651.3162573935,
                   25,1068628.5126501727,
                   30,1130136.3507894445,
                   35,1136854.8436327977,
                   40,1254630.6057572889,
                   45,1368308.7154771714,
                   50,1201938.2669539982,
                   60,1306453.487766376,
                   70, 1324224.627348659,
                   80,1320461.8101259791,
                   90,1384108.9532896597,
                   100,1422389.9799610812,
                   200,2058402.739597039,
                   500,4195950.1699826615,
                   1000,6738947.915202341
),
ncol=2,byrow=TRUE)

index = results[,1]
temps = results[,2]/1000000.

(ggplot(NULL, aes(index))  + scale_y_log10(breaks = round(seq(0, 100, by = 1),1)) + scale_x_log10() + annotation_logticks(sides="lb") +
   ylab("Time (ms)") + xlab("Neighborhood size") + theme_bw() #+ theme(legend.position="bottom")
 +geom_line(aes(y=temps), colour="springgreen4", linetype = "solid") + geom_point(aes(y=temps), colour="springgreen4", fill="springgreen4") 
)