package preferences.heuristiques;

import java.util.List;
import java.util.Set;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;

import compilateurHistorique.Instanciation;
import compilateurHistorique.DatasetInfo;
import compilateurHistorique.HistoriqueCompile;

/*   (C) Copyright 2018, Gimenez Pierre-François 
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
 * Solution optimale
 * @author Pierre-François Gimenez
 *
 */

public class HeuristiqueOptimale extends MultipleHeuristique
{	
	private int taille;
	
	public HeuristiqueOptimale(int taille)
	{
		this.taille = taille;
	}
	
	private HashMap<Instanciation, HashMap<Set<String>, Double>> scores = new HashMap<Instanciation, HashMap<Set<String>, Double>>();

	private double getScore(Set<String> var, HistoriqueCompile historique, Instanciation instance)
	{
		Double s = scores.get(instance).get(var);
		if(s != null)
		{
//			System.out.println("Réutilisation de "+var+" : "+s);
			return s;
		}
		
		double score = 0;
		if(var.size() > 0)
		{
			List<String> varL = new ArrayList<String>();
			varL.addAll(var);
			// pas besoin de mettre les 0 car ils ne participent pas au score
			HashMap<List<String>, Integer> nbExemples = historique.getNbInstancesToutesModalitees(varL, false, instance);
			List<Integer> nbExemplesList = new ArrayList<Integer>(nbExemples.values());
			Collections.sort(nbExemplesList, Collections.reverseOrder());
			for(int i = 1; i < nbExemplesList.size(); i++)
			{
				score += i*nbExemplesList.get(i);
//				System.out.println(i+" "+nbExemplesList.get(i));
			}
		}
		scores.get(instance).put(var, score);
//		System.out.println("Calcul de "+var+" : "+score);

		return score;
	}
	
	@Override
	public int hashCode()
	{
		return 0xBAAAE + taille;
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
			out.addAll(variables);
			return out;
		}
		
		if(scores.get(instance) == null)
			scores.put(instance, new HashMap<Set<String>, Double>());

		Set<String> overallBest = null;
		
		for(int k = 1; k <= taille; k++)
		{
			List<BitSet> combinaisons = generateTuples(variables.size(), k);
			
			int[] index = new int[k];
			for(BitSet bs : combinaisons)
			{
				Set<String> vars = new HashSet<String>();
				for(int i = 0; i < k; i++)
				{
					index[i] = bs.nextSetBit(i == 0 ? 0 : index[i - 1] + 1);
					vars.add(variables.get(index[i]));
				}
				
				assert checkDoublon(vars) && vars.size() == k: vars;
	
//				System.out.println("\tVérification de "+vars+" contre "+overallBest);
				if(overallBest == null || isBest(vars, overallBest, historique, dataset, instance))
					overallBest = vars;
			}
		}

		assert overallBest != null;
	
		assert checkDoublon(overallBest) && overallBest.size() <= taille && !overallBest.isEmpty(): overallBest;

//		System.out.println(out+": "+historique.getNbInstancesToutesModalitees(out, true, instance));
		
		List<String> outL = new ArrayList<String>();
		outL.addAll(overallBest);
		
//		System.out.println("Best : "+overallBest);
		return outL;
	}

	/**
	 * On vérifie qu'il n'y a aucun doublon
	 * @param vars
	 * @return
	 */
	private boolean checkDoublon(Set<String> vars)
	{
		List<String> tmp = new ArrayList<String>();
		tmp.addAll(vars);
		for(int i = 1; i < tmp.size(); i++)
			for(int j = 0; j < i; j++)
				if(tmp.get(i).equals(tmp.get(j)))
					return false;
		return true;
	}

	/**
	 * Returns true iff x > y
	 * @param x
	 * @param y
	 * @return
	 */
	private boolean isBest(Set<String> x, Set<String> y, HistoriqueCompile historique, DatasetInfo dataset, Instanciation instance)
	{
		assert !x.equals(y) : x+", "+y;
		// zy = z - y
		Set<String> zyVar = new HashSet<String>();
		zyVar.addAll(x);
		zyVar.removeAll(y);
		int zyDom = 1;
		for(String s : zyVar)
			zyDom *= dataset.vars[dataset.mapVar.get(s)].domain;
		
		// zx = z - x
		Set<String> zxVar = new HashSet<String>();
		zxVar.addAll(y);
		zxVar.removeAll(x);
		int zxDom = 1;
		for(String s : zxVar)
			zxDom *= dataset.vars[dataset.mapVar.get(s)].domain;
		
//		System.out.println(x+" : "+(zxDom * getScore(x, historique, instance) + getScore(zxVar, historique, instance))
//				+", "+y+(zyDom * getScore(y, historique, instance) + getScore(zyVar, historique, instance)));
		
		double s = zyDom * getScore(y, historique, instance) + getScore(zyVar, historique, instance)
		- zxDom * getScore(x, historique, instance) + getScore(zxVar, historique, instance);
		
		return s > 0 || (s == 0 && x.size() < y.size()); // égalité : on retourne le plus petit ensemble
	}
	
}
