package br4cp;

import java.util.ArrayList;

public class HeuristiqueVariableMCSinvPlusUn implements HeuristiqueVariable {

	@Override
	public void reordoner(int[][] contraintes,
			Ordonnancement ord) {
		ArrayList<Var> listeTemp=new ArrayList<Var>();
	ord.constGraphAdj(contraintes);
	//constNbContraintes(contraintes);
	
	int score[]=new int[ord.size];
	int scoreplus1[]=new int[ord.size];
	int max=-1;
	int minplus1=ord.size*ord.size; //car on compare à la somme !!!
	int varminplus1=-1;
	int somme;
	
	//recherche max0
	//recherche du plus grand score i
	for(int curr=0; curr<ord.size; curr++){
		for(int i=0; i<ord.size; i++){
			if(score[i]>max){
				max=score[i];
			}
		}
		
		//calcul de la somme de score i
		for(int i=0; i<ord.size; i++){
			if(score[i]!=-1){//==max){
				somme=0;
				//calcule max+1
				for(int j=0; j<ord.size; j++){
					if(score[j]!=-1 && j!=i){
						//pour tous les j non encore ajoute
						scoreplus1[j]=score[j];
						if(ord.graphAdj[i][j]>0){
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
		
		listeTemp.add(ord.variables.get(varminplus1));
		
		score[varminplus1]=-1;		//faut plus qu'elle ressorte
		//mise a jours de score
		for(int i=0; i<ord.size; i++){
			if(score[i]!=-1 && ord.graphAdj[varminplus1][i]>0){
				//recherche de l'arite max
				score[i]+=ord.graphAdj[varminplus1][i]-1;
				//score[i]+=1;
			}
		}
		
		max=-1;
		minplus1=ord.size*ord.size;
		varminplus1=-1;
			
	}
	for(int i=0; i<listeTemp.size(); i++)
		ord.variables.set(i, listeTemp.get(i));
	}
	
}
