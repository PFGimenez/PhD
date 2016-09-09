package preferences.heuristiques;

import java.util.ArrayList;

import compilateurHistorique.Instanciation;
import compilateurHistorique.MultiHistoComp;
import preferences.heuristiques.simple.HeuristiqueOrdre;

/**
 * Permet l'utilisation des anciennes heuristiques
 * @author pgimenez
 *
 */

public class VieilleHeuristique implements HeuristiqueComplexe
{
	private HeuristiqueOrdre h;
	
	public VieilleHeuristique(HeuristiqueOrdre h)
	{
		this.h = h;
	}
	
	@Override
	public String getRacine(MultiHistoComp historique, ArrayList<String> variables, Instanciation instance)
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
