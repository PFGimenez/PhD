package recommandation;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;

import compilateurHistorique.DatasetInfo;
import compilateurHistorique.Instanciation;
import compilateurHistorique.HistoriqueCompile;
import preferences.ProbabilityDistributionLog;
import preferences.UniformDistribution;
import preferences.heuristiques.HeuristiqueMultipleComposedDuel;
import preferences.heuristiques.HeuristiqueMultipleGloutonDuel;
import preferences.multipleTree.ApprentissageGloutonMultipleTree;
import preferences.multipleTree.LexicographicMultipleTree;
import preferences.penalty.AIC;
import preferences.penalty.PenaltyWeightFunction;
import recommandation.parser.ParserProcess;

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

public class AlgoLexMultipleTree implements Clusturable
{

	private ApprentissageGloutonMultipleTree algo;
	private LexicographicMultipleTree struct;
	private HashMap<String, String> valeurs;
	private boolean prune;
	private PenaltyWeightFunction phi = new AIC(1);
	private ProbabilityDistributionLog p;
	private BigInteger rangMoyen;
	
	public AlgoLexMultipleTree(ParserProcess pp)
	{
		this.prune = Boolean.parseBoolean(pp.read());
		int taille = Integer.parseInt(pp.read());
//		algo = new ApprentissageGloutonMultipleTree(300, 20, new HeuristiqueMultipleGloutonDuel(taille));
		algo = new ApprentissageGloutonMultipleTree(300, 20, new HeuristiqueMultipleComposedDuel(taille));
		valeurs = new HashMap<String, String>();
	}
	
	@Override
	public int hashCode()
	{
		return algo.hashCode() * 2 + (prune ? 1 : 0);
	}
	
	public AlgoLexMultipleTree()
	{
		this(new ApprentissageGloutonMultipleTree(300, 20, new HeuristiqueMultipleGloutonDuel(2)), false);
	}
	
	public AlgoLexMultipleTree(ApprentissageGloutonMultipleTree algo, boolean prune)
	{
		this.prune = prune;
		this.algo = algo;
		valeurs = new HashMap<String, String>();
	}
	
	public void describe()
	{
		System.out.print("LP-multiple-tree "+algo+" (prune = "+prune);
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
		int code = 0;
		for(String s : filename)
			code += s.hashCode();
		code = Math.abs(code);
		apprendDonnees(dataset, HistoriqueCompile.readInstances(dataset, filename, entete), code);
	}
	
	@Override
	public void apprendDonnees(DatasetInfo dataset, Instanciation[] instances, int code)
	{
		struct = LexicographicMultipleTree.load("multiple-lextree-"+code+"-"+hashCode());
		if(struct == null)
		{
			System.out.println("Apprentissage du LP-tree avec "+instances.length+" exemples…");
			struct = algo.apprendDonnees(dataset, instances);
	
			p = new UniformDistribution(struct.getRangMax());

			if(prune)
			{
				System.out.println("Nb nœuds avant prune : "+struct.getNbNoeuds());
				algo.pruneFeuille(phi, p);
				System.out.println("Nb nœuds après prune : "+struct.getNbNoeuds());
			}
			else
				System.out.println("Nb nœuds : "+struct.getNbNoeuds());
			struct.save("multiple-lextree-"+code+"-"+hashCode());
		}
		rangMoyen = struct.rangMoyen(instances);
	}
	
	public void printMeanRank()
	{
		BigInteger rangMax = struct.getRangMax();
		System.out.println("Rang moyen : "+rangMoyen);
		System.out.println("Rang max : "+rangMax);
		System.out.println("Rang moyen / rang max : "+new BigDecimal(rangMoyen.multiply(BigInteger.valueOf(100))).divide(new BigDecimal(rangMax), 10, RoundingMode.HALF_EVEN)+"%");
	}

	@Override
	public String recommande(String variable, ArrayList<String> possibles)
	{
		return struct.infereBest(variable, valeurs);
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
	public void terminePli()
	{
		printMeanRank();
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

	@Override
	public double metric()
	{
		BigInteger rangMax = struct.getRangMax();
		return new BigDecimal(rangMoyen).divide(new BigDecimal(rangMax), 250, RoundingMode.HALF_EVEN).doubleValue();
	}
}