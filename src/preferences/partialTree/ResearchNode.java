package preferences.partialTree;

import java.util.ArrayList;

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
	public ArrayList<Variable> voisins;
	public double g_score;
	public double f_score;
	
	/**
	 * Constructeur d'un nœud qui a un parent
	 * @param parent
	 * @param variable
	 * @param g_score
	 * @param f_score
	 */
	public ResearchNode(PartialLexTree tree, double g_score, double f_score)
	{
		this.tree = tree;
		this.g_score = g_score;
		this.f_score = f_score;
	}
	
	/**
	 * Constructeur du tout premier nœud
	 * @param voisins
	 */
	public ResearchNode(MultiHistoComp historique)
	{
		tree = new PartialLexTree(historique);
		this.g_score = 0;
		this.f_score = 0;
	}
	
	public ArrayList<PartialLexTree> getVoisisn()
	{
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
