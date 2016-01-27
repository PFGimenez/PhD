package compilateurHistorique;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;


/*   (C) Copyright 2016, Gimenez Pierre-François
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

/**
 * Feuille du VDD
 * @author pgimenez
 *
 */

public class VDDLeaf extends VDDAbstract implements Serializable
{
	private static final long serialVersionUID = 1L;
	private int nbInstances = 0;

	@Override
	public void addInstanciation(String[] values)
	{
		nbInstances++;
	}
	
	@Override
	public int getNbInstances(String[] values, int nbVarInstanciees)
	{
//		if(nbVarInstanciees > 0)
//			System.out.println("Erreur! "+nbVarInstanciees+" != 0");
		return nbInstances;
	}
	
	@Override
	protected void getNbInstancesToutesModalitees(HashMap<String, Integer> out, int nbVar, String[] values, ArrayList<String> possibles, int nbVarInstanciees)
	{}
	
	@Override
	public int getNbNoeuds()
	{
		return 1;
	}

}
