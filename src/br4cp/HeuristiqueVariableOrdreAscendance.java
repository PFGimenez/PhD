package br4cp;

import java.util.ArrayList;

public class HeuristiqueVariableOrdreAscendance implements HeuristiqueVariable {

	@Override
	public void reordoner(int[][] contraintes,
			Ordonnancement ord) {
		ArrayList<Var> listeTemp=new ArrayList<Var>();
	int indexMax;
	int temp;
	ArrayList<Integer> reordre=new ArrayList<Integer>();
	
	for(int i=0; i<contraintes.length; i++){
		//System.out.println("xaer =>> " + i);
		indexMax=-1;
		if(contraintes[i].length>=2){
			for(int j=0; j<contraintes[i].length-1; j++){
				if(reordre.indexOf(contraintes[i][j])>indexMax)
						indexMax=reordre.indexOf(contraintes[i][j]);
			}
			temp=reordre.indexOf(contraintes[i][contraintes[i].length-1]);
			if(temp<indexMax){												//mal classé
				reordre.remove(temp);
				reordre.add(indexMax, contraintes[i][contraintes[i].length-1]);
				i=-1;
			}
		}
	}
	for(int i=0; i<ord.size; i++)
		listeTemp.add(ord.variables.get(reordre.get(i)));
	for(int i=0; i<listeTemp.size(); i++)
		ord.variables.set(i, listeTemp.get(i));
	}
	
	
}
