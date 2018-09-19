package preferences.completeTree;

import java.io.BufferedWriter;
import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import compilateurHistorique.Variable;

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
 * Ordre lexicographique
 * @author Pierre-François Gimenez
 *
 */

public class LexicographicOrder extends LexicographicStructure
{
	private static final long serialVersionUID = 724129233415188700L;
	private LexicographicOrder enfant;
	
	public LexicographicOrder(Variable v)
	{
		this(v.name, v.domain, v.index);
	}
	
	public LexicographicOrder(String variable, int nbMod, int profondeur)
	{
		super(variable, nbMod, profondeur);
		this.enfant = null;
		split = false; // par définition
	}
	
	@Override
	public void updateBase(BigInteger base)
	{
		this.base = base.divide(BigInteger.valueOf(nbMod));
		if(enfant != null)
			enfant.updateBase(this.base);
	}
	
	public void setEnfant(LexicographicOrder enfant)
	{
		this.enfant = enfant;
	}

	protected void affichePrivate(BufferedWriter output) throws IOException
	{
//		output.write(nb+" [label=ordre];");
//		output.newLine();
		output.write(nb+" [label="+variable+"];");
		output.newLine();
/*		if(enfant != null)
		{
			enfant.affichePrivate(output);
			output.write(nb+" -> "+enfant.nb+";");
			output.newLine();
		}*/
		
		if(enfant != null)
		{
			enfant.affichePrivate(output);
			for(int i = 0; i<nbMod; i++)
			{
				output.write(nb+" -> "+enfant.nb+" [label=\""+ordrePref.get(i)+"\"];");
				output.newLine();
			}
		}
		else
			for(int i = 0; i<nbMod; i++)
			{
				output.write(++nbS+" [style=invisible];");				
				output.newLine();
				output.write(nb+" -> "+nbS+" [label=\""+ordrePref.get(i)+"\"];");
				output.newLine();
			}
	}
	
	public BigInteger infereRang(List<String> element, List<String> ordreVariables)
	{
		int index = ordreVariables.indexOf(variable);
		String value = element.get(index);
		ordreVariables.remove(index);
		element.remove(index);
		if(enfant == null)
			return base.multiply(BigInteger.valueOf(getPref(value)));
		else
		{
			BigInteger tmp = enfant.infereRang(element, ordreVariables);
//			if(tmp < 0)
//				throw new ArithmeticException();
			return base.multiply(BigInteger.valueOf(getPref(value))).add(tmp);
		}
	}
	
	public String infereBest(String varARecommander, HashMap<String, String> valeurs)
	{
		return infereBest(varARecommander, null, valeurs);
	}
	
	/**
	 * element et ordrevariables ne sont pas utilisés car la préférence des valeurs d'une variable ne dépend pas de la valeur des autres variables
	 */
	public String infereBest(String varARecommander, List<String> possibles, HashMap<String, String> valeurs)
	{
		if(variable.equals(varARecommander))
		{
			// on renvoie la valeur préférée parmi celles possibles
			for(int i = 0; i < nbMod-1; i++)
				if(possibles == null || possibles.contains(getPref(i)))
					return getPref(i);
			return getPref(nbMod-1);
		}
		else
			return enfant.infereBest(varARecommander, possibles, valeurs);
	}
	
	public HashMap<String, String> getConfigurationAtRank(BigInteger r)
	{
		// On est à la feuille
		if(enfant == null)
		{
			HashMap<String, String> out = new HashMap<String, String>();
			out.put(variable, getPref(r.intValue()));
			return out;
		}
		else
		{
			for(int i = 1; i <= nbMod; i++)
				if(r.compareTo(base.multiply(BigInteger.valueOf(i))) == -1)
				{
					HashMap<String, String> out = enfant.getConfigurationAtRank(r.mod(base));
					out.put(variable, getPref(i-1));
					return out;
				}
			return null;
		}
	}
	
	public int getRessemblance(LexicographicStructure other)
	{
		return 0;
	}

	@Override
	protected int getMaxNb() {
		if(enfant == null)
			return nb;
		return Math.max(nb, enfant.getMaxNb());
	}

	@Override
	public int getNbNoeuds()
	{
		if(enfant == null)
			return 1;
		return 1 + enfant.getNbNoeuds();
	}

	@Override
	protected List<LexicographicStructure> getEnfants()
	{
		ArrayList<LexicographicStructure> out = new ArrayList<LexicographicStructure>();
		if(enfant != null)
			for(int i = 0; i < nbMod; i++)
				out.add(enfant);
		return out;
	}

	@Override
	public int getTailleTable()
	{
		if(enfant == null)
			return nbMod;
		else
			return nbMod + enfant.getTailleTable();
	}
	
}
