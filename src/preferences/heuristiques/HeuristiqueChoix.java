package preferences.heuristiques;

import java.util.HashMap;
import java.util.List;

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

public class HeuristiqueChoix extends MultipleHeuristique
{	
	private int taille;
	private int seuil;
	private HeuristiqueOptimale hopt;
	private HeuristiqueMultipleComposedDuel hduel;
	
	public HeuristiqueChoix(int taille, int seuil)
	{
		this.seuil = seuil;
		this.taille = taille;
		hopt = new HeuristiqueOptimale(taille);
		hduel = new HeuristiqueMultipleComposedDuel(taille);
	}
	

	@Override
	public int hashCode()
	{
		return 0x84FCE + taille;
	}
	
	@Override
	public String toString()
	{
		return getClass().getSimpleName()+", taille groupe = "+taille;
	}
	
	@Override
	public List<String> getRacine(DatasetInfo dataset, HistoriqueCompile historique, List<String> variables, Instanciation instance)
	{	
		if(historique.getNbInstances(instance) < 2*seuil)
			return hopt.getRacine(dataset, historique, variables, instance);
		
		List<String> out = hduel.getRacine(dataset, historique, variables, instance);
		HashMap<List<String>, Integer> histo = historique.getNbInstancesToutesModalitees(out, true, instance);
		for(Integer n : histo.values())
			if(n < seuil)
				return hopt.getRacine(dataset, historique, variables, instance);
		return out;
	}

}
