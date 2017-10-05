package recommandation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import compilateurHistorique.DatasetInfo;
import compilateurHistorique.HistoriqueCompile;
import compilateurHistorique.Instanciation;
import recommandation.parser.ParserProcess;

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

public class AlgoRBNaif implements AlgoReco, Clusturable
{
	private DatasetInfo dataset;
	private Instanciation instanceReco;
	private HistoriqueCompile historique;
	private HashMap<Integer, HashMap<Integer, HashMap<Integer, HashMap<Integer, Double>>>> probaCond = new HashMap<Integer, HashMap<Integer, HashMap<Integer, HashMap<Integer, Double>>>>();
	private HashMap<Integer, HashMap<Integer, Double>> probaAPriori = new HashMap<Integer, HashMap<Integer, Double>>();
	private Random r = new Random();

	public AlgoRBNaif(ParserProcess pp)
	{}
	
	public AlgoRBNaif()
	{}
	
	public void describe()
	{
		System.out.println("Naive Bayesian network");
	}

	@Override
	public void apprendDonnees(DatasetInfo dataset, Instanciation[] instances, int code)
	{
		historique = new HistoriqueCompile(dataset);
		this.dataset = dataset;
		historique.compile(instances);
		double nbTotal = historique.getNbInstancesTotal();
		Instanciation inst = new Instanciation(dataset);
		for(int i = 0; i < dataset.vars.length; i++)
		{
			probaAPriori.put(i, new HashMap<Integer, Double>());
			probaCond.put(i, new HashMap<Integer, HashMap<Integer, HashMap<Integer, Double>>>());
			for(int j = 0; j < dataset.vars[i].values.size(); j++)
			{
				probaCond.get(i).put(j, new HashMap<Integer, HashMap<Integer, Double>>());
				inst.conditionne(i, j);
				double nb = historique.getNbInstances(inst);
				probaAPriori.get(i).put(j, nb / nbTotal);
				for(int i2 = 0; i2 < dataset.vars.length; i2++)
				{
					if(i == i2)
						continue;
					probaCond.get(i).get(j).put(i2, new HashMap<Integer, Double>());
					for(int j2 = 0; j2 < dataset.vars[i2].values.size(); j2++)
					{
						inst.conditionne(i2, j2);
						double nb2 = historique.getNbInstances(inst);
						probaCond.get(i).get(j).get(i2).put(j2, nb2 / nb);
						inst.deconditionne(i2);
					}
				}
				inst.deconditionne(i);
			}
		}
		instanceReco = new Instanciation(dataset);

	}
	
	@Override
	public void apprendDonnees(DatasetInfo dataset, ArrayList<String> filename, int nbIter, boolean entete)
	{
		int code = 0;
		for(String s : filename)
			code += s.hashCode();
		code = Math.abs(code);
		apprendDonnees(dataset, HistoriqueCompile.readInstances(dataset, filename, entete), code);
	}
	
	@Override
	public String recommande(String variable, ArrayList<String> possibles)
	{
		int indexVar = dataset.mapVar.get(variable);
		int nbVal = dataset.vars[indexVar].values.size();

		double probaMax = 0;
		String valueMax = null;
		for(int i = 0; i < nbVal; i++)  
		{
			String value = dataset.vars[indexVar].values.get(i);
			if(possibles != null && !possibles.contains(value))
				continue;
			
			double probaTmp = probaAPriori.get(indexVar).get(i);
			for(int j = 0; j < dataset.vars.length; j++)
			{
				if(j == i || instanceReco.getValue(j) == null)
					continue;
				probaTmp *= probaCond.get(indexVar).get(i).get(j).get(instanceReco.getValue(j));
			}

			if(probaTmp >= probaMax)
			{
				probaMax = probaTmp;
				valueMax = value;
			}
		}
	
		if(valueMax == null) // toutes les valeurs connues sont impossibles
		{
			assert possibles != null;
			return possibles.get(r.nextInt(possibles.size()));
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
	
	@Override
	public HashMap<String, Double> metricCoeff()
	{
		return new HashMap<String, Double>();
	}

	@Override
	public HashMap<String, Double> metric()
	{
		return new HashMap<String, Double>();
	}
}
