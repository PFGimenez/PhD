package recommandation;

import java.util.ArrayList;

import compilateurHistorique.MultiHistoComp;
import compilateurHistorique.Instanciation;
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
 * Algorithme de recommandation basé sur un classifieur bayésien naïf
 * @author pgimenez
 *
 */

public class AlgoRBNaif implements AlgoReco
{
	private MultiHistoComp historique;
	private ArrayList<String> variables;
	private Instanciation instanceReco;
	private ArrayList<String> filenameInit;

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

		historique = new MultiHistoComp(filenameInit, entete, variables);
		historique.compile(filename, entete);
		historique.apprendPrecalcul();
		instanceReco = new Instanciation();
	}
	
	@Override
	public String recommande(String variable, ArrayList<String> possibles)
	{
		ArrayList<String> valeurs = historique.getValues(variable);

		double probaMax = 0;
		String valueMax = null;
		for(String value : valeurs)
		{
			double probaTmp;// = historique.getNbInstancesAPriori(variable, value);
			probaTmp = historique.getProbaRBNaif(instanceReco, variable, value);
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
	
	public void initHistorique(ArrayList<String> filename, boolean entete)
	{
		filenameInit = new ArrayList<String>();
		filenameInit.addAll(filename);
	}
	
}
