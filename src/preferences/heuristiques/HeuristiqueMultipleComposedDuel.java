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
	public String toString()
	{
		return getClass().getSimpleName()+", taille groupe = "+taille;
	}
	
	@Override
	public List<String> getRacine(DatasetInfo dataset, HistoriqueCompile historique, List<String> variables, Instanciation instance)
	{	
		assert variables.size() > 0 && historique.getNbInstances(instance) > 0;
		
		if(variables.size() <= taille)
		{
			List<String> out = new ArrayList<String>();
			for(String s : variables)
				out.add(s);
			return out;
//			return simplify(historique.getNbInstancesToutesModalitees(variables, true, instance), variables);
		}
		
		List<List<String>> combinaisonsVar = new ArrayList<List<String>>();

		for(int k = 1; k <= taille; k++)
		{
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
		{
			for(int i = 0; i < meilleur.var.size(); i++)
				instance.conditionne(meilleur.var.get(i), meilleur.bestVal.get(i));
			
			double u_etoile = historique.getNbInstances(instance) / nbTot;
			
			for(int i = 0; i < meilleur.var.size(); i++)
				instance.deconditionne(meilleur.var.get(i));
			
			for(int i = 0; i < v.var.size(); i++)
				instance.conditionne(v.var.get(i), v.bestVal.get(i));
			
			double v_etoile = historique.getNbInstances(instance) / nbTot;
			
			for(int i = 0; i < meilleur.var.size(); i++)
				instance.conditionne(meilleur.var.get(i), meilleur.bestVal.get(i));
			
			double u_v_etoile = historique.getNbInstances(instance) / nbTot;
			
			for(int i = 0; i < meilleur.var.size(); i++)
				instance.deconditionne(meilleur.var.get(i));
			
			for(int i = 0; i < v.var.size(); i++)
				instance.deconditionne(v.var.get(i));
			
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
		
//		System.out.println("Variables avant : "+meilleur.var);
//		List<String> out = simplify(historique.getNbInstancesToutesModalitees(meilleur.var, true, instance), meilleur.var);
//		System.out.println("Variables après : "+out);
		
		List<String> out = meilleur.var;
	
		assert checkDoublon(out) && out.size() <= taille && !out.isEmpty(): out;

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

	/**
	 * Il est possible que l'ordre des valeurs soient simplifiables. Par exemple, si les valeurs préférées sont :
	 * xy, xy*, x*y*, x*y, alors ce nœud peut-être décomposé en une racine X (x > x*) et deux enfants.
	 * Auquel cas, on ne renvoie que X.
	 * D'une manière générale, on renvoie un sous-ensemble de l'ensemble des variables.
	 * @param nbExemples
	 * @param variables
	 * @return
	 */

}
