package br4cp;

import java.util.ArrayList;

public class HeuristiqueContraintesInversion implements HeuristiqueContraintes {

	public ArrayList<Integer> reorganiseContraintes(LecteurXML l)
	{
		ArrayList<Integer> reorga=new ArrayList<Integer>();

		for(int i=0; i<l.getNbConstraints(); i++)
				reorga.add(l.getNbConstraints()-(i+1));
		return reorga;
	}		
}
