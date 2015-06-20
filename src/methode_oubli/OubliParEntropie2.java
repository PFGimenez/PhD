package methode_oubli;

import java.util.ArrayList;
import java.util.Map;

import br4cp.SALADD;
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
 * Méthode d'oubli dans laquelle on oublie les variables les plus indépendantes jusqu'à atteindre un certain seuil
 * @author pgimenez
 *
 */

public class OubliParEntropie2 implements MethodeOubli {

	private int nbOubli;

	@Override
	public void learn(SALADD saladd, String prefix_file_name)
	{}
	
	@Override
	public Map<String, Double> recommandation(Var v, ArrayList<String> historiqueOperations, VDD vdd, ArrayList<String> possibles)
	{
		nbOubli = 0;
		Map<String, Double> out;
    	ArrayList<Var> dejavu=new ArrayList<Var>();
    	ArrayList<String> dejavuVal=new ArrayList<String>();
    	
    	double gainMax;
    	
    	do {
        	gainMax = 0;
        	double tailleEnsemble = vdd.countingpondere();
        	Var varAOublier = null;
        	String valAOublier = null;
        	
    		for(int i=0; i<historiqueOperations.size(); i+=2)
    		{
    			Var varcurr=vdd.getVar(historiqueOperations.get(i));
    			if(!dejavu.contains(varcurr))
    			{
    				vdd.deconditioner(varcurr);

    				double infMu = 0.;
    				
    				int dom1=v.domain;
    				int dom2=varcurr.domain;
    				double[][] table=new double[dom1][dom2];
    	
    				//calcul des proba au cas par cas
    				for(int l=0; l<dom1; l++){
    					vdd.conditioner(v, l);
    					for(int k=0; k<dom2; k++){
    						vdd.conditioner(varcurr, k);
    						table[l][k]=vdd.countingpondere();
    						vdd.deconditioner(varcurr);

    					}
    					vdd.deconditioner(v);
    				}

    				double[] som1 = new double[dom1];
    				double[] som2 = new double[dom2];
    				for(int m = 0; m < dom1; m++)
    					som1[m] = 0.;
    				for(int n = 0; n < dom2; n++)
    					som2[n] = 0.;
    				
    				int sommeTotale = 0;
    				
    				for(int m = 0; m < dom1; m++)
    					for(int n = 0; n < dom2; n++)
    					{
    						sommeTotale += table[m][n];
    						som1[m] += table[m][n];
    						som2[n] += table[m][n];
    					}

    				for(int m = 0; m < dom1; m++)
    					for(int n = 0; n < dom2; n++)
    						if(table[m][n] != 0)
    							infMu += table[m][n]/sommeTotale*Math.log(table[m][n]*sommeTotale/(som1[m]*som2[n]));
    				infMu /= Math.log(v.domain);

	    			double nouvelleTailleEnsemble = vdd.countingpondere();
	    			vdd.conditioner(varcurr, varcurr.conv(historiqueOperations.get(i+1)));
//	    			System.out.println("infMu: "+infMu+", rapport: "+nouvelleTailleEnsemble/tailleEnsemble);
	    			double gain = Math.log(nouvelleTailleEnsemble / tailleEnsemble) - infMu;
	    			if(gain > gainMax)
	    			{
	    				gainMax = gain;
	    				varAOublier = varcurr;
	    				valAOublier = historiqueOperations.get(i+1);
	    			}
	    		}
    		}
    		if(gainMax > 0)
    		{
        		nbOubli++;
	    		dejavu.add(varAOublier);
	    		dejavuVal.add(valAOublier);
	    		vdd.deconditioner(varAOublier);
    		}
    	} while(gainMax > 0);
    	
//    	System.out.println(nbOubli+" oublis");
    	if(possibles!=null)
    		out=vdd.countingpondereOnPossibleDomain(v, possibles);
    	else
    		out=vdd.countingpondereOnFullDomain(v);
    	for(int i=0; i<dejavu.size(); i++){
        	vdd.conditioner(dejavu.get(i), dejavu.get(i).conv(dejavuVal.get(i)));
    	}

    	return out;
	}

	@Override
	public int getNbOublis() {
		return nbOubli;
	}
	
}
