import heuristique_contraintes.HeuristiqueContraintesRien;
import heuristique_variable.HeuristiqueVariableMCSinvPlusUn;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import algoreco.AlgoRandom;
import algoreco.AlgoReco;
import br4cp.LecteurCdXml;
import br4cp.SALADD;

/*   (C) Copyright 2015, Gimenez Pierre-François
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

/**
 * Protocole d'évaluation
 * @author pgimenez
 *
 */

public class Recommandation {

	public static void main(String[] args)
	{
		boolean countWhenOneSolution = false;
		
		AlgoReco recommandeur = new AlgoRandom();
		
		long debut = System.currentTimeMillis();

		SALADD contraintes = new SALADD();
		contraintes.compilation("small.xml", false, true, new HeuristiqueVariableMCSinvPlusUn(), new HeuristiqueContraintesRien(), 0);
		contraintes.initialize();

		ArrayList<String> memory=new ArrayList<String>();
		
		ArrayList<String> choix1=new ArrayList<String>();
		ArrayList<String> choix2=new ArrayList<String>();
		ArrayList<String> choix3=new ArrayList<String>();

		HashMap<String,Integer[][]> matricesConfusion = new HashMap<String,Integer[][]>();
		
		for(String v: contraintes.getFreeVariables())
		{
			int domain = contraintes.getVar(v).domain;
			Integer[][] mat = new Integer[domain][domain];
			for(int i = 0; i < domain; i++)
				for(int j = 0; j < domain; j++)
					mat[i][j] = 0;
			matricesConfusion.put(v, mat);
		}
		
		LecteurCdXml lect=new LecteurCdXml();
		lect.lectureCSV("datasets/set0");
		lect.lectureCSVordre("datasets/scenario0");
		
		int[] parpos=new int[lect.nbvar];
		int[] parposnb=new int[lect.nbvar];
		for(int i=0; i<parpos.length; i++){
			parpos[i]=0;
			parposnb[i]=0;
		}
		
//		recommandeur.apprendContraintes("small.xml");

		for(int i = 0; i < 10; i++)
		{
			ArrayList<String> learning_set = new ArrayList<String>();
			for(int j = 0; j < 10; j++)
			{
				if(j != i)
					learning_set.add("datasets/set"+j);
			}
			lect.lectureCSV("datasets/set"+i);
			lect.lectureCSVordre("datasets/scenario"+i);

			recommandeur.apprendDonnees(learning_set, i);
			
			for(int test=0; test<lect.nbligne; test++)
			{
				memory.clear();
				choix1.clear();
				choix2.clear();
				choix3.clear();
		
				for(int k=0; k<lect.nbvar; k++){
					choix1.add(lect.var[k].trim());
					choix2.add(lect.domall[test][k].trim());
				}
				
				for(int k=0; k<lect.nbvar; k++){
					choix3.add(lect.ordre[test][k].trim());
				}
				
				recommandeur.oublieSession();
				
				for(int occu=0; occu<choix3.size(); occu++)
				{
					int k = choix1.indexOf(choix3.get(occu));
					String v = choix1.get(k);
					Set<String> values = contraintes.getCurrentDomainOf(choix1.get(i));
					ArrayList<String> values_array = new ArrayList<String>();
					values_array.addAll(values);
					String r = recommandeur.recommande(v, values_array);
	
					// On ne met pas à jour la matrice quand il n'y avait qu'une seule valeur possible
					if(countWhenOneSolution || values.size() > 1)
					{
						matricesConfusion.get(choix1.get(i))
							[contraintes.getVar(choix1.get(i)).conv(choix2.get(i))]
							[contraintes.getVar(choix1.get(i)).conv(r)]++;
						if(choix2.get(i).compareTo(r)==0)
							parpos[occu]++;
						parposnb[occu]++;
					}
					
					contraintes.assignAndPropagate(choix1.get(i), choix2.get(i));

				}
				
				contraintes.reinitialisation();

			}
		}
		

			System.out.println("Durée: "+(System.currentTimeMillis()-debut));
	}

}
