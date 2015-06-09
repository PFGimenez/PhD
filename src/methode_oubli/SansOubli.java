package methode_oubli;

import java.util.ArrayList;
import java.util.Map;

import br4cp.SALADD;
import br4cp.VDD;
import br4cp.Var;

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
 * Recommandation simple sans oubli
 * @author pgimenez
 *
 */

public class SansOubli implements MethodeOubli {
	private int nbReco = 0;
	private int nbPossibles = 0;

	@Override
	public Map<String, Double> recommandation(Var v, ArrayList<String> historiqueOperations, VDD vdd, ArrayList<String> possibles) {
		nbReco++;
		nbPossibles += possibles.size();
		System.out.println(nbReco+" reco, "+nbPossibles+" possibles");
		vdd.countingpondere();
		return vdd.countingpondereOnPossibleDomain(v, possibles);
	}

	@Override
	public void learn(SALADD saladd)
	{}

	@Override
	public int getNbOublis() {
		return 0;
	}
}
