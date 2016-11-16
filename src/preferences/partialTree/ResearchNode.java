package preferences.partialTree;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

import compilateurHistorique.MultiHistoComp;
import compilateurHistorique.Variable;

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

/**
 * Un nœud pour la recherche d'arbre lexico
 * @author Pierre-François Gimenez
 *
 */

public class ResearchNode
{
	public ArrayList<PartialLexTree> tree = new ArrayList<PartialLexTree>();
	public double majorantVraisemblance;
	
	// Les prochains nœuds à construire
	// Ce sont des enfants archétypes, qu'il faudra démultiplier en fixant leur variable et leur préférence
	private Queue<PartialLexTree> prochainsNoeuds = new LinkedList<PartialLexTree>();

	// Constructeur de copie, utilisé pour copier un enfant
	public ResearchNode(ResearchNode node, PartialLexTree tree)
	{
		for(PartialLexTree t : node.tree)
			this.tree.add(new PartialLexTree(t));
		this.tree.add(tree);
	}

	/**
	 * Constructeur du tout premier nœud
	 * @param voisins
	 */
	public ResearchNode(MultiHistoComp historique)
	{
		PartialLexTree.setHistorique(historique);
		tree.add(new PartialLexTree(historique));
		this.majorantVraisemblance = 0;
	}
	
	/**
	 * Copie
	 */
	public ResearchNode(PartialLexTree n, Queue<PartialLexTree> prochainsNoeuds)
	{
		this.prochainsNoeuds.addAll(prochainsNoeuds);
		for(int i = 0; i < tree.get(tree.size() - 1).nbMod; i++)
			this.prochainsNoeuds.add(new PartialLexTree(tree.get(tree.size() - 1), tree.get(tree.size() - 1).ordrePref.get(i)));
//		tree = new PartialLexTree(tree);
	}
	
	public ArrayList<ResearchNode> getVoisins()
	{
		PartialLexTree noeud = prochainsNoeuds.poll();
		ArrayList<Variable> free = noeud.getFreeVariables();
		ArrayList<ResearchNode> voisins = new ArrayList<ResearchNode>();
		
		for(Variable v : free)
		{
			// générer ordrePref
			ArrayList<String> ordrePref = null;
			ResearchNode voisin = new ResearchNode(this, noeud);
			voisin.tree.get(voisin.tree.size() - 1).setVariable(v, ordrePref);
			voisins.add(voisin);
		}
		return voisins;
	}
	
	/**
	 * L'arbre est-il fini ?
	 * @return
	 */
	public boolean isDone()
	{
		return tree.get(0).computeComplet();
	}

	public static ArrayList<ResearchNode> getInitialNodes(MultiHistoComp historique)
	{
		return null;
	}
}
