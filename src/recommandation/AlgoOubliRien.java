package recommandation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import compilateurHistorique.DatasetInfo;
import compilateurHistorique.Instanciation;
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

public class AlgoOubliRien extends Clusturable
{
	private HistoriqueCompile historique;
	private Instanciation instanceReco;
	
	public AlgoOubliRien()
	{}
	
	public AlgoOubliRien(ParserProcess pp)
	{}
	
	public void describe()
	{
		System.out.println("Oracle");
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
	public boolean isOracle()
	{
		return true;
	}
	
	@Override
	public void terminePli()
	{}

	@Override
	public void unassign(String variable)
	{
		instanceReco.deconditionne(variable);
	}

	@Override
	public void apprendDonnees(DatasetInfo dataset, List<Instanciation> instances, long code)
	{
		historique = new HistoriqueCompile(dataset);
		historique.compile(instances);
		instanceReco = new Instanciation(dataset);
	}

}
