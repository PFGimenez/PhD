package br4cp;

import java.util.ArrayList;

public class TestDiffEcartProba implements TestIndependance {

	//methode initiale, diff des ecarts de proba.
	//marche sur domaine binaire, val > 1 sur valeurs non binaires

	@Override
	public double[][] getIndependancy(ArrayList<Var> v, VDD graph) {
		double[][] variance = new double[v.size()][v.size()];
		int count=graph.countingpondere();	
		
		double[] probabilite;
		double probaTemp;
		Var var1, var2;
		int dom1, dom2, count2;
		double facteur;
		double distance;
		for(int i=0; i<v.size(); i++){
			var1=v.get(i);
			System.out.println();
			System.out.println(var1.name);
			for(int j=i+1; j<v.size(); j++){
				var2=v.get(j);
					
				distance=0;
				dom1=var1.domain;
				dom2=var2.domain;
				probabilite=new double[dom2];
				
				//calcul des probab initiales
				for(int k=0; k<dom2; k++){
					probabilite[k]=graph.countingpondereOnVal(var2, k);
					probabilite[k]=probabilite[k]/count;
				}				
				
				//calcul des proba au cas par cas
				for(int l=0; l<dom1; l++){
					graph.conditioner(var1, l);
					count2=graph.countingpondere();
					facteur=count2;
					facteur=facteur/count;
					for(int k=0; k<dom2; k++){
						graph.conditioner(var1, l);
						probaTemp=graph.countingpondereOnVal(var2, k);
						probaTemp=probaTemp/count2;
						graph.conditioner(var1, l);
						//System.out.println(graph.countingpondereOnVal(var2, k)+ " "+count2);
						distance+=Math.abs((probaTemp-probabilite[k])*facteur);
					}
					graph.deconditioner(var1);
				}
				variance[i][j]=distance;
				System.out.print(var2.name+"="+(double)(Math.round(distance*100))/100+" ");

			}
		}
		return variance;
	}

	public boolean estPlusIndependantQue(double valeur1, double valeur2)
	{
		return valeur1 < valeur2;
	}
	
}
