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
	ArrayList<Var> variables;
	protected int[][] graphAdj;
	protected int[] nbContraintes;
	int size;

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
	
	public void reordoner(int[][] contraintes, int methode){
		reordoner(contraintes, methode, false);
	}
	
	//gros morceau !!!!!
	public void reordoner(int[][] contraintes, int methode, boolean reverse){
		ArrayList<Var> listeTemp=new ArrayList<Var>();
		
		if(methode==-1){			// ordre random
			for(int i=0; i<variables.size(); i++)
					listeTemp.add(variables.get(i));

			variables.clear();
			
			while(!listeTemp.isEmpty()){
				int rand=(int) Math.floor(Math.random()*listeTemp.size());
				variables.add(listeTemp.get(rand));
				listeTemp.remove(rand);
			}
			

		}
		
		if(methode==0){			// ordre choisi
			listeTemp.add(variables.get(variables.size()-1));
			variables.remove(variables.size()-1);

			while(listeTemp.size()!=0){
				variables.add(0, listeTemp.get(listeTemp.size()-1));
				listeTemp.remove(listeTemp.size()-1);
			}

		}
		
		//MCF
		if(methode==1){ // plus contraint au moins contraint
			
			constNbContraintes(contraintes);
			
			int max=-1;
			int varmax=-1;
			
			for(int curr=0; curr<size; curr++){
				for(int i=0; i<size; i++){
					if(nbContraintes[i]>max){
						max=nbContraintes[i];
						varmax=i;
					}
				}
				//System.out.println(varmax + "   " + variables.get(varmax).name);
				listeTemp.add(variables.get(varmax));
				
				nbContraintes[varmax]=-1;		//faut plus qu'elle ressorte
				max=-1;
				varmax=-1;
			}

			for(int i=0; i<listeTemp.size(); i++)
				variables.set(i, listeTemp.get(i));
			
		}
		
		
		//for(int i=0; i<size; i++){
		//	variables.get(i).pos=i+1;
		//}
		
		//MCS-inv
		if(methode==3){
			constGraphAdj(contraintes);
			//constNbContraintes(contraintes);
			
			int score[]=new int[size];
			int max=-1;
			int varmax=-1;
			
			for(int curr=0; curr<size; curr++){
				for(int i=0; i<size; i++){
					if(score[i]>max){
						max=score[i];
						varmax=i;
					}
				}
				//System.out.println("@Ord : "+varmax + "   " + variables.get(varmax).name);
				listeTemp.add(variables.get(varmax));
				
				score[varmax]=-1;		//faut plus qu'elle ressorte
				//mise a jours de score
				for(int i=0; i<size; i++){
					if(score[i]!=-1 && graphAdj[varmax][i]>0)
						score[i]++;
				}
				max=-1;
				varmax=-1;
					
			}
			for(int i=0; i<listeTemp.size(); i++)
				variables.set(i, listeTemp.get(i));
			
		}
		
		//MCS-inv v2
		if(methode==13){
			constGraphAdj(contraintes);
			//constNbContraintes(contraintes);
			
			int score[]=new int[size];
			int facteur[]=new int[size];
			int max=-1;
			int varmax=-1;
			
			for(int curr=0; curr<size; curr++){
				for(int i=0; i<size; i++){
					if(score[i]>max){
						max=score[i];
						varmax=i;
					}
				}
				//System.out.println("@Ord : "+varmax + "   " + variables.get(varmax).name);
				listeTemp.add(variables.get(varmax));
				
				score[varmax]=-1;		//faut plus qu'elle ressorte
				//mise a jours de score
				for(int i=0; i<size; i++){
					if(score[i]!=-1 && graphAdj[varmax][i]>0){
						facteur[i]++;
						score[i]+=facteur[i];
					}
				}
				max=-1;
				varmax=-1;
					
			}
			for(int i=0; i<listeTemp.size(); i++)
				variables.set(i, listeTemp.get(i));
			
		}
		
		//BW
		if(methode==2){		//band width
			int[] bandWidth=new int[size];
			for(int i=0; i<bandWidth.length; i++)
				bandWidth[i]=0;
			
			int max;
			int varmax;
			
			constGraphAdj(contraintes);
			constNbContraintes(contraintes);
			
			//init : v0    (a changer)
			listeTemp.add(variables.get(0));
			bandWidth[0]=-1;
			
			for(int cpt=1; cpt<size; cpt++ ){
				//actualisation bandwidth
				for(int i=0; i<bandWidth.length; i++){
					if(bandWidth[i]>=0)
						bandWidth[i]=0;							//réinit
				}
				for(int i=0; i<listeTemp.size(); i++){
					for(int j=0; j<size; j++){
						if(bandWidth[j]>=0){					//pas deja passee
							if(graphAdj[i][j]>0)
								bandWidth[j]+=(int)Math.pow((listeTemp.size()-i), 2);
						}
					}
				}
				
				//recherche meilleur
				varmax=-1;
				max=-1;
				for(int i=0; i<bandWidth.length; i++){
					if (bandWidth[i]>max){
						varmax=i;
						max=bandWidth[i];
					}
				}
				
				//System.out.println(varmax + "   " + variables.get(varmax).name);
				listeTemp.add(variables.get(varmax));
					
				bandWidth[varmax]=-1;		//faut plus qu'elle ressorte
			}
			
			for(int i=0; i<listeTemp.size(); i++)
				variables.set(i, listeTemp.get(i));
			
		}
		
		if(methode==6){		//ordre d'assendance (reseaux bayesiennes)
			int indexMax;
			int temp;
			ArrayList<Integer> reordre=new ArrayList<Integer>();
			
			for(int i=0; i<contraintes.length; i++){
				//System.out.println("xaer =>> " + i);
				indexMax=-1;
				if(contraintes[i].length>=2){
					for(int j=0; j<contraintes[i].length-1; j++){
						if(reordre.indexOf(contraintes[i][j])>indexMax)
								indexMax=reordre.indexOf(contraintes[i][j]);
					}
					temp=reordre.indexOf(contraintes[i][contraintes[i].length-1]);
					if(temp<indexMax){												//mal classé
						reordre.remove(temp);
						reordre.add(indexMax, contraintes[i][contraintes[i].length-1]);
						i=-1;
					}
				}
			}
			for(int i=0; i<size; i++)
				listeTemp.add(variables.get(reordre.get(i)));
			for(int i=0; i<listeTemp.size(); i++)
				variables.set(i, listeTemp.get(i));
			
		}
		
		if(methode==7){
			constGraphAdj(contraintes);
			int[] count=new int[size];
			int min, suppr;
			ArrayList<Integer> reordre=new ArrayList<Integer>();
			
			//init COUNT
			for(int i=0; i<size; i++)
				count[i]=0;
			
			for(int v=0; v<size; v++){
				for(int vp=v+1; vp<size; vp++){
					if(graphAdj[v][vp]>0){		//ya un lien entre v et v'
						for(int vpp=0; vpp<size; vpp++){		//on parcourt les vpps
							if(vpp!=v && vpp!=vp){							//sauf v et vp
								if(graphAdj[v][vpp]>0 && graphAdj[vp][vpp]==0){		// si ya un lien entre v et v" mais pas entre v' et v"
									count[v]++;
								}
								if(graphAdj[v][vpp]==0 && graphAdj[vp][vpp]>0){			// si le contraire
									count[vp]++;

								}
							}
						}
					}
				}
			}
			//fin init
			
			//deroulement
			while(reordre.size()<size){
				
				min=size*2+1;
				suppr=-1;
				for(int i=0; i<size; i++){		//on cherche le meilleur neud
					if(count[i]<min){
						min=count[i];
						suppr=i;
					}
				}
				reordre.add(suppr);			//on l'ajoute
				count[suppr]=size*2;

				//System.out.println("supr="+suppr);
				//actualise count && graph adj
				for(int vp=0; vp<size; vp++){
					if(graphAdj[suppr][vp]>0){
						for(int vpp=vp+1; vpp<size; vpp++){
							if(graphAdj[suppr][vpp]>0){
								if(graphAdj[vp][vpp]==0){		//cas 1
									graphAdj[vp][vpp]++;
									graphAdj[vpp][vp]++;

									for(int x=0; x<size; x++){
										if(x!=suppr && x!=vp && x!=vpp){
											if(graphAdj[vp][x]>0 && graphAdj[vpp][x]>0)
												count[x]--;
											if(graphAdj[vp][x]>0 && graphAdj[vpp][x]==0)
												count[vp]++;
											if(graphAdj[vp][x]==0 && graphAdj[vpp][x]>0)
												count[vpp]++;
										}
									}
								}
							}
						}
						//on est encore dans la boucle de vp
						for(int y=0; y<size; y++){
							if(y!=suppr && y!=vp && graphAdj[vp][y]>0 && graphAdj[suppr][y]==0)
								count[vp]--;
						}
					}
				}
				for(int i=0; i<size; i++){		//on efface le suppr
					graphAdj[suppr][i]=0;
					graphAdj[i][suppr]=0;
				}
			}
			//fin while
			for(int i=0; i<size; i++)
				listeTemp.add(variables.get(reordre.get(i)));
			for(int i=0; i<listeTemp.size(); i++)
				variables.set(i, listeTemp.get(i));
			
		}
		
		if(methode==8){
			constGraphAdjOriente(contraintes);
			int[] count=new int[size];
			int min, suppr;
			ArrayList<Integer> reordre=new ArrayList<Integer>();
			
			//init COUNT
			for(int i=0; i<size; i++)
				count[i]=0;
			
			for(int v=0; v<size; v++){
				for(int vp=0; vp<size; vp++){
					if(v!=vp && graphAdj[v][vp]>0){		//ya un lien de v vers v'
						for(int vpp=0; vpp<size; vpp++){		//on parcourt les vpps
							if(vpp!=v && vpp!=vp){							//sauf v et vp
								if(graphAdj[v][vpp]>0 && graphAdj[vp][vpp]==0){		// si ya un lien entre v et v" mais pas entre v' et v"
									count[v]++;
								}
							}
						}
					}
				}
			}
			//fin init
			
			//deroulement
			while(reordre.size()<size){
				
				min=size*2+1;
				suppr=-1;
				for(int i=0; i<size; i++){		//on cherche le meilleur neud
					if(count[i]<min){
						min=count[i];
						suppr=i;
					}
				}
				reordre.add(suppr);			//on l'ajoute
				count[suppr]=size*2+1;

				//System.out.println("supr="+suppr);
				//actualise count && graph adj
				for(int vp=0; vp<size; vp++){
					if(graphAdj[suppr][vp]>0){
						for(int vpp=0; vpp<size; vpp++){
							if(graphAdj[suppr][vpp]>0 && vpp!=vp){
								if(graphAdj[vp][vpp]==0){
									for(int x=0; x<size; x++){
										if(x!=suppr && x!=vp && x!=vpp){
											if(graphAdj[x][vp]>0 && graphAdj[x][vpp]>0)
												count[x]--;
											if(graphAdj[x][vp]>0 && graphAdj[x][vpp]==0)
												count[vp]++;
											if(graphAdj[x][vp]==0 && graphAdj[x][vpp]>0)
												count[vpp]++;
										}
									}
									graphAdj[vp][vpp]++;
								}
							}
						}
						//on est encore dans la boucle de vp
						for(int y=0; y<size; y++){
							if(y!=suppr && y!=vp && graphAdj[y][vp]>0 && graphAdj[y][suppr]==0)
								count[vp]--;
						}
					}
				}
				for(int i=0; i<size; i++){		//on efface le suppr
					graphAdj[suppr][i]=0;
					graphAdj[i][suppr]=0;
				}
			}
			//fin while
			for(int i=0; i<size; i++)
				listeTemp.add(variables.get(reordre.get(i)));
			for(int i=0; i<listeTemp.size(); i++)
				variables.set(i, listeTemp.get(i));
			
		}
		
		
		//Force
		if(methode==4){
			boolean changement=true;
			float COG[]= new float[contraintes.length];		//centre gravite contrainte
			float COGvar[][]=new float[2][size];		//centre gravite variable
			float nbConvar[]=new float[size];		//par combien faut diviser le COG variable
			int cpt=0;									//nb iterations
			for(int i=0; i<size; i++){
				COGvar[1][i]=i;
			}
			
			while(changement==true){
				cpt++;
				//calcul du COG des contraintes
				for (int i=0; i<COG.length; i++){
					COG[i]=0;
					for (int j=0; j<contraintes[i].length; j++){
						COG[i]+=COGvar[1][contraintes[i][j]];			//[1] : place dans l'ordre de la variable
					}
					COG[i]=COG[i]/contraintes[i].length;
				}
				
				//calcul du COG des varialbes
				for (int i=0; i<size; i++){
					COGvar[0][i]=0;
					nbConvar[i]=0;
				}
				for (int i=0; i<COG.length; i++){
					for (int j=0; j<contraintes[i].length; j++){
						COGvar[0][contraintes[i][j]]+=COG[i];
						nbConvar[contraintes[i][j]]++;
					}
				}
				for (int i=0; i<size; i++){
					if(nbConvar[i]!=0)
						COGvar[0][i]=COGvar[0][i]/nbConvar[i];
					else
						COGvar[0][i]=size;			//si pas dans les contraintes, on les met a la fin
				}
				
				//reordering
				
				changement=false;
				float min=99999;		//on cherche le min, mais supperieur a la borne inf
				float borninf = -1;
				float indice = 0;

				while(indice<size){
					for(int i=0; i<size; i++){
						if(COGvar[0][i]<min && COGvar[0][i]>borninf)
							min=COGvar[0][i];	
					}
					for(int i=0; i<size; i++){
						if(COGvar[0][i]==min){
							if(COGvar[1][i]!=indice)		//alors on a pas fini
								changement=true;
							COGvar[1][i]=indice;
							indice++;
						}
					}
					borninf=min;
					min=99999;
				}
				
				//for(int i=0; i<size; i++)
				//	System.out.print(COGvar[1][i] + " ");
				//System.out.println();
					
			}//fin du while
			//System.out.println("fin :");
			//for(int i=0; i<size; i++)
			//	System.out.print(COGvar[1][i] + " ");
			//System.out.println();
			
			System.out.println("Ordonnancement : iterations de Force : " + cpt);
			
			//init listetemp
			for(int i=0; i<size; i++){
				listeTemp.add(null);
			}
			for(int i=0; i<size; i++){
				listeTemp.set((int)COGvar[1][i], variables.get(i));
			}
			
			for(int i=0; i<listeTemp.size(); i++)
				variables.set(i, listeTemp.get(i));
			
			
		}
		
		if(methode==9){
			constGraphAdj(contraintes);
			//constNbContraintes(contraintes);
			
			int score[]=new int[size];
			int max=-1;
			int varmax=-1;
			
			for(int curr=0; curr<size; curr++){
				for(int i=0; i<size; i++){
					if(score[i]>max){
						max=score[i];
						varmax=i;
					}
				}
				listeTemp.add(variables.get(varmax));
				
				score[varmax]=-1;		//faut plus qu'elle ressorte
				//mise a jours de score
				for(int i=0; i<size; i++){
					if(score[i]!=-1 && graphAdj[varmax][i]>0){
						//recherche de l'arite max
						score[i]+=graphAdj[varmax][i]-1;
						//score[i]+=1;
					}
				}
				
				max=-1;
				varmax=-1;
					
			}
			for(int i=0; i<listeTemp.size(); i++)
				variables.set(i, listeTemp.get(i));
			
		}
		
		//MCS-inv + 1 (somme des mcs la plus faible)
		if(methode==5){
			constGraphAdj(contraintes);
			//constNbContraintes(contraintes);
			
			int score[]=new int[size];
			int scoreplus1[]=new int[size];
			int max=-1;
			int minplus1=size*size; //car on compare à la somme !!!
			int varminplus1=-1;
			int somme;
			
			//recherche max0
			//recherche du plus grand score i
			for(int curr=0; curr<size; curr++){
				for(int i=0; i<size; i++){
					if(score[i]>max){
						max=score[i];
					}
				}
				
				//calcul de la somme de score i
				for(int i=0; i<size; i++){
					if(score[i]!=-1){//==max){
						somme=0;
						//calcule max+1
						for(int j=0; j<size; j++){
							if(score[j]!=-1 && j!=i){
								//pour tous les j non encore ajoute
								scoreplus1[j]=score[j];
								if(graphAdj[i][j]>0){
									scoreplus1[j]++;
								}
								somme+=scoreplus1[j];
							}
						}
						if(somme<minplus1){
							minplus1=somme;
							varminplus1=i;
						}
					}
				}
				
				//if(curr==0)
					//varminplus1=0;
				
				listeTemp.add(variables.get(varminplus1));
				
				score[varminplus1]=-1;		//faut plus qu'elle ressorte
				//mise a jours de score
				for(int i=0; i<size; i++){
					if(score[i]!=-1 && graphAdj[varminplus1][i]>0){
						//recherche de l'arite max
						score[i]+=graphAdj[varminplus1][i]-1;
						//score[i]+=1;
					}
				}
				
				max=-1;
				minplus1=size*size;
				varminplus1=-1;
					
			}
			for(int i=0; i<listeTemp.size(); i++)
				variables.set(i, listeTemp.get(i));
			
		}
		
		//MCS-inv + 1 (somme des ^2 des mcs la plus faible)
		if(methode==10){
			constGraphAdj(contraintes);
			//constNbContraintes(contraintes);
			
			int score[]=new int[size];
			int scoreplus1[]=new int[size];
			int max=-1;
			int minplus1=size*size; //car on compare à la somme !!!
			int varminplus1=-1;
			int somme;
			
			//recherche max0
			//recherche du plus grand score i
			for(int curr=0; curr<size; curr++){
				for(int i=0; i<size; i++){
					if(score[i]>max){
						max=score[i];
					}
				}
				
				//calcul de la somme de score i
				for(int i=0; i<size; i++){
					if(score[i]!=-1){//==max){
						somme=0;
						//calcule max+1
						for(int j=0; j<size; j++){
							if(score[j]!=-1 && j!=i){
								//pour tous les j non encore ajoute
								scoreplus1[j]=score[j];
								if(graphAdj[i][j]>0){
									scoreplus1[j]++;
								}
								somme+=Math.pow(scoreplus1[j],2);
							}
						}
						if(somme<minplus1){
							minplus1=somme;
							varminplus1=i;
						}
					}
				}
				
				//if(curr==0)
					//varminplus1=0;
				
				listeTemp.add(variables.get(varminplus1));
				
				score[varminplus1]=-1;		//faut plus qu'elle ressorte
				//mise a jours de score
				for(int i=0; i<size; i++){
					if(score[i]!=-1 && graphAdj[varminplus1][i]>0){
						//recherche de l'arite max
						score[i]+=graphAdj[varminplus1][i]-1;
						//score[i]+=1;
					}
				}
				
				max=-1;
				minplus1=size*size;
				varminplus1=-1;
					
			}
			for(int i=0; i<listeTemp.size(); i++)
				variables.set(i, listeTemp.get(i));
			
		}
		
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
