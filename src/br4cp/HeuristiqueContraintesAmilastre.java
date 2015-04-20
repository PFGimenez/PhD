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
