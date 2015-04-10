package br4cp;

import java.util.ArrayList;

public class HeuristiqueContraintesDomEcartPlusDomDomEcartPlusHardFirst implements HeuristiqueContraintes {

	public ArrayList<Integer> reorganiseContraintes(LecteurXML l)
	{
		ArrayList<Integer> reorga=new ArrayList<Integer>();

		int proddomain=1;
		int curr=l.var.size();
		int next=l.var.size();
		int previous=-1;
		
		int[] score=new int[l.getNbConstraints()];
		for(int i=0; i<l.getNbConstraints(); i++)
			score[i]=-1;
		int max=-1;
		int maxVal=-1;
	
			
		for(int i=0; i<l.getNbConstraints(); i++){
	
			if(l.cons[i]!=null){
				
				score[i]=0;
				previous=-1;
				proddomain=1;
	
				for(int prout=0; prout<l.cons[i].scopeID.length-1; prout++){
					curr=l.var.size();
					next=l.var.size();
					for(int j=0; j<l.cons[i].scopeID.length; j++){
						if(l.cons[i].scopeID[j]<curr && l.cons[i].scopeID[j]>previous){
							next=curr;
							curr=l.cons[i].scopeID[j];
						}else{
							if(l.cons[i].scopeID[j]<next && l.cons[i].scopeID[j]>previous){
								next=l.cons[i].scopeID[j];
							}
						}
					}
	
					proddomain*=l.var.get(curr).domain;
					score[i]+=(proddomain*(next-curr));
	//var(id) domain -> proddomain n-c tot
					previous=curr;
				}
				proddomain*=l.var.get(next).domain;
				score[i]+=proddomain;
				
			}
		}
		
		max=-1;
		maxVal=-1;
		int j=0;
		for(j=0; j<l.getNbConstraints(); j++){
			for(int i=0; i<l.getNbConstraints(); i++){
				if(l.cons[i]!=null && !l.cons[i].relation.softConstraint){
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
			}else{			//reste plus que des contraintes souples
				max=-1;
				maxVal=-1;
				break;
			}
		}
		
		for(; j<l.getNbConstraints(); j++){
			for(int i=0; i<l.getNbConstraints(); i++){
				if(l.cons[i]!=null && l.cons[i].relation.softConstraint){
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
			}else{			//reste plus que des contraintes nulles
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
