package preferences.heuristiques;

import java.util.List;
import java.util.ArrayList;
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

public class HeuristiqueDuel implements HeuristiqueComplexe
{
	
	private class VarAndBestVal
	{
		public String var;
		public String bestVal;
		
		public VarAndBestVal(String var, String bestVal)
		{
			this.var = var;
			this.bestVal = bestVal;
		}
	}
	
	@Override
	public String getRacine(DatasetInfo dataset, HistoriqueCompile historique, List<String> variables, Instanciation instance)
	{	
		if(variables.size() == 0 || historique.getNbInstances(instance) == 0)
			return null;
		
		HashMap<Integer, ArrayList<String>> varParMod = new HashMap<Integer, ArrayList<String>>();
		/**
		 * On trie les variables par nombre de modalité
		 */
		for(String v : variables)
		{
			ArrayList<String> l = varParMod.get(dataset.vars[dataset.mapVar.get(v)].domain);
			if(l == null)
			{
				l = new ArrayList<String>();
				varParMod.put(dataset.vars[dataset.mapVar.get(v)].domain, l);
			}
			l.add(v);
		}
		
		/**
		 * Pour chaque modalité, on prend celui qui a la plus grande probabilité max
		 */
		ArrayList<VarAndBestVal> vainqueursParMod = new ArrayList<VarAndBestVal>();
				
		for(ArrayList<String> l : varParMod.values())
		{
			VarAndBestVal best = null;
			double bestValue = Double.MIN_VALUE;
			String bestVal = null;
			for(String v : l)
			{
				int plusGrandeMod = Integer.MIN_VALUE;
				double totalMod = historique.getNbInstances(instance);
				HashMap<String, Integer> h = historique.getNbInstancesToutesModalitees(v, instance);

				for(String val : h.keySet())
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
			instance.conditionne(meilleur.var, meilleur.bestVal);
			double u_etoile = historique.getNbInstances(instance) / nbTot;
			instance.deconditionne(meilleur.var);
			
			instance.conditionne(v.var, v.bestVal);
			double v_etoile = historique.getNbInstances(instance) / nbTot;
			
			instance.conditionne(meilleur.var, meilleur.bestVal);
			double u_v_etoile = historique.getNbInstances(instance) / nbTot;
			
			instance.deconditionne(meilleur.var);
			instance.deconditionne(v.var);
			
			/**
			 * Le meilleur est-il détrôné ?
			 */
			if((u_etoile - u_v_etoile) * (dataset.vars[dataset.mapVar.get(meilleur.var)].domain - 1) < (v_etoile - u_v_etoile) * (dataset.vars[dataset.mapVar.get(v.var)].domain - 1))
				meilleur = v;
		}
		
		return meilleur.var;
	}

}
