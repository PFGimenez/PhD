package br4cp;

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

import java.util.ArrayList;


class Ordonnancement {
	
	// Attributs
	public ArrayList<Var> variables;
	protected int[][] graphAdj;
	protected int[] nbContraintes;
	public int size;

	// constructeur
	public Ordonnancement(ArrayList<Var> v){
		variables=v;
		graphAdj=new int[variables.size()][variables.size()];
		nbContraintes=new int[variables.size()];
		
		for(int i=0; i<variables.size(); i++){
			nbContraintes[i]=0;
			for(int j=0; j<variables.size(); j++)
				graphAdj[i][j]=0;
		}
		
	}
	
	public Ordonnancement(){}//a utiliser avec addVariables
	//methodes
	public void addVarialbes(ArrayList<Var> v){
		variables=v;
		graphAdj=new int[variables.size()][variables.size()];
		nbContraintes=new int[variables.size()];
		for(int i=0; i<variables.size(); i++){
			nbContraintes[i]=0;
			for(int j=0; j<variables.size(); j++)
				graphAdj[i][j]=0;
		}
		
		size=variables.size();
	}
	
	public void constGraphAdj(int[][] contraintes){
		int arite;
		for(int i=0; i<contraintes.length; i++){
			arite=contraintes[i].length;
			for(int j=0; j<contraintes[i].length; j++){
				for (int k=j+1; k<contraintes[i].length; k++){
					if(graphAdj[contraintes[i][j]][contraintes[i][k]]<arite){
						graphAdj[contraintes[i][j]][contraintes[i][k]]=arite;
						graphAdj[contraintes[i][k]][contraintes[i][j]]=arite;
					}
				}
			}
		}
	}
	
	public void constGraphAdjOriente(int[][] contraintes){
		for(int i=0; i<contraintes.length; i++){
			for(int j=0; j<contraintes[i].length-1; j++){
				graphAdj[contraintes[i][j]][contraintes[i][contraintes[i].length-1]]++;
			}
		}
	}
	
	public void constNbContraintes(int[][] contraintes){
		for (int i=0; i<contraintes.length; i++){
			for(int j=0; j<contraintes[i].length; j++){
				nbContraintes[contraintes[i][j]]++;
			}
		}
	}
	
	public void reordoner(int[][] contraintes, HeuristiqueVariable methode){
		reordoner(contraintes, methode, false);
	}
	
	//gros morceau !!!!!
	public void reordoner(int[][] contraintes, HeuristiqueVariable methode, boolean reverse){

		methode.reordoner(contraintes, this);
		//on prend l'ordre a l'envers
		if(reverse){
			Var temp;
			int j;
			for(int i=0; i<(variables.size())/2; i++){
				j=variables.size()-i-1;
				temp=variables.get(j);
				variables.set(j, variables.get(i));
				variables.set(i, temp);
			}
		}
		
		//////fiiiiiiinnnn//////////
		for(int i=0; i<size; i++){
				variables.get(i).pos=i+1;
		}
	}
	
	public void afficherOrdre(){
		System.out.println("ordre sur les variables : ");
		for (int i=0; i<variables.size(); i++)
			System.out.println(i + " : " + variables.get(i).name);

	}
	
	public void getInfo(int[][] contraintes){
		int span=0;
		int bw=0;
		int max=-1;
		int min=size+1;
		for(int i=0; i<contraintes.length; i++){
			for(int j=0; j<contraintes[i].length; j++){
				if(contraintes[i][j]<min)
					min=contraintes[i][j];
				if(contraintes[i][j]>max)
					max=contraintes[i][j];
			}
			if(max!=-1 && min!=size+1){
				span+=(max-min);
				if((max-min)>bw)
					bw=max-min;
			}
			max=-1;
			min=size+1;
		}
		
		int spanv0=0;
		int bwv0=0;
		for(int i=0; i<contraintes.length; i++){
			for(int j=0; j<contraintes[i].length; j++){
				if(variables.get(contraintes[i][j]).name.compareTo("v0") != 0){
					if(contraintes[i][j]<min)
						min=contraintes[i][j];
					if(contraintes[i][j]>max)
						max=contraintes[i][j];
				}
			}
			if(max!=-1 || min!=size+1){
				spanv0+=(max-min);
				if((max-min)>bwv0)
					bwv0=max-min;
			}
			max=-1;
			min=size+1;
		}
		System.out.println("span=" + span + "  spanv0=" + spanv0 + "  bw=" + bw + "  bwv0=" + bwv0 );
				
	}
	
	public void supprmonth(){
		variables.remove(0);
		size--;
		for(int i=0; i<variables.size(); i++)
			variables.get(i).pos=variables.get(i).pos-1;
	}

	
	//accesseurs
	public int size(){
		return variables.size();
	}
	
	public ArrayList<Var> getVariables(){
		return variables;
	}
}
