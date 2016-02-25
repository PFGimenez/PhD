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
	private Variable[] set; // contient les indices (= profondeur) des variables à instancier
	private int tailleSet;
	private Instanciation instance;
	private int nbActuel, nbMax;
	private HashMap<String, Integer> mapVar;
	
	/**
	 * On ignore les variables du cutset déjà instanciée dans l'instance fournie
	 * @param instanceActuelle
	 * @param vars
	 * @param mapVariables
	 * @param varsToInstantiate
	 */
	public IteratorInstances(Instanciation instanceActuelle, Variable[] vars, HashMap<String, Integer> mapVariables, int[] varsToInstantiate)
	{
//		mapVar = mapVariables;
		set = new Variable[varsToInstantiate.length];
		instance = instanceActuelle.clone();
//		instance.deconditionne(varsToInstantiate);
//		instance.nbVarInstanciees += varsToInstantiate.size();
		nbMax = 1;
		tailleSet = 0;
		for(int i = 0; i < varsToInstantiate.length; i++)
		{
			int indice = varsToInstantiate[i];
			if(instanceActuelle.values[indice] == null)
			{
				set[tailleSet++] = vars[indice];
				nbMax *= vars[indice].domain;
				instance.nbVarInstanciees++;
			}
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
		for(int i = 0; i < tailleSet; i++)
		{
			Variable v = set[i];
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
