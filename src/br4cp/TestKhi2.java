package br4cp;

import java.util.ArrayList;

import javastat.inference.ChisqTest;

public class TestKhi2 implements TestIndependance {
	
	ChisqTest test = new ChisqTest();
	
	public double[][] getIndependancy(ArrayList<Var> v, VDD graph)
	{
//		int count=graph.countingpondere();	
		
//		double[] probabilite1;
//		double[] probabilite2;
//		double probaTemp;
		Var var1, var2;
		int dom1, dom2;
//		int count2;
//		double facteur;
//		double distance;
		double[][] table;
		
		double[][] variance = new double[v.size()][v.size()];
		
		for(int i=0; i<v.size(); i++){
			var1=v.get(i);
			System.out.println();
			System.out.println(var1.name);
			for(int j=i+1; j<v.size(); j++){
				
				//---debut du calcul-----
				var2=v.get(j);

				dom1=var1.domain;
				dom2=var2.domain;
				table=new double[dom1][dom2];
	
				//calcul des proba au cas par cas
				for(int l=0; l<dom1; l++){
					for(int k=0; k<dom2; k++){
						graph.conditioner(var1, l);
						graph.conditioner(var2, k);
						table[l][k]=graph.countingpondere();
						graph.deconditioner(var2);

					}
					graph.deconditioner(var1);
				}
				
				variance[i][j] = test.pValue(table);
				
				System.out.print(var2.name+"="+(double)(Math.round(variance[i][j]*100))/100+" ");
				//System.out.print(var2.name+"="+distance+" ");


			}
		}
		return variance;
	}
	
}
