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

package graphOperation;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Un arbre de décomposition ternaire utilisé par DRC
 * @author Pierre-François Gimenez
 *
 */

public class ArbreDecompTernaire
{
	public NodeArbreDecompTernaire racine;
	public HashMap<Set<String>, NodeArbreDecompTernaire> allNodes = new HashMap<Set<String>, NodeArbreDecompTernaire>();
	
	public ArbreDecompTernaire(DAG dag, Map<String, Integer> mapvar, boolean verbose)
	{
		racine = new NodeArbreDecompTernaire(dag, dag.dag[0].keySet(), mapvar, verbose, null, 0, allNodes);
		System.out.println("Arbre de décomposition ternaire : "+NodeArbreDecompTernaire.nb+" nœuds");
	}
}
