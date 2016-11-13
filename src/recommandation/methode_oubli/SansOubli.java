package recommandation.methode_oubli;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import compilateur.SALADD;
import compilateur.VDD;
import compilateur.Var;


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
 * @author Pierre-François Gimenez
 *
 */

public class SansOubli implements MethodeOubliSALADD {

	@Override
	public Map<String, Double> recommandation(Var v, HashMap<String, String> historiqueOperations, VDD vdd, ArrayList<String> possibles, SALADD contraintes)
	{
		Map<String, Double> out = vdd.countingpondereOnPossibleDomain(v, possibles);
		for(String s : out.keySet())
			System.out.println(s+": "+out.get(s));
//		vdd.countingpondere();
		return out;
	}

	@Override
	public int getNbOublis() {
		return 0;
	}

	@Override
	public void setNbIter(int nbIter)
	{}

	@Override
	public void learn(SALADD saladd, String prefix_file_name)
	{}
	
	public String toString()
	{
		return getClass().getSimpleName();
	}
}
