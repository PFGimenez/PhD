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

import compilateurHistorique.EnsembleVariables;
import compilateurHistorique.InstanceMemoryManager;
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
	private Map<Instanciation, Double> cache = new HashMap<Instanciation, Double>(); // cache des proba
	private Map<EnsembleVariables, Partition> cachePartition = new HashMap<EnsembleVariables, Partition>(); // cache de la partition
	private DAG dag;
	private Map<String, Integer> mapvar;
	private boolean verbose = true;
	
	public InferenceDRC(int seuil, DAG dag, MultiHistoComp historique)
	{
		this.dag = dag;
		this.historique = historique;
		norm = historique.getNbInstancesTotal();
		this.seuil = seuil;
		mapvar = MultiHistoComp.getMapVar();
	}

	public double infere(Instanciation u, EnsembleVariables U)
	{
		System.out.println("Calcul de "+u);
		if(u.getNbVarInstanciees() == 0)
			return 1;
		
		Double valeurCachee = cache.get(u);
		if(valeurCachee != null)
			return valeurCachee;
		
		int nbu = historique.getNbInstances(u);

		// on doit calculer la valeur
		if(nbu > seuil)
		{
			if(verbose)
				System.out.println("Utilisation de l'historique (> seuil)");
			double p = nbu / norm;
			cache.put(u, p);
			return p;
		}
		
		if(!cachePartition.containsKey(U))
		{
			Set<String> instanciees = new HashSet<String>();
			instanciees.addAll(u.getVarConditionees());
			MoralGraph gm = new MoralGraph(dag, instanciees, false);
			gm.computeDijkstra();
			if(gm.getDistanceMax() <= 1)
				cachePartition.put(U, null);
			else
			{
				gm.prune();
				cachePartition.put(U, gm.computeSeparator());				
			}
		}
		
		Partition partition = cachePartition.get(U);
		
		if(partition == null)
		{
			if(verbose)
				System.out.println("Utilisation de l'historique (feuille)");
			double p = nbu / norm;
			cache.put(u, p);
			return p;
		}
		
		if(verbose)
			System.out.println("Décomposition du calcul :\n"+partition);

		IteratorInstances iterator = new IteratorInstances(partition.separateur.size());
		int[] separateur = new int[partition.separateur.size()];
		int[] g0c = new int[partition.ensembles[0].size() + partition.separateur.size()];
		int[] g1c = new int[partition.ensembles[1].size() + partition.separateur.size()];

		int i = 0;
		for(String s : partition.separateur)
			separateur[i++] = mapvar.get(s);
		
		i = 0;
		for(String s : partition.ensembles[0])
			g0c[i++] = mapvar.get(s);
		for(String s : partition.separateur)
			g0c[i++] = mapvar.get(s);

		i = 0;
		for(String s : partition.ensembles[1])
			g1c[i++] = mapvar.get(s);
		for(String s : partition.separateur)
			g1c[i++] = mapvar.get(s);

		iterator.init(u, separateur);
		Instanciation u1, u2, uS;
		EnsembleVariables G0, G1, C;
		double p = 0;
		
		u1 = u.subInstanciation(g0c);
		u2 = u.subInstanciation(g1c);
		uS = u.subInstanciation(separateur);
		G0 = u1.getEVConditionees();
		G1 = u2.getEVConditionees();
		C = uS.getEVConditionees();

		Instanciation preums = null;
		
		while(iterator.hasNext())
		{
			u = iterator.next();
			u1 = u.subInstanciation(g0c);
			u2 = u.subInstanciation(g1c);
			uS = u.subInstanciation(separateur);
			p += infere(u1, G0) * infere(u2, G1) / infere(uS, C);

			if(preums == null)
				preums = u1;
		}
		
		InstanceMemoryManager.getMemoryManager().clearFrom(preums);

		cache.put(u, p);
		return p;
	}
	
}
