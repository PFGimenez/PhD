package preferences.compare;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;

import preferences.ProbabilityDistributionLog;
import preferences.completeTree.LexicographicStructure;

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
 * La corrélation de Spearman permet de comparer deux ordres
 * C'est la corrélation de Pearson appliquée aux rangs
 * @author pgimenez
 *
 */

public class SpearmanCorrComparison implements Comparison
{
	@Override
	public double compare(LexicographicStructure arbreAppris, LexicographicStructure arbreReel, long[] rang1, ProbabilityDistributionLog p)
	{
		/**
		 * Génère aléatoirement des rangs
		 * Utilise un arbre pour obtenir le vecteur de valeurs équivalent
		 * utilise le 2e arbre pour obtenir le rang de ce vecteur
		 */
		
		int nbExemples = rang1.length;
//		long[] rang1 = new long[nbExemples];
		long[] rang2 = new long[nbExemples];
		double moyenne1 = 0., moyenne2 = 0.;
		double moyennecarre1 = 0., moyennecarre2 = 0.;
		double moyenneprod = 0.;
		for(int i = 0; i < nbExemples; i++)
		{
//			rang1[i] = Math.round(p.inverse(rng.nextDouble()));
			moyenne1 += rang1[i];
			moyennecarre1 += rang1[i] * rang1[i];
			HashMap<String, String> vecteur = arbreReel.getConfigurationAtRank(BigInteger.valueOf(rang1[i]-1));
			ArrayList<String> val = new ArrayList<String>();
			ArrayList<String> var = new ArrayList<String>();
			
			for(String s : vecteur.keySet())
			{
				var.add(s);
				val.add(vecteur.get(s));
			}
			
			rang2[i] = arbreAppris.infereRang(val, var).longValue();
//			System.out.println(rang1[i]+" "+rang2[i]);
			moyenne2 += rang2[i];
			moyennecarre2 += rang2[i] * rang2[i];
			
			moyenneprod += rang1[i] * rang2[i];
		}
		
		return (nbExemples * moyenneprod - moyenne1 * moyenne2) / (Math.sqrt(nbExemples * moyennecarre1 - moyenne1 * moyenne1) * Math.sqrt(nbExemples * moyennecarre2 - moyenne2 * moyenne2));
	}

}
