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
	private LexicographicTree[] enfant;
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
			for(LexicographicTree e : enfant)
				e.updateBase(this.base);
	}
	
	public void setEnfant(LexicographicTree[] enfant)
	{
		this.enfant = enfant;
	}
	
	public long infereRang(ArrayList<String> element, ArrayList<String> ordreVariables)
	{
		//TODO
		int index = ordreVariables.indexOf(variable);
		String value = element.get(index);
		ordreVariables.remove(index);
		element.remove(index);
		return 0;
	}

	
}
