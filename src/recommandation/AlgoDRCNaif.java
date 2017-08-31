package recommandation;

import java.util.ArrayList;
import java.util.HashMap;

import compilateurHistorique.HistoriqueCompile;
import compilateurHistorique.DatasetInfo;
import compilateurHistorique.EnsembleVariables;
import compilateurHistorique.Instanciation;
import graphOperation.ArbreDecompTernaire;
import graphOperation.DAG;
import graphOperation.InferenceDRC;

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

/**
 * Algorithme DRC qui apprend un RB naïf
 * @author Pierre-François Gimenez
 *
 */

public class AlgoDRCNaif implements AlgoReco
{
	private DatasetInfo dataset;
	private Instanciation instanceReco;
	private int seuil;
	private ArbreDecompTernaire[] decomps;
	private InferenceDRC[] inferers;
	private int equivalentSampleSize;
	private HashMap<String, Integer> mapVar;
	
	public AlgoDRCNaif()
	{
		this(5, 10);
	}
	
	public AlgoDRCNaif(int seuil, int equivalentSampleSize)
	{
		this.seuil= seuil;
		this.equivalentSampleSize = equivalentSampleSize;
	}
	
	public void describe()
	{
		System.out.println("DRC");
		System.out.println("seuil = "+seuil);
		System.out.println("equivalentSampleSize = "+equivalentSampleSize);
	}
	
/*	@Override
	public void apprendContraintes(SALADD contraintes)
	{}
	*/
	@Override
	public void apprendDonnees(DatasetInfo dataset, ArrayList<String> filename, int nbIter, boolean entete)
	{
		this.dataset = dataset;
		HistoriqueCompile historique = new HistoriqueCompile(dataset);
		historique.compile(filename, entete);
		
		mapVar = dataset.mapVar;
		decomps = new ArbreDecompTernaire[mapVar.size()];
		inferers = new InferenceDRC[mapVar.size()];
		for(String s : mapVar.keySet())
		{
			decomps[mapVar.get(s)] = new ArbreDecompTernaire(dataset, new DAG(mapVar.keySet(), s), mapVar, historique, false);
			inferers[mapVar.get(s)] = new InferenceDRC(seuil, decomps[mapVar.get(s)], dataset, historique, equivalentSampleSize, false, false);
//			decomps[mapVar.get(s)].prune(readInstances(filename, entete, -1), new BIC(), inferers[mapVar.get(s)]);
		}
		instanceReco = new Instanciation(dataset);
	}
	
	@Override
	public String recommande(String variable, ArrayList<String> possibles)
	{
		ArrayList<String> valeurs, valeurs2;
		
		// On itère que sur les valeurs possibles ou, si on n'a pas cette information, sur toutes les valeurs
		valeurs = dataset.vars[dataset.mapVar.get(variable)].values;
		valeurs2 = new ArrayList<String>();

		for(String s : valeurs)
			if(possibles == null || possibles.contains(s))
				valeurs2.add(s);
		
		EnsembleVariables U = null;
		double probaMax = 0;
		String valMax = null;
		HashMap<String, Double> res = new HashMap<String, Double>();
//		double total = 0;
		for(String val : valeurs2)
		{
			instanceReco.conditionne(variable, val);
//			System.out.println(instanceReco);
			if(U == null)
				U = instanceReco.getEVConditionees();
			double p = inferers[mapVar.get(variable)].infere(instanceReco, U);
			res.put(val, Math.exp(p));
//			total += Math.exp(p);
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
		for(InferenceDRC inferer : inferers)
			inferer.partialClearCache();
	}
	
	public String toString()
	{
		return getClass().getSimpleName()+"-"+seuil;
	}
	
	@Override
	public void termine()
	{}

	@Override
	public void unassign(String variable)
	{
		instanceReco.deconditionne(variable);
	}

	@Override
	public void terminePli()
	{}

}
