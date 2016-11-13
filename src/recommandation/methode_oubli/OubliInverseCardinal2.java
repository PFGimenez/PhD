package recommandation.methode_oubli;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import compilateur.SALADD;
import compilateur.VDD;
import compilateur.Var;


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
 * Méthode d'oubli dans laquelle on construit l'ensemble des variables qu'on conserve (d'où l'appelation d'inverse)
 * @author Pierre-François Gimenez
 *
 */

public class OubliInverseCardinal2 extends MethodeOubliRestauration {

	public OubliInverseCardinal2(int seuil)
	{
		super(seuil, null);
	}
	
	@Override
	public Map<String, Double> recommandation(Var v, HashMap<String, String> historiqueOperations, VDD vdd, ArrayList<String> possibles, SALADD contraintes)
	{
		nbOubli = 0;
		dejavu.clear();
		dejavuVal.clear();
		Map<String, Double> m;
    	
		/**
		 * Si on connaît moins ou autant de variables que l'objectif, on oublie rien
		 */
		if(historiqueOperations.size() <= 2)
			return vdd.countingpondereOnPossibleDomain(v, possibles);
		
		for(String s: historiqueOperations.keySet())
			vdd.deconditioner(vdd.getVar(s));
		
		int size = historiqueOperations.size();
		String[] set = new String[size];
		historiqueOperations.keySet().toArray(set);

		int tailleMin = vdd.countingpondere();
		String m1 = null, m2 = null;
		
		for(int i = 0; i < size-1; i++)
		{
			String s1 = set[i];
			vdd.conditioner(vdd.getVar(s1), vdd.getVar(s1).conv(historiqueOperations.get(s1)));
			for(int k = i+1; k < size; k++)
			{
				String s2 = set[k];
				vdd.conditioner(vdd.getVar(s2), vdd.getVar(s2).conv(historiqueOperations.get(s2)));

				// on garde le plus petit ensemble
				int tailleDomaine = vdd.countingpondere();
				if(tailleDomaine >= seuil && tailleDomaine < tailleMin)
				{
					tailleMin = tailleDomaine;
					m1 = s1;
					m2 = s2;
				}
				vdd.deconditioner(vdd.getVar(s2));
			}	
			vdd.deconditioner(vdd.getVar(s1));
		}
			
		vdd.conditioner(vdd.getVar(m1), vdd.getVar(m1).conv(historiqueOperations.get(m1)));
		vdd.conditioner(vdd.getVar(m2), vdd.getVar(m2).conv(historiqueOperations.get(m2)));

		m=vdd.countingpondereOnPossibleDomain(v, possibles);
		vdd.deconditioner(vdd.getVar(m1));
		vdd.deconditioner(vdd.getVar(m1));
		vdd.deconditioner(vdd.getVar(m1));

		for(String s : historiqueOperations.keySet())
		{
			vdd.conditioner(vdd.getVar(s), vdd.getVar(s).conv(historiqueOperations.get(s)));
		}
		
    	return m;
	}

}
