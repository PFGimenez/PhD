package br4cp;

import java.util.ArrayList;

public class HeuristiqueBW implements Heuristique {

	@Override
	public void reordoner(int[][] contraintes,
			Ordonnancement ord) {
		ArrayList<Var> listeTemp=new ArrayList<Var>();
	int[] bandWidth=new int[ord.size];
	for(int i=0; i<bandWidth.length; i++)
		bandWidth[i]=0;
	
	int max;
	int varmax;
	
	ord.constGraphAdj(contraintes);
	ord.constNbContraintes(contraintes);
	
	//init : v0    (a changer)
	listeTemp.add(ord.variables.get(0));
	bandWidth[0]=-1;
	
	for(int cpt=1; cpt<ord.size; cpt++ ){
		//actualisation bandwidth
		for(int i=0; i<bandWidth.length; i++){
			if(bandWidth[i]>=0)
				bandWidth[i]=0;							//réinit
		}
		for(int i=0; i<listeTemp.size(); i++){
			for(int j=0; j<ord.size; j++){
				if(bandWidth[j]>=0){					//pas deja passee
					if(ord.graphAdj[i][j]>0)
						bandWidth[j]+=(int)Math.pow((listeTemp.size()-i), 2);
				}
			}
		}
		
		//recherche meilleur
		varmax=-1;
		max=-1;
		for(int i=0; i<bandWidth.length; i++){
			if (bandWidth[i]>max){
				varmax=i;
				max=bandWidth[i];
			}
		}
		
		//System.out.println(varmax + "   " + variables.get(varmax).name);
		listeTemp.add(ord.variables.get(varmax));
			
		bandWidth[varmax]=-1;		//faut plus qu'elle ressorte
	}
	
	for(int i=0; i<listeTemp.size(); i++)
		ord.variables.set(i, listeTemp.get(i));
	}
	
}
