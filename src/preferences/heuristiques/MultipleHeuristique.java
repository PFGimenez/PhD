package preferences.heuristiques;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

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
 * Interface des heuristiques qui utilisent directement l'historique
 * @author Pierre-François Gimenez
 *
 */

public abstract class MultipleHeuristique
{
	/**
	 * 
	 * @param h
	 * @param variables encore libre
	 * @param instance actuelle
	 * @return
	 */
	public abstract List<String> getRacine(DatasetInfo dataset, HistoriqueCompile historique, List<String> variables, Instanciation instance);
	
	protected boolean decompose(Map<List<String>, Integer> nbExemples, List<String> variablesPetitEnsemble, List<String> variablesGrandEnsemble)
	{
		List<List<String>> ordrePref = new ArrayList<List<String>>();
		LinkedList<Entry<List<String>, Integer>> list = new LinkedList<Map.Entry<List<String>,Integer>>(nbExemples.entrySet());
	     Collections.sort(list, new Comparator<Entry<List<String>, Integer>>() {
	          public int compare(Entry<List<String>, Integer> o1, Entry<List<String>, Integer> o2) {
	               return -o1.getValue()
	              .compareTo(o2.getValue());
	          }
	     });

	    for (Iterator<Entry<List<String>, Integer>> it = list.iterator(); it.hasNext();) {
	        Map.Entry<List<String>, Integer> entry = it.next();
	        ordrePref.add((List<String>) entry.getKey());
	    }
		
	    List<Integer> varsIndex = new ArrayList<Integer>();
	    
	    for(String s : variablesPetitEnsemble)
	    {
	    	int index = variablesGrandEnsemble.indexOf(s);
	    	assert index != -1 : "On n'a pas inclusion ! "+variablesPetitEnsemble+" pas inclus pas "+variablesGrandEnsemble;
	    	varsIndex.add(index);
	    }
	    
		List<List<String>> vus = new ArrayList<List<String>>();
		List<String> last = null;
		for(List<String> val : ordrePref)
		{
			List<String> projection = new ArrayList<String>();
			for(Integer i : varsIndex)
				projection.add(val.get(i));
			
//			System.out.println("Comparaison de "+projection+" et de "+last+" : "+projection.equals(last));
			if(last != null && !projection.equals(last) && vus.contains(projection))
			{
				return false;
			}
			vus.add(projection);
			last = projection;
		}
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
	protected List<String> simplify(Map<List<String>, Integer> nbExemples, List<String> variables)
	{
		List<List<String>> ordrePref = new ArrayList<List<String>>();
		LinkedList<Entry<List<String>, Integer>> list = new LinkedList<Map.Entry<List<String>,Integer>>(nbExemples.entrySet());
	     Collections.sort(list, new Comparator<Entry<List<String>, Integer>>() {
	          public int compare(Entry<List<String>, Integer> o1, Entry<List<String>, Integer> o2) {
	               return -o1.getValue()
	              .compareTo(o2.getValue());
	          }
	     });

	    for (Iterator<Entry<List<String>, Integer>> it = list.iterator(); it.hasNext();) {
	        Map.Entry<List<String>, Integer> entry = it.next();
	        ordrePref.add((List<String>) entry.getKey());
	    }
	    
//	    for(List<String> l : ordrePref)
//	    	System.out.println(l);
	    
	    for(int k = 1; k < variables.size(); k++)
	    {
//	    	System.out.println("k = "+k);
			List<BitSet> combinaisons = generateTuples(variables.size(), k);
			List<List<Integer>> combinaisonsVar = new ArrayList<List<Integer>>();
			
			int[] index = new int[k];
			for(BitSet bs : combinaisons)
			{
				List<Integer> vars = new ArrayList<Integer>();
				for(int i = 0; i < k; i++)
				{
					index[i] = bs.nextSetBit(i == 0 ? 0 : index[i - 1] + 1);
					vars.add(index[i]);
				}
				assert vars.size() == k : vars;
				combinaisonsVar.add(vars);
			}
			
			for(List<Integer> vars : combinaisonsVar)
			{
//				System.out.println("Vérification des variables d'indices : "+vars);
				
				List<List<String>> vus = new ArrayList<List<String>>();
				List<String> last = null;
				boolean ok = true;
				for(List<String> val : ordrePref)
				{
					List<String> projection = new ArrayList<String>();
					for(Integer i : vars)
						projection.add(val.get(i));
					
//					System.out.println("Comparaison de "+projection+" et de "+last+" : "+projection.equals(last));
					if(last != null && !projection.equals(last) && vus.contains(projection))
					{
						ok = false;
						break;
					}
					vus.add(projection);
					last = projection;
				}
				if(ok)
				{
					List<String> out = new ArrayList<String>();
					for(Integer i : vars)
						out.add(variables.get(i));
					return out;
				}
			}

	    }
//		System.out.println("Pas de simplification possible");
	    return variables;
	}

	protected List<BitSet> generateTuples(int nbVar, int taille)
	{
		List<BitSet> sub = new ArrayList<BitSet>();
		if(taille > nbVar)
			return(sub);
		
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
