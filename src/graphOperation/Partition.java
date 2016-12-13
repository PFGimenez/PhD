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

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Une partition d'un ensemble de variables
 * @author Pierre-François Gimenez
 *
 */

public class Partition
{
	public Set<String>[] ensembles;
	public Set<String> separateur;
	
	public int[] separateurTab;
	public int[] g0cTab;
	public int[] g1cTab;
	public int[] g0Tab;
	public int[] g1Tab;

	@SuppressWarnings("unchecked")
	public Partition()
	{
		ensembles = (Set<String>[]) new HashSet[2];
		for(int i = 0; i < 2; i++)
			ensembles[i] = new HashSet<String>();
		separateur = new HashSet<String>();
	}
	
	public void updateTab(Map<String, Integer> mapvar)
	{
		separateurTab = new int[separateur.size()];
		g0cTab = new int[ensembles[0].size() + separateur.size()];
		g1cTab = new int[ensembles[1].size() + separateur.size()];
		g0Tab = new int[ensembles[0].size()];
		g1Tab = new int[ensembles[1].size()];

		int i = 0;
		for(String s : separateur)
			separateurTab[i++] = mapvar.get(s);
		
		i = 0;
		for(String s : ensembles[0])
		{
			g0cTab[i] = mapvar.get(s);
			g0Tab[i++] = mapvar.get(s);
		}
		for(String s : separateur)
			g0cTab[i++] = mapvar.get(s);

		i = 0;
		for(String s : ensembles[1])
		{
			g1cTab[i] = mapvar.get(s);
			g1Tab[i++] = mapvar.get(s);
		}
		for(String s : separateur)
			g1cTab[i++] = mapvar.get(s);
	}
	
	@Override
	public String toString()
	{
		String out = "";
		out += "G1 :";
		for(String s : ensembles[0])
			out += " "+s;
		out += "\nG2 :";
		for(String s : ensembles[1])
			out += " "+s;
		out += "\nC :";
		for(String s : separateur)
			out += " "+s;
		return out;
	}
}
