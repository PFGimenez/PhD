package br4cp;

import java.util.ArrayList;

public class HeuristiqueOrdreRandom implements Heuristique {

	@Override
	public void reordoner(int[][] contraintes,
			Ordonnancement ord) {
		ArrayList<Var> listeTemp=new ArrayList<Var>();

		for(int i=0; i<ord.variables.size(); i++)
			listeTemp.add(ord.variables.get(i));

	ord.variables.clear();
	
	while(!listeTemp.isEmpty()){
		int rand=(int) Math.floor(Math.random()*listeTemp.size());
		ord.variables.add(listeTemp.get(rand));
		listeTemp.remove(rand);
	}
	

	}

}
