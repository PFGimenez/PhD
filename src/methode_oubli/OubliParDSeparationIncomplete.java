package methode_oubli;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import test_independance.TestIndependance;
import br4cp.VDD;
import br4cp.Var;

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
 * Méthode d'oubli par d-sépration
 * @author pgimenez
 *
 */

public class OubliParDSeparationIncomplete extends MethodeDSeparation {

	public OubliParDSeparationIncomplete(int seuil, TestIndependance test)
	{
		super(seuil, test);
	}
		
	@Override
	public Map<String, Double> recommandation(Var v, HashMap<String, String> historiqueOperations, VDD vdd, ArrayList<String> possibles)
	{
		nbOubli = 0;
    	ArrayList<Var> dejavu = new ArrayList<Var>();
    	ArrayList<String> dejavuVal = new ArrayList<String>();
		ArrayList<String> connues = new ArrayList<String>();
		Map<String, Double> m;
		done.clear();
		
		for(String s: historiqueOperations.keySet())
			connues.add(vdd.getVar(s).name);

		rechercheEnProfondeur(connues, v.name, false, 0);

		int seuil=300*(possibles.size()-1);    	
		int seuil2=100*(possibles.size()-1);    	
		int n = vdd.countingpondere();
		
    	while(n<seuil){
    		boolean first = true, firstNotDone = true;
    		double min=-1, curr, minnotdone=-1;
    		Var varmin=null, varminnotdone=null, varcurr;
    		String val="", valnotdone="";
    		for(String s: historiqueOperations.keySet())
    		{
    			varcurr=vdd.getVar(s);
    			if(!dejavu.contains(varcurr)){
	    			curr=variance.get(v, varcurr);    				
//    				curr = testg2.computeInd(v, varcurr, vdd, dfcorr);
//    				vdd.conditioner(varcurr, varcurr.conv(historiqueOperations.get(i+1)));
	    			if((firstNotDone || test.estPlusIndependantQue(curr,minnotdone)) && !done.contains(varcurr.name)){
	    				firstNotDone = false;
	    				minnotdone=curr;
	    				varminnotdone=varcurr;
	    				valnotdone=historiqueOperations.get(s);
	    			}
	    			else if(first || test.estPlusIndependantQue(curr,min)){
	    				first = false;
	    				min=curr;
	    				varmin=varcurr;
	    				val=historiqueOperations.get(s);
	    			}
	    		}
    		}
    		if(varminnotdone == null)
    		{
    			if(n < seuil2/4)
    			{
		    		dejavu.add(varmin);
		    		dejavuVal.add(val);
		    		vdd.deconditioner(varmin);
    			}
    			else
    				break;
    		}
    		else
    		{
	    		dejavu.add(varminnotdone);
	    		dejavuVal.add(valnotdone);
	    		vdd.deconditioner(varminnotdone);
    		}
    		nbOubli++;
    		n = vdd.countingpondere();
    	}
  
    	m = vdd.countingpondereOnPossibleDomain(v, possibles);
    	
    	super.reconditionne(vdd);

    	return m;
	}

	@Override
	public int getNbOublis() {
		return nbOubli;
	}
	
}
