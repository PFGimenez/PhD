package preferences.completeTree;

import java.io.BufferedWriter;
import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;

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
 * @author Pierre-François Gimenez
 *
 */

public class LexicographicTree extends LexicographicStructure
{
	private static final long serialVersionUID = -2858953018327076982L;
	// un enfant peut être un LexicographicTree ou un LexicographicOrder
	private LexicographicStructure[] enfants;
	
	public LexicographicTree(String variable, int nbMod, boolean split)
	{
		super(variable, nbMod);
		enfants = null;
		this.split = split;
	}
	
	protected void affichePrivate(BufferedWriter output) throws IOException
	{
		output.write(nb+" [label="+variable+"];");
		output.newLine();
		if(!split && enfants != null)
		{
			enfants[0].affichePrivate(output);
			/*
			output.write(nb+" -> "+enfants[0].nb+" [label=\"");
			for(int i = 0; i<nbMod - 1; i++)
				output.write(ordrePref.get(i)+">");
			output.write(ordrePref.get(nbMod-1));
			output.write("\"];");
			output.newLine();*/
			
			for(int i = 0; i<nbMod; i++)
			{
				output.write(nb+" -> "+enfants[0].nb+" [label=\""+ordrePref.get(i)+"\"];");
				output.newLine();
			}	
		}
		else if(enfants != null)
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
				output.write(++nbS+" [style=invisible];");				
				output.newLine();
				output.write(nb+" -> "+nbS+" [label=\""+ordrePref.get(i)+"\"];");
				output.newLine();
			}
			
	}
		
	/**
	 * Met à jour la base des enfants existants
	 */
	public void updateBaseChildren()
	{
		if(enfants != null)
		{
			if(split)
			{
				for(LexicographicStructure e : enfants)
					if(e != null)
						e.updateBaseNoRecursive(base);
			}
			else
				enfants[0].updateBaseNoRecursive(base);
		}
		else
			System.err.println("Enfants == null !");
	}
	
	public void updateBase(BigInteger base)
	{
		this.base = base.divide(BigInteger.valueOf(nbMod));
		if(enfants != null)
			if(split)
				for(LexicographicStructure e : enfants)
					e.updateBase(this.base);
			else
				enfants[0].updateBase(this.base);
	}
	
	public void setEnfant(int indice, LexicographicStructure enfant)
	{
		if(enfants == null)
			enfants = new LexicographicStructure[nbMod];
		if(split)
			enfants[indice] = enfant;
		else // pas de split : tous les enfants sont les mêmes
		{
			for(int i = 0; i < enfants.length; i++)
				enfants[i] = enfant;
		}
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
				if(r.compareTo(base.multiply(BigInteger.valueOf(i))) < 0)
				{
					HashMap<String, String> out = enfants[i-1].getConfigurationAtRank(r.mod(base));
					out.put(variable, getPref(i-1));
					return out;
				}
			System.err.println("Rang trop grand");
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

	@Override
	protected int getMaxNb()
	{
		if(enfants == null)
			return nb;
		else
		{
			int out = nb;
			if(split)
			{
				for(LexicographicStructure e : enfants)
					out = Math.max(out, e.getMaxNb());
				return out;
			}
			else
				return Math.max(out, enfants[0].getMaxNb());
		}
	}

	@Override
	public int getNbNoeuds()
	{
		if(enfants == null)
			return 1;
		else
		{
			if(split)
			{
				int out = 1;
				for(LexicographicStructure e : enfants)
					out += e.getNbNoeuds();
				return out;
			}
			else
				return 1 + enfants[0].getNbNoeuds();
		}
	}

	@Override
	protected ArrayList<LexicographicStructure> getEnfants() {
		ArrayList<LexicographicStructure> out = new ArrayList<LexicographicStructure>();
		if(enfants != null)
		{
			if(split)
				for(LexicographicStructure e : enfants)
					out.add(e);
			else
				for(int i = 0; i < nbMod; i++)
					out.add(enfants[0]);
		}
		return out;
	}

}
