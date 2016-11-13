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
 * Heuristique = proba max
 * @author Pierre-François Gimenez
 *
 */

public class HeuristiqueProbaMax implements HeuristiqueOrdre
{

	@Override
	public double computeHeuristique(Map<String, Integer> nbExemples) { 
		double nbExemplesTotal = 0;
		for(Integer nb : nbExemples.values())
			nbExemplesTotal += nb;

		double nbMax = 0;
		for(Integer nb : nbExemples.values())
			if(nb > nbMax)
				nbMax = nb;

		return - nbMax / nbExemplesTotal;
	}

}
