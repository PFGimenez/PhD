package recommandation;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;

import compilateurHistorique.DatasetInfo;
import compilateurHistorique.Instanciation;
import compilateurHistorique.HistoriqueCompile;
import preferences.GeometricDistribution;
import preferences.ProbabilityDistributionLog;
import preferences.completeTree.ApprentissageGloutonLexTree;
import preferences.completeTree.LexicographicStructure;
import preferences.heuristiques.HeuristiqueDuel;
import preferences.penalty.AIC;
import preferences.penalty.PenaltyWeightFunction;

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

// Recommandation par apprentissage de préférences

public class AlgoLexTree implements AlgoReco, Clusturable {

	private ApprentissageGloutonLexTree algo;
	private LexicographicStructure struct;
	private HashMap<String, String> valeurs;
	private boolean prune;
	private PenaltyWeightFunction phi = new AIC(1);
	private ProbabilityDistributionLog p;
//	private String dataset;
	
	public AlgoLexTree()
	{
		this(new ApprentissageGloutonLexTree(300, 20, new HeuristiqueDuel()), false);
	}
	
	public AlgoLexTree(ApprentissageGloutonLexTree algo, boolean prune)
	{
		this.prune = prune;
		this.algo = algo;
//		this.dataset = dataset;
		valeurs = new HashMap<String, String>();
	}
	
	public void describe()
	{
		System.out.print("LP-tree "+algo+" (prune = "+prune);
		if(prune)
			System.out.print(", phi = "+phi+", p = "+p);
		System.out.println(")");
	}
	
/*	@Override
	public void apprendContraintes(SALADD contraintes)
	{}*/
	
	@Override
	public void apprendDonnees(DatasetInfo dataset, ArrayList<String> filename, int nbIter, boolean entete)
	{
		apprendDonnees(dataset, HistoriqueCompile.readInstances(dataset, filename, entete));
	}
	
	@Override
	public void apprendDonnees(DatasetInfo dataset, Instanciation[] instances)
	{
//		System.out.println(dataset+algo.toString()+"-"+nbIter);
		// Tout est déjà calculé
//		if(!algo.load(dataset+algo.toString()+"-"+nbIter))
//		{
		algo.setDatasetInfo(dataset);
		struct = algo.apprendDonnees(instances);
//		struct.affiche(algo.getHeuristiqueName());
		BigDecimal param_p = BigDecimal.valueOf(4.).divide(new BigDecimal(struct.getRangMax()), 250, RoundingMode.HALF_EVEN);
		BigDecimal log_p = BigDecimal.valueOf(Math.log(param_p.doubleValue()));
		p = new GeometricDistribution(param_p, log_p);

		if(prune)
			algo.pruneFeuille(phi, p);
//			algo.save(dataset+algo.toString()+"-"+nbIter);
//		}
	}
	
	public void printMeanRank()
	{
		System.out.println("Rang moyen : "+algo.rangMoyen());
		System.out.println("Rang max : "+algo.rangMax());		
	}

	@Override
	public String recommande(String variable, ArrayList<String> possibles)
	{
		return struct.infereBest(variable, possibles, valeurs);
	}

	@Override
	public void setSolution(String variable, String solution)
	{
		valeurs.put(variable, solution);
	}

	@Override
	public void oublieSession()
	{
		valeurs.clear();
	}

	@Override
	public void termine()
	{}

	public String toString()
	{
		return getClass().getSimpleName();
	}

	@Override
	public void unassign(String variable)
	{
		valeurs.remove(variable);
	}
}