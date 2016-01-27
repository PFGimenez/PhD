package recommandation;

import java.util.ArrayList;
import java.util.HashMap;

import compilateurHistorique.HistoComp;
import graphOperation.DSeparation;
import graphOperation.DTreeGenerator;
import graphOperation.Graphe;
import compilateur.LecteurCdXml;
import compilateur.SALADD;

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
 * Algorithme de recommandation avec arbre utilisant une méthode d'oubli
 * @author pgimenez
 *
 */

public class AlgoOubli implements AlgoReco
{
	private HistoComp historique;
	private DSeparation dsep;
	private DTreeGenerator dtreegenerator;
	private ArrayList<String> variables;
	
	public AlgoOubli(int seuil)
	{
		Graphe.setSeuil(seuil);
	}
	
	public void charge(String s)
	{
		historique = HistoComp.load(s);
	}
	
	public void save(String s)
	{
		historique.save(s);
	}
	
	@Override
	public void apprendContraintes(SALADD contraintes)
	{}
	
	@Override
	public void apprendDonnees(ArrayList<String> filename, int nbIter, boolean entete) {
		System.out.println("Apprentissage de ");
		for(int i = 0; i < filename.size(); i++)
		{
			String s = filename.get(i);
			System.out.println("	"+s+".csv");
		}
		
		// Contraintes contient des variables supplémentaire
		LecteurCdXml lect = new LecteurCdXml();
		lect.lectureCSV(filename.get(0), entete);
		
		variables = new ArrayList<String>();
		for(int i = 0; i < lect.nbvar; i++)
			variables.add(lect.var[i]);
		
//		for(int i = 0; i<lect.nbvar; i++)
//			System.out.println("Var : "+lect.var[i]);
		
//		System.out.println("Nb var: "+lect.nbvar);
		
		historique = new HistoComp(filename, entete);

		System.out.println("Compilation de l'historique finie : "+historique.getNbNoeuds()+" nœuds");
		
		String dataset = filename.get(0).substring(0, 1+filename.get(0).lastIndexOf("/"));
		dsep = new DSeparation(dataset, nbIter);
		dtreegenerator = new DTreeGenerator(dataset, nbIter);
	}
	
	@Override
	public String recommande(String variable, ArrayList<String> possibles)
	{
		ArrayList<String> requisite = dsep.getRequisiteObservation(historique.getVarConnues(), variable);

		Graphe g = new Graphe(requisite, historique, dtreegenerator);
		HashMap<String, Double> proba = new HashMap<String, Double>();
		
		for(String s : possibles)
		{
			historique.conditionne(variable, s);
			proba.put(s, g.computeProba(historique.getCurrentState()));
			historique.deconditionne(variable);
		}
		
		double probaMax = 0;
		String valueMax = null;
		for(String value : proba.keySet())
		{
//			System.out.println(value+": "+proba.get(value));
			double probaTmp = proba.get(value);
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
		historique.conditionne(variable, solution);
	}

	@Override
	public void oublieSession()
	{
		historique.deconditionneTout();
	}
	
	public String toString()
	{
		return "compilation_VDD";
	}
	
	@Override
	public void termine()
	{}

}
