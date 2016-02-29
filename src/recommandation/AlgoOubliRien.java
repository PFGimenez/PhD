package recommandation;

import java.util.ArrayList;
import java.util.HashMap;

import compilateurHistorique.HistoComp;
import compilateurHistorique.Instanciation;
import graphOperation.DSeparation;
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
 * Algorithme de recommandation qui n'oublie rien
 * @author pgimenez
 *
 */

public class AlgoOubliRien implements AlgoReco
{
	private HistoComp historique;
	private DSeparation dsep;
	private Instanciation instanceReco;
	
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
		
//		for(int i = 0; i<lect.nbvar; i++)
//			System.out.println("Var : "+lect.var[i]);
		
//		System.out.println("Nb var: "+lect.nbvar);
		
		historique.compile(filename, entete);

		System.out.println("Compilation de l'historique finie : "+historique.getNbNoeuds()+" nœuds");
			
		String dataset = filename.get(0).substring(0, 1+filename.get(0).lastIndexOf("/"));
		dsep = new DSeparation(dataset, nbIter);
		instanceReco = new Instanciation();
	}
	
	@Override
	public String recommande(String variable, ArrayList<String> possibles)
	{
		ArrayList<String> requisite = dsep.getRequisiteObservation(historique.getVarConnues(instanceReco), variable);
		Instanciation sub = instanceReco.subInstanciation(requisite);
		HashMap<String, Double> proba3 = historique.getProbaToutesModalitees(variable, possibles, false, sub);
		
		double probaMax3 = 0;
		String valueMax3 = null;
		for(String value : proba3.keySet())
		{
//			System.out.println(value+": "+proba3.get(value));
			
			double probaTmp = proba3.get(value);
			if(probaTmp >= probaMax3)
			{
				probaMax3 = probaTmp;
				valueMax3 = value;
			}
		}
		
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
		return getClass().getSimpleName();
	}
	
	@Override
	public void termine()
	{}
	
	public void initHistorique(ArrayList<String> filename, boolean entete)
	{
		historique = new HistoComp(filename, entete);
	}

}
