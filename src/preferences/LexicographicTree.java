package preferences;

import java.util.ArrayList;

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
	// un enfant peut être un LexicographicTree ou un LexicographicOrder
	private LexicographicStructure[] enfant;
	private long base;
	
	public LexicographicTree(String variable, int nbMod)
	{
		super(variable, nbMod);
		enfant = null;
	}
	
	public void updateBase(long base)
	{
		this.base = base/nbMod;
		if(enfant != null)
			for(LexicographicStructure e : enfant)
				e.updateBase(this.base);
	}
	
	public void setEnfant(int indice, LexicographicStructure enfant)
	{
		if(this.enfant == null)
			this.enfant = new LexicographicStructure[nbMod];
		this.enfant[indice] = enfant;
	}
	
	public long infereRang(ArrayList<String> element, ArrayList<String> ordreVariables)
	{
		int index = ordreVariables.indexOf(variable);
		String value = element.get(index);
		ordreVariables.remove(index);
		element.remove(index);
		if(enfant == null)
			return getPref(value)*base;
		else
		{
			int nbFils = ordrePref.indexOf(element);
			long tmp = enfant[nbFils].infereRang(element, ordreVariables);
			if(tmp < 0)
				throw new ArithmeticException();
			return getPref(value)*base + tmp;
		}
	}

	
}
