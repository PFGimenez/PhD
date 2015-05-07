package heuristique_variable;

import java.util.ArrayList;

import br4cp.Ordonnancement;
import br4cp.Var;

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

public class HeuristiqueVariableForce implements HeuristiqueVariable {

	@Override
	public void reordoner(int[][] contraintes, Ordonnancement ord) {
		ArrayList<Var> listeTemp=new ArrayList<Var>();
	boolean changement=true;
	float COG[]= new float[contraintes.length];		//centre gravite contrainte
	float COGvar[][]=new float[2][ord.size];		//centre gravite variable
	float nbConvar[]=new float[ord.size];		//par combien faut diviser le COG variable
	int cpt=0;									//nb iterations
	for(int i=0; i<ord.size; i++){
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
		for (int i=0; i<ord.size; i++){
			COGvar[0][i]=0;
			nbConvar[i]=0;
		}
		for (int i=0; i<COG.length; i++){
			for (int j=0; j<contraintes[i].length; j++){
				COGvar[0][contraintes[i][j]]+=COG[i];
				nbConvar[contraintes[i][j]]++;
			}
		}
		for (int i=0; i<ord.size; i++){
			if(nbConvar[i]!=0)
				COGvar[0][i]=COGvar[0][i]/nbConvar[i];
			else
				COGvar[0][i]=ord.size;			//si pas dans les contraintes, on les met a la fin
		}
		
		//reordering
		
		changement=false;
		float min=99999;		//on cherche le min, mais supperieur a la borne inf
		float borninf = -1;
		float indice = 0;

		while(indice<ord.size){
			for(int i=0; i<ord.size; i++){
				if(COGvar[0][i]<min && COGvar[0][i]>borninf)
					min=COGvar[0][i];	
			}
			for(int i=0; i<ord.size; i++){
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
	for(int i=0; i<ord.size; i++){
		listeTemp.add(null);
	}
	for(int i=0; i<ord.size; i++){
		listeTemp.set((int)COGvar[1][i], ord.variables.get(i));
	}
	
	for(int i=0; i<listeTemp.size(); i++)
		ord.variables.set(i, listeTemp.get(i));
	}
	
}
