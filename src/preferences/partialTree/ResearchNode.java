package preferences.partialTree;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

import compilateurHistorique.MultiHistoComp;
import compilateurHistorique.Variable;

/**
 * Un nœud pour la recherche d'arbre lexico
 * @author pgimenez
 *
 */

public class ResearchNode
{
	public PartialLexTree tree;
	public ArrayList<PartialLexTree> enfants;
	public double majorantVraisemblance;
	
	// Les prochains nœuds à construire
	// Leur parent sont déjà construits
	static private Queue<PartialLexTree> prochainsNoeuds = new LinkedList<PartialLexTree>();

	// Constructeur de copie, utilisé pour copier un enfant
	public ResearchNode(ResearchNode node)
	{
		this.tree = node.tree.getClone();
	}

	/**
	 * Constructeur du tout premier nœud
	 * @param voisins
	 */
	public ResearchNode(MultiHistoComp historique)
	{
		tree = new PartialLexTree(historique);
		this.majorantVraisemblance = 0;
	}
	
	public ArrayList<ResearchNode> getVoisins()
	{
		ResearchNode fils = prochainsNoeuds.poll();
//		while(true)
		{
			ResearchNode voisin = new ResearchNode(fils);
		}
		return null;
	}
	
	/**
	 * L'arbre est-il fini ?
	 * @return
	 */
	public boolean isDone()
	{
		return false;
	}
}
