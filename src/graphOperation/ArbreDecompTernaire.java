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

import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;

import compilateurHistorique.Instanciation;
import compilateurHistorique.MultiHistoComp;
import preferences.penalty.PenaltyWeightFunction;

/**
 * Un arbre de décomposition ternaire utilisé par DRC
 * @author Pierre-François Gimenez
 *
 */

public class ArbreDecompTernaire
{
	public NodeArbreDecompTernaire racine;
	public HashMap<Set<String>, NodeArbreDecompTernaire> allNodes = new HashMap<Set<String>, NodeArbreDecompTernaire>();
	
	public ArbreDecompTernaire(DAG dag, Map<String, Integer> mapvar, MultiHistoComp historique, boolean verbose)
	{
		racine = new NodeArbreDecompTernaire(dag, dag.dag[0].keySet(), mapvar, historique, verbose, null, 0, allNodes);
		System.out.println("Arbre de décomposition ternaire : "+NodeArbreDecompTernaire.nb+" nœuds");
	}
	
	private class NodeArbreDecompTernaireComparator implements Comparator<NodeArbreDecompTernaire>
	{

		@Override
		public int compare(NodeArbreDecompTernaire arg0, NodeArbreDecompTernaire arg1)
		{
			return arg0.domaine - arg1.domaine;
		}
		
	}
	
	public void prune(Instanciation[] allInstances, PenaltyWeightFunction f, InferenceDRC inferer)
	{
		PriorityQueue<NodeArbreDecompTernaire> file = new PriorityQueue<NodeArbreDecompTernaire>(allNodes.size(), new NodeArbreDecompTernaireComparator());
		for(NodeArbreDecompTernaire n : allNodes.values())
			file.add(n);
		boolean gain;
		double currentScore = computeScore(allInstances, f, inferer);
		while(!file.isEmpty())
		{
			NodeArbreDecompTernaire exclu = file.poll();
			gain = true;
			if(!exclu.isLeaf())
			{
				exclu.makeLeaf();
				double testScore = computeScore(allInstances, f, inferer);
				gain = testScore > currentScore;
				if(gain)
				{
					System.out.println("Ancien score : "+currentScore+", nouveau score : "+testScore);
					currentScore = testScore;
				}
				else
					exclu.unmakeLeaf();
			}
		}
		System.out.println("Taille après prune : "+countNodes(racine, new HashSet<NodeArbreDecompTernaire>()));
	}
	
	private int countNodes(NodeArbreDecompTernaire n, Set<NodeArbreDecompTernaire> faits)
	{
		if(faits.contains(n))
			return 0;
		faits.add(n);
		return 1 + (n.fils0 == null ? 0 : countNodes(n.fils0, faits)) + (n.fils1 == null ? 0 : countNodes(n.fils1, faits)) + (n.filsC == null ? 0 : countNodes(n.filsC, faits));
	}
	/*
	private int countDomain(NodeArbreDecompTernaire n, Set<NodeArbreDecompTernaire> faits)
	{
		if(faits.contains(n))
			return 0;
		faits.add(n);
		return n.domaine + (n.fils0 == null ? 0 : countDomain(n.fils0, faits)) + (n.fils1 == null ? 0 : countDomain(n.fils1, faits)) + (n.filsC == null ? 0 : countDomain(n.filsC, faits));
	}
	*/
	private double computeScore(Instanciation[] allInstances, PenaltyWeightFunction f, InferenceDRC inferer)
	{
		int nb = countNodes(racine, new HashSet<NodeArbreDecompTernaire>());
		double LL = 0;
		for(Instanciation i : allInstances)
		{
			LL += inferer.infere(i);
//			double tmp = inferer.infere(i);
//			System.out.println(tmp);
		}
		inferer.clearCache();
		System.out.println("Nb nœuds : "+nb+", LL = "+LL);		
		return LL - f.phi(allInstances.length) * nb;
	}
}
