package preferences;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Random;

import compilateurHistorique.Variable;
import preferences.completeTree.LexicographicStructure;
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
	private static Random random = new Random();	
	
	/**
	 * Génération d'un arbre avec un certaine profondeur
	 * @param nbVar
	 */
	public static LexicographicTree genere(Variable[] vars, double coeffSplit)
	{
		BigInteger rangMax = BigInteger.ONE;
		for(int i = 0; i < vars.length; i++)
			rangMax = rangMax.multiply(BigInteger.valueOf(vars[i].domain));
		
		ArrayList<Variable> varsl = new ArrayList<Variable>();
		for(int i = 0; i < vars.length; i++)
			varsl.add(vars[i]);
		
		LexicographicTree out = genereRecursif(varsl, coeffSplit);
		out.updateBase(rangMax);
		return out;
	}

	private static LexicographicTree genereRecursif(ArrayList<Variable> variablesRestantes, double coeffSplit)
	{
		LexicographicTree best = null;
	
		ArrayList<Variable> variablesTmp = new ArrayList<Variable>();
		variablesTmp.addAll(variablesRestantes);
		
		Variable top = variablesTmp.get(random.nextInt(variablesTmp.size()));
		
		variablesTmp.remove(top);
		int nbMod = top.domain;
		
		if(random.nextDouble() < coeffSplit)
		{
			best = new LexicographicTree(top.name, top.domain, true);
			best.setOrdrePrefRandom();

			// Si c'était la dernière variable, alors c'est une feuille
			if(variablesTmp.size() == 0)
				return best;

			// on split
			for(int i = 0; i < nbMod; i++)
				best.setEnfant(i, genereRecursif(variablesTmp, coeffSplit));			
		}
		else
		{
//			System.out.println("Pas de split !");
			best = new LexicographicTree(top.name, top.domain, false);
			best.setOrdrePrefRandom();

			// Si c'était la dernière variable, alors c'est une feuille
			if(variablesTmp.size() == 0)
				return best;
			
			// on ne split pas
			LexicographicStructure e = genereRecursif(variablesTmp, coeffSplit);
			for(int i = 0; i < nbMod; i++)
				best.setEnfant(i, e);
		}
		

		// A la fin, le VDD est conditionné de la même manière qu'à l'appel
		return best;
	}
	
}
