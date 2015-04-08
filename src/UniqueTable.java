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

import java.io.FileWriter;
import java.util.ArrayList;
import java.math.*;

// 0 : leaf
// 1 -> n : vars

public class UniqueTable {/*
	protected ArrayList<NodeDD> [] uniqueTable;
	protected int nbVariables;
	
	// constructeur
	
		public UniqueTable(int nbVar){
			uniqueTable = new ArrayList [nbVar+1];
			for(int i=0; i<nbVar+1; i++)
				uniqueTable[i] = new ArrayList<NodeDD>();
			
			nbVariables=nbVar;
		}
		
		//permet d'attribuer un index a chacun des neuds
		public void giveIndex(){
			int k=0;
			for(int i=0; i<uniqueTable.length; i++){
				for(int j=0; j<uniqueTable[i].size(); j++){
					uniqueTable[i].get(j).id=k;
					k++;
				}
			}
		}
		
		//supprime les neuds dont le compteur est a -1
		public void supprNeudNegatifs(){
			for(int i=0; i<uniqueTable.length; i++){
				for(int j=0; j<uniqueTable[i].size(); j++){
					if(uniqueTable[i].get(j).cpt==-1){
						remove(uniqueTable[i].get(j));
						j--;
					}
				}
			}
		}
		
		//met les valeurs des neuds a 0 (si flagLeaf=false, pas les feuilles)
		public void valNodesToZero(boolean flagLeaf){
			int start;
			if(flagLeaf)
				start=0;
			else
				start=1;
			
			for(int i=start; i<uniqueTable.length; i++){
				for(int j=0; j<uniqueTable[i].size(); j++){
					uniqueTable[i].get(j).setVal(0);
				}
			}
		}
		
		//accesseurs
		public void add(NodeDD n){
			if(n.variable!=null)
				uniqueTable[n.variable.pos].add(n);
			else
				uniqueTable[0].add(n);
		}
		
		public int size(){
			int size=0;
			for(int i=0; i<uniqueTable.length; i++)
				size+=uniqueTable[i].size();
			return size;
		}
		
		public int sizeArcs(){
			int size=1;
			for(int i=1; i<uniqueTable.length; i++)
				for(int j=0; j<uniqueTable[i].size(); j++)
					size+=uniqueTable[i].get(j).kids.size();
			return size;
		}
		
		public int size(int var){
			return uniqueTable[var].size();
		}
		
		public NodeDD get(int var, int i){
			return uniqueTable[var].get(i);
		}
		
		public ArrayList<NodeDD> get(int var){
			return uniqueTable[var];
		}
		
		public NodeDD getun(){
			NodeDD un=new NodeDD(null,1);
			int a=recherche(un);
			if(a==-1){					//le neud un n'existe pas
				uniqueTable[0].add(un);
			}else{
				un=uniqueTable[0].get(a);
			}
			
			return un;
		}
		
		//un, zero, meme fonction
		public NodeDD getzero(){
			
			if(uniqueTable[0].size()>0)					//si il est en debut d'indice, on le trouve tout de suite
				if(uniqueTable[0].get(0).value==0)
					return uniqueTable[0].get(0);
			
			NodeDD zero=new NodeDD(null,0);
			int a=recherche(zero);
			if(a==-1){					//le neud un n'existe pas
				uniqueTable[0].add(0, zero);
			}else{
				zero=uniqueTable[0].get(a);
				uniqueTable[0].remove(a);
				uniqueTable[0].add(0, zero);
				
			}
			
			return zero;
		}
		
		public void remove(int var, int i){
			remove(uniqueTable[var].get(i));
		}
		
		public void remove(NodeDD n){	
			int position;
			if (n.variable!=null)
				position=n.variable.pos;
			else
				position=0;
			
			while(n.kids.size()!=0){
				if(n.kids.get(0).fils.fathers.size()==1){	//on veille a ne pas laisser d'orphelins
					remove(n.kids.get(0).fils);
				}							
				n.kids.get(0).remove();		//on supprime tous les arcs fils
			}
			if(uniqueTable[position].indexOf(n)!=-1)			//ne pas supprimer un arc qui a deja été suppr de l'ut
				uniqueTable[position].remove(uniqueTable[position].indexOf(n));
		}
		
		//renvoie la position dans ut d'un neud identique a n (-1 si aucun)
		public int recherche(NodeDD n){
			int j;
			int id;
			if(n.variable!=null)	//si c'est pas une feuille, on recupere l'id
				id=n.variable.pos;
			else					// si c'est une feuille, on cherche dans les zeros
				id=0;
			
			j=uniqueTable[id].indexOf(n);
			for(int i=0; i<uniqueTable[id].size(); i++){
				if(i!=j){
					if(n.compare(uniqueTable[id].get(i))){
						return i;
					}
				}
			}
			
			return -1;
		}
		
		//initialise tous les cpt a une valeure x
		public void cptTo(int x){
			for(int i=0; i<uniqueTable.length; i++){
				for(int j=0; j<uniqueTable[i].size(); j++){
					uniqueTable[i].get(j).cpt=x;
				}
			}
		}
		
		//initialise tous les counting a -1
		public void countingToMoinsUn(){
			for(int i=0; i<uniqueTable.length; i++){
				for(int j=0; j<uniqueTable[i].size(); j++){
					uniqueTable[i].get(j).counting=-1;
					uniqueTable[i].get(j).pondere=0;
				}
			}
		}
		
		//initialise tous les cptMult a une valeure x
		public void memoireTo(double x){
			for(int i=0; i<uniqueTable.length; i++){
				for(int j=0; j<uniqueTable[i].size(); j++){
					uniqueTable[i].get(j).memoire=x;
				}
			}
		}
		
		//initialise tous les valeurs de chaques neuds a x (en general 0)
		public void valueTo(int x){
			for(int i=0; i<uniqueTable.length; i++){
				for(int j=0; j<uniqueTable[i].size(); j++){
					uniqueTable[i].get(j).value=x;
				}
			}
		}
		
		public void copieToNull(){
			for(int i=0; i<uniqueTable.length; i++){
				for(int j=0; j<uniqueTable[i].size(); j++){
					uniqueTable[i].get(j).copie.clear();
					uniqueTable[i].get(j).indcopie.clear();
				}
			}
		}
		
	    public void rechercheNoeudInutile(){
	    	boolean useless;
	    	cptTo(0);
			for(int i=1; i<uniqueTable.length; i++){
				for(int j=0; j<uniqueTable[i].size(); j++){
		        	useless=true;
		        	
		            for(int k=1; k<uniqueTable[i].get(j).kids.size(); k++){
		            	if( 	uniqueTable[i].get(j).kids.get(k).getVal()!=uniqueTable[i].get(j).kids.get(0).getVal() ||
		            			uniqueTable[i].get(j).kids.get(k).getCoef()!=uniqueTable[i].get(j).kids.get(0).getCoef() ||
		            			(uniqueTable[i].get(j).kids.get(k).bottom==0)!=(uniqueTable[i].get(j).kids.get(0).bottom==0) ||
		            			uniqueTable[i].get(j).kids.get(k).fils!=uniqueTable[i].get(j).kids.get(0).fils){		         
		            		useless=false;												//ce neud sert a qqch
		            		break;														//innutile d'aller plus loin
		            	}
		            }
		            if(useless){
		            	uniqueTable[i].get(j).courtcircuit();        //ce neud est court circuité, et peut etre supprimé
		            	//uniqueTable[i].remove(j); //plouf
		            	//j--;
		            }
				}
	        }
			supprNeudNegatifs();
	    }
	    
	    public void cutBottom(){
	    	for(int i=1; i<uniqueTable.length; i++){
	    		for(int j=0; j<uniqueTable[i].size(); j++){
	    			for(int k=0; k<uniqueTable[i].get(j).kids.size(); k++){
	    				if(uniqueTable[i].get(j).kids.get(k).bottom > 0){
	    					//on supprime le fils si besoin
	    					if(uniqueTable[i].get(j).kids.get(k).fils.fathers.size()==1){	//on veille a ne pas laisser d'orphelins
	    						remove(uniqueTable[i].get(j).kids.get(k).fils);
	    					}	
	    					uniqueTable[i].get(j).kids.get(k).changerFils(getzero());
	    					uniqueTable[i].get(j).kids.get(k).setVal(0);
	    				}
	    			}
	    		}
	    	}
	    }
	
		//public void test(int x){
		//	int a=0, b=0;
		//	for(int i=0; i<uniqueTable.size(); i++){
		//		a+=uniqueTable.get(i).fathers.size();
		//		b+=uniqueTable.get(i).kids.size();
		//	}
		//	if(a!=b){
		//		//erreur
		//		uniqueTable.get(x).cpt=100;
		//	}
		//}
	    
		public void compterNeudsSansPere(){
			int cpt=0;
			for(int i=0; i<uniqueTable.length; i++){
				for(int j=0; j<uniqueTable[i].size(); j++){
					if (uniqueTable[i].get(j).fathers.size()==0)
						cpt++;						
				}
			}
			if (cpt!=0)
				System.out.println("/!\\ neuds solitaires : " + cpt);
			
			for(int i=0; i<uniqueTable.length; i++){
				for(int j=0; j<uniqueTable[i].size(); j++){
					if (uniqueTable[i].get(j).fathers.size()==0){
						remove(uniqueTable[i].get(j));
						System.out.println("plop");
					}
				}
			}
			for(int i=0; i<uniqueTable.length; i++){
				for(int j=0; j<uniqueTable[i].size(); j++){
					if (uniqueTable[i].get(j).fathers.size()==0){
						remove(uniqueTable[i].get(j));
						System.out.println("plip");
					}

				}
			}
			for(int i=0; i<uniqueTable.length; i++){
				for(int j=0; j<uniqueTable[i].size(); j++){
					if (uniqueTable[i].get(j).fathers.size()==0)
						remove(uniqueTable[i].get(j));					
				}
			}
			for(int i=0; i<uniqueTable.length; i++){
				for(int j=0; j<uniqueTable[i].size(); j++){
					if (uniqueTable[i].get(j).fathers.size()==0)
						remove(uniqueTable[i].get(j));					
				}
			}
			for(int i=0; i<uniqueTable.length; i++){
				for(int j=0; j<uniqueTable[i].size(); j++){
					if (uniqueTable[i].get(j).fathers.size()==0)
						remove(uniqueTable[i].get(j));					
				}
			}
			for(int i=0; i<uniqueTable.length; i++){
				for(int j=0; j<uniqueTable[i].size(); j++){
					if (uniqueTable[i].get(j).fathers.size()==0)
						remove(uniqueTable[i].get(j));					
				}
			}
			for(int i=0; i<uniqueTable.length; i++){
				for(int j=0; j<uniqueTable[i].size(); j++){
					if (uniqueTable[i].get(j).fathers.size()==0)
						remove(uniqueTable[i].get(j));					
				}
			}
			for(int i=0; i<uniqueTable.length; i++){
				for(int j=0; j<uniqueTable[i].size(); j++){
					if (uniqueTable[i].get(j).fathers.size()==0)
						remove(uniqueTable[i].get(j));					
				}
			}
			for(int i=0; i<uniqueTable.length; i++){
				for(int j=0; j<uniqueTable[i].size(); j++){
					if (uniqueTable[i].get(j).fathers.size()==0)
						remove(uniqueTable[i].get(j));					
				}
			}
			for(int i=0; i<uniqueTable.length; i++){
				for(int j=0; j<uniqueTable[i].size(); j++){
					if (uniqueTable[i].get(j).fathers.size()==0)
						remove(uniqueTable[i].get(j));					
				}
			}
		}
		
		//ligne commente : peut etre amelioré pour sldd* -> aadd
		//date de l'epoque ou on utilisait des fractions. inutile maintenant !
		//public void modeAaddON(){
		//	for(int i=0; i<uniqueTable.length; i++){
		//		for(int j=0; j<uniqueTable[i].size(); j++){
		//			for(int k=0; k<uniqueTable[i].get(j).kids.size(); k++){
		//				uniqueTable[i].get(j).kids.get(k).fracAddi.n=uniqueTable[i].get(j).kids.get(k).val;	
		//				uniqueTable[i].get(j).kids.get(k).val=0;
		//				if(uniqueTable[i].get(j).kids.get(k).fils.isLeaf()){		//cas des arcs avant feuilles (mult a 0)
		//					//uniqueTable[i].get(j).kids.get(k).fracAddi.add(uniqueTable[i].get(j).kids.get(k).fracMult);
		//					uniqueTable[i].get(j).kids.get(k).fracMult.n=0;			//on met les arcs du bas a zero
		//				}
		//			}
		//		}
		//	}
		//}
		
		//public void printValFeuillesAdd(){
		//	for(int i=0; i<this.uniqueTable[0].size(); i++)
		//		System.out.print(this.uniqueTable[0].get(i).value + " ");
		//	System.out.println();
		//}
		
		//public void aproximationAdd(){
		//	for(int i=0; i<this.uniqueTable[0].size(); i++){
		//		
		//		double c=this.uniqueTable[0].get(i).value;
		//		int j=0;
		//		long comp =1000000000;
		//		long temp=1000000000;
		//		//comp=comp*temp;
		//		if(c>0){
		//			while(c<comp){
		//				c=c*10;
		//				j++;
		//			}
		//			temp=Math.round(c);
		//			c=(double)temp*(Math.pow(10, -j));
		//		}
		//		this.uniqueTable[0].get(i).value=c;
		//	}

			
		//}
		
		//fusion de deux ut
		public void fusion(UniqueTable ut2){
			for(int i=0; i<uniqueTable.length; i++){
				while(ut2.uniqueTable[i].size()>0){
					ut2.uniqueTable[i].get(0).variable=this.uniqueTable[i].get(0).variable;
					uniqueTable[i].add(ut2.uniqueTable[i].get(0));
					ut2.uniqueTable[i].remove(0);
				}
			}
			this.giveIndex();
		}
		
		public NodeDD getWithIndex(int ind){
			for(int i=0; i<uniqueTable.length; i++){
				for(int j=0; j<uniqueTable[i].size(); j++){
					if (uniqueTable[i].get(j).id==ind)
						return uniqueTable[i].get(j);
				}
			}
			System.out.println("@ut err, pas de neud trouve a cet index");
			return null;
		}
		
		public void addToInt(){
			for(int i=0; i<uniqueTable[0].size(); i++){
				uniqueTable[0].get(i).setVal(Math.round(uniqueTable[0].get(i).getVal()));
			}
		}*/
}
