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

package recommandation;

import java.util.HashMap;

import compilateurHistorique.DatasetInfo;
import compilateurHistorique.Instanciation;

/**
 * Algorithme qui peut utiliser un cluster
 * @author pgimenez
 *
 */

public interface Clusturable extends AlgoReco
{
	public void apprendDonnees(DatasetInfo dataset, Instanciation[] instances, int code);
	public HashMap<String, Double> metricCoeff();
	public HashMap<String, Double> metric();
	public double distance(Instanciation current, Instanciation center);
}
