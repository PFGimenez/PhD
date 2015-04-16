package br4cp;

import java.util.ArrayList;

import javastat.inference.ChisqTest;

public class TestKhi2Statistique implements TestIndependance {
	
	private ChisqTest test = new ChisqTest();
	
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

//				variance[i][j] = test.pValue(simplify(dom1, dom2, table));
				variance[i][j] = test.testStatistic(simplify(dom1, dom2, table));				
				System.out.print(var2.name+"="+(double)(Math.round(variance[i][j]*100))/100+" ");
				//System.out.print(var2.name+"="+distance+" ");


			}
		}
		return variance;
	}
	
	private double[][] simplify(int tailleV1, int tailleV2, double[][] table2)
	{
    	double[][] table = new double[tailleV1][tailleV2];
    	for(int i = 0; i < tailleV1; i++)
    		for(int j = 0; j < tailleV2; j++)
    			table[i][j] = table2[i][j];
		double[] sommeV1 = new double[tailleV1];
		double[] sommeV2 = new double[tailleV2];
		boolean[] suppr1 = new boolean[tailleV1];
		boolean[] suppr2 = new boolean[tailleV2];
		int newTailleV1 = tailleV1;
		int newTailleV2 = tailleV2;
		
		for(int i = 0; i < tailleV1; i++)
		{
			int somme = 0;
			for(int j = 0; j < tailleV2; j++)
				somme += table[i][j];
			sommeV1[i] = somme;
			if(somme == 0)
			{
				suppr1[i] = true;
				newTailleV1--;
			}
			else
				suppr1[i] = false;
		}
		
		for(int i = 0; i < tailleV2; i++)
		{
			int somme = 0;
			for(int j = 0; j < tailleV1; j++)
				somme += table[j][i];
			sommeV2[i] = somme;
			if(somme == 0)
			{
				suppr2[i] = true;
				newTailleV2--;
			}
			else
				suppr2[i] = false;
		}

		double[][] newTable = new double[newTailleV1][newTailleV2];
		double[] newSommeV1 = new double[newTailleV1];
		double[] newSommeV2 = new double[newTailleV2];

		int a = 0, b = 0;
		for(int i = 0; i < tailleV1; i++)
		{
			if(sommeV1[i] == 0)
				continue;
			int somme = 0;
			for(int j = 0; j < tailleV2; j++)
				somme += table[i][j];
			newSommeV1[a] = somme;
			a++;
		}
		
		for(int i = 0; i < tailleV2; i++)
		{
			if(sommeV2[i] == 0)
				continue;
			int somme = 0;
			for(int j = 0; j < tailleV1; j++)
				somme += table[j][i];
			newSommeV2[b] = somme;
			b++;
		}
		
		a = 0;
		b = 0;
		
		for(int i = 0; i < tailleV1; i++)
			if(suppr1[i])
				continue;
			else
			{
				b = 0;
				for(int j = 0; j < tailleV2; j++)
				{
					if(suppr2[j])
						continue;
					else
						newTable[a][b++] = table[i][j];
				}
				a++;
			}
		return newTable;
	}
	
	public boolean estPlusIndependantQue(double valeur1, double valeur2)
	{
		return valeur1 < valeur2;
	}
}
