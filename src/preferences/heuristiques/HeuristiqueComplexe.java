package preferences.heuristiques;

import java.util.ArrayList;

import compilateurHistorique.Instanciation;
import compilateurHistorique.MultiHistoComp;

/**
 * Interface des heuristiques qui utilisent directement l'historique
 * @author pgimenez
 *
 */

public interface HeuristiqueComplexe
{
	/**
	 * 
	 * @param h
	 * @param variables encore libre
	 * @param instance actuelle
	 * @return
	 */
	public String getRacine(MultiHistoComp historique, ArrayList<String> variables, Instanciation instance);
}
