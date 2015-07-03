package recommandation.methode_oubli;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import methode_oubli.MethodeDSeparation;
import compilateur.VDD;
import compilateur.Var;
import compilateur.test_independance.TestIndependance;
import JSci.maths.statistics.NormalDistribution;

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
 * Méthode d'oubli par d-séparation avec comme critère d'arrêt un test statistique
 * @author pgimenez
 *
 */

public class OubliParDSeparationTestStudent extends MethodeDSeparation {

	private NormalDistribution norm = new NormalDistribution();
	private double seuilNorm;
	
	public OubliParDSeparationTestStudent(int seuil, TestIndependance test, double seuilProba)
	{
		super(seuil, test);
		seuilNorm = norm.cumulative(1-seuilProba/2);
	}
	
	@Override
	public Map<String, Double> recommandation(Var v, HashMap<String, String> historiqueOperations, VDD vdd, ArrayList<String> possibles)
	{
		done.clear();
		nbOubli = 0;
		dejavu.clear();
		dejavuVal.clear();
		ArrayList<String> connues = new ArrayList<String>();
		Map<String, Double> m;
		
		for(String s: historiqueOperations.keySet())
			connues.add(vdd.getVar(s).name);

		rechercheEnProfondeur(connues, v.name, false, 0);
		
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

    	if(possibles.size() == 2)
    	{
        	int n = vdd.countingpondere();
	    	while(n < seuil)
	    	{
	    		if(n >= 30)
	    		{
    	    		vdd.conditioner(v, v.conv(possibles.get(0)));
    	        	double n0 = vdd.countingpondere();
    	        	vdd.deconditioner(v);
    	        	double p = n0/n;
    	    		double statistique = (p - 0.5) * Math.sqrt(n) / (p*(1-p));

    	    		// Si le test est significative, on ne fait plus d'oubli
    	    		if(Math.abs(statistique) > seuilNorm)
    	    			break;
	    		}
	    		boolean first = true;
	    		double min=-1, curr;
	    		Var varmin=null, varcurr;
	    		String val="";
	    		for(String s: historiqueOperations.keySet())
	    		{
	    			varcurr=vdd.getVar(s);
	    			if(!dejavu.contains(varcurr)){
		    			curr=variance.get(v, varcurr);
	    				if(first || test.estPlusIndependantQue(curr,min)){
		    				first = false;
		    				min=curr;
		    				varmin=varcurr;
		    				val=historiqueOperations.get(s);
		    			}
		    		}
	    		}
	    		nbOubli++;
	    		dejavu.add(varmin);
	    		dejavuVal.add(val);
	    		vdd.deconditioner(varmin);
	    		n = vdd.countingpondere();
	    	}
    	}
    	
    	else
    	{
    		super.restaure(historiqueOperations, vdd, v);
    	}
    	
       	m = vdd.countingpondereOnPossibleDomain(v, possibles);

    	super.reconditionne(vdd);

    	return m;
	}
	
}
