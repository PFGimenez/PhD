package br4cp;

import java.util.ArrayList;

public class HeuristiqueContraintesRien implements HeuristiqueContraintes {

	public ArrayList<Integer> reorganiseContraintes(LecteurXML l)
	{
		ArrayList<Integer> reorga=new ArrayList<Integer>();

		for(int i=0; i<l.getNbConstraints(); i++)
			reorga.add(i);
		return reorga;
	}
}
