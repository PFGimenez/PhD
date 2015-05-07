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

public class HeuristiqueContraintesAmilastre implements HeuristiqueContraintes {

	public ArrayList<Integer> reorganiseContraintes(ArrayList<Var> var, Constraint[] cons)
	{
		ArrayList<Integer> reorga=new ArrayList<Integer>();
		int nbContraintes = cons.length;
		boolean contient, depasse;
		for(int i=0; i<var.size(); i++){					//on parcour toutes les variable, on ajoute la contrainte si rien derriere
			for(int j=0; j<nbContraintes; j++){
				contient=false; 
				depasse=false;
				if(cons[j]!=null){
					for(int k=0; k<cons[j].scopeID.length; k++){
						if(cons[j].scopeID[k]>i){
							depasse=true;
							break;
						}
						if(cons[j].scopeID[k]==i){
							contient=true;
						}
					}
					if(contient && !depasse){			//ya la derniere variable, mais on depasse pas
						reorga.add(j);
					}
				}
			}
		}
				
	
		for(int i=0; i<nbContraintes; i++){
			if(cons[i]==null)
			reorga.add(i);
		}
		return reorga;
	}			
}
