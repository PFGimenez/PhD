package preferences;

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
 * La loi géométrique
 * @author Pierre-François Gimenez
 *
 */

public class GeometricDistribution implements ProbabilityDistributionLog
{
	private double logp;
	private double logq;
	
	public GeometricDistribution(double p)
	{
		logp = Math.log(p);
		logq = Math.log(1-p);
	}

	@Override
	public double logProbability(double x)
	{
		return x * logq + logp;
	}

	@Override
	public double inverse(double p)
	{
		return Math.round(Math.log(1-p) / logq) + 1;
	}

}
