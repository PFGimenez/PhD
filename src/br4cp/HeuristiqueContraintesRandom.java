package br4cp;

import java.util.ArrayList;

public class HeuristiqueContraintesRandom implements HeuristiqueContraintes {

	public ArrayList<Integer> reorganiseContraintes(LecteurXML l)
	{
		ArrayList<Integer> reorga=new ArrayList<Integer>();
	reorga.add(0);
	int random;
	for(int i=1; i<l.getNbConstraints(); i++){
		random=(int)Math.floor(Math.random()*(reorga.size()+1));
		reorga.add(random, i);
	}
		return reorga;
	}	
	
}
