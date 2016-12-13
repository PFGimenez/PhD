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

import java.util.Map;

import compilateurHistorique.EnsembleVariables;

/**
 * Un arbre de décomposition ternaire utilisé par DRC
 * @author Pierre-François Gimenez
 *
 */

public class ArbreDecompTernaire
{
	public NodeArbreDecompTernaire racine;
	private boolean verbose;
	
	public ArbreDecompTernaire(DAG dag, Map<String, Integer> mapvar, boolean verbose)
	{
		this.verbose = verbose;
		racine = new NodeArbreDecompTernaire(dag, dag.dag[0].keySet(), mapvar);
	}

	public NodeArbreDecompTernaire getNode(EnsembleVariables U)
	{
		if(verbose)
			System.out.println("Recherche du noeud de décomposition pour "+U);
		return getNodeRecursif(U, racine);
	}

	private NodeArbreDecompTernaire getNodeRecursif(EnsembleVariables U, NodeArbreDecompTernaire n)
	{
		// le calcul n'est pas décomposé
		if(n.partition == null)
			return n;
		
		boolean dansG0 = false, dansG1 = false;
		for(int i = 0; i < U.vars.length; i++)
		{
			int nb = U.vars[i];
			if(!dansG0)
				for(int j = 0; j < n.partition.g0Tab.length; j++)
					if(n.partition.g0Tab[j] == nb)
					{
						dansG0 = true;
						break;
					}
			
			if(!dansG1)
				for(int j = 0; j < n.partition.g1Tab.length; j++)
					if(n.partition.g1Tab[j] == nb)
					{
						dansG1 = true;
						break;
					}
		}
		
		if(!dansG0)
		{
			if(!dansG1)
				return getNodeRecursif(U, n.filsC); // ni dans G0, ni dans G1
			else
				return getNodeRecursif(U, n.fils1); // seulement dans G1
		}
		else
		{
			if(!dansG1)
				return getNodeRecursif(U, n.fils0); // seulement dans G0
			else
				return n; // dans G0 et G1
		}
	}

}
