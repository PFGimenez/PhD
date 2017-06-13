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
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Un nœud de l'arbre de décomposition ternaire utilisé par DRC
 * @author Pierre-François Gimenez
 *
 */

public class NodeArbreDecompTernaire
{
	public NodeArbreDecompTernaire fils0 = null, fils1 = null, filsC = null;
	public Partition partition = null;
	public static int nb = 0;
	private static HashMap<Set<String>, NodeArbreDecompTernaire> nodes = new HashMap<Set<String>, NodeArbreDecompTernaire>();
	
/*	public NodeArbreDecompTernaire()
	{
		nb++;
//		System.out.println(nb++);
	}*/
	
	public NodeArbreDecompTernaire(DAG dag, Set<String> instanciees, Map<String, Integer> mapvar, boolean verbose, MoralGraph parent, int profondeur)
	{
		nb++;
		nodes.put(instanciees, this);
/*		nbAtteints[profondeur]++;
		int i = 0;
		while(nbAtteints[i] != 0)
		{
			System.out.print(i+" : "+nbAtteints[i]+", ");
			i++;
		}
		System.out.println();*/
//		System.out.println(nb++);
		MoralGraph gm = new MoralGraph(dag, instanciees, verbose);
		if(instanciees.size() > 0)
		{
			gm.computeDijkstra();
			
			assert parent == null || gm.diminution(parent);
//			if(parent != null && !gm.diminution(parent))
//				throw new NotAdmissibleException();
			
			// si c'est décomposable
			if(gm.getDistanceMax() > 1)
			{
//				System.out.println("Distance max : "+gm.getDistanceMax());
				partition = gm.computeSeparator();
				partition.updateTab(mapvar);
				Set<String> g0c = new HashSet<String>();
				g0c.addAll(partition.ensembles[0]);
				g0c.addAll(partition.separateur);
				Set<String> g1c = new HashSet<String>();
				g1c.addAll(partition.ensembles[1]);
				g1c.addAll(partition.separateur);

				fils0 = nodes.get(g0c);
				if(fils0 == null)
					fils0 = new NodeArbreDecompTernaire(dag, g0c, mapvar, verbose, gm, profondeur+1);
				
				fils1 = nodes.get(g1c);
				if(fils1 == null)
					fils1 = new NodeArbreDecompTernaire(dag, g1c, mapvar, verbose, gm, profondeur+1);
				
				filsC = nodes.get(partition.separateur);
				if(filsC == null)
					filsC = new NodeArbreDecompTernaire(dag, partition.separateur, mapvar, verbose, gm, profondeur+1);
			}
//			else
//				System.out.println("Feuille");
		}
		else
		{
//			System.out.println("Graphe vide");
/*			fils0 = new NodeArbreDecompTernaire();
			fils1 = new NodeArbreDecompTernaire();
			filsC = new NodeArbreDecompTernaire();*/
		}
	}

}
