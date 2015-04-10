package br4cp;

import java.util.ArrayList;

public class HeuristiqueOrdreChoisi implements Heuristique{

	public void reordoner(int[][] contraintes, Ordonnancement ord)
	{
		ArrayList<Var> listeTemp=new ArrayList<Var>();
		
		listeTemp.add(ord.variables.get(ord.variables.size()-1));
		ord.variables.remove(ord.variables.size()-1);
	
		while(listeTemp.size()!=0){
			ord.variables.add(0, listeTemp.get(listeTemp.size()-1));
			listeTemp.remove(listeTemp.size()-1);
		}

	}
	
}
