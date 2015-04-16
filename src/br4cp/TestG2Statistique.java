package br4cp;

import java.util.ArrayList;

public class TestG2Statistique implements TestIndependance
{

	@Override
	public double[][] getIndependancy(ArrayList<Var> v, VDD graph) {
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
		double[] sum1;
		double[] sum2;
		
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
				sum1=new double[dom1];
				sum2=new double[dom2];
	
				for(int l=0; l<dom1; l++)
					sum1[l] = 0;
				for(int l=0; l<dom2; l++)
					sum2[l] = 0;
				
				//calcul des proba au cas par cas
				for(int l=0; l<dom1; l++){
					for(int k=0; k<dom2; k++){
						graph.conditioner(var1, l);
						graph.conditioner(var2, k);
						table[l][k]=graph.countingpondere();
						graph.deconditioner(var2);
						sum1[l] += table[l][k];
						sum2[k] += table[l][k];
					}
					graph.deconditioner(var1);
				}
				
				variance[i][j] = 0;
				for(int l=0; l<dom1; l++)
					for(int k=0; k<dom2; k++)
						if(table[l][k] != 0)
							variance[i][j] += table[l][k]*Math.log(table[l][k]/(sum1[l]*sum2[k]));
				
				System.out.print(var2.name+"="+(double)(Math.round(variance[i][j]*100))/100+" ");
			}
		}
		return variance;
	}

	@Override
	public boolean estPlusIndependantQue(double valeur1, double valeur2) {
		return Math.abs(valeur1) < Math.abs(valeur2);
	}

}
