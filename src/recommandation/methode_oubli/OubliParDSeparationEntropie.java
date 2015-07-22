package recommandation.methode_oubli;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

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
 * Méthode d'oubli par d-sépration suivi d'un oubli par entropie
 * @author pgimenez
 *
 */

public class OubliParDSeparationEntropie extends MethodeDSeparation {

	private double seuilGain = 0.001;

	public OubliParDSeparationEntropie(int seuil, TestIndependance test, String prefixData, double seuilGain)
	{
		super(seuil, test, prefixData);
		this.seuilGain = seuilGain;
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

		Var varGainMax = null;
		String valGainMax = null;
		Var varcurr;
		double gainMax;
		
//		int dfcorr = 1;
		
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
		
		do {
			gainMax = -1000;

			for(String s: historiqueOperations.keySet())
			{
    			varcurr=vdd.getVar(s);
    			
    			/**
    			 * On ne regarde que le gain des variables qu'on a pas déjà décidé d'oublier
    			 */
    			if(!dejavu.contains(varcurr))
    			{
	    			/**
	    			 * On oublie temporairement
	    			 */
//    				System.out.println("n de base: "+vdd.countingpondere());
		    		vdd.deconditioner(varcurr);
		    		
					// Cette entropie est la même pour chaque couple à considérer
					double n_en_oubliant = vdd.countingpondere();
					Double[] p_initial = new Double[v.domain];
					vdd.countingpondereOnFullDomain(v).values().toArray(p_initial);
//					System.out.println("p_initial[0]: "+p_initial[0]);
//					System.out.println("n_en_oubliant: "+n_en_oubliant);
					double entropie_en_oubliant = calcule_entropie(p_initial, n_en_oubliant);
					
					double entropie = 0;
					for(int k = 0; k < varcurr.domain; k++)
					{
		        		vdd.conditioner(varcurr, k);
	    				//System.out.println("Oubli de "+varcurr.name);
			    		double n = vdd.countingpondere();
			    		
			    		if(n == 0) // ceci est tout à fait possible du fait des contraintes
			    		{
			    			vdd.deconditioner(varcurr);
			    			continue;
			    		}
//			    		System.out.println("n pour mod "+k+" sur "+varcurr.domain+": "+n);
			    		Double[] p = new Double[v.domain];
		    			vdd.countingpondereOnFullDomain(v).values().toArray(p);
		    			//System.out.println("p[0]: "+p[0]);
		    			//System.out.println("n: "+n);
	//					double gamma_corrected = 1-Math.pow(1-gamma, 1./possibles.size());
						//System.out.println("gamma_corrected: "+gamma_corrected);
						entropie += n/n_en_oubliant*calcule_entropie(p, n);
						vdd.deconditioner(varcurr);
					}
					
//					System.out.println("entropie_en_oubliant: "+entropie_en_oubliant);
//					System.out.println("entropie: "+entropie);
	        		vdd.conditioner(varcurr, varcurr.conv(historiqueOperations.get(s)));
	        		double gain = - (entropie_en_oubliant - entropie);
//	    			System.out.println("Gain: "+gain);
	        		
	        		if(gain > gainMax)
	        		{
	        			varGainMax = varcurr;
	        			valGainMax = historiqueOperations.get(s);
	        			gainMax = gain;
	        		}
    			}
			}
			if(gainMax >= seuilGain)
			{
				nbOubli++;
//				System.out.println("Variable oubliée: "+varGainMax.name+", gain: "+gainMax);
				dejavu.add(varGainMax);
				dejavuVal.add(valGainMax);
				vdd.deconditioner(varGainMax);
			}
			else
			{
//				System.out.println("Pas oublié, gain: "+gainMax);
			}
		} while(gainMax >= seuilGain);
		
		super.restaure(historiqueOperations, vdd, v);
    	
    	m = vdd.countingpondereOnPossibleDomain(v, possibles);
    	
    	super.reconditionne(vdd);

    	return m;
	}

	private double calcule_entropie(Double[] p, double n)
	{
//		double z = norm.inverse(1-gamma/2);
		double z = 1.9599639845400536;
		double n_tilde = n+z*z;
		Arrays.sort(p);
		int taille = p.length;
		
		double[] pi = new double[taille];
		double entropie = 0;
		
		for(int i = 0; i < taille; i++)
		{
			if(i > 0)
				p[i] += p[i-1];
			
			double p_tilde = (p[i] * n + z * z / 2) / n_tilde;
			pi[i] = p_tilde + z * Math.sqrt(p_tilde * (1 - p_tilde) / n_tilde);
			// pi contient en fait la somme cumulée
			entropie += -(p[i] / 2 * Math.log(pi[i] / 2) + (1 - p[i] / 2) * Math.log(1 - pi[i] / 2));
		}
		entropie /= (taille * Math.log(taille));
		return entropie;
	}
	
}
