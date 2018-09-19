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
import java.util.HashMap;
import java.util.List;

/**
 * Interface des LP-tree
 * Permet notamment les arbres partiellement générés
 * @author Pierre-François Gimenez
 *
 */

public interface LexTreeInterface {

	public BigInteger getRangMax();
	
	/**
	 * Calcule la configuration à un certain rang.
	 * LE RANG CONSIDÉRÉ COMMENCE À 0 ET PAS À 1
	 * @param rang
	 * @return
	 */
	public HashMap<String, String> getConfigurationAtRank(BigInteger rang);

	/**
	 * Calcule le rang d'une configuration
	 * LE RANG CONSIDÉRÉ COMMENCE À 0 ET PAS À 1
	 * @param rang
	 * @return
	 */
	public BigInteger infereRang(List<String> val, List<String> var);
	
	public int getTailleTable();	

}
