import heuristique_contraintes.HeuristiqueContraintesRien;
import heuristique_variable.HeuristiqueVariableMCSinv;
import heuristique_variable.HeuristiqueVariableMCSinvPlusUn;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import br4cp.LecteurCdXml;
import br4cp.SALADD;

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
 
/**
 * Recommandation dans le cas du réseau bayésien
 * @author pgimenez
 *
 */

public class RecoRB {

	public static void main(String[] args)
	{
		boolean countWhenOneSolution = false;
		
		SALADD x = new SALADD();
		SALADD contraintes = new SALADD();
		
		HashMap<String,Integer[][]> matricesConfusion = new HashMap<String,Integer[][]>();
		
		String file_names = "bn0.xml";
//		file_names = "data/test.xml";
		int verbose = 3;
		contraintes.compilation("small.xml", false, true, new HeuristiqueVariableMCSinvPlusUn(), new HeuristiqueContraintesRien(), verbose);
		contraintes.initialize();

		x.compilation(file_names, true, false, new HeuristiqueVariableMCSinv(), new HeuristiqueContraintesRien(), verbose);
		x.initialize();
		
		ArrayList<String> memory=new ArrayList<String>();
	
		ArrayList<String> choix1=new ArrayList<String>();
		ArrayList<String> choix2=new ArrayList<String>();
		ArrayList<String> choix3=new ArrayList<String>();
	
		LecteurCdXml lect=new LecteurCdXml();
		lect.lectureCSV("datasets/set0");
		lect.lectureCSVordre("datasets/scenario0");
		
		int[] parpos=new int[lect.nbvar];
		int[] parposnb=new int[lect.nbvar];
		for(int i=0; i<parpos.length; i++){
			parpos[i]=0;
			parposnb[i]=0;
		}
		
		long debut = System.currentTimeMillis();
		
		int success=0, echec=0;

		for(String v: x.getFreeVariables())
		{
			int domain = contraintes.getVar(v).domain;
			Integer[][] mat = new Integer[domain][domain];
			for(int i = 0; i < domain; i++)
				for(int j = 0; j < domain; j++)
					mat[i][j] = 0;
			matricesConfusion.put(v, mat);
		}
		
		for(int test=0; test<lect.nbligne; test++){

			memory.clear();
			choix1.clear();
			choix2.clear();
			choix3.clear();
	
			for(int i=0; i<lect.nbvar; i++){
				choix1.add(lect.var[i].trim());
				choix2.add(lect.domall[test][i].trim());
			}
			
			for(int i=0; i<lect.nbvar; i++){
				choix3.add(lect.ordre[test][i].trim());
			}

			
			//double nb;
			int i;
			
			Map<String, Double> recomandations;
			String best;
			double bestproba;
	
			for(int occu=0; occu<choix3.size(); occu++){
				i=choix1.indexOf(choix3.get(occu));
				
				//possibles=saladdCompil.getCurrentDomainOf(choix1.get(i));
				recomandations=x.calculeDistributionAPosteriori(choix1.get(i));
				best="";
				bestproba=-1;
				
				ArrayList<String> l=new ArrayList<String>();
				l.addAll(x.getDomainOf(choix1.get(i)));
	
				Set<String> values = contraintes.getCurrentDomainOf(choix1.get(i));
				
				
				for(String value: values){
					String d = value;
					if(recomandations.get(d) == null)
						continue;
					if(recomandations.get(d)>bestproba){
						bestproba=recomandations.get(d);
						best=d;
					}

				}
//				System.out.println("bestReco:"+best+" vraiChoix:"+choix2.get(i));
				
				// On ne met pas à jour la matrice quand il n'y avait qu'une seule valeur possible
				if(countWhenOneSolution || values.size() > 1)
				{
					matricesConfusion.get(choix1.get(i))
						[x.getVar(choix1.get(i)).conv(choix2.get(i))]
						[x.getVar(choix1.get(i)).conv(best)]++;
				}
				
				if(choix2.get(i).compareTo(best)==0){
					if(countWhenOneSolution || values.size() > 1)
					{
						success++;
						parpos[occu]++;
						parposnb[occu]++;
					}
				}else{
					if(contraintes.getCurrentDomainOf(choix1.get(i)).contains(choix2.get(i)) && values.size() > 1){
						echec++;
						best=choix2.get(i);
						parposnb[occu]++;
					}else{
						System.out.println("error");
					}
				}
				memory.add(choix1.get(i));
				memory.add(best);
				
//				System.out.println("affectation : "+choix1.get(i)+" <= "+best);
				contraintes.assignAndPropagate(choix1.get(i), best);
				x.assignAndPropagate(choix1.get(i), best);
	//			saladdCompil.assignAndPropagate(choix1.get(i), best);
	//			System.out.println("apres choix "+choix1.get(i)+"="+best+" ; reste "+saladdHisto.getVDD().countingpondere());
				choix1.remove(i);
				choix2.remove(i);
//				System.out.println("------");
			}
			contraintes.reinitialisation();
			x.reinitialisation();
		//	System.out.println(success+" "+success10+" "+success20 + " success; "+ echec+ " "+echec10+" "+echec20+" echecs; "+ error+" errors"+ " reste:"+saladd.nb_echantillonsHistorique());
			double pourcent;
			for(i=0; i<parpos.length; i++){
				if(parposnb[i] > 0)
				{
					pourcent=100.*parpos[i]/parposnb[i];
					System.out.print(pourcent+", ");
				}
				else
				{
					System.out.print("x, ");
				}
			}
			System.out.println();
			pourcent=100.*success/(success+echec);
		
			System.out.println((test+1)+"/"+lect.nbligne+": "+pourcent+"%");
//			System.out.println(error+" erreurs");

			
		}

			System.out.println("Durée: "+(System.currentTimeMillis()-debut));
	}

}