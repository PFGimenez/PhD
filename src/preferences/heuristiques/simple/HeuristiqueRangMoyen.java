package preferences.heuristiques.simple;

import java.util.ArrayList;
import java.util.Collections;
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
 * Heuristique = rang moyen
 * @author pgimenez
 *
 */

public class HeuristiqueRangMoyen implements HeuristiqueOrdre
{

	@Override
	public double computeHeuristique(Map<String, Integer> nbExemples) { 
		int nbValeurs = nbExemples.size();
		double nbExemplesTotal = 0;
		for(Integer nb : nbExemples.values())
			nbExemplesTotal += nb;

		ArrayList<Integer> values = new ArrayList<Integer>();
		values.addAll(nbExemples.values());
		Collections.sort(values);

		double out = 0;
		for(int i = 0; i < nbValeurs; i++)
			out += (i+0.5)/nbValeurs*values.get(nbValeurs-1-i)/nbExemplesTotal;
		return out;
	}

}
