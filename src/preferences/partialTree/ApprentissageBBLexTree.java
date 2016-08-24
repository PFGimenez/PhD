package preferences.partialTree;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.PriorityQueue;

import compilateurHistorique.MultiHistoComp;

/**
 * Apprentissage d'un arbre lexicographique par un algorithme Branch & Bound
 * @author pgimenez
 *
 */

public class ApprentissageBBLexTree
{

	/**
	 * Comparateur de noeud utilisé par la priority queue
	 * @author pgimenez
	 *
	 */
	private class ResearchNodeComparator implements Comparator<ResearchNode>
	{
		@Override
		public int compare(ResearchNode arg0, ResearchNode arg1)
		{
			int out = (int) Math.signum((arg0.f_score - arg1.f_score));
			return out;
		}
	}

//	private final ArrayList<Integer> closedset = new ArrayList<Integer>();
	private final PriorityQueue<ResearchNode> openset = new PriorityQueue<ResearchNode>(1000, new ResearchNodeComparator());
	
	public PartialLexTree research(MultiHistoComp historique)
	{
		
		/**
		 * Première étape : recherche en profondeur d'une solution non-optimale
		 */

		double reachedScore = greedyLearningScore(historique);
		
		/**
		 * Deuxième étape : recherche BB tout en connaissant une bonne solution
		 */
		
		openset.add(new ResearchNode(historique));

		while(!openset.isEmpty())
		{
			// On récupère et on supprime le meilleur nœud
			ResearchNode best = openset.poll();
			
			
			
		}
		
		
		return null;
	}
	
	/**
	 * Renvoie la vraisemblance de la solution trouvée par algorithme glouton
	 * @param historique
	 * @return
	 */
	private double greedyLearningScore(MultiHistoComp historique)
	{
		return 0;
	}
	
	private double heuristique(PartialLexTree tree)
	{
		return 0;
	}
}
