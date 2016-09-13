package preferences.partialTree;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.PriorityQueue;

import compilateurHistorique.MultiHistoComp;
import preferences.loiProbabilite.Loi;

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
			int out = (int) Math.signum((arg0.majorantVraisemblance - arg1.majorantVraisemblance));
			return out;
		}
	}

	private final PriorityQueue<ResearchNode> openset = new PriorityQueue<ResearchNode>(1000, new ResearchNodeComparator());
	
	public PartialLexTree research(MultiHistoComp historique, Loi loi)
	{
		/**
		 * Première étape : recherche en profondeur d'une solution non-optimale
		 */

		double reachedScore = greedyLearning(historique).calculeVraisemblance(loi);
		
		/**
		 * Deuxième étape : recherche BB tout en connaissant une bonne solution
		 */
		
		openset.addAll(ResearchNode.getInitialNodes(historique));
//		openset.add(new ResearchNode());
		
		ResearchNode best = null;
		
		while(true)
		{
			if(openset.isEmpty())
				throw new NullPointerException("Aucune solution trouvée ?!");

			// On récupère et on supprime le meilleur nœud
			best = openset.poll();
			
			if(best.isDone())
				break;
			
			ArrayList<ResearchNode> voisins = best.getVoisins();
			
			for(ResearchNode v : voisins)
			{
				/**
				 * Ce voisin ne pourra jamais faire mieux que la solution gloutonne
				 */
				if(v.tree.get(0).majoreVraisemblance(loi) < reachedScore)
					continue;
				
				openset.add(v);
			}
			
		}
		
		return best.tree.get(0);
	}
	
	/**
	 * Renvoie la solution trouvée par algorithme glouton
	 * @param historique
	 * @return
	 */
	private PartialLexTree greedyLearning(MultiHistoComp historique)
	{
		return null;
	}
	
}
