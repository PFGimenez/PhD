package preferences.heuristiques;

import java.util.ArrayList;
import java.util.List;

import compilateurHistorique.DatasetInfo;
import compilateurHistorique.HistoriqueCompile;
import compilateurHistorique.Instanciation;

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
 * Heuristique gloutonne qui trouve plusieurs variables pour un nœud
 * @author Pierre-François Gimenez
 *
 */

public class HeuristiqueMultipleDuel implements MultipleHeuristique
{
	private HeuristiqueDuel h = new HeuristiqueDuel();
	private int taille;
	
	public HeuristiqueMultipleDuel(int taille)
	{
		this.taille = taille;
	}
	
	@Override
	public String toString()
	{
		return getClass().getSimpleName()+", taille groupe = "+taille;
	}
	
	@Override
	public List<String> getRacine(DatasetInfo dataset, HistoriqueCompile historique, List<String> variables,
			Instanciation instance)
	{
		List<String> out = new ArrayList<String>();
		List<String> vars = new ArrayList<String>();
		vars.addAll(variables);
		for(int i = 0; i < taille; i++)
		{
			String v = h.getRacine(dataset, historique, vars, instance);
			vars.remove(v);
			out.add(v);
			if(vars.isEmpty())
				return out;
		}
		return out;
	}

}
