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

package preferences.completeTree;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Interface des LP-tree
 * Permet notamment les arbres partiellement générés
 * @author Pierre-François Gimenez
 *
 */

public interface LexTreeInterface {

	public BigInteger getRangMax();
	
	public HashMap<String, String> getConfigurationAtRank(BigInteger rang);

	public BigInteger infereRang(ArrayList<String> val, ArrayList<String> var);
}
