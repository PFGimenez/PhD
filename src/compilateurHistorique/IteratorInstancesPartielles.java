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
 * Un itérateur sur les instances qui permet aussi les instances partielles
 * @author pgimenez
 *
 */

public class IteratorInstancesPartielles implements Iterator<Instanciation>
{
	private ArrayList<Variable> set; // contient les indices (= profondeur) des variables à instancier
	private HashMap<String, Integer> mapVar;
	private Instanciation instance;
	private int nbVarInstanciees;
	private int nbActuel, nbMax;

	public IteratorInstancesPartielles(Instanciation instanceActuelle, Variable[] vars, HashMap<String, Integer> mapVariables, ArrayList<String> varsToInstantiate)
	{
		mapVar = mapVariables;
		set = new ArrayList<Variable>();
		instance = instanceActuelle.clone();
		instance.deconditionne(varsToInstantiate);
		instance.nbVarInstanciees += varsToInstantiate.size();
		nbVarInstanciees = instance.nbVarInstanciees;
		nbMax = 1;
		for(String s : varsToInstantiate)
		{
			Variable var = vars[mapVar.get(s)];
			set.add(0,var);
			nbMax *= var.domain + 1;
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
		instance.nbVarInstanciees = nbVarInstanciees;
		int tmp = nbActuel;
		for(Variable v : set)
		{
			instance.values[v.profondeur] = tmp % (v.domain + 1);
			
			if(instance.values[v.profondeur] == v.domain)
			{
				instance.nbVarInstanciees--;
				instance.values[v.profondeur] = null;
			}
			
			tmp /= (v.domain + 1);
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
