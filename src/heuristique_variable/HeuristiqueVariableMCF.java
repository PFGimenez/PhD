package heuristique_variable;

import java.util.ArrayList;

import br4cp.Ordonnancement;
import br4cp.Var;

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

public class HeuristiqueVariableMCF implements HeuristiqueVariable {

	@Override
	public void reordoner(int[][] contraintes,
			Ordonnancement ord) {
		
		ArrayList<Var> listeTemp=new ArrayList<Var>();
		
		ord.constNbContraintes(contraintes);
		
		int max=-1;
		int varmax=-1;
		
		for(int curr=0; curr<ord.size; curr++){
			for(int i=0; i<ord.size; i++){
				if(ord.nbContraintes[i]>max){
					max=ord.nbContraintes[i];
					varmax=i;
				}
			}
			//System.out.println(varmax + "   " + variables.get(varmax).name);
			listeTemp.add(ord.variables.get(varmax));
			
			ord.nbContraintes[varmax]=-1;		//faut plus qu'elle ressorte
			max=-1;
			varmax=-1;
		}

		for(int i=0; i<listeTemp.size(); i++)
			ord.variables.set(i, listeTemp.get(i));
		
		
	}

}
