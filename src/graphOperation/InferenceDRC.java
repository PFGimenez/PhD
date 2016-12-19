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
	private List<Map<Instanciation, Double>> caches = new ArrayList<Map<Instanciation, Double>>(); // cache des proba
	private ArbreDecompTernaire decomp;
	private boolean verbose = false;
	private Variable[] vars;
	private double equivalentSampleSize = 10;
	private Stack<Double> pileProba = new Stack<Double>();

	public InferenceDRC(int seuil, ArbreDecompTernaire decomp, MultiHistoComp historique, int equivalentSampleSize, boolean verbose)
	{
		this.verbose = verbose;
		this.decomp = decomp;
		this.historique = historique;
		vars = historique.getVariablesLocal();
		norm = historique.getNbInstancesTotal();
		for(int i = 0; i < vars.length+1; i++)
			caches.add(new HashMap<Instanciation, Double>());
		this.seuil = seuil;
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
		return infere(u, U, decomp.getNode(U));
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
		
		if(u.getNbVarInstanciees() != U.vars.length)
		{
			System.out.println(u+" "+U);
			int z = 0;
			z = 1/z;
		}
		
		int nbVar = u.getNbVarInstanciees();
		
		if(nbVar == 0)
			return 0;
		
		Double valeurCachee = caches.get(nbVar).get(u);
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
				caches.get(nbVar).put(u.clone(), p);
	//			if(verbose)
	//				System.out.println("p("+u+") = "+p);
				return p;
			}
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
			double p = estimeProba(u, nbu);
			caches.get(nbVar).put(u.clone(), p);
//			if(verbose)
//				System.out.println("p("+u+") = "+p);
			return p;
		}
		
		if(verbose)
			System.out.println("Décomposition du calcul :\n"+partition);

		IteratorInstances iterator = new IteratorInstances(partition.separateur.size());

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
			
			double pC, pG0, pG1;
			
			if(uS.equals(u1))
			{
				pC = 0;
				pG0 = 0;
				pG1 = infere(u2, G1, t.fils1);
			}
			else if(uS.equals(u2))
			{
				pC = 0;
				pG0 = infere(u1, G0, t.fils0);
				pG1 = 0;
			}
			else
			{
				pC = infere(uS, C, t.filsC);
				pG0 = infere(u1, G0, t.fils0);
				pG1 = infere(u2, G1, t.fils1);
			}
			
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
			if(q < 0.001)
				p += Math.log1p(q);
			else
				p += Math.log(1 + q);
		
		InstanceMemoryManager.getMemoryManager().clearFrom(preums);

/*		if(p > 0)
		{
			System.out.println("p = "+p);
			int z = 0;
			z = 1/z;
		}*/
		
		caches.get(nbVar).put(u.clone(), p);
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
		for(Map<Instanciation, Double> cache : caches)
		{
			for(Instanciation u : cache.keySet())
				if(u.getNbVarInstanciees() > 5)
					remove.add(u);
			for(Instanciation u : remove)
				cache.remove(u);
		}
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
