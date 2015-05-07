package heuristique_contraintes;

import java.util.ArrayList;

import br4cp.Var;
import br4cp.LecteurXML.Constraint;

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

	public ArrayList<Integer> reorganiseContraintes(ArrayList<Var> var, Constraint[] cons)
	{
		int nbContraintes = cons.length;
		ArrayList<Integer> reorga=new ArrayList<Integer>();

		long ecart;
		long[] score=new long[nbContraintes];
		for(int i=0; i<nbContraintes; i++)
			score[i]=-1;
		
		for(int i=0; i<nbContraintes; i++){
			if(cons[i]!=null){
				if(!cons[i].relation.softConstraint){			//part 1 : hard
					long ecartmax=0;
					long domainprod=1;
					score[i]=0;
					for(int j=0; j<cons[i].scopeID.length; j++){
						for(int k=j+1; k<cons[i].scopeID.length; k++){
							ecart=Math.abs(cons[i].scopeID[j]-cons[i].scopeID[k]);
							if(ecart>ecartmax)
								ecartmax=ecart;
						}
						domainprod*=var.get(cons[i].scopeID[j]).domain;
						
					}
					score[i]=domainprod*ecartmax;
				}
			}
				
		}
		
		long max=-1;
		int maxVal=-1;
		for(int j=0; j<nbContraintes; j++){
			for(int i=0; i<nbContraintes; i++){
				if(cons[i]!=null){
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
			
		for(int i=0; i<nbContraintes; i++){
			if(cons[i]!=null){
				if(cons[i].relation.softConstraint){				//part 2 soft
					long ecartmax=0;
					long domainprod=1;
					score[i]=0;
					for(int j=0; j<cons[i].scopeID.length; j++){
						for(int k=j+1; k<cons[i].scopeID.length; k++){
							ecart=Math.abs(cons[i].scopeID[j]-cons[i].scopeID[k]);
							if(ecart>ecartmax)
								ecartmax=ecart;
						}
						domainprod*=var.get(cons[i].scopeID[j]).domain;
					}
					score[i]=domainprod*ecartmax;
				}
			}
		}
		
		max=-1;
		maxVal=-1;
		for(int j=0; j<nbContraintes; j++){
			for(int i=0; i<nbContraintes; i++){
				if(cons[i]!=null){
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
				for(int i=0; i<nbContraintes; i++){
					if(cons[i]==null){
						reorga.add(i);
					}
				}
				break;
			}
			
		}
		return reorga;
	}			
	
}
