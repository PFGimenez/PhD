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

import compilateurHistorique.DatasetInfo;
import compilateurHistorique.HistoriqueCompile;

/**
 * Un nœud de l'arbre de décomposition ternaire utilisé par DRC
 * @author Pierre-François Gimenez
 *
 */

public class NodeArbreDecompTernaire
{
	public NodeArbreDecompTernaire fils0 = null, fils1 = null, filsC = null;
	public NodeArbreDecompTernaire savefils0 = null, savefils1 = null, savefilsC = null;
	public Partition partition = null, savepartition = null;
	public static int nb = 0;
	public final int nbNode;
	public int domaine = 1;
	
	public String toString()
	{
		if(partition == null)
			return "Feuille";
		return Integer.toString(partition.ensembles[0].size() + partition.ensembles[1].size() + partition.separateur.size());
	}
	
	public boolean isLeaf()
	{
		return fils0 == null && fils1 == null && filsC == null;
	}
	
	public void makeLeaf()
	{
		savefils0 = fils0;
		savefils1 = fils1;
		savefilsC = filsC;
		savepartition = partition;
		fils0 = null;
		fils1 = null;
		filsC = null;
		partition = null;
	}
	
	public void unmakeLeaf()
	{
		fils0 = savefils0;
		fils1 = savefils1;
		filsC = savefilsC;	
		partition = savepartition;
	}
	
	public NodeArbreDecompTernaire(DatasetInfo dataset, DAG dag, Set<String> instanciees, Map<String, Integer> mapvar, HistoriqueCompile historique, boolean verbose, MoralGraph parent, int profondeur, HashMap<Set<String>, NodeArbreDecompTernaire> nodes)
	{
		nbNode = nb++;
		nodes.put(instanciees, this);
		MoralGraph gm = new MoralGraph(dag, instanciees, verbose);
		if(instanciees.size() > 0)
		{
			gm.computeDijkstra();
			
			assert parent == null || gm.diminution(parent);
			
			for(String v : instanciees)
				domaine *= dataset.vars[mapvar.get(v)].domain;
			
			// si c'est décomposable
			if(gm.getDistanceMax() > 1)// && domaine > 6)
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
					fils0 = new NodeArbreDecompTernaire(dataset, dag, g0c, mapvar, historique, verbose, gm, profondeur+1, nodes);
				
				fils1 = nodes.get(g1c);
				if(fils1 == null)
					fils1 = new NodeArbreDecompTernaire(dataset, dag, g1c, mapvar, historique, verbose, gm, profondeur+1, nodes);
				
				filsC = nodes.get(partition.separateur);
				if(filsC == null)
					filsC = new NodeArbreDecompTernaire(dataset, dag, partition.separateur, mapvar, historique, verbose, gm, profondeur+1, nodes);
			}
//			else
//				System.out.println("Feuille");
		}
	}

}
