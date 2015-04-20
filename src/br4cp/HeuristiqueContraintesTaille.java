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

public class HeuristiqueContraintesTaille implements HeuristiqueContraintes {
	
	public ArrayList<Integer> reorganiseContraintes(LecteurXML l)
	{
		ArrayList<Integer> reorga=new ArrayList<Integer>();
		int max=0;
		int maxVal=-1;
		for(int j=0; j<l.getNbConstraints(); j++){
			for(int i=0; i<l.getNbConstraints(); i++){
	
				if(l.cons[i]!=null){
					if(!reorga.contains(i) && l.cons[i].arity>max){
						max=l.cons[i].arity;
						maxVal=i;
					}
				}
			}
			if(maxVal!=-1){
				reorga.add(maxVal);
				max=0;
				maxVal=-1;
			}else{			//reste plus que des contraintes supprimes
				for(int i=0; i<l.getNbConstraints(); i++){
					if(l.cons[i]==null)
						reorga.add(i);
				}
				break;
			}
			
		}
		return reorga;
	}		
}
