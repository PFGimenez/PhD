package methode_oubli;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import test_independance.TestIndependance;
import JSci.maths.statistics.NormalDistribution;
import JSci.maths.statistics.TDistribution;
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

public class OubliParDSeparationTestStudent extends MethodeDSeparation {

	private TDistribution t;
	private NormalDistribution norm = new NormalDistribution();
	private double[] seuils = new double[30];
	private double seuilNorm;
	
	public OubliParDSeparationTestStudent(int seuil, TestIndependance test, double seuilProba)
	{
		super(seuil, test);
		for(int n = 1; n < 30; n++)
		{
			t = new TDistribution(n);
			seuils[n] = t.cumulative(1-seuilProba/2);
		}
		seuilNorm = norm.cumulative(1-seuilProba/2);
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
		
//		int dfcorr = 1;
		
		for(String s: historiqueOperations.keySet())
			connues.add(vdd.getVar(s).name);

		rechercheEnProfondeur(connues, v.name, false, 0);
		
		for(String s: historiqueOperations.keySet())
		{
			Var connue = vdd.getVar(s);
//			dfcorr *= connue.domain;
			if(!done.contains(connue.name))
			{
	    		dejavu.add(connue);
	    		dejavuVal.add(historiqueOperations.get(s));
	    		vdd.deconditioner(connue);
	    		nbOubli++;
			}
		}
//		System.out.println("Oubli d-sep: "+nbOubli);
//		int nbOubliSauv = nbOubli;

		int seuil=10*possibles.size();
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
	//    				curr = testg2.computeInd(v, varcurr, vdd, dfcorr);
	//    				vdd.conditioner(varcurr, varcurr.conv(historiqueOperations.get(i+1)));
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
    	
    	
//		System.out.println("Oubli seuil: "+(nbOubli-nbOubliSauv));
		
		/*
		int seuil=200;    	
    	while(vdd.countingpondere()<seuil)
    	{
    		int distanceMax = Integer.MIN_VALUE;
    		Var varmin = null;
    		String val = null;
    		for(int i=0; i<historiqueOperations.size(); i+=2)
    		{
    			Var varcurr = vdd.getVar(historiqueOperations.get(i));
    			if(!dejavu.contains(varcurr) && distances.get(varcurr.name) > distanceMax)
    			{
    				distanceMax = distances.get(varcurr.name);
    				varmin = varcurr;
    				val=historiqueOperations.get(i+1);
    			}
    		}
//    		System.out.println(varmin.name);
    		nbOubli++;
    		dejavu.add(varmin);
    		dejavuVal.add(val);
    		vdd.deconditioner(varmin);
    	}
*/
    	m = vdd.countingpondereOnPossibleDomain(v, possibles);
    	
    	for(int i = 0; i < dejavu.size(); i++)
    	{
        	vdd.conditioner(dejavu.get(i), dejavu.get(i).conv(dejavuVal.get(i)));
    	}

    	return m;
	}

	@Override
	public int getNbOublis() {
		return nbOubli;
	}
	
}
