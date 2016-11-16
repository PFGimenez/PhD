package recommandation;

import java.util.ArrayList;
import java.util.HashMap;

import preferences.completeTree.ApprentissageGloutonLexStructure;
import preferences.completeTree.LexicographicStructure;

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

// Recommandation par apprentissage de préférences

public class AlgoLexTree implements AlgoReco {

	private ApprentissageGloutonLexStructure algo;
	private LexicographicStructure struct;
	private HashMap<String, String> valeurs;
//	private String dataset;
	
	public AlgoLexTree(ApprentissageGloutonLexStructure algo, String dataset)
	{
		this.algo = algo;
//		this.dataset = dataset;
		valeurs = new HashMap<String, String>();
	}
	
/*	@Override
	public void apprendContraintes(SALADD contraintes)
	{}*/
	
	@Override
	public void apprendDonnees(ArrayList<String> filename, int nbIter, boolean entete)
	{
//		System.out.println(dataset+algo.toString()+"-"+nbIter);
		// Tout est déjà calculé
//		if(!algo.load(dataset+algo.toString()+"-"+nbIter))
//		{
		struct = algo.apprendDonnees(filename, entete);
		struct.affiche(algo.getHeuristiqueName());
//			algo.save(dataset+algo.toString()+"-"+nbIter);
//		}
	}

	@Override
	public String recommande(String variable, ArrayList<String> possibles)
	{
		return struct.infereBest(variable, possibles, valeurs);
	}

	@Override
	public void setSolution(String variable, String solution)
	{
		valeurs.put(variable, solution);
	}

	@Override
	public void oublieSession()
	{
		valeurs.clear();
	}

	@Override
	public void termine()
	{}

	public String toString()
	{
		return getClass().getSimpleName();
	}

	public void initHistorique(ArrayList<String> filename, boolean entete)
	{
		algo.apprendDomainesVariables(filename, entete);
	}

	@Override
	public void unassign(String variable)
	{
		valeurs.remove(variable);
	}
}