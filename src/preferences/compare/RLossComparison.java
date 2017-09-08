package preferences.compare;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;

import preferences.ProbabilityDistributionLog;
import preferences.completeTree.LexTreeInterface;

/*   (C) Copyright 2017, Gimenez Pierre-François 
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
 * Comparaison par ranking loss
 * @author Pierre-François Gimenez
 *
 */

public class RLossComparison implements Comparison
{

	@Override
	public double compare(LexTreeInterface arbreAppris, LexTreeInterface arbreReel, BigInteger[] rangs, ProbabilityDistributionLog p)
	{
		BigInteger out = BigInteger.ZERO;
		for(int i = 0; i < rangs.length; i++)
		{
			HashMap<String, String> vecteur = arbreReel.getConfigurationAtRank(rangs[i].subtract(BigInteger.ONE));
			ArrayList<String> val = new ArrayList<String>();
			ArrayList<String> var = new ArrayList<String>();
			
			for(String s : vecteur.keySet())
			{
				var.add(s);
				val.add(vecteur.get(s));
			}

			BigInteger r = arbreAppris.infereRang(val, var);
			out = out.add(r.add(BigInteger.ONE).subtract(rangs[i]));
		}
		
		return new BigDecimal(out).divide(new BigDecimal(rangs.length), 250, RoundingMode.HALF_EVEN).divide(new BigDecimal(arbreReel.getRangMax()), 250, RoundingMode.HALF_EVEN).doubleValue();
	}

}
