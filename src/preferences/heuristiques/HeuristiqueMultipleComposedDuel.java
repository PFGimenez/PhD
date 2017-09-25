package preferences.heuristiques;

import java.util.List;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashMap;

import compilateurHistorique.Instanciation;
import compilateurHistorique.DatasetInfo;
import compilateurHistorique.HistoriqueCompile;

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
 * Heuristique qui converge bien
 * @author Pierre-François Gimenez
 *
 */

public class HeuristiqueMultipleComposedDuel extends MultipleHeuristique
{	
	private int taille;
	
	public HeuristiqueMultipleComposedDuel(int taille)
	{
		this.taille = taille;
	}
	
	private class VarAndBestVal
	{
		public List<String> var;
		public List<String> bestVal;
		
		public VarAndBestVal(List<String> var, List<String> bestVal)
		{
			this.var = var;
			this.bestVal = bestVal;
		}
	}
	
	@Override
	public int hashCode()
	{
		return 0x4251 + taille;
	}
	
	@Override
	public String toString()
	{
		return getClass().getSimpleName()+", taille groupe = "+taille;
	}
	
	private int nbVarInstanciees;
	
	@Override
	public List<String> getRacine(DatasetInfo dataset, HistoriqueCompile historique, List<String> variables, Instanciation instance)
	{	
		assert variables.size() > 0 && historique.getNbInstances(instance) > 0;
		nbVarInstanciees = instance.getNbVarInstanciees();
		Instanciation instanceBis = instance.clone();
		
		if(variables.size() <= taille)
		{
			List<String> out = new ArrayList<String>();
			out.addAll(variables);
			return out;
//			return simplify(historique.getNbInstancesToutesModalitees(variables, true, instance), variables);
		}
		
		List<List<String>> combinaisonsVar = new ArrayList<List<String>>();
		VarAndBestVal overallBest = null;
		
		for(int k = 1; k <= taille; k++)
		{
			combinaisonsVar.clear();
			List<BitSet> combinaisons = generateTuples(variables.size(), k);
			
			int[] index = new int[k];
			for(BitSet bs : combinaisons)
			{
				List<String> vars = new ArrayList<String>();
				for(int i = 0; i < k; i++)
				{
					index[i] = bs.nextSetBit(i == 0 ? 0 : index[i - 1] + 1);
					vars.add(variables.get(index[i]));
				}
				
				assert checkDoublon(vars) && vars.size() == k: vars;
	
				combinaisonsVar.add(vars);
			}
		
			HashMap<Integer, List<List<String>>> varParMod = new HashMap<Integer, List<List<String>>>();
			
			/**
			 * On trie les variables par nombre de modalité
			 */
			for(List<String> vars : combinaisonsVar)
			{
				int domaine = 1;
				for(String v : vars)
					domaine *= dataset.vars[dataset.mapVar.get(v)].domain;
				
				List<List<String>> l = varParMod.get(domaine);
				if(l == null)
				{
					l = new ArrayList<List<String>>();
					varParMod.put(domaine, l);
				}
				l.add(vars);
			}
			
			/**
			 * Pour chaque modalité, on prend celui qui a la plus grande probabilité max
			 */
			List<VarAndBestVal> vainqueursParMod = new ArrayList<VarAndBestVal>();
					
			for(List<List<String>> l : varParMod.values())
			{
				VarAndBestVal best = null;
				double bestValue = 0;
				List<String> bestVal = null;
				for(List<String> v : l)
				{
					int plusGrandeMod = Integer.MIN_VALUE;
					double totalMod = historique.getNbInstances(instance);
					HashMap<List<String>, Integer> h = historique.getNbInstancesToutesModalitees(v, false, instance);
	
					for(List<String> val : h.keySet())
					{
						int i = h.get(val);
						if(i > plusGrandeMod)
						{
							bestVal = val;
							plusGrandeMod = i;
						}
					}
	
					double tmp = plusGrandeMod / totalMod;
					if(best == null || tmp > bestValue)
					{
						bestValue = tmp;
						best = new VarAndBestVal(v, bestVal);
					}
				}	
				
				vainqueursParMod.add(best);
			}
			
			/**
			 * On fait affronter les meilleurs de chaque modalité
			 */
			
			VarAndBestVal meilleur = vainqueursParMod.get(0);
			vainqueursParMod.remove(0);
			
			double nbTot = historique.getNbInstances(instance);
	
			for(VarAndBestVal v : vainqueursParMod)
				meilleur = duel(meilleur, v, instance, instanceBis, historique, dataset, nbTot);
			
			/**
			 * On fait affronter "meilleur" composé de k variables et "overallBest" composé de <k variables
			 */
			
			assert meilleur.var.size() == k;
			assert overallBest == null || overallBest.var.size() < k;
			
			if(overallBest == null)
				overallBest = meilleur;
			else
			{
				boolean inclusion = true;
				for(String s : overallBest.var)
					if(!meilleur.var.contains(s))
					{
						inclusion = false;
						break;
					}
				if(inclusion)
				{
					VarAndBestVal grandEnsemble = meilleur, petitEnsemble = overallBest;
					if(decompose(historique.getNbInstancesToutesModalitees(grandEnsemble.var, true, instance), petitEnsemble.var, grandEnsemble.var))
						overallBest = petitEnsemble;
					else
						overallBest = grandEnsemble;
				}
				else
					overallBest = duel(meilleur, overallBest, instance, instanceBis, historique, dataset, nbTot);
			}
		}

		assert overallBest != null;
		
//		System.out.println("Variables avant : "+meilleur.var);
//		List<String> out = simplify(historique.getNbInstancesToutesModalitees(overallBest.var, true, instance), overallBest.var);
//		System.out.println("Variables après : "+out);
		
		List<String> out = overallBest.var;
	
		assert checkDoublon(out) && out.size() <= taille && !out.isEmpty(): out;

//		System.out.println(out+": "+historique.getNbInstancesToutesModalitees(out, true, instance));
		
		return out;
	}

	/**
	 * On vérifie qu'il n'y a aucun doublon
	 * @param vars
	 * @return
	 */
	private boolean checkDoublon(List<String> vars)
	{
		for(int i = 1; i < vars.size(); i++)
			for(int j = 0; j < i; j++)
				if(vars.get(i).equals(vars.get(j)))
					return false;
		return true;
	}


	private VarAndBestVal duel(VarAndBestVal u, VarAndBestVal v, Instanciation instance, Instanciation instanceBis, HistoriqueCompile historique, DatasetInfo dataset, double nbTot)
	{
		assert instance.getNbVarInstanciees() == nbVarInstanciees;
		assert instanceBis.getNbVarInstanciees() == nbVarInstanciees;
		for(int i = 0; i < u.var.size(); i++)
			instance.conditionne(u.var.get(i), u.bestVal.get(i));
		
		double u_etoile = historique.getNbInstances(instance) / nbTot;
		
		for(int i = 0; i < v.var.size(); i++)
			instanceBis.conditionne(v.var.get(i), v.bestVal.get(i));
		
		double v_etoile = historique.getNbInstances(instanceBis) / nbTot;
		
		double u_v_etoile;
		
		boolean compatible = instance.isCompatible(instanceBis);
		
		if(!compatible)
			u_v_etoile = 0;
		else
		{
			for(int i = 0; i < v.var.size(); i++)
				instance.conditionne(v.var.get(i), v.bestVal.get(i));

			u_v_etoile = historique.getNbInstances(instance) / nbTot;
		}
		
		for(int i = 0; i < u.var.size(); i++)
			instance.deconditionne(u.var.get(i));
		
		for(int i = 0; i < v.var.size(); i++)
		{
			instance.deconditionne(v.var.get(i));
			instanceBis.deconditionne(v.var.get(i));
		}
		
		int domaineIntersection = 1;
		for(String s : u.var)
			if(v.var.contains(s))
				domaineIntersection *= dataset.vars[dataset.mapVar.get(s)].domain;
		
		int domaineMeilleur = 1;
		for(String s : u.var)
			domaineMeilleur *= dataset.vars[dataset.mapVar.get(s)].domain;

		int domaineV = 1;
		for(String s : v.var)
			domaineV *= dataset.vars[dataset.mapVar.get(s)].domain;

		int oneIfCompatible = compatible ? 1 : 0;
		
		/**
		 * Le meilleur est-il détrôné ?
		 */
		if((u_etoile - u_v_etoile) * (domaineMeilleur / domaineIntersection - oneIfCompatible) < (v_etoile - u_v_etoile) * (domaineV / domaineIntersection - oneIfCompatible))
			return v;
		return u;
	}
	
}
