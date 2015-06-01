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
 * Oubli par calcul d'entropie.
 * @author pgimenez
 *
 */

public class OubliParEntropie implements MethodeOubli {

	@Override
	public Map<String, Double> recommandation(Var v, ArrayList<String> historiqueOperations, VDD vdd, ArrayList<String> possibles) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void learn(SALADD saladd)
	{
		
	}
}
