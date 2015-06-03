package methode_oubli;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

import JSci.maths.statistics.NormalDistribution;
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
 * Oubli par calcul d'entropie.
 * @author pgimenez
 *
 */

public class OubliParEntropie implements MethodeOubli {

	double gamma = 0.05;
	
	NormalDistribution norm = new NormalDistribution();
	
	@Override
	public Map<String, Double> recommandation(Var v, ArrayList<String> historiqueOperations, VDD vdd, ArrayList<String> possibles)
	{
		ArrayList<Var> varOubliees = new ArrayList<Var>();
    	ArrayList<String> valOubliees = new ArrayList<String>();
		Var varGainMax = null;
		String valGainMax = null;
		Var varcurr;
		double gainMax;
		
		/**
		 * Choix glouton des variables à oublier.
		 */
		do {
			gainMax = 0.;
			// Cette entropie est la même pour chaque couple à considérer
			double n_sans_oublier = vdd.countingpondere();
			Double[] p_initial = new Double[possibles.size()];
			vdd.countingpondereOnPossibleDomain(v, possibles).values().toArray(p_initial);
			//System.out.println("p_initial[0]: "+p_initial[0]);
			//System.out.println("n_sans_oublier: "+n_sans_oublier);
			double entropie_sans_oublier = calcule_entropie(p_initial, n_sans_oublier, gamma, possibles.size());
			
			for(int i=0; i<historiqueOperations.size(); i+=2)
			{
				double gain;
    			varcurr=vdd.getVar(historiqueOperations.get(i));
    			
    			/**
    			 * On ne regarde que le gain des variables qu'on a pas déjà décidé d'oublier
    			 */
    			if(!varOubliees.contains(varcurr))
    			{
	    			/**
	    			 * On oublie temporairement
	    			 */
		    		vdd.deconditioner(varcurr);
    				//System.out.println("Oubli de "+varcurr.name);
		    		double n = vdd.countingpondere();
	
		    		Double[] p = new Double[possibles.size()];
	    			vdd.countingpondereOnPossibleDomain(v, possibles).values().toArray(p);
	    			//System.out.println("p[0]: "+p[0]);
	    			//System.out.println("n: "+n);
					double gamma_corrected = 1-Math.pow(1-gamma, 1./possibles.size());
					//System.out.println("gamma_corrected: "+gamma_corrected);
					double entropie = calcule_entropie(p, n, gamma_corrected, possibles.size());
	        		vdd.conditioner(varcurr, varcurr.conv(historiqueOperations.get(i+1)));
	        		
	        		gain = entropie_sans_oublier - entropie;
	    			//System.out.println("Gain: "+gain);
	        		
	        		if(gain > gainMax)
	        		{
	        			varGainMax = varcurr;
	        			valGainMax = historiqueOperations.get(i+1);
	        			gainMax = gain;
	        		}
    			}
			}
			if(gainMax > 0)
			{
				varOubliees.add(varGainMax);
				valOubliees.add(valGainMax);
				vdd.deconditioner(varGainMax);
			}
		} while(gainMax > 0);
		
		/**
		 * On calcule la distribution de probabilité une fois l'oubli effectué
		 */
		Map<String, Double> m = vdd.countingpondereOnPossibleDomain(v, possibles);
		 
		/**
		 * On reconditionne tout ce qu'on a oublié
		 */
		for(int i = 0; i < varOubliees.size(); i++)
			vdd.conditioner(varOubliees.get(i), varOubliees.get(i).conv(valOubliees.get(i)));
		
		return m;
	}
	
	private double calcule_entropie(Double[] p, double n, double gamma, int nb_possibles)
	{
		double z = norm.inverse(1-gamma/2);
		double n_tilde = n+z*z;
		Arrays.sort(p);
		
		double[] pi = new double[nb_possibles];
		double entropie = 0;
		
		for(int i = 0; i < p.length; i++)
		{
			// Dans la formule, il est écrit p*n+... Mais comme ici p n'est pas une probabilité mais une
			// cardinalité, il faudrait diviser par n.
			double p_tilde = (p[i]+z*z/2)/n_tilde;
			pi[i] = p_tilde+z*Math.sqrt(p_tilde*(1-p_tilde)/n_tilde);
			
			// pi contient en fait la somme cumulée
			if(i > 0)
			{
				pi[i] += pi[i-1];
				
				// p devient au fur et à mesure la somme cumulée de p, c'est-à-dire Tp*
				p[i] += p[i-1];
			}
			entropie += -(p[i] / 2 * Math.log(pi[i] / 2) + (1 - p[i] / 2)* Math.log(1 - pi[i] / 2));
		}
		entropie /= (nb_possibles * Math.log(nb_possibles));
		
		return entropie;
	}

	@Override
	public void learn(SALADD saladd)
	{
		
	}
}