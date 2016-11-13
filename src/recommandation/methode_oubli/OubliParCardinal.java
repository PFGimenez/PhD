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
 * Méthode d'oubli dans laquelle on oublie les variables les plus contraignantes
 * @author Pierre-François Gimenez
 *
 */

public class OubliParCardinal extends MethodeDSeparation {

	private int seuil;
	private boolean oubliPlusCourant;
	
	public OubliParCardinal(int seuil, String dataset, boolean oubliPlusCourant)
	{
		super(seuil, null, dataset);
		this.seuil = seuil;
		this.oubliPlusCourant = oubliPlusCourant;
	}
	
	@Override
	public Map<String, Double> recommandation(Var v, HashMap<String, String> historiqueOperations, VDD vdd, ArrayList<String> possibles, SALADD contraintes)
	{
		nbOubli = 0;
		dejavu.clear();
		dejavuVal.clear();
		Map<String, Double> m;
		int n, nnext = seuil, ntemp;

		done.clear();
		ArrayList<String> connues = new ArrayList<String>();
		
		for(String s: historiqueOperations.keySet())
			connues.add(vdd.getVar(s).name);

		// Oubli par d-séparation
		rechercheEnProfondeur(connues, v.name, false, contraintes);
		
		for(String s: historiqueOperations.keySet())
		{
			Var connue = vdd.getVar(s);
			if(!done.contains(connue.name))
			{
	    		dejavu.add(connue);
	    		dejavuVal.add(historiqueOperations.get(s));
	    		vdd.deconditioner(connue);
	    		nbOubli++;
			}
		}
		
		n = vdd.countingpondere();
		
		// Oubli par cardinalité
		while(n < seuil){
//				System.out.println("Inférieur au seuil, oubli");
			boolean first = true;
			double min = -1, curr;
			Var varmin = null, varcurr;
			String val = "";
			for(String s: historiqueOperations.keySet())
			{
				varcurr=vdd.getVar(s);
				if(!dejavu.contains(varcurr))
				{
					vdd.deconditioner(varcurr);
					ntemp = vdd.countingpondere();
					curr = ntemp - n;
					
					// Si on oublie le plus courant et que cette variable n'apporte rien, on l'oubli directement
					if(oubliPlusCourant && curr == 0)
					{
						// n ne change pas car ntemp = n
						// varcurr est déjà déconditionnée
						dejavu.add(varcurr);
						dejavuVal.add(historiqueOperations.get(s));
						nbOubli++;
						continue;
					}
					
					vdd.conditioner(varcurr, varcurr.conv(historiqueOperations.get(s)));
					if(first || (oubliPlusCourant && curr < min) || (!oubliPlusCourant && curr > min))
					{
	    				first = false;
	    				min = curr;
	    				varmin = varcurr;
	    				val = historiqueOperations.get(s);
	    				nnext = ntemp;
	    			}
	    		}
			}
			nbOubli++;
			dejavu.add(varmin);
			dejavuVal.add(val);
			vdd.deconditioner(varmin);
			n = nnext;
		}
    	
		m=vdd.countingpondereOnPossibleDomain(v, possibles);

		super.reconditionne(vdd);

    	return m;
	}
	
	public String toString()
	{
		if(oubliPlusCourant)
			return getClass().getSimpleName() + "-C2-"+seuil;
		else
			return getClass().getSimpleName() + "-R2-"+seuil;
	}


}
