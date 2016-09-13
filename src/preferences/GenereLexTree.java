package preferences;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import preferences.completeTree.LexicographicTree;

/*   (C) Copyright 2015, Gimenez Pierre-François 
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
 * Génération d'un arbre lexicographique
 * @author pgimenez
 *
 */

public class GenereLexTree
{
	
	/**
	 * Génération d'un arbre avec un certaine profondeur
	 * @param nbVar
	 */
	public static LexicographicTree genere(int nbVar)
	{
		ArrayList<String> variables;
		variables = new ArrayList<String>();
		for(int i = 0; i < nbVar; i++)
			variables.add("V"+i);
		LexicographicTree out = genereRecursif(variables);
		out.updateBase(BigInteger.valueOf(2).pow(nbVar));
		return out;
	}

	private static LexicographicTree genereRecursif(ArrayList<String> variablesRestantes)
	{
		Random random = new Random();
		LexicographicTree best = null;
	
		ArrayList<String> variablesTmp = new ArrayList<String>();
		variablesTmp.addAll(variablesRestantes);
		
		Map<String, Integer> nbEx = new HashMap<String, Integer>();
		nbEx.put("0", random.nextInt(1000));
		nbEx.put("1", random.nextInt(1000));
		best = new LexicographicTree(variablesTmp.get(random.nextInt(variablesTmp.size())), 2, true);
		best.setOrdrePref(nbEx);

		// Si c'était la dernière variable, alors c'est une feuille
		if(variablesTmp.size() == 1)
			return best;

		variablesTmp.remove(best.getVar());
		int nbMod = best.getNbMod();
		for(int i = 0; i < nbMod; i++)
			best.setEnfant(i, genereRecursif(variablesTmp));

		// A la fin, le VDD est conditionné de la même manière qu'à l'appel
		return best;
	}
	
}
