package recommandation;

import java.util.ArrayList;

import compilateurHistorique.DatasetInfo;
import compilateurHistorique.Instanciation;

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
 * Algorithme de recommandation basé sur un classifieur bayésien naïf
 * @author Pierre-François Gimenez
 *
 */

public class AlgoRBNaif implements AlgoReco
{
	private DatasetInfo dataset;
	private Instanciation instanceReco;

/*	@Override
	public void apprendContraintes(SALADD contraintes)
	{}*/
	
	public void describe()
	{
		System.out.println("Naive Bayesian network");
	}

	
	@Override
	public void apprendDonnees(DatasetInfo dataset, ArrayList<String> filename, int nbIter, boolean entete)
	{
		this.dataset = dataset;
//		historique.apprendPrecalcul();
		instanceReco = new Instanciation(dataset);
	}
	
	@Override
	public String recommande(String variable, ArrayList<String> possibles)
	{
		ArrayList<String> valeurs = dataset.vars[dataset.mapVar.get(variable)].values;


		double probaMax = 0;
		String valueMax = null;
		for(String value : valeurs)
		{
			if(possibles != null && !possibles.contains(value))
				continue;
			
			double probaTmp;// = historique.getNbInstancesAPriori(variable, value);
			probaTmp = 0; // TODO
//			probaTmp = historique.getProbaRBNaif(instanceReco, variable, value);
			if(probaTmp >= probaMax)
			{
				probaMax = probaTmp;
				valueMax = value;
			}
		}
		return valueMax;
	}

	@Override
	public void setSolution(String variable, String solution)
	{
		instanceReco.conditionne(variable, solution);
	}

	@Override
	public void oublieSession()
	{
		instanceReco.deconditionneTout();
	}
	
	public String toString()
	{
		return getClass().getSimpleName();
	}
	
	@Override
	public void termine()
	{}
	
	@Override
	public void terminePli()
	{}
	
	@Override
	public void unassign(String variable)
	{
		instanceReco.deconditionne(variable);
	}
}
