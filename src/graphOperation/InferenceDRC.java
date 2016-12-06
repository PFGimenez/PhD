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

package graphOperation;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import compilateurHistorique.Instanciation;
import compilateurHistorique.IteratorInstances;
import compilateurHistorique.MultiHistoComp;

/**
 * L'inférence par l'algorithme DRC
 * @author Pierre-François Gimenez
 *
 */

public class InferenceDRC
{
	private MultiHistoComp historique;
	private double norm; // le nombre d'instances totales, pour normaliser la proba
	private int seuil; // le nombre d'exemples à partir duquel on estime avec l'historique
	private Map<Instanciation, Double> cache = new HashMap<Instanciation, Double>();
	private Map<Instanciation, Partition> cachePartition = new HashMap<Instanciation, Partition>(); // TODO : clé = ensemble de variables, pas une instanciation
	private DAG dag;
	private Map<String, Integer> mapvar;
	
	public InferenceDRC(int seuil, DAG dag, MultiHistoComp historique)
	{
		this.dag = dag;
		this.historique = historique;
		norm = historique.getNbInstancesTotal();
		this.seuil = seuil;
		mapvar = MultiHistoComp.getMapVar();
	}

	public double infere(Instanciation u)
	{
		if(u.getNbVarInstanciees() == 0)
			return 1;
		
		Double valeurCachee = cache.get(u);
		if(valeurCachee != null)
			return valeurCachee;
		
		int nbu = historique.getNbInstances(u);
		// on doit calculer la valeur
		if(nbu > seuil)
		{
			double p = nbu / norm;
			cache.put(u, p);
			return p;
		}
		
		if(!cachePartition.containsKey(u))
		{
			Set<String> instanciees = new HashSet<String>();
			instanciees.addAll(u.getVarConditionees());
			MoralGraph gm = new MoralGraph(dag, instanciees, false);
			gm.computeDijkstra();
			if(gm.getDistanceMax() <= 1)
				cachePartition.put(u, null);
			else
			{
				gm.prune();
				cachePartition.put(u, gm.computeSeparator());				
			}
		}
		
		Partition partition = cachePartition.get(u);
		
		if(partition == null)
		{
			double p = nbu / norm;
			cache.put(u, p);
			return p;
		}
		
		IteratorInstances iterator = new IteratorInstances(partition.separateur.size());
		int[] separateur = new int[partition.separateur.size()];
		int[] g0 = new int[partition.ensembles[0].size() + partition.separateur.size()];
		int[] g1 = new int[partition.ensembles[1].size() + partition.separateur.size()];

		int i = 0;
		for(String s : partition.separateur)
			separateur[i++] = mapvar.get(s);
		
		i = 0;
		for(String s : partition.ensembles[0])
			g0[i++] = mapvar.get(s);
		for(String s : partition.separateur)
			g0[i++] = mapvar.get(s);

		i = 0;
		for(String s : partition.ensembles[1])
			g1[i++] = mapvar.get(s);
		for(String s : partition.separateur)
			g1[i++] = mapvar.get(s);

		iterator.init(u, separateur);
		Instanciation u1, u2, uS;
		double p = 0;
		
		while(iterator.hasNext())
		{
			u1 = u.subInstanciation(g0);
			u2 = u.subInstanciation(g1);
			uS = u.subInstanciation(separateur);
			p += infere(u1) * infere(u2) / infere(uS);
		}
		
		cache.put(u, p);
		return p;
	}
	
}
