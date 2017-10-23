package compilateurHistorique;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

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
 * @author Pierre-François Gimenez
 *
 */

public class IteratorInstancesPartielles implements Iterator<Instanciation>, Iterable<Instanciation>
{
	private ArrayList<Variable> set; // contient les indices (= profondeur) des variables à instancier
	private HashMap<String, Integer> mapVar;
	private Instanciation instance;
	private int nbActuel, nbMax;

	public IteratorInstancesPartielles(Instanciation instanceActuelle, DatasetInfo dataset, List<String> varsToInstantiate)
	{
		this(instanceActuelle, dataset.vars, dataset.mapVar, varsToInstantiate);		
	}
	
	public IteratorInstancesPartielles(Instanciation instanceActuelle, Variable[] vars, HashMap<String, Integer> mapVariables, List<String> varsToInstantiate)
	{
		mapVar = mapVariables;
		set = new ArrayList<Variable>();
		instance = instanceActuelle.clone();
		instance.deconditionne(varsToInstantiate);
//		nbVarInstanciees = instance.nbVarInstanciees + varsToInstantiate.size();
		nbMax = 1;
		for(String s : varsToInstantiate)
		{
			Variable var = vars[mapVar.get(s)];
			set.add(0,var);
			nbMax *= var.domain + 1;
		}
		nbActuel = 0;
	}
	
	public int getNbInstances()
	{
		return nbMax;
	}
	
	@Override
	public boolean hasNext()
	{
		return nbActuel < nbMax;
	}

	@Override
	public Instanciation next()
	{
		Instanciation out = instance.clone();
		int tmp = nbActuel;
		for(Variable v : set)
		{
			int val = tmp % (v.domain + 1);
			assert !out.isConditionne(v.index);
			
			if(val != v.domain)
				out.conditionne(v.index, val);

			tmp /= (v.domain + 1);
		}
		nbActuel++;
		return out;
	}

	@Override
	public void remove()
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public Iterator<Instanciation> iterator()
	{
		return this;
	}

}
