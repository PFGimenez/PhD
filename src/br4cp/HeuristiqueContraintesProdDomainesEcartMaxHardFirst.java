package br4cp;

import java.util.ArrayList;

public class HeuristiqueContraintesProdDomainesEcartMaxHardFirst implements HeuristiqueContraintes {

	public ArrayList<Integer> reorganiseContraintes(LecteurXML l)
	{
		ArrayList<Integer> reorga=new ArrayList<Integer>();

		long ecart;
		long[] score=new long[l.getNbConstraints()];
		for(int i=0; i<l.getNbConstraints(); i++)
			score[i]=-1;
		
		for(int i=0; i<l.getNbConstraints(); i++){
			if(l.cons[i]!=null){
				if(!l.cons[i].relation.softConstraint){			//part 1 : hard
					long ecartmax=0;
					long domainprod=1;
					score[i]=0;
					for(int j=0; j<l.cons[i].scopeID.length; j++){
						for(int k=j+1; k<l.cons[i].scopeID.length; k++){
							ecart=Math.abs(l.cons[i].scopeID[j]-l.cons[i].scopeID[k]);
							if(ecart>ecartmax)
								ecartmax=ecart;
						}
						domainprod*=l.var.get(l.cons[i].scopeID[j]).domain;
						
					}
					score[i]=domainprod*ecartmax;
				}
			}
				
		}
		
		long max=-1;
		int maxVal=-1;
		for(int j=0; j<l.getNbConstraints(); j++){
			for(int i=0; i<l.getNbConstraints(); i++){
				if(l.cons[i]!=null){
					if(score[i]>max){
						max=score[i];
						maxVal=i;
					}
				}
			}
			if(maxVal!=-1){
				reorga.add(maxVal);
				score[maxVal]=-1;
				max=0;
				maxVal=-1;
			}else{
				break;
			}
		}
			
		for(int i=0; i<l.getNbConstraints(); i++){
			if(l.cons[i]!=null){
				if(l.cons[i].relation.softConstraint){				//part 2 soft
					long ecartmax=0;
					long domainprod=1;
					score[i]=0;
					for(int j=0; j<l.cons[i].scopeID.length; j++){
						for(int k=j+1; k<l.cons[i].scopeID.length; k++){
							ecart=Math.abs(l.cons[i].scopeID[j]-l.cons[i].scopeID[k]);
							if(ecart>ecartmax)
								ecartmax=ecart;
						}
						domainprod*=l.var.get(l.cons[i].scopeID[j]).domain;
					}
					score[i]=domainprod*ecartmax;
				}
			}
		}
		
		max=-1;
		maxVal=-1;
		for(int j=0; j<l.getNbConstraints(); j++){
			for(int i=0; i<l.getNbConstraints(); i++){
				if(l.cons[i]!=null){
					if(score[i]>max){
						max=score[i];
						maxVal=i;
					}
				}
			}
			if(maxVal!=-1){
				reorga.add(maxVal);
				score[maxVal]=-1;
				max=-1;
				maxVal=-1;
			}else{			//reste plus que des contraintes supprimes
				for(int i=0; i<l.getNbConstraints(); i++){
					if(l.cons[i]==null){
						reorga.add(i);
					}
				}
				break;
			}
			
		}
		return reorga;
	}			
	
}
