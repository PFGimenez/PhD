/*   (C) Copyright 2017, Gimenez Pierre-François 
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

package compilateurHistorique;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

/**
 * Un ensemble d'historique réparti en cluster
 * @author Pierre-François Gimenez
 *
 */

public class Clusters
{
	private int k;
	private MultiHistoComp[] historiques;
	private Instanciation[] instanciations;
	private Instanciation[] centres;
	private int nbVars;
	private List<Instanciation>[] partitions;
	private int nbInstances;
	
	@SuppressWarnings("unchecked")
	public Clusters(int k, ArrayList<String> filename, boolean entete)
	{
		this.k = k;
		historiques = new MultiHistoComp[k];
		instanciations = MultiHistoComp.readInstances(filename, entete, -1);
		nbVars = instanciations[0].values.length;
		nbInstances = instanciations.length;
		centres = new Instanciation[k];
		partitions = (List<Instanciation>[]) new List[k];
		Random r = new Random();
		for(int i = 0; i < k; i++)
			centres[i] = instanciations[r.nextInt(nbInstances)];
		boolean change;
		do {
			change = updatePartition();
			if(change)
				updateCentres();
		} while(change);
		for(int i = 0; i < k; i++)
		{
			historiques[i] = new MultiHistoComp(filename, entete, null);
			Instanciation[] part = new Instanciation[partitions[i].size()];
			for(int j = 0; j < partitions[i].size(); j++)
				part[j] = partitions[i].get(j);
			historiques[i].compile(part, null);
		}
			
	}
	
	private boolean updatePartition()
	{
		int hash = partitions.hashCode();
		for(int i = 1; i < k; i++)
			partitions[i].clear();
		for(Instanciation e : instanciations)
			partitions[getPartition(e)].add(e);

		return partitions.hashCode() != hash;
	}
	
	public int getPartition(Instanciation e)
	{
		int min = centres[0].distance(e);
		int argmin = 0;
		for(int i = 1; i < k; i++)
		{
			int minTmp = centres[i].distance(e);
			if(minTmp < min)
			{
				min = minTmp;
				argmin = i;
			}
		}
		return argmin;
	}
	
	private void updateCentres()
	{
		HashMap<Integer, HashMap<Integer, Integer>> nb = new HashMap<Integer, HashMap<Integer, Integer>>();
		for(int v = 0; v < nbVars; v++)
			nb.put(v, new HashMap<Integer, Integer>());
		
		for(int i = 0; i < k; i++)
		{
			for(Instanciation e : partitions[i])
			{
				for(int v = 0; v < nbVars; v++)
				{
					HashMap<Integer, Integer> occ = nb.get(v);
					Integer curr = occ.get(e.values[v]);
					if(curr == null)
						curr = 0;
					curr += 1;
					occ.put(e.values[v], curr);
				}
			}
			for(int v = 0; v < nbVars; v++)
			{
				Integer bestValue = null;
				Integer nbBest = Integer.MIN_VALUE;
				HashMap<Integer, Integer> occ = nb.get(v);
				for(Integer value : occ.keySet())
				{
					if(occ.get(value) > nbBest)
					{
						nbBest = occ.get(value);
						bestValue = value;
					}
				}
				centres[i].values[v] = bestValue;

			}
		}
	}
	
}
