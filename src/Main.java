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
import java.io.File;

import br4cp.*;

public class Main {

	
/*	public static void main(String[] args) {
		
		SALADD cs;
		cs=new SALADD(Protocol.BT);
		long sum=0, temps, start, start2, start3, temps2, sum2=0;
		int nbactions=0;
		LecteurCdXml lecteur=new LecteurCdXml();

		//cs.readProblem("hbigPrices");
		//cs.initialize();
		//for(int i=0; i<1000; i++){
		int i=0;
			lecteur.lectureXml("scenarios-big-minimal.xml", i);
			start2=System.currentTimeMillis();
			cs.readProblem("souffleuse");
			cs.initialize();
			System.out.println(System.currentTimeMillis()-start2+"ms init");
			System.out.print(cs.getFreeVariables().size()+" ");
			System.out.println(cs.getFreeVariables());
			System.out.println();
			start=System.currentTimeMillis();
			for(int j=0; j<lecteur.dom.length; j++){
				start3=System.currentTimeMillis();
				System.out.println(lecteur.dom[j]+" -> "+cs.getCurrentDomainOf(lecteur.var[j]));
				cs.assignAndPropagate(lecteur.var[j], lecteur.dom[j]);
				System.out.println(cs.maxCost() +" " + cs.minCost());
				temps=System.currentTimeMillis()-start3;
				System.out.println(temps+"ms");
				cs.getFreeVariables();
				System.out.print(cs.getFreeVariables().size()+" ");
				System.out.println(cs.getFreeVariables());
			}
			temps=System.currentTimeMillis()-start;
			temps2=System.currentTimeMillis()-start2;
			System.out.println(i+"("+lecteur.dom.length+") "+temps+"ms "+temps2+"ms "+cs.isConfigurationComplete()+" "+cs.maxCost()+" "+cs.minCost());
			nbactions+=lecteur.dom.length;
			sum+=temps;
			sum2+=temps2;

			
			//BT							h=3
			//0(33) 349ms 1901ms true 0 0
			//0(33) 441ms 2025ms true 0 0
			//0(33) 411ms 2001ms true 0 0
			//FCP							h=5
			//0(33) 358ms 1509ms true 8998 8998
			//0(33) 282ms 1438ms true 8998 8998
			//0(33) 304ms 1435ms true 8998 8998
//
			//BT							h=5
			//0(33) 511ms 3492ms true 0 0
			//0(33) 484ms 3474ms true 0 0
			//0(33) 499ms 3594ms true 0 0
			//FCP							h=3
			//0(33) 671ms 3015ms true 8998 8998
			//0(33) 662ms 2748ms true 8998 8998
			//0(33) 738ms 3015ms true 8998 8998

			
			
			
		}
			
/*			String var, val;
				
				var="v23";
				val="1";
				cs.assignAndPropagate(var, val);
				System.out.println(var+"="+val+" ["+cs.maxCost() +" " + cs.minCost()+"]");
				System.out.println(cs.getFreeVariables());
				System.out.println(cs.getCurrentDomainOf("v50"));
				System.out.println();
				
				var="v55";
				val="0";
				cs.assignAndPropagate(var, val);
				System.out.println(var+"="+val+" ["+cs.maxCost() +" " + cs.minCost()+"]");
				System.out.println(cs.getFreeVariables());
				System.out.println(cs.getCurrentDomainOf("v50"));
				System.out.println();
				
				var="v29_0_Serie";
				val="1";
				cs.assignAndPropagate(var, val);
				System.out.println(var+"="+val+" ["+cs.maxCost() +" " + cs.minCost()+"]");
				System.out.println(cs.getFreeVariables());
				System.out.println(cs.getCurrentDomainOf("v50"));
				System.out.println();
				
				var="v25";
				val="1";
				cs.assignAndPropagate(var, val);
				System.out.println(var+"="+val+" ["+cs.maxCost() +" " + cs.minCost()+"]");
				System.out.println(cs.getFreeVariables());
				System.out.println(cs.getCurrentDomainOf("v50"));
				System.out.println();
				
				var="v12";
				val="1";
				cs.assignAndPropagate(var, val);
				System.out.println(var+"="+val+" ["+cs.maxCost() +" " + cs.minCost()+"]");
				System.out.println(cs.getFreeVariables());
				System.out.println(cs.getCurrentDomainOf("v50"));
				System.out.println();
				
				var="v13";
				val="1";
				cs.assignAndPropagate(var, val);
				System.out.println(var+"="+val+" ["+cs.maxCost() +" " + cs.minCost()+"]");
				System.out.println(cs.getFreeVariables());
				System.out.println(cs.getCurrentDomainOf("v50"));
				System.out.println();
		

				var="v29_0_Serie";
				val="0";
				cs.unassignAndRestore(var);
				//cs.assignAndPropagate(var, val);
				System.out.println(var+"="+val+" ["+cs.maxCost() +" " + cs.minCost()+"]");
				System.out.println(cs.getFreeVariables());
				System.out.println(cs.getCurrentDomainOf("v50"));
				System.out.println();
				
				
				
				/*v23=1
				v55=0
				v29_0_Serie=1
				v25=1
				v12=1
				v13=0

				2) It's conflict : v50=0

				3) Restore Value step :
				v29_0_Serie=1*/
		

		//System.out.println(cs.maxCost());
		//System.out.println(cs.minCost());

//System.out.println("---");
//System.out.println(sum+"ms");
//System.out.println(sum2+"ms");
//System.out.println(nbactions+" actions");

		
	/*}*/

public static void main(String[] args) {
		
		ArrayList<String> fichiersACompiler;
		HeuristiqueVariable[] heuristiquesVariables = {new HeuristiqueVariableOrdreRandom(), new HeuristiqueVariableOrdreChoisi(), new HeuristiqueVariableMCF(), new HeuristiqueVariableBW(), new HeuristiqueVariableMCSinv(), new HeuristiqueVariableForce(), new HeuristiqueVariableMCSinvPlusUn(), new HeuristiqueVariableOrdreAscendance(), new HeuristiqueVariable7(), new HeuristiqueVariable8(), new HeuristiqueVariable9(), new HeuristiqueVariableMCSinvPlusUnAutreVersion()};
		HeuristiqueContraintes[] heuristiquesContraintes = {new HeuristiqueContraintesInversion(), new HeuristiqueContraintesRandom(), new HeuristiqueContraintesRien(), new HeuristiqueContraintesTaille(), new HeuristiqueContraintesAmilastre(), new HeuristiqueContraintesEcartMaxMaxScore(), new HeuristiqueContraintesEcartMaxMinScore(), new HeuristiqueContraintesDomaineMaxDomaineMaxEcartMax(), new HeuristiqueContraintesDomaineMaxEcartMaxMin(), new HeuristiqueContraintesDomaineMaxDomaineMaxEcartMaxHardFirst(), new HeuristiqueContraintesProdDomainesEcartMaxHardFirst(), new HeuristiqueContraintesDomEcartPlusDomDomEcartPlusHardFirst()};
		boolean arg_err=false;
		boolean arg_plus;
		int arg_heuristique=0;
		int arg_heuristique_cons=0;
		String arg_formefinale="";
		String arg_FichierSortie="";
		boolean flag_fichierSortie;
		boolean flag_beg;
		String warning="";
		boolean warn=true;
		String commande="";
		int arg_affich_text;
		boolean flag_read;
		String arg_read;
		
		args=new String[6];
		args[0]="big";
		args[1]="-t=+";
		args[2]="bigPrices";
		args[3]="-h=5";
		args[4]="-hcon=0";
		args[5]="-text=0";
		

	
	
		if(args.length<1){
			System.out.println("pas assez d'arguments");
			System.exit(0);
		}
		
		
		flag_read=false;
		for(int i=0; i<args.length; i++){
			if(args[i].startsWith("-read=")){	
				flag_read=true;
			}
		}
		
		//read
		flag_read=false;
		arg_read="";
		for(int i=0; i<args.length; i++){
			if(args[i].startsWith("-read=")){	
				flag_read=true;
				commande= args[i].substring(6);
				if(commande.length()>0){				//nom de fichier
					File f = new File(commande);					//extention deja renseignee
					if(f.exists() && !f.isDirectory()) {
						arg_read=commande;
					}else{							
						f = new File(commande+".dot");					//extention deja renseignee
						if(f.exists() && !f.isDirectory()) {
							arg_read=commande+".dot";
						}else{
							f = new File(args[i]+".DOT");					//extention deja renseignee
							if(f.exists() && !f.isDirectory()) {
								arg_read=commande+".DOT";
							}else{												//rien
								System.out.println("fichier en lecture "+args[i]+" inconnu");
								System.out.println("programme interompu");
								System.exit(0);
							}
						}
					}
					System.out.println("entrez un nom de fichier a lire : \"-read=nom_fichier.dot\"");
					System.out.println("programme interompu");
					System.exit(0);
				}
			}
		}

		if(!flag_read){
			//names
			fichiersACompiler=new ArrayList<String>();
			for(int i=0; i<args.length; i++){
				if(!args[i].startsWith("-") && args[i].length()>0){				//nom de fichier
					File f = new File(args[i]);					//extention deja renseignee
					if(f.exists() && !f.isDirectory()) {
						fichiersACompiler.add(args[i]);
					}else{							
						f = new File(args[i]+".xml");					//extention deja renseignee
						if(f.exists() && !f.isDirectory()) {
							fichiersACompiler.add(args[i]+".xml");
						}else{
							f = new File(args[i]+".XML");					//extention deja renseignee
							if(f.exists() && !f.isDirectory()) {
								fichiersACompiler.add(args[i]+".XML");
							}else{												//rien
								System.out.println("fichier "+args[i]+" inconnu");
							}
						}
					}
				}
			}
			
			if(fichiersACompiler.size()==0){
				System.out.println("aucun fichier a compiler ou a lire");
				System.out.println("programme interompu");
				System.exit(0);
			}
		
		
			//type
			arg_plus=true;
			for(int i=0; i<args.length; i++){
				if(args[i].startsWith("-t=")){
					warn=false;
					//type
					commande= args[i].substring(3);
					if(commande.compareTo("plus")==0 || commande.compareTo("time")==0 || commande.compareTo("p")==0 || commande.compareTo("t")==0 || commande.compareTo("+")==0 || commande.compareTo("*")==0){
						if (commande.compareTo("time")==0 || commande.compareTo("t")==0 || commande.compareTo("*")==0 )
							arg_plus=false;
						else
							arg_plus=true;
					}else{
						arg_err=true;
						System.out.println("erreur :  option \"-t\" doit etre \"plus\" ou \"time\"");
					}
				}
			}
			if(warn){
				warning+="warrning :  type non renseigné \n";
			}

		
			//h
			arg_heuristique=3;
			for(int i=0; i<args.length; i++){
				if(args[i].startsWith("-h=")){
					commande= args[i].substring(3);
	
					try {
						arg_heuristique=Integer.parseInt(commande);
						if (arg_heuristique>10 || arg_heuristique<-1){
							arg_err=true;
							System.out.println("erreur :  l'heuristique \"-h\" doit etre un nombre compris entre 0 et 10");
						}
					} catch (NumberFormatException e) {
						arg_err=true;
						System.out.println("erreur :  l'heuristique \"-h\" doit etre un nombre");
					}
				}	
			}
			
			//hcontraintes
			
			arg_heuristique_cons=0;
			for(int i=0; i<args.length; i++){
				if(args[i].startsWith("-hcon=")){
					commande= args[i].substring(6);
					try {
						arg_heuristique_cons=Integer.parseInt(commande);
						if (arg_heuristique_cons>10 || arg_heuristique_cons<-1){
							arg_err=true;
							System.out.println("erreur :  l'heuristique \"-hcon\" doit etre un nombre compris entre 0 et 10");
						}
					} catch (NumberFormatException e) {
						arg_err=true;
						System.out.println("erreur :  l'heuristique \"-hcon\" doit etre un nombre");
					}
				}	
			}
		}else{	//on est dans le read
			fichiersACompiler=null;
			arg_plus=true;
		}
		
		//finalform
		arg_formefinale="prout";
		for(int i=0; i<args.length; i++){
			if(args[i].startsWith("-ff=")){
				arg_formefinale=args[i].substring(4);
				if(!(arg_formefinale.contains("AADD") || arg_formefinale.contains("SLDDp") || arg_formefinale.contains("SLDDt") || arg_formefinale.contains("ADD") ) ){
					arg_err=true;
					System.out.println("erreur : forme final (" + args[4] + ") non accepté. possibilités : AADD, SLDDp, SLDDt, ADD");
				}
			}
		}
		
		//savename
		flag_fichierSortie=false;
		for(int i=0; i<args.length; i++){
			if(args[i].startsWith("-save=")){
				arg_FichierSortie=args[i].substring(6);
				flag_fichierSortie=true;
			}
		}
		
		//noskip
		
		flag_beg=false;
		for(int i=0; i<args.length; i++){
			if(args[i].compareToIgnoreCase("-noskip")==0){
				flag_beg=true;
			}
		}
		
		//affichetext
		arg_affich_text=1;
		for(int i=0; i<args.length; i++){
			if(args[i].startsWith("-text=")){
				commande= args[i].substring(6);
				try {
					arg_affich_text=Integer.parseInt(commande);
					if (arg_affich_text>3 || arg_affich_text<0){
						arg_err=true;
						System.out.println("erreur :  affichage de texte \"-text\" doit etre un nombre compris entre 0 et 3");
					}
				} catch (NumberFormatException e) {
					arg_err=true;
					System.out.println("erreur :  affichage de texte \"-text\" doit etre un nombre");
				}
			}	
		}
		
		
		if(arg_err){
			System.out.println("programme interompu");
			System.exit(0);
		}
		
		if(warn && arg_affich_text>0){
			System.out.println(warning);
		}
		
		SALADD cs;
		cs=new SALADD(Protocol.BT);
		
		// Le "+1" de heuristiquesVariables[arg_heuristique+1] vient du fait que les numéros d'heuristique commencent à -1 et le tableau commence à 0.
		// Idem pour heuristiquesContraintes avec +2
		
		if(!flag_read)
			cs.procedureCompilation(fichiersACompiler, arg_plus, heuristiquesVariables[arg_heuristique+1], heuristiquesContraintes[arg_heuristique_cons+2], arg_formefinale, arg_FichierSortie, flag_fichierSortie, flag_beg, arg_affich_text);
		else
			cs.procedureChargement(arg_read, arg_formefinale, arg_FichierSortie, flag_fichierSortie, flag_beg, arg_affich_text);

	}



//reco

/*	public static void main(String[] args) {

// 		ReecritureFichier r;
//		r=new ReecritureFichier();
//		r.test_training();

		
		
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
	saladd.calculerVarianceHistorique(4, "smallhist/smallvariance");
	
	
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
		int success=0, echec=0, error=0;
		int success10=0, echec10=0;
		int success20=0, echec20=0;
		
		Map<String, Double> recomandations;
		//Set<String> possibles;
		String best;
		double bestproba;

		for(int occu=0; occu<choix3.size(); occu++){
			i=choix1.indexOf(choix3.get(occu));
			
			//possibles=saladdCompil.getCurrentDomainOf(choix1.get(i));
			recomandations=saladd.reco(choix1.get(i));
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

	}*/
	
}

/*
 * 		ArrayList<String> plop=new ArrayList<String>();
		plop.add("v1");
		plop.add("v2");
		plop.add("v3");
		plop.add("v4");
		plop.add("v5");
		plop.add("v6");
		plop.add("v7");
		plop.add("v8");
		plop.add("v9");
		plop.add("v10");
		plop.add("v11");
		plop.add("v12");
		plop.add("v13");
		plop.add("v14");
		plop.add("v15");
		plop.add("v16");
		plop.add("v17");
		plop.add("v18");
		plop.add("v19");
		plop.add("v21");
		plop.add("v23");
		plop.add("v24");
		plop.add("v25");
		plop.add("v26");
		plop.add("v27");
		plop.add("v28");
		plop.add("v29");
		plop.add("v30");
		plop.add("v31");
		plop.add("v32");
		plop.add("v33");
		plop.add("v34");
		plop.add("v35");
		plop.add("v36");
		plop.add("v37");
		plop.add("v38");
		plop.add("v39");
		plop.add("v40");
		plop.add("v41");
		plop.add("v42");
		plop.add("v43");
		plop.add("v44");
		plop.add("v45");
		plop.add("v46");
		plop.add("v93");
		plop.add("v51");
		plop.add("v52");
		plop.add("v53");
		*/
