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

public class HeuristiqueContraintesDomaineMaxDomaineMaxEcartMax implements HeuristiqueContraintes {

	public ArrayList<Integer> reorganiseContraintes(ArrayList<Var> var, Constraint[] cons)
	{
		int nbContraintes = cons.length;
		ArrayList<Integer> reorga=new ArrayList<Integer>();
		int ecart;
		int domain;
		int[] score=new int[nbContraintes];
		for(int i=0; i<nbContraintes; i++){
			int ecartmax=0;
			int domainmax=0;
			int domainmax2=1;
			if(cons[i]!=null){
				for(int j=0; j<cons[i].scopeID.length; j++){
					for(int k=j+1; k<cons[i].scopeID.length; k++){
						ecart=Math.abs(cons[i].scopeID[j]-cons[i].scopeID[k]);
						if(ecart>ecartmax)
							ecartmax=ecart;
					}
					domain=var.get(cons[i].scopeID[j]).domain;
					if(domain>domainmax){
						domainmax2=domainmax;
						domainmax=domain;
					}else{
						if(domain>domainmax2)
							domainmax2=domain;
					}
				}
				score[i]=domainmax*domainmax2*ecartmax;
			}
		}
		
		int max=0;
		int maxVal=-1;
		for(int j=0; j<nbContraintes; j++){
			for(int i=0; i<nbContraintes; i++){
				if(cons[i]!=null){
					if(!reorga.contains(i) && score[i]>max){
						max=score[i];
						maxVal=i;
					}
				}
			}
			if(maxVal!=-1){
				reorga.add(maxVal);
				max=0;
				maxVal=-1;
			}else{			//reste plus que des contraintes supprimes
				for(int i=0; i<nbContraintes; i++){
					if(cons[i]==null)
						reorga.add(i);
				}
				break;
			}
			
		}
		return reorga;
	}		
}
