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
import java.util.Map;
import java.util.Stack;

import compilateurHistorique.DatasetInfo;
import compilateurHistorique.EnsembleVariables;
import compilateurHistorique.InstanceMemoryManager;
import compilateurHistorique.Instanciation;
import compilateurHistorique.IteratorInstances;
import compilateurHistorique.HistoriqueCompile;
import compilateurHistorique.Variable;

/**
 * L'inférence par l'algorithme DRC
 * @author Pierre-François Gimenez
 *
 */

public class InferenceDRC
{
	private HistoriqueCompile historique;
	private double norm; // le nombre d'instances totales, pour normaliser la proba
	private int seuil; // le nombre d'exemples à partir duquel on estime avec l'historique
	private Map<Instanciation, Double>[] caches; // cache des proba
	private ArbreDecompTernaire decomp;
	private boolean verbose = false;
	private Variable[] vars;
	private double equivalentSampleSize;
//	private Map<Instanciation, Double>[] cachesHisto; // cache des nbInstances
	private Stack<Double> pileProba = new Stack<Double>();
	private InstanceMemoryManager instancemm;
	private DatasetInfo dataset;
	private boolean useCardinal;

	@SuppressWarnings("unchecked")
	public InferenceDRC(int seuil, ArbreDecompTernaire decomp, DatasetInfo dataset, HistoriqueCompile historique, int equivalentSampleSize, boolean verbose, boolean useCardinal)
	{
		this.dataset = dataset;
		instancemm = InstanceMemoryManager.getMemoryManager(dataset);
		this.equivalentSampleSize = equivalentSampleSize;
		this.verbose = verbose;
		this.decomp = decomp;
		this.historique = historique;
//		this.useCacheHisto = useCacheHisto;
		vars = dataset.vars;
		norm = historique.getNbInstancesTotal();
		this.useCardinal = useCardinal;
		if(verbose)
			System.out.println("Taille historique : "+norm);
		caches = (Map<Instanciation, Double>[]) new Map[vars.length+1]; 
		this.seuil = seuil;

		for(int i = 0; i < vars.length+1; i++)
			caches[i] = new HashMap<Instanciation, Double>();
//		if(useCacheHisto)
//			learnCacheHisto();
	}
	
/*	@SuppressWarnings("unchecked")
	private void learnCacheHisto()
	{
		cachesHisto = (Map<Instanciation, Double>[]) new Map[vars.length+1]; 
		for(int i = 0; i < vars.length+1; i++)
			cachesHisto[i] = new HashMap<Instanciation, Double>();
		System.out.println("Apprentissage de l'historique…");
		for(int i = 0; i < vars.length; i++)
			recursifLearnCacheHisto(new Instanciation(), i, new HashSet<Instanciation>());
		System.out.println("Apprentissage de l'historique terminé. Taille : ");
		for(int i = 0; i < vars.length; i++)
			System.out.println(i+" vars : "+cachesHisto[i].size());
	}

	private void recursifLearnCacheHisto(Instanciation u, int newVar, Set<Instanciation> done)
	{
		Variable v = vars[newVar];
		u.conditionne(newVar, 0);
		EnsembleVariables U = u.getEVConditionees();
		int nbVar = U.vars.length;
		
		for(int j = 0; j < v.domain; j++)
		{
			u.conditionne(newVar, j);
			if(done.contains(u))
				return;
			done.add(u.clone());
			int nbu = historique.getNbInstances(u);
//			System.out.println(u+" : "+nbu+" "+seuil);
			if(nbu > 50)
			{
//				System.out.println("On continue");
				cachesHisto[nbVar].put(u.clone(), estimeProba(u, U, nbu));
				for(int i = 0; i < vars.length; i++)
				{
					if(!u.isConditionne(i))
						recursifLearnCacheHisto(u.clone(), i, done);
				}
			}
		}
//		for(int i = 0; i < vars.length; i++)
//			System.out.println(i+" vars : "+cachesHisto[i].size());

	}
*/
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
		return infere(u, U, decomp.racine);
	}
	
	/**
	 * Renvoie log(p(u))
	 * @param u
	 * @param U
	 * @return
	 */
	public double infere(Instanciation u, EnsembleVariables U, NodeArbreDecompTernaire t)
	{
		if(verbose)
			System.out.println("Calcul de "+u);
		
		assert u.getNbVarInstanciees() == U.vars.length;
		
		int nbVar = u.getNbVarInstanciees();
		
		if(nbVar == 0)
			return 0;
		
		Double valeurCachee = caches[nbVar].get(u);
		if(valeurCachee != null)
		{
			if(verbose)
				System.out.println("Utilisation de la proba en cache : "+Math.exp(valeurCachee));
			return valeurCachee;
		}
		
		int nbu = -1;
		
/*		if(useCacheHisto)
		{
			Double p = cachesHisto[nbVar].get(u);
			if(p != null)
				return p;
		}*/
		
		int domaine = 1;
		
		for(Integer i : U.vars)
			domaine *= vars[i].domain;
		
		
		if(!useCardinal && u.getNbVarInstanciees() <= 4)
 		{
 			nbu = historique.getNbInstances(u);
			if(/*u.getNbVarInstanciees() == 1 || */nbu > seuil)
			{
				double p = estimeProba(u, U, nbu);
	
				if(verbose)
					System.out.println("Utilisation de l'historique (> seuil) : "+Math.exp(p)+" ("+nbu+" > "+seuil+")");
				caches[nbVar].put(u.clone(), p);
	//			if(verbose)
	//				System.out.println("p("+u+") = "+p);
				return p;
			}
 		}
		
		if(useCardinal && domaine <= norm / 100)
		{
			nbu = historique.getNbInstances(u);
			double p = estimeProba(u, U, nbu);

			if(verbose)
				System.out.println("Utilisation de l'historique (> seuil) : "+Math.exp(p)+" ("+nbu+")");
			caches[nbVar].put(u.clone(), p);
//			if(verbose)
//				System.out.println("p("+u+") = "+p);
			return p;
		}
		
		Partition partition = null;
		
		if(t != null)
			partition = t.partition;
		
		if(t == null || partition == null)
		{
			/**
			 * On est à une feuille, et il n'y a pas assez d'exemple.
			 */
			if(nbu == -1)
				nbu = historique.getNbInstances(u);
			double p = estimeProba(u, U, nbu);
			caches[nbVar].put(u.clone(), p);
//			if(verbose)
//				System.out.println("p("+u+") = "+p);
			return p;
		}
		/*
		System.out.print("U : ");
		for(int i = 0; i < U.vars.length; i++)
			System.out.print(vars[U.vars[i]].name+", ");
		System.out.println();
		
		System.out.print("G0 : ");
		for(String s : partition.ensembles[0])
			System.out.print(s+", ");
		System.out.println();
		
		System.out.print("G1 : ");
		for(String s : partition.ensembles[1])
			System.out.print(s+", ");
		System.out.println();
		
		System.out.print("S : ");
		for(String s : partition.separateur)
			System.out.print(s+", ");
		System.out.println();*/

		
		boolean dansG0 = true, dansG1 = true, trouve;
		for(int i = 0; i < U.vars.length; i++)
		{
			int nb = U.vars[i];
			if(dansG0)
			{
				trouve = false;
				for(int j = 0; j < partition.g0cTab.length; j++)
					if(partition.g0cTab[j] == nb)
					{
						trouve = true;
						break;
					}
				
				dansG0 = trouve;
			}
			
			if(dansG1)
			{
				trouve = false;
				for(int j = 0; j < partition.g1cTab.length; j++)
					if(partition.g1cTab[j] == nb)
					{
						trouve = true;
						break;
					}
				dansG1 = trouve;
			}
			
			if(!dansG0 && !dansG1)
				break;
		}
		
//		System.out.println("Dans G0 : "+dansG0);
//		System.out.println("Dans G1 : "+dansG1);
		
		if(verbose && (dansG0 || dansG1))
			System.out.println("On passe directement au niveau inférieur");
		
		if(dansG0 && dansG1)
			return infere(u, U, t.filsC);
		else if(dansG0)
			return infere(u, U, t.fils0);
		else if(dansG1)
			return infere(u, U, t.fils1);
		
		if(verbose)
			System.out.println("Décomposition du calcul :\n"+partition);

		IteratorInstances iterator = new IteratorInstances(partition.separateur.size(), dataset);

		iterator.init(u.clone(), partition.separateurTab);
		Instanciation u1, u2, uS;
		EnsembleVariables G0 = null, G1 = null, C = null;
		
		Instanciation preums = null, iter;
		
		Double max = null;
		int nbIter = 0;
		while(iterator.hasNext())
		{
			nbIter++;
			iter = iterator.next();
			u1 = iter.subInstanciation(partition.g0cTab);
			u2 = iter.subInstanciation(partition.g1cTab);
			uS = iter.subInstanciation(partition.separateurTab);
			
			if(G0 == null)
			{
				G0 = u1.getEVConditionees();
				G1 = u2.getEVConditionees();
				C = uS.getEVConditionees();
				preums = u1;
			}
			
			double pC = infere(uS, C, t.filsC);
			double pG0 = infere(u1, G0, t.fils0);
			double pG1 = infere(u2, G1, t.fils1);
			
			double p = pG0 + pG1 - pC;
			
			if(verbose)
				System.out.println("Probas : "+pG0+" "+pG1+" "+pC);

			assert pC >= (pG0*1.1) && pC >= (pG1*1.1) : pC + " " + pG0 + " " + pG1 + "\n" + uS + "\n" + u1 + "\n" + u2 + "\n"; // prend en compte l'aléa d'estimation
			assert pG0 != Double.NEGATIVE_INFINITY && pG1 != Double.NEGATIVE_INFINITY;
			
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
		assert p != null;
			
		double q = 0;
		for(int k = 0; k < nbIter-1; k++)		
		{
			double d = pileProba.pop();
			assert d <= max;
			q += Math.exp(d - max);
		}
		
		if(nbIter > 1)
			if(q < 0.001)
				p += Math.log1p(q);
			else
				p += Math.log(1 + q);
		
		instancemm.clearFrom(preums);

		if(verbose)
			System.out.println(u+". Histo : "+Math.exp(estimeProba(u, U, nbu))+" ("+nbu+"). Obtenu : "+Math.exp(p));
		
		caches[nbVar].put(u.clone(), p);
		return p;
	}
	
	/**
	 * Estime la probabilité log(p(u)) à partir de l'historique
	 * Applique un ajustement avec une distribution Beta
	 * @param u
	 * @param nbu
	 * @return
	 */
	private double estimeProba(Instanciation u, EnsembleVariables U, int nbu)
	{
		int domaine = 1;		
		for(int i = 0; i < U.vars.length; i++)
			domaine *= vars[U.vars[i]].domain;
		double out = Math.log(nbu + equivalentSampleSize/domaine) - Math.log(norm + equivalentSampleSize);
		assert out <= 0;
//		System.out.println(u+", nb = "+nbu+", domaine = "+domaine+" : "+out);

		return out;
	}

	/**
	 * Ne supprime que certaines valeurs du cache peu susceptible d'être réutilisées
	 */
	public void partialClearCache()
	{
//		Set<Instanciation> remove = new HashSet<Instanciation>();
		for(int i = 5; i < caches.length; i++)
			caches[i].clear();
/*		for(Map<Instanciation, Double> cache : caches)
		{
			for(Instanciation u : cache.keySet())
				if(u.getNbVarInstanciees() > 5)
					remove.add(u);
			for(Instanciation u : remove)
				cache.remove(u);
		}*/
	}
	
	/**
	 * Réinitialise le cache complètement
	 */
	public void clearCache()
	{
		for(Map<Instanciation, Double> cache : caches)
			cache.clear();
	}
	
}
