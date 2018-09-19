package recommandation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import compilateurHistorique.DatasetInfo;
import compilateurHistorique.Instanciation;
import preferences.completeTree.ApprentissageExactLexOrder;
import preferences.completeTree.LexicographicOrder;
import recommandation.parser.ParserProcess;

/*   (C) Copyright 2018, Gimenez Pierre-François 
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
 * Recom par LP-tree linéaire exact
 * @author pgimenez
 *
 */

public class AlgoExactLinLexTree extends Clusturable
{

	private ApprentissageExactLexOrder algo;
	private LexicographicOrder struct;
	private HashMap<String, String> valeurs;
	
	public AlgoExactLinLexTree(ParserProcess pp)
	{
		this();
	}

	@Override
	public int hashCode()
	{
		return algo.hashCode();
	}
	
	public AlgoExactLinLexTree()
	{
		algo = new ApprentissageExactLexOrder();
		valeurs = new HashMap<String, String>();
	}
	
	public void describe()
	{
		System.out.println("Exact linear LP-tree "+algo.getClass().getSimpleName());
	}
	
	@Override
	public void apprendDonnees(DatasetInfo dataset, List<Instanciation> instances, long code)
	{
		struct = algo.apprendDonnees(dataset, instances);
	}
	
	public void printMeanRank()
	{
/*		BigInteger rangMax = struct.getRangMax();
		System.out.println("Rang moyen : "+rangMoyen);
		System.out.println("Rang max : "+rangMax);
		System.out.println("Rang moyen / rang max : "+new BigDecimal(rangMoyen.multiply(BigInteger.valueOf(100))).divide(new BigDecimal(rangMax), 10, RoundingMode.HALF_EVEN)+"%");*/
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
	public HashMap<String, Double> metricCoeff()
	{
		HashMap<String, Double> out = new HashMap<String, Double>();
//		BigInteger rangMax = struct.getRangMax();
//		out.put("Rang moyen", new BigDecimal(rangMoyen).divide(new BigDecimal(rangMax), 250, RoundingMode.HALF_EVEN).doubleValue());
		return out;
	}

	@Override
	public HashMap<String, Double> metric()
	{
		HashMap<String, Double> out = new HashMap<String, Double>();
		System.out.println("Taille arbre : "+struct.getNbNoeuds());
		out.put("Taille arbre", (double)struct.getNbNoeuds());
		return out;
	}

}