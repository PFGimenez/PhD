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

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.Stack;

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
	private List<String> variables = new ArrayList<String>();
	private List<String> arcs = new ArrayList<String>();
	private String feuille;
	private Partition partition = null;
	private Set<String> variablesInstanciees;

	public MoralGraph(DAG dag, Set<String> variablesInstanciees)
	{
		this.variablesInstanciees = variablesInstanciees;
		graphe = new HashMap<String, Set<String>>();
		
		Stack<String> pile = new Stack<String>();
		
		// on ne conserve que les ancêtres de variables
		for(String v : variablesInstanciees)
			pile.push(v);
		
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
			variables.add(n);
		}

		// on cherche une feuille (feuille = autant de voisins que de parents
		for(String n : graphe.keySet())
		{
			if(graphe.get(n).size() == dag.dag[parents].get(n).size())
			{
				feuille = n;
				System.out.println("Feuille : "+n);
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
		Set<String> done = new HashSet<String>(); // on ne veut pas afficher les arcs en double (A -- B et B -- A)
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
				if(partition != null) // on colore les partitions si elles sont déjà calculées
					output.write(n+" [fillcolor="+(partition.ensembles[0].contains(n) ? "red" : partition.ensembles[1].contains(n) ? "blue" : "green")+", label="+n+"];");
				else
					output.write(n+" [label=\""+n+" ("+(nodes.get(n) == null ? "" : nodes.get(n).g)+")\"];");
				output.newLine();
				for(String v : graphe.get(n))
				{
					if(done.contains(v))
						continue;
					output.write(n+" -- "+v+";");
					output.newLine();
				}
				done.add(n);
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
	private boolean dijkstraDone = false;
	
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
	private void computeDijkstra()
	{
		if(!dijkstraDone)
			dijkstraDone = true;
		else
			return;
		
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
			for(String v : graphe.get(n.var))
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
		System.out.println("Distance max = "+distanceMax);
	}
	
	/**
	 * Récupère la distance d'un sommet à la feuille
	 * @param v
	 * @return
	 */
	public int getDistance(String v)
	{
		return nodes.get(v).g;
	}
	
	/**
	 * Récupère tous les nœuds les plus éloignés de la feuille
	 * @return
	 */
	private List<String> getZ()
	{
		computeDijkstra();
		List<String> out = new ArrayList<String>();
		System.out.print("Z : ");
		for(String v : graphe.keySet())
			if(nodes.get(v).g == distanceMax && variablesInstanciees.contains(v))
			{
				System.out.print(v+" ");
				out.add(v);
			}
		System.out.println();
		return out;
	}
	
	public Partition computeSeparator()
	{
		try {
			FileWriter fichier;
			BufferedWriter output;
	
			fichier = new FileWriter("/tmp/hg");
			output = new BufferedWriter(fichier);
			
			// on transforme de problème de séparateur en un problème de partitionnement d'un hypergraphe
			for(String s : graphe.keySet())
			{
				for(String v : graphe.get(s))
					arcs.add(s.compareTo(v) < 0 ? s+v : v+s);
			}
			output.write(graphe.size()+" "+arcs.size()+" 1");
			List<String> Z = getZ();
			for(String s : graphe.keySet())
			{
				output.newLine();
				int poids = 1;
//				if(variablesInstanciees.contains(s))
//					poids = 0;
				if(s.equals(feuille) || Z.contains(s))
					poids = 1000;
				output.write(Integer.toString(poids));
				for(String v : graphe.get(s))
					output.write(" "+(arcs.indexOf(s.compareTo(v) < 0 ? s+v : v+s)+1));
			}
			output.close();
			
			// appel à hmetis : décomposition de l'hypergraphe
			Process proc = Runtime.getRuntime().exec("lib/hmetis-1.5-linux/shmetis /tmp/hg 2 1");
			BufferedReader input = new BufferedReader(new InputStreamReader(proc.getInputStream()));
			BufferedReader error = new BufferedReader(new InputStreamReader(proc.getErrorStream()));
            while ((input.readLine()) != null) {}
            while ((error.readLine()) != null) {}
            proc.waitFor();
            
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream("/tmp/hg.part.2")));
            String line = br.readLine();

            while(line != null)
            {
            	int partition = Integer.parseInt(line);
            	

				line = br.readLine();
            }
            br.close();
//            (new File("/tmp/hg.part.2")).delete();
            
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		}
		return null;
		
	}
	
}
