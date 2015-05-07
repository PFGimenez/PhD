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

public class HeuristiqueVariable9 implements HeuristiqueVariable {

	@Override
	public void reordoner(int[][] contraintes,
			Ordonnancement ord) {
		ArrayList<Var> listeTemp=new ArrayList<Var>();
		ord.constGraphAdj(contraintes);
	//constNbContraintes(contraintes);
	
	int score[]=new int[ord.size];
	int max=-1;
	int varmax=-1;
	
	for(int curr=0; curr<ord.size; curr++){
		for(int i=0; i<ord.size; i++){
			if(score[i]>max){
				max=score[i];
				varmax=i;
			}
		}
		listeTemp.add(ord.variables.get(varmax));
		
		score[varmax]=-1;		//faut plus qu'elle ressorte
		//mise a jours de score
		for(int i=0; i<ord.size; i++){
			if(score[i]!=-1 && ord.graphAdj[varmax][i]>0){
				//recherche de l'arite max
				score[i]+=ord.graphAdj[varmax][i]-1;
				//score[i]+=1;
			}
		}
		
		max=-1;
		varmax=-1;
			
	}
	for(int i=0; i<listeTemp.size(); i++)
		ord.variables.set(i, listeTemp.get(i));
	}
	
}
