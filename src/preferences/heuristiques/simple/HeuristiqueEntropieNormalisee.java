package preferences.heuristiques.simple;

import java.util.Map;

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
 * Heuristique = entropie normalisée
 * @author Pierre-François Gimenez
 *
 */

public class HeuristiqueEntropieNormalisee implements HeuristiqueOrdre
{

	@Override
	public double computeHeuristique(Map<String, Integer> nbExemples) { 
		double nbExemplesTotal = 0;
		for(Integer nb : nbExemples.values())
			nbExemplesTotal += nb;

		double entropie = 0;
		for(Integer nb : nbExemples.values())
			if(nb != 0)
				entropie -= nb/nbExemplesTotal * Math.log(nb/nbExemplesTotal);

		// Normalisation de l'entropie entre 0 et 1 (afin de pouvoir comparer les entropies de variables au nombre de modalité différents)
//		System.out.println(entropie+" "+entropie/Math.log(nbExemples.size()));
		entropie /= Math.log(nbExemples.size());
		return entropie;
	}

}
