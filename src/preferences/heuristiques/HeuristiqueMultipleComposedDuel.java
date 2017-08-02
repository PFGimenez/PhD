package preferences.heuristiques;

import java.util.List;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashMap;
import java.util.Iterator;

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

public class HeuristiqueMultipleComposedDuel implements MultipleHeuristique
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
	public String toString()
	{
		return getClass().getSimpleName()+", taille groupe = "+taille;
	}
	
	@Override
	public List<String> getRacine(DatasetInfo dataset, HistoriqueCompile historique, List<String> variables, Instanciation instance)
	{	
		if(variables.size() == 0 || historique.getNbInstances(instance) == 0)
			return null;
		
		if(variables.size() <= taille)
			return variables;
		
		List<BitSet> combinaisons = generateTuples(variables.size());
		List<List<String>> combinaisonsVar = new ArrayList<List<String>>();
		
		int[] index = new int[taille];
		for(BitSet bs : combinaisons)
		{
			List<String> vars = new ArrayList<String>();
			for(int i = 0; i < taille; i++)
			{
				index[i] = bs.nextSetBit(i == 0 ? 0 : index[i-1]);
				vars.add(variables.get(index[i]));
			}

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
			double bestValue = Double.MIN_VALUE;
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
				if(tmp > bestValue)
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
		{
			for(int i = 0; i < taille; i++)
				instance.conditionne(meilleur.var.get(i), meilleur.bestVal.get(i));
			
			double u_etoile = historique.getNbInstances(instance) / nbTot;
			
			for(int i = 0; i < taille; i++)
				instance.deconditionne(meilleur.var.get(i));
			
			
			for(int i = 0; i < taille; i++)
				instance.conditionne(v.var.get(i), v.bestVal.get(i));
			
			double v_etoile = historique.getNbInstances(instance) / nbTot;
			
			for(int i = 0; i < taille; i++)
				instance.conditionne(meilleur.var.get(i), meilleur.bestVal.get(i));
			double u_v_etoile = historique.getNbInstances(instance) / nbTot;
			
			for(int i = 0; i < taille; i++)
			{
				instance.deconditionne(meilleur.var.get(i));
				instance.deconditionne(v.var.get(i));
			}
			
			int domaineMeilleur = 1;
			for(String s : meilleur.var)
				domaineMeilleur *= dataset.vars[dataset.mapVar.get(s)].domain;

			int domaineV = 1;
			for(String s : v.var)
				domaineV *= dataset.vars[dataset.mapVar.get(s)].domain;

			
			/**
			 * Le meilleur est-il détrôné ?
			 */
			if((u_etoile - u_v_etoile) * (domaineMeilleur - 1) < (v_etoile - u_v_etoile) * (domaineV - 1))
				meilleur = v;
		}
		return meilleur.var;
	}

	private List<BitSet> generateTuples(int nbVar)
	{
		List<BitSet> sub = new ArrayList<BitSet>();
		sub.add(new BitSet(nbVar));
		for(int v = 0; v < nbVar; v++)
		{
			int size = sub.size();
			for(int i = 0; i < size; i++)
			{
				BitSet bs = sub.get(i);
				if(bs.cardinality() < taille)
				{
					BitSet newbs = (BitSet) bs.clone();
					newbs.set(v);
					sub.add(newbs);
				}
				
			}
		}
		
		/*
		 * Il peut y avoir des tuples avec moins que la taille requise
		 */
		Iterator<BitSet> iter = sub.iterator();
		while(iter.hasNext())
			if(iter.next().cardinality() < taille)
				iter.remove();
		return sub;
	}

}
