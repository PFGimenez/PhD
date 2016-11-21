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

package contraintes;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import compilateurHistorique.Variable;

/**
 * Une contrainte entre n variables
 * @author Pierre-François Gimenez
 *
 */

public class Constraint implements AbstractConstraint
{
	public Variable[] vars;
	private static Random r = new Random();
	private int indexRecherche;
	private int nbTrouve;
	private int nbAllowed;
	private Set<Integer> setForbiddenValues = new HashSet<Integer>();
	
	/**
	 * Génère une contrainte aléatoire entre deux variables respectant une certaine dureté
	 * @param var1
	 * @param var2
	 * @param durete
	 */
	public Constraint(double durete, Variable[] vars)
	{
		this.vars = vars;
		if(vars.length < 2)
			System.err.println("Erreur lors de la construction d'une constrainte !");

		int nbValues = 1;
		for(Variable v : vars)
			nbValues *= v.domain;
		
		int objectif = (int) (nbValues * durete + .5);
		nbAllowed = nbValues - objectif;
		
		int nbValInterdite = 0;
		while(nbValInterdite < objectif)
		{
			Integer[] vector = new Integer[vars.length];
			int hash = 0;
			for(int i = 0; i < vector.length; i++)
			{
				vector[i] = r.nextInt(vars[i].domain);
				hash *= vars[i].domain;
				hash += vector[i];
			}

			if(setForbiddenValues.add(hash))
				nbValInterdite++;
//			System.out.println("	"+nbValInterdite+" / "+objectif);
		}
	}

	@Override
	public boolean hasNext()
	{
		return nbTrouve < nbAllowed;
	}

	@Override
	public String next()
	{
		while(setForbiddenValues.contains(indexRecherche))
			indexRecherche++;

		String out = "";
		int tmp = indexRecherche;
		for(int i = vars.length - 1; i >= 0; i--)
		{
			out += " "+(tmp % vars[i].domain);
			tmp /= vars[i].domain;
		}
		indexRecherche++;
		nbTrouve++;
		return out;
	}

	@Override
	public void reinitIterator()
	{
		indexRecherche = 0;
		nbTrouve = 0;
	}
	
	@Override
	public String getScope()
	{
		String out = "";
		for(Variable v : vars)
			out += v.name+" ";
		return out;
	}

	@Override
	public int getNbVariables()
	{
		return vars.length;
	}

	@Override
	public int getNbAllowed()
	{
		return nbAllowed;
	}

	@Override
	public void remove()
	{
		throw new UnsupportedOperationException();
	}
}
