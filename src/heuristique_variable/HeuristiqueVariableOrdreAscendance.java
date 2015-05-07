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

public class HeuristiqueVariableOrdreAscendance implements HeuristiqueVariable {

	@Override
	public void reordoner(int[][] contraintes,
			Ordonnancement ord) {
		ArrayList<Var> listeTemp=new ArrayList<Var>();
	int indexMax;
	int temp;
	ArrayList<Integer> reordre=new ArrayList<Integer>();
	
	for(int i=0; i<contraintes.length; i++){
		//System.out.println("xaer =>> " + i);
		indexMax=-1;
		if(contraintes[i].length>=2){
			for(int j=0; j<contraintes[i].length-1; j++){
				if(reordre.indexOf(contraintes[i][j])>indexMax)
						indexMax=reordre.indexOf(contraintes[i][j]);
			}
			temp=reordre.indexOf(contraintes[i][contraintes[i].length-1]);
			if(temp<indexMax){												//mal classé
				reordre.remove(temp);
				reordre.add(indexMax, contraintes[i][contraintes[i].length-1]);
				i=-1;
			}
		}
	}
	for(int i=0; i<ord.size; i++)
		listeTemp.add(ord.variables.get(reordre.get(i)));
	for(int i=0; i<listeTemp.size(); i++)
		ord.variables.set(i, listeTemp.get(i));
	}
	
	
}
