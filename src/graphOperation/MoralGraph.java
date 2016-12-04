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

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.Stack;

import compilateurHistorique.Variable;

/**
 * Un graphe moral
 * @author Pierre-François Gimenez
 *
 */

public class MoralGraph
{
	private static final int parents = 0;
	private static final int enfants = 1;
	private Map<String, Set<String>> graphe;
	private String feuille;
	private List<Variable> variablesInstanciees;

	public MoralGraph(DAG dag, List<Variable> variablesInstanciees)
	{
		this.variablesInstanciees = variablesInstanciees;
		graphe = new HashMap<String, Set<String>>();
		
		Stack<String> pile = new Stack<String>();
		
		// on ne conserve que les ancêtres de variables
		for(Variable v : variablesInstanciees)
			pile.push(v.name);
		
		while(!pile.isEmpty())
		{
			String n = pile.pop();
			if(graphe.containsKey(n))
				continue;
			
			Set<String> voisins = new HashSet<String>();
			for(String s : dag.dag[parents].get(n)) // on ajoute tous les parents comme voisins…
			{
				voisins.add(s);
				pile.add(s); // il faudra les ajouter aussi au graphe
			}
			for(String s : dag.dag[enfants].get(n)) // et tous les enfants
				voisins.add(s);
			graphe.put(n, voisins); // on stocke les voisins de n
		}

		// on cherche une feuille (feuille = autant de voisins que de parents
		for(String n : graphe.keySet())
		{
			if(graphe.get(n).size() == dag.dag[parents].get(n).size())
			{
				feuille = n;
				break; // la première trouvée suffira
			}
		}
		
		// on marie les parents
		for(String n : graphe.keySet())
		{
			List<String> listeParents = dag.dag[parents].get(n);
			for(String p1 : listeParents)
				for(String p2 : listeParents)
				{
					if(p1.equals(p2))
						continue; // on ne veut pas être son propre voisin
					graphe.get(p1).add(p2);
					graphe.get(p2).add(p1);
				}
		}
	}
	
	/**
	 * Exporte le graphe sous format .dot
	 * @param filename
	 */
	public void printGraphe(String filename)
	{
		try {
			
			FileWriter fichier;
			BufferedWriter output;
	
			fichier = new FileWriter(filename+".dot");
			output = new BufferedWriter(fichier);
			output.write("graph G { ");
			output.newLine();
			output.write("ordering=out;");			
			output.newLine();
			
			for(String n : graphe.keySet())
			{
				output.write(n+" [label="+n+"];");
				output.newLine();
				for(String v : graphe.get(n))
				{
					output.write(n+" -- "+v+";");
					output.newLine();
				}
			}
			output.write("}");
			output.newLine();
			output.close();
		} catch (IOException e) {
			e.printStackTrace();
		}		
	}
	
	private HashMap<String, NodeDijkstra> nodes = new HashMap<String, NodeDijkstra>();
	private int distanceMax = 0;
	
	private class NodeDijkstraComparator implements Comparator<NodeDijkstra>
	{
		@Override
		public int compare(NodeDijkstra arg0, NodeDijkstra arg1)
		{
			return arg0.g - arg1.g;
		}
	}	
	
	/**
	 * Calcule Dijkstra pour la feuille trouvée durant le constructeur
	 */
	public void computeDijkstra()
	{
		PriorityQueue<NodeDijkstra> openset = new PriorityQueue<NodeDijkstra>(100, new NodeDijkstraComparator());

		for(String s : graphe.keySet())
			nodes.put(s, new NodeDijkstra(s));
		
		NodeDijkstra depart = nodes.get(feuille);
		depart.g = 0;
		openset.add(depart);
		
		while(!openset.isEmpty())
		{
			NodeDijkstra n = openset.poll();
			if(n.visited) // déjà traité (il y a des doublons dans l'openset)
				continue;
			
			n.visited = true;
			for(String v : graphe.get(n))
			{
				NodeDijkstra voisin = nodes.get(v);
				if(voisin.visited) // déjà traité
					continue;
				
				if(n.g + 1 < voisin.g)
				{
					voisin.g = n.g + 1; // mise à jour de la distance
					if(voisin.g > distanceMax && variablesInstanciees.contains(voisin.var))
						distanceMax = voisin.g;
					openset.add(voisin);
				}
			}
		}
	}
	
	public int getDistance(Variable v)
	{
		return nodes.get(v.name).g;
	}
	
	/**
	 * Récupère tous les nœuds les plus éloignés de la feuille
	 * @return
	 */
	public List<String> getZ()
	{
		List<String> out = new ArrayList<String>();
		for(String v : graphe.keySet())
			if(nodes.get(v).g == distanceMax && variablesInstanciees.contains(v))
				out.add(v);
		return out;
	}
	
	/**
	 * Partitionne l'espace en V1 et V2
	 * @return
	 */
	public Partition getV1V2()
	{
		Partition out = new Partition(2);
		for(String v : graphe.keySet())
			out.ensembles[nodes.get(v).g <= distanceMax/2 ? 0 : 1].add(v);
		return out;
	}
	
}
