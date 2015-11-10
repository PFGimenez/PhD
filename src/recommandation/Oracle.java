package recommandation;

import compilateur.test_independance.TestEcartMax;
import recommandation.methode_oubli.OubliParIndependance;

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
 * Algorithme Oracle
 * @author pgimenez
 *
 */

public class Oracle extends AlgoSaladdOubli
{

	public Oracle(String dataset)
	{
		super(new OubliParIndependance(0, new TestEcartMax()), dataset);
	}

}
