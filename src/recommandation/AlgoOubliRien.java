package recommandation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import compilateur.SALADD;
import compilateurHistorique.DatasetInfo;
import compilateurHistorique.Instanciation;
import compilateurHistorique.Variable;
import recommandation.parser.ParserProcess;
import compilateurHistorique.HistoriqueCompile;

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
 * Algorithme de recommandation qui n'oublie rien
 * @author Pierre-François Gimenez
 *
 */

public class AlgoOubliRien implements AlgoReco, Clusturable
{
	private HistoriqueCompile historique;
	private Instanciation instanceReco;
	private SALADD contraintes;
	
	public AlgoOubliRien()
	{}
	
	public AlgoOubliRien(ParserProcess pp)
	{}
	
	@Override
	public void apprendContraintes(SALADD contraintes)
	{
		this.contraintes = contraintes;
	}
	
	public void describe()
	{
		System.out.println("Oracle");
	}
	
	@Override
	public void apprendDonnees(DatasetInfo dataset, ArrayList<String> filename, int nbIter, boolean entete) 
	{
		apprendDonnees(dataset, HistoriqueCompile.readInstances(dataset, filename, entete), 0);
	}
	
	@Override
	public String recommande(String variable, ArrayList<String> possibles)
	{
		HashMap<String, Double> proba3 = historique.getProbaToutesModalitees(variable, false, instanceReco);
		
		double probaMax3 = 0;
		String valueMax3 = null;
		for(String value : proba3.keySet())
		{
			double probaTmp = proba3.get(value);
			if(probaTmp >= probaMax3)
			{
				probaMax3 = probaTmp;
				valueMax3 = value;
			}
		}
		
		assert possibles == null || possibles.contains(valueMax3);
		
		return valueMax3;
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
		return "Oracle";
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
	public void apprendDonnees(DatasetInfo dataset, Instanciation[] instances, int code)
	{
		if(contraintes != null)
		{
			List<Instanciation> inst = new ArrayList<Instanciation>();
			for(int i = 0; i < instances.length; i++)
			{
				boolean possible = true;
				for(Variable v : dataset.vars)
				{
					if(!contraintes.isPresentInCurrentDomain(v.name, instances[i].getValue(v.name)))
					{
						possible = false;
						break;
					}
					contraintes.assignAndPropagate(v.name, instances[i].getValue(v.name));
					if(!contraintes.isPossiblyConsistent())
					{
						possible = false;
						break;
					}
				}
				if(possible)
					inst.add(instances[i]);
				contraintes.reinitialisation();
				contraintes.propagation();
			}
			System.out.println("En tout : "+instances.length);
			System.out.println("Possibles : "+inst.size());
			instances = new Instanciation[inst.size()];
			for(int i = 0; i < instances.length; i++)
				instances[i] = inst.get(i);
		}
		historique = new HistoriqueCompile(dataset);
		historique.compile(instances);
		instanceReco = new Instanciation(dataset);
	}
	
	public boolean isOracle()
	{
		return true;
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
