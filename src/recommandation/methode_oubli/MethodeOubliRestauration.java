package recommandation.methode_oubli;

import java.util.ArrayList;
import java.util.HashMap;

import compilateur.SALADD;
import compilateur.VDD;
import compilateur.Var;
import compilateur.Variance;
import compilateur.test_independance.TestIndependance;

/*   (C) Copyright 2016, Gimenez Pierre-François 
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
 * Classe abtraite pour les méthodes d'oubli à base de restauration
 * @author pgimenez
 *
 */

public abstract class MethodeOubliRestauration implements MethodeOubliSALADD
{
	protected int seuil;
	protected TestIndependance test;
	protected Variance variance = null;
	protected int nbOubli = 0;
	protected ArrayList<Var> dejavu = new ArrayList<Var>();
	protected ArrayList<String> dejavuVal = new ArrayList<String>();
	protected int nbIter;
	
	public void setNbIter(int nbIter)
	{
		this.nbIter = nbIter;
	}
	
	public MethodeOubliRestauration(int seuil, TestIndependance test)
	{
		this.seuil = seuil;
		this.test = test;
	}
	
	@Override
	public void learn(SALADD saladd, String prefix_file_name)
	{
		if(test != null)
			variance = saladd.calculerVarianceHistorique(test, prefix_file_name);
	}
	
	protected void restaureSeuil(HashMap<String, String> historiqueOperations, int nbPossibles, VDD vdd, Var v)
	{
		while(vdd.countingpondere() < seuil*(nbPossibles-1))
		{
//			System.out.println("Inférieur au seuil, oubli");
			boolean first = true;
			double min = -1, curr;
			Var varmin = null, varcurr;
			String val = "";
			for(String s: historiqueOperations.keySet())
			{
				varcurr=vdd.getVar(s);
				if(!dejavu.contains(varcurr)){
	    			curr=variance.get(v, varcurr);
					if(first || test.estPlusIndependantQue(curr,min)){
	    				first = false;
	    				min = curr;
	    				varmin = varcurr;
	    				val = historiqueOperations.get(s);
	    			}
	    		}
			}
			// il n'y a plus de variables à oublier
			if(varmin == null)
				break;
			nbOubli++;
			dejavu.add(varmin);
			dejavuVal.add(val);
			vdd.deconditioner(varmin);
		}
	}

	public void reconditionne(VDD vdd)
	{
    	for(int i = 0; i < dejavu.size(); i++)
        	vdd.conditioner(dejavu.get(i), dejavu.get(i).conv(dejavuVal.get(i)));
	}
	
	@Override
	public int getNbOublis()
	{
		return nbOubli;
	}
	
	public String toString()
	{
		return getClass().getSimpleName();
	}

}
