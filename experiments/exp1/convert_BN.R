#!/usr/bin/Rscript

{
  library(bnlearn)
      fitted = read.bif("~/code/experiments/exp1/insurance.bif")
      fichier = "~/code/experiments/exp1/insurance.xml";
    
    cat("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n\n", file=fichier)

cat("<!-- DTD for the XMLBIF 0.3 format -->\n<!DOCTYPE BIF [\n<!ELEMENT BIF ( NETWORK )*>\n<!ATTLIST BIF VERSION CDATA #REQUIRED>\n   <!ELEMENT NETWORK ( NAME, ( PROPERTY | VARIABLE | DEFINITION )* )>\n      <!ELEMENT NAME (#PCDATA)>\n        <!ELEMENT VARIABLE ( NAME, ( OUTCOME |  PROPERTY )* ) >\n          <!ATTLIST VARIABLE TYPE (nature|decision|utility) \"nature\">\n           <!ELEMENT OUTCOME (#PCDATA)>\n            <!ELEMENT DEFINITION ( FOR | GIVEN | TABLE | PROPERTY )* >\n              <!ELEMENT FOR (#PCDATA)>\n                <!ELEMENT GIVEN (#PCDATA)>\n                  <!ELEMENT TABLE (#PCDATA)>\n                    <!ELEMENT PROPERTY (#PCDATA)>\n                      ]>\n", file=fichier, append=TRUE)
  
    cat("<BIF VERSION=\"0.3\">\n",file=fichier, append=TRUE)
    cat("<NETWORK>\n",file=fichier, append=TRUE)

cat("<NAME>Reco</NAME>\n",file=fichier, append=TRUE)
    # Variables
    for(v in 1:length(fitted))
    {
      cat("<VARIABLE TYPE=\"nature\">\n", file=fichier, append=TRUE)
      cat(paste(paste("\t<NAME>",names(fitted)[v],sep=""),"</NAME>\n",sep=""), file=fichier, append=TRUE)
#      cat("\t<TYPE>discrete</TYPE>\n", file=fichier, append=TRUE)
#      if(is.element("999",levels(training_set[,v])))
#      {
#        cat("\t<OUTCOME>999</OUTCOME>\n", file=fichier, append=TRUE)
#      }
  for(w in 1:length(levels(factor(rownames(fitted[[v]]$prob)))))
      {
        cat(paste(paste("\t<OUTCOME>",levels(factor(rownames(fitted[[v]]$prob)))[w],sep=""),"</OUTCOME>\n",sep=""), file=fichier, append=TRUE)
      }
      cat("</VARIABLE>\n\n", file=fichier, append=TRUE)
    }

    # Probabilités
    for(v in 1:length(fitted))
    {
      cat("<DEFINITION>\n", file=fichier, append=TRUE)
      cat(paste(paste("\t<FOR>",fitted[[v]]$node,sep=""),"</FOR>\n",sep=""), file=fichier, append=TRUE)
      if(length(fitted[[v]]$parents) > 0)
      {
#        for(w in 1:length(fitted[[v]]$parents))
#        {
#          cat(paste(paste("\t<GIVEN>",fitted[[v]]$parents[[w]],sep=""),"</GIVEN>\n",sep=""), file=fichier, append=TRUE)
#        }
        for(w in 1:length(fitted[[v]]$parents))
        {
          cat(paste(paste("\t<GIVEN>",fitted[[v]]$parents[[length(fitted[[v]]$parents)-w+1]],sep=""),"</GIVEN>\n",sep=""), file=fichier, append=TRUE)
        }
      }
      cat("\t<TABLE>", file=fichier, append=TRUE)

#      n = length(levels(factor(training_set[,v]))) + 1
      
#       if(length(fitted[[v]]$prob) > 0)
#       {
#         for(k in 0:(n-1))
#         {
#           for(w in 1:(length(fitted[[v]]$prob)/n))
#           {
#             cat(paste(fitted[[v]]$prob[[(n*w-k)]],""), file=fichier, append=TRUE)
#           }
#         }
#       }

      for(w in 1:(length(fitted[[v]]$prob)))
      {
        cat(paste(fitted[[v]]$prob[[w]],""), file=fichier, append=TRUE)
      }
      
      
      cat("</TABLE>\n", file=fichier, append=TRUE)
      cat("</DEFINITION>\n\n", file=fichier, append=TRUE)
      
    }
cat("</NETWORK>\n",file=fichier, append=TRUE)
cat("</BIF>\n",file=fichier, append=TRUE)


    #    write.bif(paste(paste('~/SALADD/bn',as.character(i),sep=''),'.bif',sep=''), fitted)
#    write.net(paste(paste('~/SALADD/bn',as.character(i),sep=''),'.net',sep=''), fitted)
    print("Réseau bayésien enregistré.")

graphviz.plot(fitted)
}