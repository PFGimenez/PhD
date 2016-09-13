package preferences;

import JSci.maths.statistics.ProbabilityDistribution;

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

public class LinearDistribution extends ProbabilityDistribution
{
	private double a, b;
	
	public LinearDistribution(double nbValeur, double probaLast)
	{
		a = (nbValeur * probaLast - 1) / (nbValeur * nbValeur - nbValeur*(nbValeur+1)/2);
		b = probaLast - a * nbValeur;
		if(a >= 0)
			throw new IllegalArgumentException("a = "+a);
	}
	
	@Override
	public double cumulative(double rang)
	{
		return rang * (probability(1) + probability(rang)) / 2;
	}

	@Override
	public double inverse(double y)
	{
		return Math.round((-2 * b - a + Math.sqrt((2 * b + a) * (2 * b + a)+ 8 * a * y)) / (2 * a)) + 1;
	}

	@Override
	public double probability(double x)
	{
		return a * (x - 1) + b;
	}

}
