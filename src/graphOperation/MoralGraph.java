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
import java.util.Iterator;
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
	private String feuille;
	private Partition partition = null;
	private Set<String> variablesInstanciees;
	private boolean verbose;

	public MoralGraph(DAG dag, Set<String> variablesInstanciees, boolean verbose)
	{
		this.verbose = verbose;
		if(verbose)
			System.out.println("Variables instanciées : "+variablesInstanciees);
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
		}

		// on ne garde que les voisins qui sont dans le graphe (sinon un fils en fait pruné pourrait être un voisin…)
		for(String n : graphe.keySet())
		{
			Iterator<String> iter = graphe.get(n).iterator();
			while(iter.hasNext())
				if(!graphe.containsKey(iter.next()))
					iter.remove();
		}		
		
		// on cherche une feuille (feuille = autant de voisins que de parents
		for(String n : graphe.keySet())
		{
			if(graphe.get(n).size() == dag.dag[parents].get(n).size())
			{
				feuille = n;
				if(verbose)
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
				output.write(n+" [label=\""+n+" "+(nodes.get(n) == null ? "" : "("+(nodes.get(n).g == Integer.MAX_VALUE ? "infini" : nodes.get(n).g)+")")+"\"");
				if(partition != null) // on colore les partitions si elles sont déjà calculées
					output.write(", fillcolor="+(partition.ensembles[0].contains(n) ? "chartreuse3" : partition.ensembles[1].contains(n) ? "firebrick2" : "white")+", style=filled");
				output.write("];");

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
	public void computeDijkstra()
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
		
		// si des nœuds ne sont pas atteints, la distance maximale est l'infini
		for(String s : graphe.keySet())
			if(!nodes.get(s).visited)
			{
				distanceMax = Integer.MAX_VALUE;
				break;
			}
	}
	
	/**
	 * Récupère la distance entre la feuille et Z
	 * @return
	 */
	public int getDistanceMax()
	{
		return distanceMax;
	}
	
	/**
	 * Récupère tous les nœuds les plus éloignés de la feuille
	 * Dijkstra doit avoir été appelé
	 * @return
	 */
	public List<String> getZ()
	{
		List<String> out = new ArrayList<String>();
		for(String v : graphe.keySet())
			if(nodes.get(v).g == distanceMax && variablesInstanciees.contains(v))
				out.add(v);
		if(verbose)
			System.out.println("Z = "+out);
		return out;
	}
	
	/**
	 * Supprime les nœuds inutile (dont la distance à la feuille est strictement plus grande que l)
	 * Dijkstra doit avoir été appelé
	 */
	public void prune()
	{
		Set<String> supprimes = new HashSet<String>();
		for(String s : graphe.keySet())
			if(nodes.get(s).g > distanceMax || (nodes.get(s).g == distanceMax && !variablesInstanciees.contains(s)))
				supprimes.add(s);
		if(verbose)
			System.out.println("Pruné : "+supprimes);
		for(String s : supprimes)
			graphe.remove(s);
		for(String s : graphe.keySet())
			graphe.get(s).removeAll(supprimes);
	}
	
	private class Arc
	{
		public String u, v;
		
		public Arc(String u, String v)
		{
			this.u = u;
			this.v = v;
		}

		@Override
		public int hashCode()
		{
			if(u.compareTo(v) < 0)
				return (u+v).hashCode();
			else
				return (v+u).hashCode();
		}
		
		public boolean equals(Object o)
		{
			return o.hashCode() == hashCode();
		}
	}
	
	public Partition computeSeparator()
	{
		try {
			if(distanceMax == Integer.MAX_VALUE) // le graphe est déjà coupé
			{
				if(verbose)
					System.out.println("G0 et G1 déjà disjoints");
	            partition = new Partition();
	            for(String s : graphe.keySet())
	            	if(nodes.get(s).visited)
	            		partition.ensembles[0].add(s);
	            	else
	            		partition.ensembles[1].add(s);
	            return partition;
			}
			
			List<String> Z = getZ();
			List<Arc> arcs = new ArrayList<Arc>();
			
			BufferedWriter output, fixFile;
	
			output = new BufferedWriter(new FileWriter("/tmp/hg"));
			
			int nbHyperArcsRetires = 0;
			
			// on transforme de problème de séparateur en un problème de partitionnement d'un hypergraphe
			for(String s : graphe.keySet())
			{
				if(graphe.get(s).size() <= 1) // pas besoin de mettre une hyper-arête qui ne concerne qu'un seul nœud
				{
					nbHyperArcsRetires++;
					continue;
				}

//				System.out.println("Sommet : "+s);
				for(String v : graphe.get(s))
				{
					Arc a = new Arc(s,v);
					if(!arcs.contains(a))
						arcs.add(a);
				}
			}
			output.write((graphe.size()-nbHyperArcsRetires)+" "+arcs.size()+" 1");
			for(String s : graphe.keySet())
			{
				if(graphe.get(s).size() <= 1) // idem
					continue;

				output.newLine();
				int poids = 10;
				if(variablesInstanciees.contains(s))
					poids = 1; // shmetis peut avoir des problèmes avec des poids nuls…
				if(s.equals(feuille) || Z.contains(s))
					poids = 10000;
				output.write(Integer.toString(poids));
				for(String v : graphe.get(s))
					output.write(" "+(arcs.indexOf(new Arc(s,v))+1));					
			}
			output.close();
			
			fixFile = new BufferedWriter(new FileWriter("/tmp/ff"));
			for(Arc a : arcs)
			{
				if(a.u.equals(feuille) || a.v.equals(feuille))
					fixFile.write("0");
				else if(Z.contains(a.u) || Z.contains(a.v))
					fixFile.write("1");
				else
					fixFile.write("-1");
				fixFile.newLine();
			}
			fixFile.close();
			
			// appel à hmetis : décomposition de l'hypergraphe
			Process proc = Runtime.getRuntime().exec("lib/hmetis-1.5-linux/shmetis /tmp/hg /tmp/ff 2 1");
			BufferedReader input = new BufferedReader(new InputStreamReader(proc.getInputStream()));
			BufferedReader error = new BufferedReader(new InputStreamReader(proc.getErrorStream()));
            while ((input.readLine()) != null) {}
            while ((error.readLine()) != null) {}
            proc.waitFor();
            
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream("/tmp/hg.part.2")));
            String line = br.readLine();

            partition = new Partition();
            int l = 0;
            while(line != null)
            {
            	int p = Integer.parseInt(line);
            	Arc a = arcs.get(l);
            	partition.ensembles[p].add(a.u);
            	partition.ensembles[p].add(a.v);
				line = br.readLine();
				l++;
            }
            br.close();
            (new File("/tmp/hg.part.2")).delete();
            
            // on a presque fini. pour l'instant, on a deux ensembles tels que leur intersection est le séparateur
            
            // récupération de l'intersection…
            for(String s : partition.ensembles[0])
            	if(partition.ensembles[1].contains(s))
            		partition.separateur.add(s);
            
            // on retire l'intersection aux deux premiers ensembles
            partition.ensembles[0].removeAll(partition.separateur);
            partition.ensembles[1].removeAll(partition.separateur);
            
            // c'est fini !
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		}
		return partition;
		
	}
	
	public String getFeuille()
	{
		return feuille;
	}
	
	public Partition getPartition()
	{
		return partition;
	}
	
}
