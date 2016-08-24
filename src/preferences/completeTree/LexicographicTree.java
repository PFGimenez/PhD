package preferences.completeTree;

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
 * Arbre lexicographique incomplet
 * @author pgimenez
 *
 */

public class LexicographicTree extends LexicographicStructure
{
	private static final long serialVersionUID = -2858953018327076982L;
	// un enfant peut être un LexicographicTree ou un LexicographicOrder
	private LexicographicStructure[] enfants;
	
	public LexicographicTree(String variable, int nbMod, HeuristiqueOrdre h)
	{
		super(variable, nbMod, h);
		enfants = null;
	}
	
	protected void affichePrivate(BufferedWriter output) throws IOException
	{
		output.write(nb+" [label="+variable+"];");
		output.newLine();
		if(enfants != null)
		{
			for(int i = 0; i<nbMod; i++)
			{
				enfants[i].affichePrivate(output);
				output.write(nb+" -> "+enfants[i].nb+" [label=\""+ordrePref.get(i)+"\"];");
				output.newLine();
			}
		}
		else
			for(int i = 0; i<nbMod; i++)
			{
				output.write(++nbS+" [style=invisible]");
				output.write(nb+" -> "+nbS+" [label=\""+ordrePref.get(i)+"\"];");
				output.newLine();
			}
			
	}
	
	public void updateBase(BigInteger base)
	{
		this.base = base.divide(BigInteger.valueOf(nbMod));
		if(enfants != null)
			for(LexicographicStructure e : enfants)
				e.updateBase(this.base);
	}
	
	public void setEnfant(int indice, LexicographicStructure enfant)
	{
		if(this.enfants == null)
			this.enfants = new LexicographicStructure[nbMod];
		this.enfants[indice] = enfant;
	}
	
	public BigInteger infereRang(ArrayList<String> element, ArrayList<String> ordreVariables)
	{
		int index = ordreVariables.indexOf(variable);
		String value = element.get(index);
		ordreVariables.remove(index);
		element.remove(index);
		if(enfants == null)
			return base.multiply(BigInteger.valueOf(getPref(value)));
		else
		{
			int nbFils = ordrePref.indexOf(value);
			BigInteger tmp = enfants[nbFils].infereRang(element, ordreVariables);
//			if(tmp < 0)
//				throw new ArithmeticException();
			return base.multiply(BigInteger.valueOf(getPref(value))).add(tmp);
		}
	}
	
	public String infereBest(String varARecommander, ArrayList<String> possibles, HashMap<String, String> valeurs)
	{
		if(variable.equals(varARecommander))
		{
			for(int i = 0; i < nbMod-1; i++)
				if(possibles == null || possibles.contains(getPref(i)))
					return getPref(i);
			return getPref(nbMod-1);
		}
		
		String val = valeurs.get(variable);
		
		if(val == null) // variable non instanciée : on prend le meilleur
			return enfants[0].infereBest(varARecommander, possibles, valeurs);
		else // variable instanciée
			return enfants[getPref(val)].infereBest(varARecommander, possibles, valeurs);
	}

	public int getRessemblance(LexicographicStructure other)
	{
		if(other instanceof LexicographicOrder)
			return other.getRessemblance(this);

		LexicographicTree otherT = (LexicographicTree) other;
		if(otherT.variable.equals(variable) && otherT.getPref(0).equals(getPref(0)))
		{
//			System.out.println("Egal");
			if(enfants == null || otherT.enfants == null)
				return 1;
			else
				return 1 + enfants[0].getRessemblance(otherT.enfants[0]);
		}
//		System.out.println(otherT.variable+" "+variable);
//		System.out.println(otherT.getPref(0)+" "+getPref(0));
//		System.out.println(otherT.variable.equals(variable)+" "+otherT.getPref(0).equals(getPref(0)));
		return 0;
	}
	
	public ArrayList<String> getVarOrdre()
	{
		ArrayList<String> out;
		if(enfants == null || !(enfants[0] instanceof LexicographicTree))
			out = new ArrayList<String>();
		else
			out = ((LexicographicTree)enfants[0]).getVarOrdre();
		out.add(0,variable);
		return out;
	}
	
	public HashMap<String, String> getConfigurationAtRank(BigInteger r)
	{
		// On est à la feuille
		if(enfants == null)
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
					HashMap<String, String> out = enfants[i-1].getConfigurationAtRank(r.mod(base));
					out.put(variable, getPref(i-1));
					return out;
				}
			System.out.println("ERREUR");
			return null;
		}
	}
	
	public ArrayList<String> getPrefOrdre()
	{
		ArrayList<String> out;
		if(enfants == null || !(enfants[0] instanceof LexicographicTree))
			out = new ArrayList<String>();
		else
			out = ((LexicographicTree)enfants[0]).getVarOrdre();
		out.add(0,getPref(0));
		return out;
	}
}
