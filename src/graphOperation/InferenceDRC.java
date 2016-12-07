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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
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
	private double logNorm; // le nombre d'instances totales, pour normaliser la proba
	private int seuil; // le nombre d'exemples à partir duquel on estime avec l'historique
	private Map<Instanciation, Double> cache = new HashMap<Instanciation, Double>(); // cache des proba
	private Map<EnsembleVariables, Partition> cachePartition = new HashMap<EnsembleVariables, Partition>(); // cache de la partition
	private DAG dag;
	private Map<String, Integer> mapvar;
	private boolean verbose = false;
	private List<Double> tmp_p = new ArrayList<Double>();
	
	// TODO : indice cache partition : X et l
	
	public InferenceDRC(int seuil, DAG dag, MultiHistoComp historique, boolean verbose)
	{
		this.verbose = verbose;
		this.dag = dag;
		this.historique = historique;
		logNorm = Math.log(historique.getNbInstancesTotal());
		this.seuil = seuil;
		mapvar = MultiHistoComp.getMapVar();
	}

	/**
	 * Renvoie log(p(u))
	 * @param u
	 * @param U
	 * @return
	 */
	public double infere(Instanciation u, EnsembleVariables U)
	{
		if(verbose)
			System.out.println("Calcul de "+u);
		if(u.getNbVarInstanciees() == 0)
			return 1;
		
		Double valeurCachee = cache.get(u);
		if(valeurCachee != null)
		{
			if(verbose)
				System.out.println("Utilisation de la proba en cache : "+Math.exp(valeurCachee));
			return valeurCachee;
		}
		
		int nbu = historique.getNbInstances(u);

		// on doit calculer la valeur
		if(nbu > seuil)
		{
			double p = Math.log(nbu) - logNorm;
			if(verbose)
				System.out.println("Utilisation de l'historique (> seuil) : "+Math.exp(p));
			cache.put(u.clone(), p);
//			if(verbose)
//				System.out.println("p("+u+") = "+p);
			return p;
		}
		
		if(!cachePartition.containsKey(U))
		{
			if(verbose)
				System.out.println("Calcul de la partition");
			Set<String> instanciees = new HashSet<String>();
			instanciees.addAll(u.getVarConditionees());
			MoralGraph gm = new MoralGraph(dag, instanciees, false);
			gm.computeDijkstra();
			if(gm.getDistanceMax() <= 1)
			{
				if(verbose)
					System.out.println("Famille !");
				cachePartition.put(U, null);
			}
			else
			{
				gm.prune();
				Partition p = gm.computeSeparator();
				cachePartition.put(U, p);
			}
		}
		else if(verbose)
			System.out.println("Utilisation de la partition en cache");
		
		Partition partition = cachePartition.get(U);
		
		if(partition == null)
		{
			double p = Math.log(nbu) - logNorm;
			if(verbose)
				System.out.println("Utilisation de l'historique (feuille) : "+Math.exp(p));
			cache.put(u.clone(), p);
//			if(verbose)
//				System.out.println("p("+u+") = "+p);
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
		
		u1 = u.subInstanciation(g0c);
		u2 = u.subInstanciation(g1c);
		uS = u.subInstanciation(separateur);
		G0 = u1.getEVConditionees();
		G1 = u2.getEVConditionees();
		C = uS.getEVConditionees();

		Instanciation preums = null;
		
		tmp_p.clear();
		Double max = null;
		
		while(iterator.hasNext())
		{
			u = iterator.next();
			u1 = u.subInstanciation(g0c);
			u2 = u.subInstanciation(g1c);
			uS = u.subInstanciation(separateur);
			
			if(preums == null)
				preums = u1;
			
			double pG0 = infere(u1, G0);
			double pG1 = infere(u2, G1);
			double pC = infere(uS, C);
			
			double p = pG0 + pG1 - pC;
			
			if(pG0 == Double.NEGATIVE_INFINITY || pG1 == Double.NEGATIVE_INFINITY)
				continue;
			
			if(max == null)
				max = p;
			else if(max < p)
			{
				tmp_p.add(max);
				max = p;
			}
			else
				tmp_p.add(p);
		}
		
		Double p = max;
		if(p == null)
			return Double.NEGATIVE_INFINITY;
			
		double q = 0;
		for(Double d : tmp_p)
			q += Math.exp(d - max);
		if(!tmp_p.isEmpty())
			p += Math.log1p(q);
		
		InstanceMemoryManager.getMemoryManager().clearFrom(preums);

		cache.put(u.clone(), p);
		return p;
	}
	
	/**
	 * Ne supprime que certaines valeurs du cache peu susceptible d'être réutilisées
	 */
	public void partialClearCache()
	{
		Set<Instanciation> remove = new HashSet<Instanciation>();
		Set<EnsembleVariables> removePartition = new HashSet<EnsembleVariables>();
		for(Instanciation u : cache.keySet())
		{
			if(u.getNbVarInstanciees() > 5)
				remove.add(u);
			if(u.getNbVarInstanciees() > 7)
				removePartition.add(u.getEVConditionees());
		}
		for(Instanciation u : remove)
			cache.remove(u);
		for(EnsembleVariables U : removePartition)
			cachePartition.remove(U);
	}
	
	/**
	 * Réinitialise le cache complètement
	 */
	public void clearCache()
	{
		cache.clear();
		cachePartition.clear();
	}
	
}
