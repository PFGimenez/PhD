package br4cp;

import java.util.ArrayList;

public class TestNul implements TestIndependance {

	@Override
	public double[][] getIndependancy(ArrayList<Var> v, VDD graph) {
		double[][] variance = new double[v.size()][v.size()];
		for(int i=0; i<v.size(); i++){
			for(int j=i+1; j<v.size(); j++){
				variance[i][j]=0;
			}
		}
		return variance;
	}

	public boolean estPlusIndependantQue(double valeur1, double valeur2)
	{
		return false;
	}
	
}
