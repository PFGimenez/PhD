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
 * Algorithme DRC
 * @author Pierre-François Gimenez
 *
 */

public class AlgoDRC extends AlgoRecoRB
{
	private HistoriqueCompile historique;
	private Instanciation instanceReco;
	private int seuil;
	private String RBfile;
	private ArbreDecompTernaire decomp;
	private InferenceDRC inferer;
	private int equivalentSampleSize;
	
	public AlgoDRC()
	{
		this(50, 10);
	}
	
	public AlgoDRC(ParserProcess pp)
	{
		this();
	}
	
	public AlgoDRC(int seuil, int equivalentSampleSize)
	{
		super("hc");
		this.seuil = seuil;
		this.equivalentSampleSize = equivalentSampleSize;
	}
	
	public void describe()
	{
		System.out.println("DRC");
		System.out.println("seuil = "+seuil);
		System.out.println("equivalentSampleSize = "+equivalentSampleSize);
	}
	
	public void apprendDonnees(DatasetInfo dataset, Instanciation[] instances, int code)
	{
		// apprentissage du RB
		learnBN(dataset, instances, code);
		this.dataset = dataset;
		historique = new HistoriqueCompile(dataset);
		historique.compile(instances);
		decomp = new ArbreDecompTernaire(dataset, new DAG(RBfile), dataset.mapVar, historique, false);
		inferer = new InferenceDRC(seuil, decomp, dataset, historique, equivalentSampleSize, false, false);
//		decomp.prune(readInstances(filename, entete, -1), new BIC(), inferer);
		instanceReco = new Instanciation(dataset);
		(new DAG(RBfile)).printGraphe("RB bug");
		decomp.printGraphe("arbre décomp bug");

	}
	
/*	@Override
	public void apprendDonnees(DatasetInfo dataset, ArrayList<String> filename, int nbIter, boolean entete)
	{
		this.dataset = dataset;
		historique = new HistoriqueCompile(dataset);
		historique.compile(filename, entete);
		decomp = new ArbreDecompTernaire(dataset, new DAG(RBfile), dataset.mapVar, historique, false);
		inferer = new InferenceDRC(seuil, decomp, dataset, historique, equivalentSampleSize, false, false);
//		decomp.prune(readInstances(filename, entete, -1), new BIC(), inferer);
		instanceReco = new Instanciation(dataset);
		(new DAG(RBfile)).printGraphe("RB bug");
		decomp.printGraphe("arbre décomp bug");
	}*/
	
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
			double p = inferer.infere(instanceReco, U);
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
	
	@Override
	public void unassign(String variable)
	{
		instanceReco.deconditionne(variable);
	}

	@Override
	public void apprendRB(String file)
	{
		RBfile = file;
	}

	@Override
	public void terminePli()
	{}
	


}
