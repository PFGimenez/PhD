package preferences;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;

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

/**
 * Structure qui se base sur l'ordre lexicographique. Classe abstraite
 * @author pgimenez
 *
 */

public abstract class LexicographicStructure
{
	protected int nbMod;
	private ArrayList<String> ordrePref;
	protected String variable;
	private double entropie;
	protected long base;
	
	public LexicographicStructure(String variable, int nbMod)
	{
		this.nbMod = nbMod;
		this.variable = variable;		
		ordrePref = new ArrayList<String>();
	}
		
	/**
	 * Met à jour l'entropie et ordrePref.
	 * @param nbExemples
	 */
	public void setNbExemples(Map<String, Double> nbExemples)
	{
		double nbExemplesTotal = 0;
		for(Double nb : nbExemples.values())
			nbExemplesTotal += nb;

		entropie = 0;
		for(Double nb : nbExemples.values())
		{
			entropie -= nb/nbExemplesTotal * Math.log(nb/nbExemplesTotal);
		}
		// Normalisation de l'entropie entre 0 et 1 (afin de pouvoir comparer les entropies de variables au nombre de modalité différents)
		entropie /= Math.log(nbMod);
		
		// Calcul de ordrePref
		
		// From http://stackoverflow.com/questions/109383/how-to-sort-a-mapkey-value-on-the-values-in-java
		LinkedList<Entry<String, Double>> list = new LinkedList<Map.Entry<String,Double>>(nbExemples.entrySet());
	     Collections.sort(list, new Comparator<Entry<String, Double>>() {
	          public int compare(Entry<String, Double> o1, Entry<String, Double> o2) {
	               return o1.getValue()
	              .compareTo(o2.getValue());
	          }
	     });

	    for (Iterator<Entry<String, Double>> it = list.iterator(); it.hasNext();) {
	        Map.Entry<String, Double> entry = it.next();
	        ordrePref.add((String) entry.getKey());
	    }		
	}
	
	protected int getPref(String valeur)
	{
		for(int i = 0; i < nbMod - 1; i++)
			if(ordrePref.get(i).equals(valeur))
				return i;
		return nbMod - 1;
	}
	
	public int getNbMod()
	{
		return nbMod;
	}
	
	public long getBase()
	{
		return base;
	}
	
	public String getVar()
	{
		return variable;
	}

	public double getEntropie()
	{
		return entropie;
	}
	
	public abstract void updateBase(long base);
	
	public abstract long infereRang(ArrayList<String> element, ArrayList<String> ordreVariables);
	
}
