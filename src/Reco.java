import java.util.ArrayList;
import java.util.Map;

import br4cp.*;


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

public class Reco {



	public static void main(String[] args) {

// 		ReecritureFichier r;
//		r=new ReecritureFichier();
//		r.test_training();

		TestIndependance testInd = new TestKhi2Max();
			
		SALADD saladd;
		//SALADD saladdCompil;
	
		saladd=new SALADD(null);
		//saladdCompil=new SALADD(null);
	
		saladd.compilationDHistorique("smallhist/smallHistory.xml", 2);
		
	//	ArrayList<String> s=new ArrayList<String>();
	//	s.add("small.xml");
	//	s.add("smallPrices.xml");
	//	saladd.compilation(s, true, 5, 7, 0);
		
		saladd.initialize();
		//saladdCompil.initialize();
		saladd.calculerVarianceHistorique(testInd, "smallhist/smallvariance");
		
		
		ArrayList<String> memory=new ArrayList<String>();
		//for(int i=0; i<x.variables.size(); i++){
	//	System.out.println("avant choix : "+saladdHisto.getVDD().countingpondere());
	
		ArrayList<String> choix1=new ArrayList<String>();
		ArrayList<String> choix2=new ArrayList<String>();
		ArrayList<String> choix3=new ArrayList<String>();
	
		
		LecteurCdXml lect=new LecteurCdXml();
		//lect.lectureTxt("test", 0, 18);
		lect.lectureCSV("smallhist/smallHistory_test");
		lect.lectureCSVordre("smallhist/smallHistory_testOrdre");
		
		
		int succtot=0;
		int echectot=0;
		int succtot10=0;
		int echectot10=0;
		int succtot20=0;
		int echectot20=0;
		
		int[] parpos=new int[lect.nbvar];
		for(int i=0; i<parpos.length; i++){
			parpos[i]=0;
		}
		
		
		for(int test=0; test<lect.nbligne; test++){
			memory.clear();
			choix1.clear();
			choix2.clear();
			choix3.clear();
	
			for(int i=0; i<lect.nbvar; i++){
				choix1.add(lect.var[i]);
				choix2.add(lect.domall[test][i]);
			}
			//for(int i=0; i<lect.nbvar; i++){
			for(int i=0; i<lect.nbvar; i++){
				choix3.add(lect.ordre[test][i]);
			}
			
			//double nb;
			int i;
			int success=0, echec=0;//, error=0;
			int success10=0, echec10=0;
			int success20=0, echec20=0;
			
			Map<String, Double> recomandations;
			//Set<String> possibles;
			String best;
			double bestproba;
	
			for(int occu=0; occu<choix3.size(); occu++){
				i=choix1.indexOf(choix3.get(occu));
				
				//possibles=saladdCompil.getCurrentDomainOf(choix1.get(i));
				recomandations=saladd.reco(choix1.get(i), testInd);
				best="";
				bestproba=-1;
				
				ArrayList<String> l=new ArrayList<String>();
				l.addAll(saladd.getDomainOf(choix1.get(i)));
	
				for(int j=0; j<saladd.getSizeOfDomainOf(choix1.get(i)); j++){
					String d=l.get(j);
					//if(possibles.contains(d)){
					if(recomandations.get(d)>bestproba){
						bestproba=recomandations.get(d);
						best=d;
					}
	//					System.out.println(choix1.get(i)+"="+d +" : "+recomandations.get(d)*100+"%" );
					//}else{
					//	System.out.println(choix1.get(i)+"="+d +" : "+recomandations.get(d)*100+"%  -- interdit --");
					//}
				}
	//			System.out.println("best:"+best+" vrai:"+choix2.get(i));
				
				if(choix2.get(i).compareTo(best)==0){
	//				System.out.println("success");
					success++;
					if(occu<10)
						success10++;
					if(occu<20)
						success20++;
					parpos[occu]++;
				}else{
					//if(possibles.contains(choix2.get(i))){
	//					System.out.println("echec");
						echec++;
						best=choix2.get(i);
						if(occu<10)
							echec10++;
						if(occu<20)
							echec20++;
					//}else{
					//	System.out.println("error");
					//	error++;
					//}
				}
				
				memory.add(choix1.get(i));
				memory.add(best);
				saladd.assignAndPropagate(choix1.get(i), best);
	//			saladdCompil.assignAndPropagate(choix1.get(i), best);
	//			System.out.println("apres choix "+choix1.get(i)+"="+best+" ; reste "+saladdHisto.getVDD().countingpondere());
				choix1.remove(i);
				choix2.remove(i);
			}
		//	System.out.println(success+" "+success10+" "+success20 + " success; "+ echec+ " "+echec10+" "+echec20+" echecs; "+ error+" errors"+ " reste:"+saladd.nb_echantillonsHistorique());
			double pourcent, pourcent10, pourcent20;
			for(i=0; i<parpos.length; i++){
				pourcent=test+1;
				pourcent=parpos[i]/pourcent;
				pourcent=pourcent*100;
				System.out.print(pourcent+" ");
			}
			System.out.println();
			
			saladd.reinitialisation();
			succtot+=success;
			echectot+=echec;
			succtot10+=success10;
			echectot10+=echec10;
			succtot20+=success20;
			echectot20+=echec20;
			pourcent=succtot+echectot;
			pourcent=succtot/pourcent;
			pourcent=pourcent*100;
			pourcent10=succtot10+echectot10;
			pourcent10=succtot10/pourcent10;
			pourcent10=pourcent10*100;
			pourcent20=succtot20+echectot20;
			pourcent20=succtot20/pourcent20;
			pourcent20=pourcent20*100;
		
			System.out.println(test+"/"+lect.nbligne+" : " + pourcent+"% - 10="+pourcent10+"% - 20="+pourcent20+"%");
		
		}
		System.out.println("final : see above");

	}

}
