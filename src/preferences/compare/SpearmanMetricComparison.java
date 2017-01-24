package preferences.compare;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;

import preferences.ProbabilityDistributionLog;
import preferences.completeTree.LexTreeInterface;

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
 * Métrique de Spearman
 * TODO : refaire
 * @author Pierre-François Gimenez
 *
 */

public class SpearmanMetricComparison implements Comparison
{

	@Override
	public double compare(LexTreeInterface arbreAppris, LexTreeInterface arbreReel, BigInteger[] rangs, ProbabilityDistributionLog p)
	{
		double out = 0;
/*		for(int i = 0; i < rangs.length; i++)
		{
			HashMap<String, String> vecteur = arbreReel.getConfigurationAtRank(BigInteger.valueOf(rangs[i]-1));
			ArrayList<String> val = new ArrayList<String>();
			ArrayList<String> var = new ArrayList<String>();
			
			for(String s : vecteur.keySet())
			{
				var.add(s);
				val.add(vecteur.get(s));
			}
			
			double r = arbreAppris.infereRang(val, var).longValue();
			out += r - rangs[i];
		}*/
		
		return out / rangs.length; //a.getRangMax().divide(BigInteger.valueOf(rangs.length)).doubleValue() * out;

	}

}
