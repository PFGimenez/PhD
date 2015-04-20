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

public class HeuristiqueVariableOrdreRandom implements HeuristiqueVariable {

	@Override
	public void reordoner(int[][] contraintes,
			Ordonnancement ord) {
		ArrayList<Var> listeTemp=new ArrayList<Var>();

		for(int i=0; i<ord.variables.size(); i++)
			listeTemp.add(ord.variables.get(i));

	ord.variables.clear();
	
	while(!listeTemp.isEmpty()){
		int rand=(int) Math.floor(Math.random()*listeTemp.size());
		ord.variables.add(listeTemp.get(rand));
		listeTemp.remove(rand);
	}
	

	}

}
