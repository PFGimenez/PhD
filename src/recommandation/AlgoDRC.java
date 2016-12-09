package recommandation;

import java.util.ArrayList;
import java.util.HashMap;

import compilateurHistorique.MultiHistoComp;
import compilateurHistorique.EnsembleVariables;
import compilateurHistorique.Instanciation;
import graphOperation.DAG;
import graphOperation.InferenceDRC;

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
 * Algorithme DRC
 * @author Pierre-François Gimenez
 *
 */

public class AlgoDRC implements AlgoRecoRB
{
	private MultiHistoComp historique;
	private Instanciation instanceReco;
	private int seuil;
	private String RBfile;
	private InferenceDRC inferer;
	private int equivalentSampleSize;

	public AlgoDRC(int seuil, int equivalentSampleSize)
	{
		this.seuil= seuil;
		this.equivalentSampleSize = equivalentSampleSize;
	}
	
/*	@Override
	public void apprendContraintes(SALADD contraintes)
	{}
	*/
	@Override
	public void apprendDonnees(ArrayList<String> filename, int nbIter, boolean entete)
	{
		historique.compile(filename, entete);
		inferer = new InferenceDRC(seuil, new DAG(RBfile), historique, equivalentSampleSize, false);
		instanceReco = new Instanciation();
	}
	
	@Override
	public String recommande(String variable, ArrayList<String> possibles)
	{
		ArrayList<String> valeurs, valeurs2;
		
		// On itère que sur les valeurs possibles ou, si on n'a pas cette information, sur toutes les valeurs
		valeurs = historique.getValues(variable);
		valeurs2 = new ArrayList<String>();

		for(String s : valeurs)
			if(possibles == null || possibles.contains(s))
				valeurs2.add(s);
		
		EnsembleVariables U = null;
		double probaMax = 0;
		String valMax = null;
		HashMap<String, Double> res = new HashMap<String, Double>();
		double total = 0;
		for(String val : valeurs2)
		{
			instanceReco.conditionne(variable, val);
//			System.out.println(instanceReco);
			if(U == null)
				U = instanceReco.getEVConditionees();
			double p = inferer.infere(instanceReco, U);
			res.put(val, Math.exp(p));
			total += Math.exp(p);
			if(valMax == null || p > probaMax)
			{
				probaMax = p;
				valMax = val;
			}
			instanceReco.deconditionne(variable);
		}
		
/*		for(String val : valeurs2)
			System.out.println(val+" : "+res.get(val)/total);*/

		return valMax;
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
		inferer.partialClearCache();
	}
	
	public String toString()
	{
		if(seuil == -1)
			return "AlgoRC";
		else
			return getClass().getSimpleName()+"-"+seuil;
	}
	
	@Override
	public void termine()
	{}
	
	public void initHistorique(ArrayList<String> filename, boolean entete)
	{
		historique = new MultiHistoComp(filename, entete, null);
	}

	@Override
	public void unassign(String variable)
	{
		instanceReco.deconditionne(variable);
	}

	@Override
	public void apprendRB(String file)
	{
		this.RBfile = file;
	}
}
