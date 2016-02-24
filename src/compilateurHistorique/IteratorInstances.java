package compilateurHistorique;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

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
 * Un itérateur sur les instances
 * @author pgimenez
 *
 */

public class IteratorInstances implements Iterator<Instanciation>
{
	private ArrayList<Variable> set; // contient les indices (= profondeur) des variables à instancier
	private HashMap<String, Integer> mapVar;
	private Instanciation instance;
	private int nbActuel, nbMax;

	public IteratorInstances(Instanciation instanceActuelle, Variable[] vars, HashMap<String, Integer> mapVariables, ArrayList<String> varsToInstantiate)
	{
		mapVar = mapVariables;
		set = new ArrayList<Variable>();
		instance = instanceActuelle.clone();
		nbMax = 1;
		for(String s : varsToInstantiate)
		{
			Variable var = vars[mapVar.get(s)];
			set.add(var);
			nbMax *= var.domain;
		}
		nbActuel = 0;
	}
	
	@Override
	public boolean hasNext()
	{
		return nbActuel < nbMax;
	}

	@Override
	public Instanciation next()
	{
		int tmp = nbActuel;
		for(Variable v : set)
		{
			instance.values[v.profondeur] = tmp % v.domain;
			tmp /= v.domain;
		}
		nbActuel++;
		return instance.clone();
	}

	@Override
	public void remove()
	{
		throw new UnsupportedOperationException();
	}

}
