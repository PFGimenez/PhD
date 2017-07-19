package preferences.heuristiques;

import java.util.ArrayList;

import compilateurHistorique.Instanciation;
import compilateurHistorique.DatasetInfo;
import compilateurHistorique.HistoriqueCompile;
import preferences.heuristiques.simple.HeuristiqueOrdre;

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
 * Permet l'utilisation des anciennes heuristiques
 * @author Pierre-François Gimenez
 *
 */

public class VieilleHeuristique implements HeuristiqueComplexe
{
	public HeuristiqueOrdre h;
	
	public VieilleHeuristique(HeuristiqueOrdre h)
	{
		this.h = h;
	}
	
	@Override
	public String getRacine(DatasetInfo dataset, HistoriqueCompile historique, ArrayList<String> variables, Instanciation instance)
	{
		double min = Integer.MAX_VALUE;
		String best = null;
		for(String v : variables)
		{
			double tmp = h.computeHeuristique(historique.getNbInstancesToutesModalitees(v, null, true, instance));
			if(tmp < min)
			{
				min = tmp;
				best = v;
			}
		}
		
		return best;
	}

}
