package preferences.penalty;

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
 * Akaike Information Criterion
 * @author Pierre-François Gimenez
 *
 */

public class AIC implements PenaltyWeightFunction
{
	private double k;
	
	public AIC(double k)
	{
		this.k = k;
	}

	/**
	 * k = 1 dans le cas de l'apprentissage de réseaux bayésiens
	 */
	public AIC()
	{
		k = 1;
	}
	
	@Override
	public double phi(int n)
	{
		return k;
	}
	
	public String toString()
	{
		return "AIC with parameter : "+k;
	}
}
