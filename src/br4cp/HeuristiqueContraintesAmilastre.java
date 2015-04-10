package br4cp;

import java.util.ArrayList;

public class HeuristiqueContraintesAmilastre implements HeuristiqueContraintes {

	public ArrayList<Integer> reorganiseContraintes(LecteurXML l)
	{
		ArrayList<Integer> reorga=new ArrayList<Integer>();
		boolean contient, depasse;
		for(int i=0; i<l.var.size(); i++){					//on parcour toutes les variable, on ajoute la contrainte si rien derriere
			for(int j=0; j<l.getNbConstraints(); j++){
				contient=false; 
				depasse=false;
				if(l.cons[j]!=null){
					for(int k=0; k<l.cons[j].scopeID.length; k++){
						if(l.cons[j].scopeID[k]>i){
							depasse=true;
							break;
						}
						if(l.cons[j].scopeID[k]==i){
							contient=true;
						}
					}
					if(contient && !depasse){			//ya la derniere variable, mais on depasse pas
						reorga.add(j);
					}
				}
			}
		}
				
	
		for(int i=0; i<l.getNbConstraints(); i++){
			if(l.cons[i]==null)
			reorga.add(i);
		}
		return reorga;
	}			
}
