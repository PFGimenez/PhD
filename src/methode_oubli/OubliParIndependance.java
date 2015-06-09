package methode_oubli;

import java.util.ArrayList;
import java.util.Map;

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

public class OubliParIndependance implements MethodeOubli {

	private int nbOubli;
	private Variance variance = null;
	private TestIndependance test;
	
	public OubliParIndependance(TestIndependance test)
	{
		this.test = test;
	}
	
	@Override
	public void learn(SALADD saladd)
	{
		variance=saladd.calculerVarianceHistorique(test, "smallhist/smallvariance");
	}
	
	@Override
	public Map<String, Double> recommandation(Var v, ArrayList<String> historiqueOperations, VDD vdd, ArrayList<String> possibles)
	{
		nbOubli = 0;
		Map<String, Double> m;
		int seuil=1000;
		//System.out.println("avant : "+uht.size());
    	ArrayList<Var> dejavu=new ArrayList<Var>();
    	ArrayList<String> dejavuVal=new ArrayList<String>();
    	
    	while(vdd.countingpondere()<seuil){
    		boolean first = true;
    		double min=-1, curr;
    		Var varmin=null, varcurr;
    		String val="";
    		for(int i=0; i<historiqueOperations.size(); i+=2){
    			varcurr=vdd.getVar(historiqueOperations.get(i));
    			if(!dejavu.contains(varcurr)){
	    			curr=variance.get(v, varcurr);
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
