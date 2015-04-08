package br4cp;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

/*   (C) Copyright 2013, Schmidt Nicolas
 * 
 *   This program is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

class Variance {

	ArrayList <Var> variables;
	int count;
	double[][] variance;
	int meth;
	
	public Variance(ArrayList<Var> v, VDD graph, int methode, String name){
		meth=methode;
		variables=v;
		variance=new double[variables.size()][variables.size()];
		
		
		if(!load(name)){
	
		//tout a 0
		if(meth==0){
			for(int i=0; i<v.size(); i++){
				for(int j=i+1; j<v.size(); j++){
					variance[i][j]=0;
				}
			}
		}
		
		//methode initiale, diff des ecarts de proba.
		//marche sur domaine binaire, val > 1 sur valeurs non binaires
		if(meth==1){
		
		count=graph.countingpondere();	
		
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
		}
		
		//calcule de l'écart max
		if(meth==2){
			count=graph.countingpondere();	
			
			double[] probabilite;
			double probaTemp;
			Var var1, var2;
			int dom1, dom2, count2;
			double facteur;
			double distance;
			double maxproba;
			double maxprobapossible;
			
			for(int i=0; i<v.size(); i++){
				var1=v.get(i);
				System.out.println();
				System.out.println(var1.name);
				for(int j=i+1; j<v.size(); j++){
//				for(int j=0; j<v.size(); j++){
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
						maxproba=0;
						maxprobapossible=0;
						
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
							
							if(Math.abs(probaTemp-probabilite[k])>maxproba)
								maxproba=Math.abs(probaTemp-probabilite[k]);
							if(1-probabilite[k] > maxprobapossible)
								maxprobapossible=1-probabilite[k];
							//distance+=Math.abs((probaTemp-probabilite[k])*facteur);
						}
						maxproba=maxproba/maxprobapossible;
						distance+=maxproba*facteur;
						graph.deconditioner(var1);
					}
					
					
					variance[i][j]=distance;
					System.out.print(var2.name+"="+(double)(Math.round(distance*100))/100+" ");

				}
			}
		}
		
		if(meth==3){
			
		count=graph.countingpondere();	
		
		double[] probabilite1;
		double[] probabilite2;
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
				
				//---debut du calcul-----
				var2=v.get(j);
					
				distance=0;
				dom1=var1.domain;
				dom2=var2.domain;
				probabilite1=new double[dom1];
				probabilite2=new double[dom2];
				
				//calcul des probab initiales
//				for(int k=0; k<dom1; k++){
//					probabilite1[k]=graph.countingpondereOnVal(var1, k);
//				}
				//calcul des probab initiales
				for(int k=0; k<dom2; k++){
					probabilite2[k]=graph.countingpondereOnVal(var2, k);
				}				
				//calcul des proba au cas par cas
				for(int l=0; l<dom1; l++){
					probabilite1[l]=graph.countingpondereOnVal(var1, l);
					for(int k=0; k<dom2; k++){
						graph.conditioner(var1, l);

						//graph.conditioner(var1, l);
						probaTemp=graph.countingpondereOnVal(var2, k);
						facteur=probabilite1[l]*probabilite2[k];
						if(facteur>5){
							facteur=facteur/count;
							distance+=Math.pow(probaTemp-facteur, 2)/facteur;
						}
						}
					graph.deconditioner(var1);
				}
				variance[i][j]=distance;
				System.out.print(var2.name+"="+(double)(Math.round(distance*100))/100+" ");
				//System.out.print(var2.name+"="+distance+" ");


			}
		}
		}
		//khi2 pifi
		if(meth==4){
			
		count=graph.countingpondere();	
		
		double[] probabilite1;
		double[] probabilite2;
		double probaTemp;
		Var var1, var2;
		int dom1, dom2, count2;
		double facteur;
		double distance;
		int[][] table;
		
		for(int i=0; i<v.size(); i++){
			var1=v.get(i);
			System.out.println();
			System.out.println(var1.name);
			for(int j=i+1; j<v.size(); j++){
				
				//---debut du calcul-----
				var2=v.get(j);

				dom1=var1.domain;
				dom2=var2.domain;
				table=new int[dom1][dom2];
	
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
				
				variance[i][j]=khi2(dom1, dom2, table);
				
				System.out.print(var2.name+"="+(double)(Math.round(variance[i][j]*100))/100+" ");
				//System.out.print(var2.name+"="+distance+" ");


			}
		}
		}
		save(name);
		}
		
		
    } 

	
	public void string(){
		for(int i=0; i<variables.size(); i++){
			for(int j=0; j<variables.size(); j++){
				System.out.print(variance[i][j]+" ");
			}
			System.out.println();
		}
	}
	
	public double get(Var v1, Var v2){
		int index1, index2;
		double val1, val2;
		index1=variables.indexOf(v1);
		index2=variables.indexOf(v2);
		val1=variance[index1][index2];
		val2=variance[index2][index1];
		if (val1>val2)
			return val1;
		return val2;

	}
	
	//name : small medium big?
	public void save(String name){
		String file=name+"_variance_m"+meth+".txt";
		
		String ligne;
		
	   	FileWriter fW;

		try{
			fW = new FileWriter(file);
		
			for(int i=0; i<variance.length; i++){
				ligne="";
				for(int j=0; j<variance[i].length; j++){
					ligne+=""+variance[i][j]+" ";
				}
				fW.write(ligne+"\n");
			}
			fW.close();

	}catch (Exception e){
		System.out.println(e.toString());
	}
	}
	
	public boolean load(String name){
		String file=name+"_variance_m"+meth+".txt";
		
		String ligne;
		
		FileReader fR;
		InputStream ips;
		InputStreamReader ipsr=null;
		BufferedReader br=null;
		
		File f=new File(file);
		
		String numbers[];
		int i=0;
		
		if(f.canRead()){

			try{
			fR = new FileReader(file);
		
			ips=new FileInputStream(file); 
			ipsr=new InputStreamReader(ips);
			br=new BufferedReader(ipsr);
			
			while((ligne=br.readLine())!=null){
			
				numbers=ligne.split(" ");
				for(int j=0; j<variance[i].length; j++){
					variance[i][j]=Double.parseDouble(numbers[j]);
				}
				i++;
			}

			}catch (Exception e){
				System.out.println(e.toString());
			}
		}else
			return false;
		
		return true;
	}
	
	//------------------------------------//
	/**
	 * Test d'indépendance du Khi2
	 * @author pifi
	 *
	 */
	/*public double khi2(int tailleV1, int tailleV2, int[][] table)
	{
		double statistique = 0;
		int[] sommeV1 = new int[tailleV1];
		int[] sommeV2 = new int[tailleV2];

		for(int i = 0; i < tailleV1; i++)
		{
			int somme = 0;
			for(int j = 0; j < tailleV2; j++)
				somme += table[i][j];
			sommeV1[i] = somme;
		}

		for(int i = 0; i < tailleV2; i++)
		{
			int somme = 0;
			for(int j = 0; j < tailleV1; j++)
				somme += table[j][i];
			sommeV2[i] = somme;
		}

		int N = 0;
		for(int i = 0; i < tailleV1; i++)
			for(int j = 0; j < tailleV2; j++)
				N += table[i][j];
		
		for(int i = 0; i < tailleV1; i++)
			for(int j = 0; j < tailleV2; j++)
			{
				double Eij = ((double)sommeV1[i]*sommeV2[j])/N;
				statistique += (table[i][j]-Eij)*(table[i][j]-Eij)/Eij;
			}
		//for(int cpti=0; cpti<tailleV1; cpti++){
		//	for(int cptj=0; cptj<tailleV2; cptj++){
		//		System.out.print(table[cpti][cptj]+" ");
		//	}
		//	System.out.println();
		//}
		//System.out.println("statistique: "+statistique);
		return pochisq(statistique, (tailleV1-1)*(tailleV2-1));
	}
	
	
    private static final double LOG_SQRT_PI = Math.log(Math.sqrt(Math.PI));
    private static final double I_SQRT_PI = 1 / Math.sqrt(Math.PI);
    private static final int MAX_X = 20; // max value to represent exp(x)
 
    private double pochisq(double x, int df) {
        double a, s;
        double e, c, z;
 
        if (x <= 0.0 || df < 1) {
            return 1.0;
        }
        a = 0.5 * x;
        boolean even = (df & 1) == 0;
        double y = 0;
        if (df > 1) {
            y = ex(-a);
        }
        s = (even ? y : (2.0 * poz(-Math.sqrt(x))));
        if (df > 2) {
            x = 0.5 * (df - 1.0);
            z = (even ? 1.0 : 0.5);
            if (a > MAX_X) {
                e = (even ? 0.0 : LOG_SQRT_PI);
                c = Math.log(a);
                while (z <= x) {
                    e = Math.log(z) + e;
                    s += ex(c * z - a - e);
                    z += 1.0;
                }
                return s;
            } else {
                e = (even ? 1.0 : (I_SQRT_PI / Math.sqrt(a)));
                c = 0.0;
                while (z <= x) {
                    e = e * (a / z);
                    c = c + e;
                    z += 1.0;
                }
                return c * y + s;
            }
        } else {
            return s;
        }
    }
 
 
    private double poz(double z) {
        double y, x, w;
        double Z_MAX = 6.0; // Maximum meaningful z value
        if (z == 0.0) {
            x = 0.0;
        } else {
            y = 0.5 * Math.abs(z);
            if (y >= (Z_MAX * 0.5)) {
                x = 1.0;
            } else if (y < 1.0) {
                w = y * y;
                x = ((((((((0.000124818987 * w
                        - 0.001075204047) * w + 0.005198775019) * w
                        - 0.019198292004) * w + 0.059054035642) * w
                        - 0.151968751364) * w + 0.319152932694) * w
                        - 0.531923007300) * w + 0.797884560593) * y * 2.0;
            } else {
                y -= 2.0;
                x = (((((((((((((-0.000045255659 * y
                        + 0.000152529290) * y - 0.000019538132) * y
                        - 0.000676904986) * y + 0.001390604284) * y
                        - 0.000794620820) * y - 0.002034254874) * y
                        + 0.006549791214) * y - 0.010557625006) * y
                        + 0.011630447319) * y - 0.009279453341) * y
                        + 0.005353579108) * y - 0.002141268741) * y
                        + 0.000535310849) * y + 0.999936657524;
            }
        }
        return z > 0.0 ? ((x + 1.0) * 0.5) : ((1.0 - x) * 0.5);
    }
 
 
    private double ex(double x) {
        return (x < -MAX_X) ? 0.0 : Math.exp(x);
    }*/
    
    
    
    public double khi2(int tailleV1, int tailleV2, int[][] table2)
	{
    	double[][] table = new double[tailleV1][tailleV2];
    	for(int i = 0; i < tailleV1; i++)
    		for(int j = 0; j < tailleV2; j++)
    			table[i][j] = ((double)table2[i][j])/1000.;
		double statistique = 0;
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
		
		int N = 0;
		for(int i = 0; i < tailleV1; i++)
			for(int j = 0; j < tailleV2; j++)
				N += table[i][j];

		for(int i = 0; i < newTailleV1; i++)
			for(int j = 0; j < newTailleV2; j++)
			{
				double Eij = ((double)newSommeV1[i]*newSommeV2[j])/N;
				statistique += (newTable[i][j]-Eij)*(newTable[i][j]-Eij)/Eij;
			}
//		System.out.println("statistique: "+statistique);
		return pochisq(statistique, (newTailleV1-1)*(newTailleV2-1));
	}
	
	
    private static final double LOG_SQRT_PI = Math.log(Math.sqrt(Math.PI));
    private static final double I_SQRT_PI = 1 / Math.sqrt(Math.PI);
    private static final int MAX_X = 20; // max value to represent exp(x)
 
   /* POCHISQ -- probability of chi-square value
        Adapted from:
        Hill, I. D. and Pike, M. C. Algorithm 299
        Collected Algorithms for the CACM 1967 p. 243
        Updated for rounding errors based on remark in
        ACM TOMS June 1985, page 185
    */
    private double pochisq(double x, int df) {
        double a, s;
        double e, c, z;
 
        if (x <= 0.0 || df < 1) {
            return 1.0;
        }
        a = 0.5 * x;
        boolean even = (df & 1) == 0;
        double y = 0;
        if (df > 1) {
            y = Math.exp(-a);
        }
        s = (even ? y : (2.0 * poz(-Math.sqrt(x))));
        if (df > 2) {
            x = 0.5 * (df - 1.0);
            z = (even ? 1.0 : 0.5);
            if (a > MAX_X) {
                e = (even ? 0.0 : LOG_SQRT_PI);
                c = Math.log(a);
                while (z <= x) {
                    e = Math.log(z) + e;
                    s += Math.exp(c * z - a - e);
                    z += 1.0;
                }
                return s;
            } else {
                e = (even ? 1.0 : (I_SQRT_PI / Math.sqrt(a)));
                c = 0.0;
                while (z <= x) {
                    e = e * (a / z);
                    c = c + e;
                    z += 1.0;
                }
                return c * y + s;
            }
        } else {
            return s;
        }
    }
 
 
    private double poz(double z) {
        double y, x, w;
        double Z_MAX = 6.0; // Maximum meaningful z value
        if (z == 0.0) {
            x = 0.0;
        } else {
            y = 0.5 * Math.abs(z);
            if (y >= (Z_MAX * 0.5)) {
                x = 1.0;
            } else if (y < 1.0) {
                w = y * y;
                x = ((((((((0.000124818987 * w
                        - 0.001075204047) * w + 0.005198775019) * w
                        - 0.019198292004) * w + 0.059054035642) * w
                        - 0.151968751364) * w + 0.319152932694) * w
                        - 0.531923007300) * w + 0.797884560593) * y * 2.0;
            } else {
                y -= 2.0;
                x = (((((((((((((-0.000045255659 * y
                        + 0.000152529290) * y - 0.000019538132) * y
                        - 0.000676904986) * y + 0.001390604284) * y
                        - 0.000794620820) * y - 0.002034254874) * y
                        + 0.006549791214) * y - 0.010557625006) * y
                        + 0.011630447319) * y - 0.009279453341) * y
                        + 0.005353579108) * y - 0.002141268741) * y
                        + 0.000535310849) * y + 0.999936657524;
            }
        }
        return z > 0.0 ? ((x + 1.0) * 0.5) : ((1.0 - x) * 0.5);
    }
		

}
