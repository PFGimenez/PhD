#!/usr/bin/Rscript

{
  # Parameters : outfile header dataset1 dataset2 …
  
  args = commandArgs(trailingOnly=TRUE)
  if(length(args) < 4)
  {
    stop("Pas assez de paramètres ! Paramètres : outfile algo header dataset1 dataset2 ...")
  }
  fichier = args[1]
  algo = args[2]
  header = eval(parse(text=args[3])) # on convertit le texte en booléen
  print(paste("Working directory :", getwd()))
  print(paste("Output :",fichier))
  print(paste("Algo :",algo))
  print(paste("Header :", header))
  dataset = args[4:length(args)]
  library(bnlearn, lib.loc="~/R_libs")

  for(j in 1:length(dataset))
  {
    print(dataset[j])
    if(j == 1)
    {
      training_set = read.csv(file=dataset[j], header=header, stringsAsFactors=F)
    }
    else
    {
      training_set = rbind(training_set, read.csv(file=dataset[j], header=header, stringsAsFactors=F))
    }
  }
  
  training_set[] = lapply(training_set, as.character)
  
  for(k in 1:dim(training_set)[2])
  {
    if(length(c(unique(training_set[,k]))) == 1) # ajout d'une valeur artificielle si besoin est
    {
      training_set[,k] = factor(training_set[,k],levels=c(999,unique(training_set[,k])))
    }
    else
    {
      training_set[,k] = factor(training_set[,k],levels=c(unique(training_set[,k])))
    }
  }
  
  if(algo == "hc")
  {
    bn = hc(training_set) #apprentissage de la structure
  } else if(algo == "gs")
  {
    bn = gs(training_set)
  } else if(algo == "tabu")
  {
    bn = tabu(training_set)
  } else if(algo == "mmhc")
  {
    bn = mmhc(training_set)
  } else
  {
    print(paste("Algo",algo,"inconnu !"))
  }
  fitted = bn.fit(bn, training_set, iss = 1, method = "bayes")

    cat("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n\n", file=fichier)
    
    cat("<!-- DTD for the XMLBIF 0.3 format -->\n<!DOCTYPE BIF [\n<!ELEMENT BIF ( NETWORK )*>\n<!ATTLIST BIF VERSION CDATA #REQUIRED>\n   <!ELEMENT NETWORK ( NAME, ( PROPERTY | VARIABLE | DEFINITION )* )>\n      <!ELEMENT NAME (#PCDATA)>\n        <!ELEMENT VARIABLE ( NAME, ( OUTCOME |  PROPERTY )* ) >\n          <!ATTLIST VARIABLE TYPE (nature|decision|utility) \"nature\">\n           <!ELEMENT OUTCOME (#PCDATA)>\n            <!ELEMENT DEFINITION ( FOR | GIVEN | TABLE | PROPERTY )* >\n              <!ELEMENT FOR (#PCDATA)>\n                <!ELEMENT GIVEN (#PCDATA)>\n                  <!ELEMENT TABLE (#PCDATA)>\n                    <!ELEMENT PROPERTY (#PCDATA)>\n                      ]>\n", file=fichier, append=TRUE)
    
    cat("<BIF VERSION=\"0.3\">\n",file=fichier, append=TRUE)
    cat("<NETWORK>\n",file=fichier, append=TRUE)
    
    cat("<NAME>Reco</NAME>\n",file=fichier, append=TRUE)
    # Variables
    for(v in 1:dim(training_set)[2])
    {
      cat("<VARIABLE TYPE=\"nature\">\n", file=fichier, append=TRUE)
      cat(paste(paste("\t<NAME>",colnames(training_set)[v],sep=""),"</NAME>\n",sep=""), file=fichier, append=TRUE)
      #      cat("\t<TYPE>discrete</TYPE>\n", file=fichier, append=TRUE)
      if(is.element("999",levels(training_set[,v])))
      {
        cat("\t<OUTCOME>999</OUTCOME>\n", file=fichier, append=TRUE)
      }
      for(w in 1:length(levels(factor(training_set[,v]))))
      {
        cat(paste(paste("\t<OUTCOME>",levels(factor(training_set[,v]))[w],sep=""),"</OUTCOME>\n",sep=""), file=fichier, append=TRUE)
      }
      cat("</VARIABLE>\n\n", file=fichier, append=TRUE)
    }
    
    # Probabilités
    for(v in 1:dim(training_set)[2])
    {
      cat("<DEFINITION>\n", file=fichier, append=TRUE)
      cat(paste(paste("\t<FOR>",fitted[[v]]$node,sep=""),"</FOR>\n",sep=""), file=fichier, append=TRUE)
      if(length(fitted[[v]]$parents) > 0)
      {
        for(w in 1:length(fitted[[v]]$parents))
        {
          cat(paste(paste("\t<GIVEN>",fitted[[v]]$parents[[length(fitted[[v]]$parents)-w+1]],sep=""),"</GIVEN>\n",sep=""), file=fichier, append=TRUE)
        }
      }
      cat("\t<TABLE>", file=fichier, append=TRUE)
      
      for(w in 1:(length(fitted[[v]]$prob)))
      {
        cat(paste(fitted[[v]]$prob[[w]],""), file=fichier, append=TRUE)
      }
      
      cat("</TABLE>\n", file=fichier, append=TRUE)
      cat("</DEFINITION>\n\n", file=fichier, append=TRUE)
      
    }
    cat("</NETWORK>\n",file=fichier, append=TRUE)
    cat("</BIF>\n",file=fichier, append=TRUE)
    print("Done.")
}