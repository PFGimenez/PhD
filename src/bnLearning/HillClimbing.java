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

package bnLearning;

import java.util.List;

import compilateurHistorique.MultiHistoComp;
import preferences.penalty.PenaltyWeightFunction;

/**
 * Apprentissage de réseau bayésien par Hill Climbing (méthode basée uniquement sur score)
 * @author Pierre-François Gimenez
 *
 */

public class HillClimbing {

	private PenaltyWeightFunction penalty;
	private MultiHistoComp historique;
	private int nbExemples;
	private List<BNNode> noeuds;
	
	public HillClimbing(MultiHistoComp historique, PenaltyWeightFunction penalty)
	{
		this.historique = historique;
		this.penalty = penalty;
		nbExemples = historique.getNbInstancesTotal();
	}
	
	public LearntBN learn()
	{
		LearntBN current = new LearntBN(noeuds);
		return null;
	}
	
	/**
	 * Calcule le score d'un réseau bayésien
	 * @param bn
	 * @return
	 */
	private double score(LearntBN bn)
	{
		return loglikelihood(bn) + penalty.phi(bn.getTaille()) * nbExemples;
	}
	
	/**
	 * Calcule la log-likelihood des données en fonction du réseau bayésien
	 * @param bn
	 * @return
	 */
	private double loglikelihood(LearntBN bn)
	{
		return 0;
	}
}
