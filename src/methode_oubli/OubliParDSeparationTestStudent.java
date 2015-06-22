package methode_oubli;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import test_independance.TestIndependance;
import JSci.maths.statistics.NormalDistribution;
import JSci.maths.statistics.TDistribution;
import br4cp.LecteurXML;
import br4cp.Ordonnancement;
import br4cp.SALADD;
import br4cp.VDD;
import br4cp.Var;
import br4cp.Variance;

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

public class OubliParDSeparationTestStudent implements MethodeOubli {

	private int nbOubli;
	private Variance variance = null;
	private TestIndependance test;
//	private TestG2 testg2 = new TestG2();

	private HashMap<String, ArrayList<String>>[] reseau;
	private ArrayList<String> done = new ArrayList<String>();
//	private HashMap<String, Integer> distances = new HashMap<String, Integer>();
	
	private static final int parents = 0;
	private static final int enfants = 1;
	private TDistribution t;
	private NormalDistribution norm = new NormalDistribution();
	private double[] seuils = new double[30];
	private double seuilNorm;
	
	public OubliParDSeparationTestStudent(TestIndependance test)
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
		Ordonnancement ord;			
		ord = new Ordonnancement();
		LecteurXML xml=new LecteurXML(ord);
		reseau = xml.lectureReseauBayesien("bn_hc_new_0.xml");
	}
	
	private void rechercheEnProfondeur(ArrayList<String> connues, String v, boolean vientDeParent, int distance)
	{
/*		if(!distances.containsKey(v))
			distances.put(v, distance);
		else if(distances.get(v) > distance)
		{
			distances.remove(v);
			distances.put(v, distance);
		}
			*/
		done.add(v);
		ArrayList<String> listeParents = reseau[parents].get(v);
		ArrayList<String> listeEnfants = reseau[enfants].get(v);

		/**
		 * D'abord, on étend la recherche aux enfants.
		 * Il n'y a pas de problème de collision.
		 * Si v est connu, on bloque la recherche.
		 * On ne vérifie pas qu'ils sont "done" parce que:
		 * - par acyclicité, il n'y aura pas de boucle infinie
		 * - c'est nécessaire dans le cas d'une V-structure qui peut être "done" et pourtant
		 * peut avoir des parents à explorer
		 */
 		if(!connues.contains(v))
			for(String enf: listeEnfants)
				rechercheEnProfondeur(connues, enf, true, distance + 1);
		
		boolean aUnEnfantConnu = false;
		for(String enf: listeEnfants)
			if(connues.contains(enf))
			{
				aUnEnfantConnu = true;
				break;
			}
		
		/**
		 * La recherche des parents est un peu plus complexe, et prend compte des V-structure
		 */
		if(!(connues.contains(v) || aUnEnfantConnu) && vientDeParent)
		{
			/**
			 * Si on n'est pas connu (et qu'aucun de nos enfants ne l'est)
			 * et qu'on provient d'un parent, on a une V-structure: on s'arrête là.
			 */
			return;
		}
		else if(!vientDeParent)
		{
			/**
			 * Si on vient d'un fils, alors il n'y a pas de V-structure.
			 * On peut propager à conditionner de ne pas être connu.
			 */
			if(!connues.contains(v))
				for(String par: listeParents)
					if(!done.contains(par))
						rechercheEnProfondeur(connues, par, false, distance + 1);
		}
		else
		{
			/**
			 * Dernière possibilité: on vient d'un parent et on (ou un de nos fils) est connu.
			 * Dans ce cas, la V-structure est ignoré.
			 */
			for(String par: listeParents)
				if(!done.contains(par))
					rechercheEnProfondeur(connues, par, false, distance + 1);
		}
	}
	
	@Override
	public Map<String, Double> recommandation(Var v, ArrayList<String> historiqueOperations, VDD vdd, ArrayList<String> possibles)
	{
		nbOubli = 0;
    	ArrayList<Var> dejavu = new ArrayList<Var>();
    	ArrayList<String> dejavuVal = new ArrayList<String>();
		ArrayList<String> connues = new ArrayList<String>();
		Map<String, Double> m;
		done.clear();
		
//		int dfcorr = 1;
		
		for(int i = 0; i < historiqueOperations.size(); i += 2)
			connues.add(vdd.getVar(historiqueOperations.get(i)).name);

		rechercheEnProfondeur(connues, v.name, false, 0);
		
		for(int i = 0; i < historiqueOperations.size(); i += 2)
		{
			Var connue = vdd.getVar(historiqueOperations.get(i));
//			dfcorr *= connue.domain;
			if(!done.contains(connue.name))
			{
	    		dejavu.add(connue);
	    		dejavuVal.add(historiqueOperations.get(i+1));
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
