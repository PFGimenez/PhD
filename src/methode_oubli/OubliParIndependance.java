package methode_oubli;

import java.util.ArrayList;
import java.util.Map;

import br4cp.VDD;
import br4cp.Var;
import br4cp.Variance;
import test_independance.TestIndependance;

public class OubliParIndependance implements MethodeOubli {

	private Variance variance = null;
	private TestIndependance test;
	
	public OubliParIndependance(TestIndependance test)
	{
		this.test = test;
	}
	
	@Override
	public Map<String, Double> recommandation(ArrayList<Var> variables, Var v, ArrayList<String> historiqueOperations, VDD vdd)
	{
		if(variance == null)
			variance=new Variance(variables, vdd, test, "smallhist/smallvariance");

		Map<String, Double> m;
		int seuil=100;
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
    		dejavu.add(varmin) ;
    		dejavuVal.add(val);
    		vdd.deconditioner(varmin);
    	}
    	
    	m=vdd.countingpondereOnFullDomain(v);
    	for(int i=0; i<dejavu.size(); i++){
        	vdd.conditioner(dejavu.get(i), dejavu.get(i).conv(dejavuVal.get(i)));
    	}

    	return m;
	}
	
}
