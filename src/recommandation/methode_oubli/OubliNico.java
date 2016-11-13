package recommandation.methode_oubli;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import compilateur.SALADD;
import compilateur.VDD;
import compilateur.Var;
import compilateur.test_independance.TestIndependance;


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
 * Méthode d'oubli dans laquelle on oublie les variables les plus indépendantes jusqu'à atteindre un certain seuil
 * @author Pierre-François Gimenez
 *
 */

public class OubliNico extends MethodeOubliRestauration {

	public OubliNico(int seuil, TestIndependance test)
	{
		super(seuil, test);
	}
	
	@Override
	public Map<String, Double> recommandation(Var v, HashMap<String, String> historiqueOperations, VDD vdd, ArrayList<String> possibles, SALADD contraintes)
	{
		nbOubli = 0;
		dejavu.clear();
		dejavuVal.clear();
		Map<String, Double> m;
    	
		while(vdd.countingpondere() < seuil){
//			System.out.println("Inférieur au seuil, oubli");
			boolean first = true;
			double min = -1, curr;
			Var varmin = null, varcurr;
			String val = "";
			for(String s: historiqueOperations.keySet())
			{
				varcurr=vdd.getVar(s);
				if(!dejavu.contains(varcurr)){
	    			curr=variance.get(v, varcurr);
					if(first || test.estPlusIndependantQue(curr,min)){
	    				first = false;
	    				min = curr;
	    				varmin = varcurr;
	    				val = historiqueOperations.get(s);
	    			}
	    		}
			}
			nbOubli++;
			dejavu.add(varmin);
			dejavuVal.add(val);
			vdd.deconditioner(varmin);
		}
    	
		m=vdd.countingpondereOnPossibleDomain(v, possibles);

		super.reconditionne(vdd);

    	return m;
	}

}
