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
import java.util.Stack;

import compilateurHistorique.EnsembleVariables;
import compilateurHistorique.InstanceMemoryManager;
import compilateurHistorique.Instanciation;
import compilateurHistorique.IteratorInstances;
import compilateurHistorique.MultiHistoComp;
import compilateurHistorique.Variable;

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
	private Map<EnsembleVariables, MoralGraph> cachePartition = new HashMap<EnsembleVariables, MoralGraph>(); // cache des partitions
	private DAG dag;
	private Map<String, Integer> mapvar;
	private boolean verbose = false;
	private Variable[] vars;
	private double equivalentSampleSize = 10;
	private Stack<Double> pileProba = new Stack<Double>();

	public InferenceDRC(int seuil, DAG dag, MultiHistoComp historique, int equivalentSampleSize, boolean verbose)
	{
		this.verbose = verbose;
		this.dag = dag;
		this.historique = historique;
		vars = historique.getVariablesLocal();
		norm = historique.getNbInstancesTotal();
		this.seuil = seuil;
		mapvar = MultiHistoComp.getMapVar();
	}

	/**
	 * Renvoie log(p(u))
	 * @param u
	 * @return
	 */
	public double infere(Instanciation u)
	{
		return infere(u, u.getEVConditionees());
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
		
		if(u.getNbVarInstanciees() != U.vars.length)
		{
			System.out.println(u+" "+U);
			int z = 0;
			z = 1/z;
		}
		
		if(u.getNbVarInstanciees() == 0)
			return 0;
		
		Double valeurCachee = cache.get(u);
		if(valeurCachee != null)
		{
			if(verbose)
				System.out.println("Utilisation de la proba en cache : "+Math.exp(valeurCachee));
			return valeurCachee;
		}
		
		int nbu = -1;
		
		// on doit calculer la valeur
		if(u.getNbVarInstanciees() <= 4)
		{
			nbu = historique.getNbInstances(u);
			if(u.getNbVarInstanciees() == 1 || nbu > seuil)
			{
				double p = estimeProba(u, nbu);
	
				if(verbose)
					System.out.println("Utilisation de l'historique (> seuil) : "+Math.exp(p));
				cache.put(u.clone(), p);
	//			if(verbose)
	//				System.out.println("p("+u+") = "+p);
				return p;
			}
		}
		
		MoralGraph gm;
		if(!cachePartition.containsKey(U))
		{
			if(verbose)
				System.out.println("Calcul de la partition");
			Set<String> instanciees = new HashSet<String>();
			instanciees.addAll(u.getVarConditionees());
			gm = new MoralGraph(dag, instanciees, false);
			gm.computeDijkstra();
			if(gm.getDistanceMax() <= 1)
			{
				if(verbose)
					System.out.println("Famille !");
			}
			else
			{
				gm.prune();
				gm.computeSeparator();
			}
			cachePartition.put(U.clone(), gm);
		}
		else
		{
			gm = cachePartition.get(U);
			if(verbose)
				System.out.println("Utilisation de la partition en cache");
		}
		
		Partition partition = gm.getPartition();
		
		if(partition == null)
		{
			/**
			 * On est à une feuille, et il n'y a pas assez d'exemple.
			 * On décompose p(fils, parent) = p(fils | parents) * p(parents)
			 * Et la proba conditionnelle a un prior
			 */
			if(nbu == -1)
				nbu = historique.getNbInstances(u);
			double p = estimeProba(u, nbu);
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

		iterator.init(u.clone(), separateur);
		Instanciation u1, u2, uS;
		EnsembleVariables G0 = null, G1 = null, C = null;
		
		Instanciation preums = null, iter;
		
		Double max = null;
		int nbIter = 0;
		while(iterator.hasNext())
		{
			nbIter++;
			iter = iterator.next();
			u1 = iter.subInstanciation(g0c);
			u2 = iter.subInstanciation(g1c);
			uS = iter.subInstanciation(separateur);
			
			if(G0 == null)
			{
				G0 = u1.getEVConditionees();
				G1 = u2.getEVConditionees();
				C = uS.getEVConditionees();
				preums = u1;
			}
			
			double pC = infere(uS, C);
			double pG0 = infere(u1, G0);
			double pG1 = infere(u2, G1);
			
			double p = pG0 + pG1 - pC;
			
			if(verbose)
				System.out.println("Probas : "+pG0+" "+pG1+" "+pC);

/*			if(pC < pG0 || pC < pG1)
			{
				System.out.println("Erreur : "+pG0+" "+pG1+" "+pC);
				int z = 0;
				z = 1/z;
			}*/
						
/*			if(pG0 == Double.NEGATIVE_INFINITY || pG1 == Double.NEGATIVE_INFINITY)
			{
				System.out.println("Error");
				continue;
			}*/
			
			if(max == null)
				max = p;
			else if(max < p)
			{
				pileProba.push(max);
				max = p;
			}
			else
				pileProba.push(p);
		}
		
		Double p = max;
//		if(p == null)
//			return Double.NEGATIVE_INFINITY;
			
		double q = 0;
		for(int k = 0; k < nbIter-1; k++)		
		{
			double d = pileProba.pop();
			if(d > max)
			{
				System.out.println("Erreur d > max !");
				int z = 0;
				z = 1/z;
			}
			q += Math.exp(d - max);
		}
		if(nbIter > 1)
			p += Math.log1p(q);
		
		InstanceMemoryManager.getMemoryManager().clearFrom(preums);

/*		if(p > 0)
		{
			System.out.println("p = "+p);
			int z = 0;
			z = 1/z;
		}*/
		
		cache.put(u.clone(), p);
		return p;
	}
	
	/**
	 * Estime la probabilité log(p(u)) à partir de l'historique
	 * Applique un ajustement avec une distribution Beta
	 * @param u
	 * @param nbu
	 * @return
	 */
	private double estimeProba(Instanciation u, int nbu)
	{
		int domaine = 1;
		for(int i = 0; i < vars.length; i++)
			if(u.isConditionne(i))
				domaine *= vars[i].domain;
		
		return Math.log(nbu + equivalentSampleSize/domaine) - Math.log(norm + equivalentSampleSize);
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
