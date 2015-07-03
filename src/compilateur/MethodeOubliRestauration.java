package compilateur;

import java.util.ArrayList;
import java.util.HashMap;

import compilateur.test_independance.TestIndependance;


/**
 * Classe abtraite pour les méthodes d'oubli à base de restauration
 * @author pgimenez
 *
 */

public abstract class MethodeOubliRestauration implements MethodeOubli
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
		variance = saladd.calculerVarianceHistorique(test, prefix_file_name);
	}
	
	protected void restaure(HashMap<String, String> historiqueOperations, VDD vdd, Var v)
	{
		while(vdd.countingpondere()<seuil){
			boolean first = true;
			double min=-1, curr;
			Var varmin=null, varcurr;
			String val="";
			for(String s: historiqueOperations.keySet())
			{
				varcurr=vdd.getVar(s);
				if(!dejavu.contains(varcurr)){
	    			curr=variance.get(v, varcurr);
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

}
