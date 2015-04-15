package br4cp;

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
	
	public Variance(ArrayList<Var> v, VDD graph, TestIndependance test, String name){
		variables=v;
		
		variance = (double[][]) DataSaver.charger(name+"_variance_m"+test.getClass().getSimpleName()+".txt");
		
		if(variance == null)
		{
			variance = test.getIndependancy(v, graph);
			DataSaver.sauvegarder(variance, name+"_variance_m"+test.getClass().getName()+".txt");
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
	/*
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
			fR.close();
			br.close();

			}catch (Exception e){
				System.out.println(e.toString());
			}
		}else
			return false;
		
		return true;
	}
	*/		

}
