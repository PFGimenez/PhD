/*   (C) Copyright 2017, Gimenez Pierre-François 
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

package preferences.cpnet;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import compilateurHistorique.DatasetInfo;
import compilateurHistorique.HistoriqueCompile;
import compilateurHistorique.Instanciation;
import compilateurHistorique.IteratorInstancesPartielles;
import compilateurHistorique.Variable;
import graphOperation.DAG;
import graphOperation.MoralGraph;

/**
 * Un CP-net acyclique
 * @author pgimenez
 *
 */

public class CPNet {
	private HashMap<String, CPNetNode> nodes = new HashMap<String, CPNetNode>();
	private List<CPNetNode> nodesTriTopologique = new ArrayList<CPNetNode>();
	
	public CPNet(DatasetInfo dataset, HistoriqueCompile historique, String RBFile)
	{
		MoralGraph mg = new MoralGraph(new DAG(RBFile), dataset.mapVar.keySet(), false);
		
		// On crée les sommets
		for(Variable var : dataset.vars)
			nodes.put(var.name, new CPNetNode(var.name));
		
		int cardMax = 0;
		for(String s : dataset.mapVar.keySet())
			cardMax = Math.max(cardMax, mg.graphe.get(s).size());
		
		for(int nbParent = 1; nbParent <= cardMax; nbParent++)
			for(String s : dataset.mapVar.keySet())
				learnParent(dataset, nodes.get(s), mg.graphe.get(s), nbParent, historique);
		
		for(Variable var : dataset.vars)
		{
			CPNetNode current = nodes.get(var.name);
			current.learnTable(historique, dataset);
		}
		
		constructTriTopologique();
	}
	
	private void learnParent(DatasetInfo dataset, CPNetNode node, Set<String> all, int nbParent, HistoriqueCompile historique)
	{
		// tous les voisins par arc non orienté
		List<CPNetNode> possibleParent = new ArrayList<CPNetNode>();
		for(String s : all)
		{
			// on retire arcs déjà orientés
			if(!node.enfants.contains(nodes.get(s)) && !node.parents.contains(nodes.get(s)))
				possibleParent.add(nodes.get(s));
			
			// on va ignorer les arcs qui mèneraient à un cycle
			
		}

		int nbNewParent = nbParent - node.parents.size();
		List<BitSet> candidats = generateTuples(possibleParent.size(), nbNewParent);
		List<List<CPNetNode>> parentsTuples = new ArrayList<List<CPNetNode>>();
		
		int[] index = new int[nbNewParent];
		for(BitSet bs : candidats)
		{
			List<CPNetNode> parents = new ArrayList<CPNetNode>();
			for(int i = 0; i < nbNewParent; i++)
			{
				index[i] = bs.nextSetBit(i == 0 ? 0 : index[i - 1] + 1);
				parents.add(possibleParent.get(index[i]));
			}
			assert parents.size() == nbNewParent : parents;
			parentsTuples.add(parents);
		}
		
		List<CPNetNode> chosenParents = null;

		for(List<CPNetNode> parents : parentsTuples)
		{
			List<String> varParents = new ArrayList<String>();
			for(CPNetNode n : parents)
				varParents.add(n.variable);
			for(CPNetNode n : node.parents) // on complète avec les parents déjà connus
				varParents.add(n.variable);
			
			assert varParents.size() == nbParent : varParents;
			IteratorInstancesPartielles iter = new IteratorInstancesPartielles(new Instanciation(dataset), dataset, varParents);
			int last = -1;
			boolean first = false;
			for(Instanciation inst : iter)
			{				
				int hash = historique.computeOrder(node.variable, inst).hashCode();
				if(first)
					last = hash;
				first = false;
				if(hash != last)
				{
					// l'ordre varie !
					chosenParents = parents;
					break;
				}
			}
			if(chosenParents != null)
				break;
		}

		if(chosenParents != null)
		{
			for(CPNetNode n : chosenParents)
				if(!node.parents.contains(n))
				{
					node.parents.add(n);
					n.enfants.add(node);
				}
		}
	}

	/**
	 * Construction de la liste triée topologiquement à partir des noeuds
	 */
	private void constructTriTopologique()
	{
		nodesTriTopologique.clear();
		List<CPNetNode> startNodes = new ArrayList<CPNetNode>();
		
		for(CPNetNode n : nodes.values())
			if(n.parents.isEmpty())
				startNodes.add(n);
		
		while(!startNodes.isEmpty())
		{
			CPNetNode n = startNodes.remove(0);
			nodesTriTopologique.add(n);
			for(CPNetNode e : n.enfants)
			{
				boolean parents = false;
				for(CPNetNode p : e.parents)
					if(!nodesTriTopologique.contains(p))
						parents = true;
				if(!parents)
					startNodes.add(e);
			}
		}
		assert nodesTriTopologique.size() == nodes.size();
	}
	
	/**
	 * Complète avec l'extension préférée
	 * @param inst
	 */
	public void complete(Instanciation inst)
	{
		for(CPNetNode n : nodesTriTopologique)
			n.complete(inst);
	}
	
	
	private List<BitSet> generateTuples(int nbVar, int taille)
	{
		List<BitSet> sub = new ArrayList<BitSet>();
		if(taille > nbVar)
			return(sub);
		
		sub.add(new BitSet(nbVar));
		for(int v = 0; v < nbVar; v++)
		{
			int size = sub.size();
			for(int i = 0; i < size; i++)
			{
				BitSet bs = sub.get(i);
				if(bs.cardinality() < taille)
				{
					BitSet newbs = (BitSet) bs.clone();
					newbs.set(v);
					sub.add(newbs);
				}
				
			}
		}
		
		/*
		 * Il peut y avoir des tuples avec moins que la taille requise
		 */
		Iterator<BitSet> iter = sub.iterator();
		while(iter.hasNext())
			if(iter.next().cardinality() < taille)
				iter.remove();
		return sub;
	}

}
