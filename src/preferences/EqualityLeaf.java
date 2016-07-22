package preferences;

import java.io.BufferedWriter;
import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;

import preferences.heuristiques.HeuristiqueOrdre;

/*   (C) Copyright 2015, Gimenez Pierre-François 
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
 * Nœud d'un arbre lexico qui regroupe plusieurs exemples, considérés comme égaux. Le "rang" de chacun de ces exemples sera le rang moyen.
 * @author pgimenez
 *
 */

public class EqualityLeaf extends LexicographicStructure {

	private static final long serialVersionUID = 724129233415188700L;
	private BigInteger rang;
	
	public EqualityLeaf()
	{
		super("no var", 0, null);
	}
	
	@Override
	public void updateBase(BigInteger base)
	{
		this.base = base.divide(BigInteger.valueOf(nbMod));
		rang = base.add(BigInteger.ONE).divide(BigInteger.valueOf(2));
	}

	protected void affichePrivate(BufferedWriter output) throws IOException
	{
		output.write(nb+" [label=ordre];");
		output.newLine();
	}
	
	public BigInteger infereRang(ArrayList<String> element, ArrayList<String> ordreVariables)
	{
		return rang;
	}
	
	/**
	 * element et ordrevariables ne sont pas utilisés car la préférence des valeurs d'une variable ne dépend pas de la valeur des autres variables
	 */
	public String infereBest(String varARecommander, ArrayList<String> possibles, HashMap<String, String> valeurs)
	{
		return null;
	}
	
	public HashMap<String, String> getConfigurationAtRank(BigInteger r)
	{
		return new HashMap<String, String>();
	}
	
	@Override
	public int getRessemblance(LexicographicStructure other)
	{
		return 0;
	}
	
}
