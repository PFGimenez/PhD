package br4cp;

import java.util.ArrayList;

public class HeuristiqueMCF implements Heuristique {

	@Override
	public void reordoner(int[][] contraintes,
			Ordonnancement ord) {
		
		ArrayList<Var> listeTemp=new ArrayList<Var>();
		
		ord.constNbContraintes(contraintes);
		
		int max=-1;
		int varmax=-1;
		
		for(int curr=0; curr<ord.size; curr++){
			for(int i=0; i<ord.size; i++){
				if(ord.nbContraintes[i]>max){
					max=ord.nbContraintes[i];
					varmax=i;
				}
			}
			//System.out.println(varmax + "   " + variables.get(varmax).name);
			listeTemp.add(ord.variables.get(varmax));
			
			ord.nbContraintes[varmax]=-1;		//faut plus qu'elle ressorte
			max=-1;
			varmax=-1;
		}

		for(int i=0; i<listeTemp.size(); i++)
			ord.variables.set(i, listeTemp.get(i));
		
		
	}

}
