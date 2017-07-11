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
	private HistoriqueCompile[] historiques;
	private Instanciation[] instanciations;
	private Instanciation[] centres;
	private int nbVars;
	private List<Instanciation>[] clusters, clustersTmp;
	private int nbInstances;
	
	@SuppressWarnings("unchecked")
	public Clusters(int k, ArrayList<String> filename, boolean entete)
	{
		this.k = k;
		historiques = new HistoriqueCompile[k];
		DatasetInfo dataset = new DatasetInfo(filename, entete);
		instanciations = HistoriqueCompile.readInstances(dataset, filename, entete);
		nbVars = instanciations[0].values.length;
		nbInstances = instanciations.length;
		centres = new Instanciation[k];
		clusters = (List<Instanciation>[]) new List[k];
		clustersTmp = (List<Instanciation>[]) new List[k];
		Random r = new Random();
		for(int i = 0; i < k; i++)
			centres[i] = instanciations[r.nextInt(nbInstances)];
		boolean change;
		
		// Calcul de k-means
		do {
			change = updateClusters();
			if(change)
				updateCentres();
		} while(change);
		
		/*
		 * Création des historiques pour chaque cluster
		 */
		for(int i = 0; i < k; i++)
		{
			historiques[i] = new HistoriqueCompile(dataset);
			Instanciation[] part = new Instanciation[clusters[i].size()];
			for(int j = 0; j < clusters[i].size(); j++)
				part[j] = clusters[i].get(j);
			historiques[i].compile(part);
		}
			
	}
	
	/**
	 * Met à jour les clusters.
	 * Renvoie "vrai" si les clusters ont effectivement changé
	 * @return
	 */
	private boolean updateClusters()
	{
		boolean change = false;
		for(int i = 0; i < k; i++)
			clustersTmp[i].clear();
		for(Instanciation e : instanciations)
		{
			int k = getNearestCluster(e);
			if(!change && !clusters[k].contains(e)) // si e a changé de cluster
				change = true;
			clustersTmp[k].add(e);
		}
		for(int i = 0; i < k; i++)
		{
			clusters[i].clear();
			clusters[i].addAll(clustersTmp[i]);
		}
		return change;
	}
	
	/**
	 * Renvoie le cluster qui correspond à l'instanciation
	 * @param e
	 * @return
	 */
	public int getNearestCluster(Instanciation e)
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
	
	/**
	 * Recalcule les centres des clusters
	 */
	private void updateCentres()
	{
		HashMap<Integer, HashMap<Integer, Integer>> nb = new HashMap<Integer, HashMap<Integer, Integer>>();
		for(int v = 0; v < nbVars; v++)
			nb.put(v, new HashMap<Integer, Integer>());
		
		for(int i = 0; i < k; i++)
		{
			for(Instanciation e : clusters[i])
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
