/*   (C) Copyright 2015, Gimenez Pierre-François 
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

package preferences.completeTree;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;

import compilateurHistorique.DatasetInfo;
import compilateurHistorique.HistoriqueCompile;
import compilateurHistorique.Instanciation;
import compilateurHistorique.Variable;

/**
 * Apprentissage optimal d'ordre lexicographique
 * @author Pierre-François Gimenez
 *
 */

public class ApprentissageExactLexOrder implements ApprentissageLex
{
	
	
	public LexicographicOrder apprendDonnees(DatasetInfo dataset, List<Instanciation> instances)
	{
		HistoriqueCompile historique = new HistoriqueCompile(dataset);
		historique.compile(instances);
		
		LexicographicOrder out = null;
		HashMap<Variable, Double> scores = new HashMap<Variable, Double>();
		HashMap<Variable, LexicographicOrder> arbres = new HashMap<Variable, LexicographicOrder>();
		Instanciation empty = new Instanciation(dataset);
		Variable[] vars = dataset.vars;

		for(Variable v : vars)
		{
			LexicographicOrder tmp = new LexicographicOrder(v);
			HashMap<String, Integer> inst = historique.getNbInstancesToutesModalitees(v.name, true, empty);
			tmp.setOrdrePref(inst);
			arbres.put(v, tmp);
			double score = 0;
			for(int i = 1; i < v.domain; i++)
				score += i * inst.get(tmp.ordrePref.get(i));
			score *= 1. / (v.domain - 1.);
			scores.put(v, score);
//			System.out.println(v+" "+inst+" "+score);
		}
		
		LinkedList<Entry<Variable, Double>> list = new LinkedList<Entry<Variable, Double>>(scores.entrySet());
	    
		Collections.sort(list, new Comparator<Entry<Variable, Double>>() {
	          public int compare(Entry<Variable, Double> o1, Entry<Variable, Double> o2) {
	               return o1.getValue()
	              .compareTo(o2.getValue());
	          }
	    });

	    List<Variable> ordered = new ArrayList<Variable>();
	     
	    Iterator<Entry<Variable, Double>> it = list.iterator();
	    while(it.hasNext())
	        ordered.add(it.next().getKey());
		
	    LexicographicOrder pred = null;
		for(Variable v : ordered)
		{
			LexicographicOrder a = arbres.get(v);			
			if(pred == null)
				out = arbres.get(v);
			else
				pred.setEnfant(a);
			pred = a;
		}

		BigInteger base = BigInteger.ONE;
		for(Variable v : vars)
			base = base.multiply(BigInteger.valueOf(v.domain));
		out.updateBase(base);
		
		return out;
	}

}
