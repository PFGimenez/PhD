package preferences;

import java.math.BigDecimal;
import java.math.BigInteger;

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

public interface ProbabilityDistributionLog
{
//	public double cumulative(double rang);
//	public double inverse(double y);
//	public double probability(double x);
	public BigDecimal logProbability(BigInteger x);
	public BigInteger inverseBigInteger(double y);
}
