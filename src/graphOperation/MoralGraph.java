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
	private List<String> feuilles;
	private String departDijkstra;
	private Partition partition = null;
	private Set<String> variablesInstanciees;
	private boolean verbose;
	private List<Arc> arcs;
	private Set<String> keys = new HashSet<String>();
	private DAG dag;

	public MoralGraph(DAG dag, Set<String> variablesInstanciees, boolean verbose)
	{
		this.dag = dag;
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

		keys.addAll(graphe.keySet());

		// on ne garde que les voisins qui sont dans le graphe (sinon un fils en fait pruné pourrait être un voisin…)
		for(String n : keys)
		{
			Iterator<String> iter = graphe.get(n).iterator();
			while(iter.hasNext())
				if(!graphe.containsKey(iter.next()))
					iter.remove();
		}		
		
		feuilles = new ArrayList<String>();
		
		// on cherche une feuille (feuille = autant de voisins que de parents)
		for(String n : keys)
		{
			if(graphe.get(n).size() == dag.dag[parents].get(n).size())
			{
				feuilles.add(n);
				if(verbose)
					System.out.println("Feuille : "+n);
			}
		}

		if(!feuilles.isEmpty())
			departDijkstra = feuilles.get(0);
		
		// on marie les parents
		for(String n : keys)
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

			for(String n : keys)
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
	 * Un nœud de Dijkstra
	 * @author Pierre-François Gimenez
	 *
	 */
	public class NodeDijkstra
	{
		public String var;
		public int g = Integer.MAX_VALUE;
		public boolean visited = false;
		
		public NodeDijkstra(String var)
		{
			this.var = var;
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

		for(String s : keys)
			nodes.put(s, new NodeDijkstra(s));
		
		NodeDijkstra depart = nodes.get(departDijkstra);
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
		for(String s : keys)
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
		for(String v : keys)
			if(nodes.get(v).g == distanceMax && variablesInstanciees.contains(v))
				out.add(v);
		if(verbose)
			System.out.println("Z = "+out);
		return out;
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
		// On ne refait pas le calcul si on peut l'éviter…
		if(partition != null)
			return partition;

		// U est vide
		if(feuilles.isEmpty())			
			return null;
		
		computeDijkstra();

		if(distanceMax == Integer.MAX_VALUE) // le graphe est déjà coupé
		{
			if(verbose)
				System.out.println("G0 et G1 déjà disjoints");
            partition = new Partition();
            for(String s : keys)
            	if(nodes.get(s).visited)
            		partition.ensembles[0].add(s);
            	else
            		partition.ensembles[1].add(s);
            
            assert !partition.ensembles[0].isEmpty();
            assert !partition.ensembles[1].isEmpty();
            assert partition.separateur.isEmpty();
            
            return partition;
		}
		
		arcs = new ArrayList<Arc>();
		
		try {
			BufferedWriter output;
	
			output = new BufferedWriter(new FileWriter("/tmp/hg"));
			
			int nbHyperArcsRetires = 0;
			
			// on transforme de problème de séparateur en un problème de partitionnement d'un hypergraphe
			for(String s : keys)
			{
				if(graphe.get(s).size() <= 1) // pas besoin de mettre une hyper-arête qui ne concerne qu'un seul nœud
				{
					nbHyperArcsRetires++;
					continue;
				}
	
	//			System.out.println("Sommet : "+s);
				for(String v : graphe.get(s))
				{
					Arc a = new Arc(s,v);
					if(!arcs.contains(a))
						arcs.add(a);
				}
			}
			output.write((graphe.size()-nbHyperArcsRetires)+" "+arcs.size()+" 1");
			for(String s : keys)
			{
				if(graphe.get(s).size() <= 1) // idem
					continue;
	
				output.newLine();
				int poids = 100;
				if(variablesInstanciees.contains(s))
					poids = 1; // shmetis peut avoir des problèmes avec des poids nuls…
				output.write(Integer.toString(poids));
				for(String v : graphe.get(s))
					output.write(" "+(arcs.indexOf(new Arc(s,v))+1));					
			}
			output.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		
		if(feuilles.size() == 1)
			writeFixFileUneFeuille();
		else
			writeFixFilePlusieursFeuilles();
		
		return readPartition();
	}
	
	public void writeFixFilePlusieursFeuilles()
	{
		String vMax = null;
		int dMax = 0;
		for(String s : feuilles)
		{
			int d = nodes.get(s).g;
			if(vMax == null || d > dMax)
			{
				vMax = s;
				dMax = d;
			}
		}
		
		try {
			BufferedWriter fixFile = new BufferedWriter(new FileWriter("/tmp/ff"));
			for(Arc a : arcs)
			{
				if(a.u.equals(departDijkstra) || a.v.equals(departDijkstra))
					fixFile.write("0");
				else if(a.u.equals(vMax) || a.v.equals(vMax))
					fixFile.write("1");
				else
					fixFile.write("-1");
				fixFile.newLine();
			}
			fixFile.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void writeFixFileUneFeuille()
	{	
		try {
			BufferedWriter fixFile = new BufferedWriter(new FileWriter("/tmp/ff"));
			for(Arc a : arcs)
			{
				if(a.u.equals(departDijkstra) || a.v.equals(departDijkstra))
					fixFile.write("0");
				else if(nodes.get(a.u).g >= distanceMax || nodes.get(a.v).g >= distanceMax)
					fixFile.write("1");
				else
					fixFile.write("-1");
				fixFile.newLine();
			}
			fixFile.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private Partition readPartition()
	{
		try {
			// appel à hmetis : décomposition de l'hypergraphe
			Process proc = Runtime.getRuntime().exec("lib/hmetis-1.5-linux/shmetis /tmp/hg /tmp/ff 2 1");
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
	        
	        if(verbose)
	        	System.out.println("Séparator : "+partition.separateur);
	        
	        // on retire l'intersection aux deux premiers ensembles
	        partition.ensembles[0].removeAll(partition.separateur);
	        partition.ensembles[1].removeAll(partition.separateur);
	        
	        assert !partition.ensembles[0].isEmpty();
	        assert !partition.ensembles[1].isEmpty();
	        assert !partition.separateur.isEmpty();
	        
	        // c'est fini !
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		}
		return partition;
	}
	
	public Partition getPartition()
	{
		return partition;
	}
	
	/**
	 * Y a-t-il bien diminution de la hauteur / du nombre de variables ?
	 * @param fils
	 * @return
	 */
	public boolean diminution(MoralGraph parent)
	{
/*		Set<String> s = new HashSet<String>();
		
		// G1 et V
		for(String n : variablesInstanciees)
			if(parent.variablesInstanciees.contains(n))
				s.add(n);
		
		// s ou C
		s.addAll(parent.partition.separateur);
		*/
		
//		System.out.println(parent.keys.size()+" "+parent.distanceMax+" -> "+keys.size()+" "+distanceMax);
		
		boolean ok = parent.dijkstraDone && dijkstraDone &&
				(parent.keys.size() > keys.size() ||
				(parent.keys.size() == keys.size() && parent.distanceMax > distanceMax));
		if(!ok)
		{
			dag.printGraphe("asserErrorDAG");
			parent.printGraphe("assertErrorParent");
			printGraphe("assertErrorChild");
			System.out.println("Parent. Dijkstra : "+parent.dijkstraDone+". Anc(|V|) = "+parent.keys.size()+". H = "+parent.distanceMax+". Var : "+parent.keys);
			System.out.println("Fils. Dijkstra : "+dijkstraDone+". Anc(|(V \\cap G) \\cup C|) = "+keys.size()+". H = "+distanceMax+". Vars : "+keys);
		}
		return ok;
	}
	
}
