package recommandation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import compilateur.SALADD;
import compilateurHistorique.DatasetInfo;
import compilateurHistorique.Instanciation;

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
 * Recommandation aléatoire
 * @author Pierre-François Gimenez
 *
 */

public class AlgoRandom implements AlgoReco, Clusturable
{

	private Random r = new Random();
	private DatasetInfo dataset;
	
	@Override
	public void apprendContraintes(SALADD contraintes)
	{}
	
	public void describe()
	{
		System.out.println("Random recommander");
	}
	
	@Override
	public void apprendDonnees(DatasetInfo dataset, ArrayList<String> filename, int nbIter, boolean entete)
	{
		this.dataset = dataset;
	}

	@Override
	public String recommande(String variable, ArrayList<String> possibles)
	{
		if(possibles == null)
			possibles = dataset.vars[dataset.mapVar.get(variable)].values;
		return possibles.get(r.nextInt(possibles.size()));
	}

	@Override
	public void setSolution(String variable, String solution)
	{}

	@Override
	public void oublieSession()
	{}

	@Override
	public void termine()
	{}
	
	@Override
	public void terminePli()
	{}

	public String toString()
	{
		return getClass().getSimpleName();
	}

	@Override
	public void unassign(String variable)
	{}

	@Override
	public void apprendDonnees(DatasetInfo dataset, Instanciation[] instances, int code)
	{
		this.dataset = dataset;
	}

	@Override
	public HashMap<String, Double> metricCoeff()
	{
		return new HashMap<String, Double>();
	}

	public boolean isOracle()
	{
		return false;
	}

	@Override
	public HashMap<String, Double> metric()
	{
		return new HashMap<String, Double>();
	}
	
	public double distance(Instanciation current, Instanciation center)
	{
		return current.distance(center);
	}

}
