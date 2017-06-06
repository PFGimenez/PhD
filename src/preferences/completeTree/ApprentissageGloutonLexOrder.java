package preferences.completeTree;

import java.util.ArrayList;

import compilateurHistorique.Instanciation;
import preferences.heuristiques.HeuristiqueComplexe;

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
 * Apprentissage d'un ordre lexicographique
 * @author Pierre-François Gimenez
 *
 */

public class ApprentissageGloutonLexOrder extends ApprentissageGloutonLexStructure
{
	public ApprentissageGloutonLexOrder(HeuristiqueComplexe h)
	{
		this.h = h;
	}
	
	public String toString()
	{
		return "ApprentissageGloutonLexOrder, heuristique = "+h.getClass().getSimpleName();
	}
	
	public LexicographicStructure apprendDonnees(ArrayList<String> filename, boolean entete)
	{
		return apprendDonnees(filename, entete, -1);
	}
	
	/**
	 * Compilation de l'historique dans un SLDD.
	 * Apprend l'ordre des variables
	 * @param filename
	 */
	public LexicographicStructure apprendDonnees(ArrayList<String> filename, boolean entete, int nbExemplesMax)
	{
		super.apprendDonnees(filename, entete, nbExemplesMax);
		struct = apprendOrdre(new Instanciation(), variables);
		struct.updateBase(base);
		return struct;
	}
	
}
