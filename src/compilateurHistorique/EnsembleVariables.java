package compilateurHistorique;

/*   (C) Copyright 2016, Pierre-François Gimenez
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
 * Un ensemble de variables
 * @author Pierre-François Gimenez
 *
 */

public class EnsembleVariables
{
	public int[] vars;
	
	public EnsembleVariables(int nb)
	{
		vars = new int[nb];
	}
	
	@Override
	public int hashCode()
	{
		int out = 0;
		for(int i = 0; i < vars.length; i++)
			out = 2*out + vars[i];
		return out;
	}
	
	@Override
	public boolean equals(Object o)
	{
		if(!(o instanceof EnsembleVariables))
			return false;
		EnsembleVariables o2 = (EnsembleVariables) o;
		if(vars.length != o2.vars.length)
			return false;
		for(int i = 0; i < vars.length; i++)
			if(vars[i] != o2.vars[i])
				return false;
		return true;
	}
}
