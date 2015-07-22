package recommandation.methode_oubli;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

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
 * Méthode d'oubli par d-séparation incomplète avec comme critère d'arrêt un test statistique
 * @author pgimenez
 *
 */

public class OubliParDSeparationIncompleteTestStudent extends MethodeDSeparation {

	private NormalDistribution norm = new NormalDistribution();
	private double seuilNorm;
	
	private int seuil2;

	public OubliParDSeparationIncompleteTestStudent(int seuilDSepare, int seuilNonDSepare, TestIndependance test, double seuilProba, String prefixData)
	{
		super(seuilDSepare, test, prefixData);
		this.seuil2 = seuilNonDSepare;
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
		
//		int dfcorr = 1;
		
		for(String s: historiqueOperations.keySet())
			connues.add(vdd.getVar(s).name);

		rechercheEnProfondeur(connues, v.name, false, 0);

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
	    		nbOubli++;
	    		if(varminnotdone == null)
	    		{
	    			if(n < seuil2)
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
	    		n = vdd.countingpondere();
	    	}
    	}
    	
    	else
    	{
    		int n = vdd.countingpondere();
	    	while(n < seuil){
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
	    		nbOubli++;
	    		if(varminnotdone == null)
	    		{
	    			if(n < seuil2)
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
	    		n = vdd.countingpondere();
	    	}
    	}
    	
    	m = vdd.countingpondereOnPossibleDomain(v, possibles);
    	
    	super.reconditionne(vdd);

    	return m;
	}
	
}
