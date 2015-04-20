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

public class HeuristiqueVariableOrdreChoisi implements HeuristiqueVariable{

	public void reordoner(int[][] contraintes, Ordonnancement ord)
	{
		ArrayList<Var> listeTemp=new ArrayList<Var>();
		
		listeTemp.add(ord.variables.get(ord.variables.size()-1));
		ord.variables.remove(ord.variables.size()-1);
	
		while(listeTemp.size()!=0){
			ord.variables.add(0, listeTemp.get(listeTemp.size()-1));
			listeTemp.remove(listeTemp.size()-1);
		}

	}
	
}
