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

public class HeuristiqueVariableOrdreRandom implements HeuristiqueVariable {

	@Override
	public ArrayList<Var> reordoner(int[][] contraintes, ArrayList<Var> listeVariables, Ordonnancement ord) {
		ArrayList<Var> listeTemp=new ArrayList<Var>();
		ArrayList<Var> liste=new ArrayList<Var>();

		
		for(int i=0; i<listeVariables.size(); i++)
			listeTemp.add(listeVariables.get(i));
	
	while(!listeTemp.isEmpty()){
		int rand=(int) Math.floor(Math.random()*listeTemp.size());
		liste.add(listeTemp.get(rand));
		listeTemp.remove(rand);
	}
	
	return liste;
	

	}

}
