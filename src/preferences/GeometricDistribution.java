package preferences;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;

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
	private BigDecimal logp;
	private BigDecimal logq;
	
	/**
	 * Constructeur si p est très proche de 0
	 * @param p
	 * @param logp
	 */
	public GeometricDistribution(BigDecimal p, BigDecimal logp)
	{
		this.logp = logp;
		logq = p.multiply(p).divide(BigDecimal.valueOf(2)).add(p).negate(); // DL de log(q) = log(1-p) = -p -p*p/2
	}
	
	/*
	public GeometricDistribution(double p)
	{
		logp = Math.log(p);
		logq = Math.log(1-p);
	}*/

	@Override
	public BigDecimal logProbability(BigInteger x)
	{
		return new BigDecimal(x).multiply(logq).add(logp);
	}

/*	public double inverse(double p)
	{
		return Math.round(Math.log(1-p) / logq) + 1;
	}*/

	@Override
	public BigInteger inverseBigInteger(double p)
	{
		return BigDecimal.valueOf(Math.log(1-p)).divide(logq, 250, RoundingMode.HALF_EVEN).toBigInteger().add(BigInteger.ONE);
	}
	
	public String toString()
	{
		return "GeometricDistribution, log(p) = "+logp;
	}

}
