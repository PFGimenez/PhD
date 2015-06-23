package methode_oubli;

import java.util.ArrayList;

import test_independance.TestIndependance;
import br4cp.SALADD;
import br4cp.VDD;
import br4cp.Var;
import br4cp.Variance;

public abstract class MethodeOubliRestauration implements MethodeOubli
{
	private int seuil;
	private TestIndependance test;
	private Variance variance = null;
	protected int nbOubli = 0;
	protected ArrayList<Var> dejavu = new ArrayList<Var>();
	protected ArrayList<String> dejavuVal = new ArrayList<String>();

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
	
	protected void restaure(ArrayList<String> historiqueOperations, VDD vdd, Var v)
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

	public void reconditionne(VDD vdd)
	{
    	for(int i = 0; i < dejavu.size(); i++)
    	{
        	vdd.conditioner(dejavu.get(i), dejavu.get(i).conv(dejavuVal.get(i)));
    	}
	}

}
