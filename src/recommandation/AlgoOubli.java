package recommandation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import compilateurHistorique.HistoComp;
import compilateur.LecteurCdXml;
import compilateur.SALADD;
import compilateur.Var;

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
	private SALADD contraintes;
	
	public AlgoOubli()
	{}
	
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
	{
		this.contraintes = contraintes;
	}

	@Override
	public void apprendDonnees(ArrayList<String> filename, int nbIter) {
		System.out.println("Apprentissage de ");
		for(int i = 0; i < filename.size(); i++)
		{
			String s = filename.get(i);
			System.out.println("	"+s+".csv");
		}
		
		// Contraintes contient des variables supplémentaire
		LecteurCdXml lect = new LecteurCdXml();
		lect.lectureCSV(filename.get(0));
		ArrayList<String> varDispo = new ArrayList<String>();
		for(int i = 0; i < lect.nbvar; i++)
			varDispo.add(lect.var[i]);
		
//		for(int i = 0; i<lect.nbvar; i++)
//			System.out.println("Var : "+lect.var[i]);
		
//		System.out.println("Nb var: "+lect.nbvar);
		
		int i = 0;
		if(contraintes != null)
		{
			Var[] ordreVar = new Var[lect.nbvar];
			for(Var v : contraintes.getOrd().getVariables())
				if(varDispo.contains(v.name))
				{
					ordreVar[i++] = v;
	//				System.out.println(v.pos);
				}
			historique = new HistoComp(ordreVar);
		}
		else
		{
			String[] ordreVar = new String[lect.nbvar];
			varDispo.toArray(ordreVar);
			historique = new HistoComp(ordreVar);
		}
		

		historique.compileHistorique(filename);
		System.out.println("Compilation de l'historique finie");
	}

	@Override
	public String recommande(String variable, ArrayList<String> possibles)
	{
		HashMap<String, Integer> proba = historique.getNbInstancesToutesModalitees(variable, possibles);
		
		int probaMax = 0;
		String valueMax = null;
		for(String value : proba.keySet())
		{
			System.out.println(value+": "+proba.get(value));
			int probaTmp = proba.get(value);
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
		return "compilation_arbre";
	}
	
	@Override
	public void termine()
	{}

}
