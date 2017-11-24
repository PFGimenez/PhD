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

package compilateurHistorique.neighbohroodSize;

/**
 * Un nombre constant de voisins
 * @author pgimenez
 *
 */

public class ConstantNeighborhood implements NeighborhoodSizeComputer
{
	private int param;
	
	public ConstantNeighborhood(int param)
	{
		this.param = param;
	}
	
	@Override
	public int getNbVoisins(int nbVar) {
		return param;
	}

	@Override
	public String toString()
	{
		return "Nb voisins = "+param;
	}
}
