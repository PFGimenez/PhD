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

import java.util.Random;

import compilateurHistorique.Variable;

/**
 * Une contrainte
 * @author Pierre-François Gimenez
 *
 */

public class Constraint
{
	public Variable var1, var2;
	public boolean[][] allowedValues;
	private static Random r = new Random();
	public int nbAllowed;
	/**
	 * Génère une contrainte aléatoire entre deux variables respectant une certaine dureté
	 * @param var1
	 * @param var2
	 * @param durete
	 */
	public Constraint(Variable var1, Variable var2, double durete)
	{
		this.var1 = var1;
		this.var2 = var2;
		int objectif = (int) (var1.domain * var2.domain * durete + .5);
		
		allowedValues = new boolean[var1.domain][var2.domain];
		for(int i = 0; i < var1.domain; i++)
			for(int j = 0; j < var2.domain; j++)
			allowedValues[i][j] = true; // tout est autorisé par défaut
		
		int nbValInterdite = 0;
		while(nbValInterdite < objectif)
		{
			int val1 = r.nextInt(var1.domain);
			int val2 = r.nextInt(var2.domain);
			if(allowedValues[val1][val2])
			{
				allowedValues[val1][val2] = false;
				nbValInterdite++;
			}
		}
		
		nbAllowed = var1.domain * var2.domain - nbValInterdite;
	}
	
}
