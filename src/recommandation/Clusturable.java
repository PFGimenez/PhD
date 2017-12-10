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

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import compilateur.SALADD;
import compilateurHistorique.DatasetInfo;
import compilateurHistorique.HistoriqueCompile;
import compilateurHistorique.Instanciation;

/**
 * Algorithme qui peut utiliser un cluster
 * @author pgimenez
 *
 */

public abstract class Clusturable implements AlgoReco
{
	protected SALADD contraintes;
	protected DatasetInfo dataset;
	
	public abstract void apprendDonnees(DatasetInfo dataset, List<Instanciation> instances, long code);

	@Override
	public final void apprendContraintes(SALADD contraintes)
	{
		this.contraintes = contraintes;
	}

	@Override
	public final void apprendDonnees(DatasetInfo dataset, ArrayList<String> filename, int nbIter, boolean entete)
	{
		this.dataset = dataset;
		int code = 0;
		for(String s : filename)
			code += s.hashCode();
		code = Math.abs(code);
		try {
			apprendDonnees(dataset, HistoriqueCompile.readPossibleInstances(dataset, filename, entete, contraintes, 1), code);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public HashMap<String, Double> metricCoeff()
	{
		return new HashMap<String, Double>();
	}
	
	public HashMap<String, Double> metric()
	{
		return new HashMap<String, Double>();
	}

	public double distance(Instanciation current, Instanciation center)
	{
		return current.distance(center);
	}
	
	public boolean isOracle()
	{
		return false;
	}
}
