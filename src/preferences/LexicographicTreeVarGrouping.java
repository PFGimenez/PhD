package preferences;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;

import heuristiques.HeuristiqueOrdre;

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
 * Arbre lexicographique incomplet dont les nœuds peuvent concerner plusieurs variables
 * @author pgimenez
 *
 */

public class LexicographicTreeVarGrouping extends LexicographicStructure
{
	private static final long serialVersionUID = -2858953018327076982L;
	// un enfant peut être un LexicographicTree ou un LexicographicOrder
	private LexicographicStructure[] enfants;
	private String[] variables;
	private int nbVar;
	
	public LexicographicTreeVarGrouping(String[] variables, int nbMod, HeuristiqueOrdre h)
	{
		super(null, nbMod, h);
		this.variables = variables;
		nbVar = variables.length;
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
				output.write(nb+" -> "+enfants[i].nb+" [label="+ordrePref.get(i)+"];");
				output.newLine();
			}
		}
	}
	
	public void updateBase(long base)
	{
		this.base = base/nbMod;
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
	
	public long infereRang(ArrayList<String> element, ArrayList<String> ordreVariables)
	{
		int index = ordreVariables.indexOf(variable);
		String value = element.get(index);
		ordreVariables.remove(index);
		element.remove(index);
		if(enfants == null)
			return getPref(value)*base;
		else
		{
			int nbFils = ordrePref.indexOf(value);
			long tmp = enfants[nbFils].infereRang(element, ordreVariables);
			if(tmp < 0)
				throw new ArithmeticException();
			return getPref(value)*base + tmp;
		}
	}
	
	public String infereBest(String varARecommander, ArrayList<String> possibles, ArrayList<String> element, ArrayList<String> ordreVariables)
	{
		if(variable.equals(varARecommander))
		{
			for(int i = 0; i < nbMod-1; i++)
				if(possibles.contains(getPref(i)))
					return getPref(i);
			return getPref(nbMod-1);
		}
		
		int nbEnfant;
		int index = ordreVariables.indexOf(variable);
		if(index == -1)
			nbEnfant = 0;
		else
		{
			String value = element.get(index);
			ordreVariables.remove(index);
			element.remove(index);
			nbEnfant = getPref(value);
		}
		return enfants[nbEnfant].infereBest(varARecommander, possibles, element, ordreVariables);
	}


	
}
