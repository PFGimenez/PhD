package br4cp;

import java.util.ArrayList;

/*   (C) Copyright 2013, Schmidt Nicolas
 * 
 *   This program is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

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
