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
 * Méthode d'oubli dans laquelle on construit l'ensemble des variables qu'on conserve (d'où l'appelation d'inverse)
 * @author pgimenez
 *
 */

public class OubliInverseIndependance extends MethodeOubliRestauration {

	private int nbVarConservees;
	
	public OubliInverseIndependance(int seuil, TestIndependance test, int nbVarConservees)
	{
		super(seuil, test);
		this.nbVarConservees = nbVarConservees;
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
//		if(historiqueOperations.size() <= nbVarConservees)
//			return vdd.countingpondereOnPossibleDomain(v, possibles);
		
//		nbOubli = historiqueOperations.size() - nbVarConservees;
		
		for(String s: historiqueOperations.keySet())
			vdd.deconditioner(vdd.getVar(s));
		
		for(int i = 0; i < nbVarConservees; i++)
		{
			boolean first = true;
			double min = -1, curr;
			Var varmin = null, varcurr;
			String val = "";
			for(String s: historiqueOperations.keySet())
			{
				varcurr=vdd.getVar(s);
				if(!dejavu.contains(varcurr)){
	    			curr=variance.get(v, varcurr);
					if(first || test.estPlusIndependantQue(min,curr)){
	    				first = false;
	    				min = curr;
	    				varmin = varcurr;
	    				val = historiqueOperations.get(s);
	    			}
	    		}
			}
			
			// Plus de variable disponible
			if(varmin == null)
				break;
			
			vdd.conditioner(varmin, varmin.conv(val));
			
			// Si on passe sous le seuil, on arrête la recherche
			if(vdd.countingpondere() < seuil*(possibles.size()-1))
			{
				vdd.deconditioner(varmin);
				break;
			}

			dejavu.add(varmin);
			dejavuVal.add(val);
		}

		m = vdd.countingpondereOnPossibleDomain(v, possibles);

		for(Var var : dejavu)
			vdd.deconditioner(var);

		for(String s : historiqueOperations.keySet())
		{
			vdd.conditioner(vdd.getVar(s), vdd.getVar(s).conv(historiqueOperations.get(s)));
		}
		
    	return m;
	}

	public String toString()
	{
		return super.toString()+"-"+nbVarConservees+"vars"+"-"+test.getClass().getSimpleName();
	}
	
	
}
