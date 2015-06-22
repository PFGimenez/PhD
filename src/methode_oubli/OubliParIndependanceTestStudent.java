package methode_oubli;

import java.util.ArrayList;
import java.util.Map;

import JSci.maths.statistics.NormalDistribution;
import JSci.maths.statistics.TDistribution;
import br4cp.SALADD;
import br4cp.VDD;
import br4cp.Var;
import br4cp.Variance;
import test_independance.TestIndependance;

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
 * @author pgimenez
 *
 */

public class OubliParIndependanceTestStudent implements MethodeOubli {

	private int nbOubli;
	private Variance variance = null;
	private TestIndependance test;
	private TDistribution t;
	private NormalDistribution norm = new NormalDistribution();
	private double[] seuils = new double[30];
	private double seuilNorm;
	
	public OubliParIndependanceTestStudent(TestIndependance test)
	{
		double seuilProba = 0.05; // 5%

		this.test = test;
		for(int n = 1; n < 30; n++)
		{
			t = new TDistribution(n);
			seuils[n] = t.cumulative(1-seuilProba/2);
		}
		seuilNorm = norm.cumulative(1-seuilProba/2);
	}
	
	@Override
	public void learn(SALADD saladd)
	{
		variance=saladd.calculerVarianceHistorique(test, "smallhist/smallvariance");
	}
	
	@Override
	public Map<String, Double> recommandation(Var v, ArrayList<String> historiqueOperations, VDD vdd, ArrayList<String> possibles)
	{
//		int dfcorr = 1;
				
/*		for(int i = 0; i < historiqueOperations.size(); i += 2)
		{
			Var connue = vdd.getVar(historiqueOperations.get(i));
			dfcorr *= connue.domain;
		}*/
		
		nbOubli = 0;
		Map<String, Double> m;
		int seuil=50*possibles.size();
		//System.out.println("avant : "+uht.size());
    	ArrayList<Var> dejavu=new ArrayList<Var>();
    	ArrayList<String> dejavuVal=new ArrayList<String>();
    	
    	if(possibles.size() == 2)
    	{
        	int n = vdd.countingpondere();
	    	while(n < seuil)
	    	{
/*	    		if(n > 1 && n < 30)
	    		{
    	    		vdd.conditioner(v, v.conv(possibles.get(0)));
    	        	double n0 = vdd.countingpondere();
    	        	vdd.deconditioner(v);
    	        	double p = n0/n;
    	    		double statistique = (p - 0.5) * Math.sqrt(n) / (p*(1-p));
    	    		
    	    		// Si le test est significative, on ne fait plus d'oubli
    	    		if(Math.abs(statistique) > seuils[n-1])
    	    			break;
	    		}
	    		else */if(n >= 30)
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
	    		for(int i=0; i<historiqueOperations.size(); i+=2){
	    			varcurr=vdd.getVar(historiqueOperations.get(i));
	    			if(!dejavu.contains(varcurr)){
		    			curr=variance.get(v, varcurr);
	//    				curr = testg2.computeInd(v, varcurr, vdd, dfcorr);
	//    				vdd.conditioner(varcurr, varcurr.conv(historiqueOperations.get(i+1)));
	    				if(first || test.estPlusIndependantQue(curr,min)){
		    				first = false;
		    				min=curr;
		    				varmin=varcurr;
		    				val=historiqueOperations.get(i+1);
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
	    	while(vdd.countingpondere()<seuil){
	    		boolean first = true;
	    		double min=-1, curr;
	    		Var varmin=null, varcurr;
	    		String val="";
	    		for(int i=0; i<historiqueOperations.size(); i+=2){
	    			varcurr=vdd.getVar(historiqueOperations.get(i));
	    			if(!dejavu.contains(varcurr)){
		    			curr=variance.get(v, varcurr);
	//    				curr = testg2.computeInd(v, varcurr, vdd, dfcorr);
	//    				vdd.conditioner(varcurr, varcurr.conv(historiqueOperations.get(i+1)));
	    				if(first || test.estPlusIndependantQue(curr,min)){
		    				first = false;
		    				min=curr;
		    				varmin=varcurr;
		    				val=historiqueOperations.get(i+1);
		    			}
		    		}
	    		}
	    		nbOubli++;
	    		dejavu.add(varmin);
	    		dejavuVal.add(val);
	    		vdd.deconditioner(varmin);
	    	}
    	}
    	
//    	System.out.println(nbOubli+" oublis");
    	m=vdd.countingpondereOnPossibleDomain(v, possibles);
    	for(int i=0; i<dejavu.size(); i++){
        	vdd.conditioner(dejavu.get(i), dejavu.get(i).conv(dejavuVal.get(i)));
    	}

    	return m;
	}

	@Override
	public int getNbOublis() {
		return nbOubli;
	}
	
}
