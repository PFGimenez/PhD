import java.io.File;
import java.util.ArrayList;
import java.util.Set;

import compilateur.LecteurCdXml;
import compilateur.SALADD;
import compilateur.Var;
import compilateur.test_independance.*;
import compilateurHistorique.HistoComp;
import preferences.*;
import recommandation.*;
import recommandation.methode_oubli.*;


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

/**
 * TODO LIST
 * implémenter Bayes Ball pour calculer la d-séparation
 * implémenter une D-séparation déterministe
 *
 */

public class Recommandation {

	/**
	 * Les fichiers .csv sont utilisés pour la gestion de la recommandation, indépendamment de l'algorithme qui recommande
	 * Les fichiers setX_exemples.csv sont aussi utilisés pour l'apprentissage bayésien avec BNlearn
	 * Les fichiers setX_exemples_pour_compilation.xml sont utilisés par SALADD pour apprendre des historiques
	 * Les fichiers setX_exemples.xml (et setX_scenario.xml) peuvent être utilisés par les autres algorithmes
	 * 
	 * contraintes.xml sert à connaître les contraintes, qui sont compilées dans un SLDD.
	 * On en a aussi besoin pour connaître les variables, même lorsque les contraintes en elle-même ne sont pas utilisées
	 * 
	 * CSVconverter permet de générer les .csv et setX_exemples_pour_compilation.xml à partir de setX_exemples.xml
	 * Recommandation peut servir à générer setX_exemples.xml et setX_scenario.xml à partir des .csv
	 * @throws InterruptedException 
	 */
	
	public static void main(String[] args)
	{	
		// TODO : durée en fonction du nombre de variables connues ?
		final boolean verbose = true;
		final boolean oracle = true;		
		final boolean testRapide = false;
		final boolean sleep = true;
		
		// La seule différence entre la version avec contraintes et la version sans est l'affectation (ou non) dans le SLDD des contraintes
		final boolean contraintesPresentes = true;
	
		String dataset = "renault_big_court";
		String prefixData = "datasets/"+dataset+"/";
		
		AlgoReco recommandeur;
		
//		recommandeur = new AlgoRandom();				// Algorithme de choix aléatoire
//		recommandeur = new AlgoRBJayes(prefixData);		// Réseaux bayésiens
//		recommandeur = new Oracle(prefixData);					// Oracle (connaît l'ensemble de tests)
//		recommandeur = new AlgoOubliTout();
//		recommandeur = new AlgoSaladdOubli(new SansOubli(),prefixData);
//		recommandeur = new AlgoSaladdOubli(new OubliParCardinal(50, prefixData, false),prefixData);	// D-séparation + oubli par cardinal
//		recommandeur = new AlgoSaladdOubli(new OubliParIndependance(50, new TestEcartMax()), prefixData);		// Oubli par indépendance (non conditionnelle)
//		recommandeur = new AlgoSaladdOubli(new OubliParDSeparationDecomposition(50, new TestEcartMax(), prefixData),prefixData);		// D-séparation + oubli par indépendance
//		recommandeur = new AlgoSaladdOubli(new OubliParDSeparation(50, new TestEcartMax(), prefixData),prefixData);		// D-séparation + oubli par indépendance
//		recommandeur = new AlgoSaladdOubli(new OubliInverseCardinal2(50),prefixData);		// construction des variables à garder par cardinalité
//		recommandeur = new AlgoSaladdOubli(new OubliInverseIndependance(50, new TestEcartMax(), 50),prefixData);		// construction des variables à garder par indépendance
//		recommandeur = new AlgoLexTree(new ApprentissageLexOrder(), prefixData);
//		recommandeur = new AlgoLexTree(new ApprentissageLexTree(10, 200), prefixData);
		recommandeur = new AlgoOubli();
		
		// Pas des algorithmes de recommandation mais de conversion vers XML. Utilisé pour la génération de données
//		recommandeur = new XMLconverter(prefixData);
//		recommandeur = new XMLconverter2(prefixData);
		
		if(args.length == 1)
		{
			String o = "";
			if(oracle)
				o = "-O";
			String c = "";
			if(contraintesPresentes)
				c = "-C";
			System.out.println(dataset+o+c+"-"+recommandeur);
			return;
		}

		long toutDebut = System.currentTimeMillis();
		
		System.out.println("Début du test de "+recommandeur);
		System.out.println("Dataset = "+dataset);
		System.out.println("Oracle = "+oracle);
		System.out.println("Test rapide = "+testRapide);
		System.out.println("Contraintes = "+contraintesPresentes);
		

		int echec = 0, succes = 0, trivial = 0;

		String fichierContraintes = prefixData+"contraintes.xml";
		
		SALADD contraintes;
		contraintes = null;

		if(new File(fichierContraintes).exists())			
		{
			System.out.println("Compilation des contraintes");
			contraintes = new SALADD();
			contraintes.compilation(fichierContraintes, true, 4, 0, 0);
			contraintes.propagation();
		}
		else
		{
			System.out.println("Pas de fichier de contraintes!");
			if(contraintesPresentes)
			{
				System.out.println("Veuillez relancez avec \"contraintesPresentes = false\"");
				return;
			}
		}
	

		System.out.println(" finie");

		LecteurCdXml lect=new LecteurCdXml();
		lect.lectureCSV(prefixData+"set0_exemples");
		lect.lectureCSVordre(prefixData+"set0_scenario");
		
//		ArrayList<String> variables_tmp = new ArrayList<String>();
//		variables_tmp.addAll(contraintes.getFreeVariables());
//		recommandeur.initialisation(variables_tmp);

		ArrayList<String> variables=new ArrayList<String>();
		ArrayList<String> solutions=new ArrayList<String>();
		ArrayList<String> ordre=new ArrayList<String>();
		
//		HashMap<String,Integer[][]> matricesConfusion = new HashMap<String,Integer[][]>();
		
/*		for(String v: contraintes.getFreeVariables())
		{
			int domain = contraintes.getVar(v).domain;
			Integer[][] mat = new Integer[domain][domain];
			for(int i = 0; i < domain; i++)
				for(int j = 0; j < domain; j++)
					mat[i][j] = 0;
//			matricesConfusion.put(v, mat);
		}*/
		
		
		long[] instancesRestantes = new long[lect.nbvar];
		int[] oubliparpos = new int[lect.nbvar];
		int[] parpos = new int[lect.nbvar];
		int[] parposTrivial = new int[lect.nbvar];
		int[] parposnb = new int[lect.nbvar];
		int[] parModalite = new int[1000];
		int[] parModaliteNb = new int[1000];
		int[] parOubli = new int[lect.nbvar];
		int[] parOubliNb = new int[lect.nbvar];
		int[] parTauxOubli = new int[11];
		int[] parTauxOubliNb = new int[11];
		for(int i=0; i<parpos.length; i++){
			oubliparpos[i] = 0;
			parpos[i]=0;
			parposTrivial[i]=0;
			parposnb[i]=0;
			parOubli[i] = 0;
			parOubliNb[i] = 0;
			instancesRestantes[i] = 0;
		}
		for(int i = 0; i < 11; i++)
		{
			parTauxOubli[i] = 0;
			parTauxOubliNb[i] = 0;
		}
		for(int i = 0; i < 1000; i++)
		{
			parModalite[i] = 0;
			parModaliteNb[i] = 0;
		}
		
		if(contraintesPresentes)
			recommandeur.apprendContraintes(contraintes);

		long duree = 0;
		long avant;
		
		for(int i = 0; i < 10; i++)
//		for(int i = 9; i < 10; i++)
		{
//			avant = System.currentTimeMillis();
			ArrayList<String> learning_set = new ArrayList<String>();
			if(oracle)
			{
				learning_set.add(prefixData+"set"+i+"_exemples");
			}
			else if(testRapide) // on apprend un seul jeu d'exemple, mais pas celui sur lequel on sera évalué
			{
				learning_set.add(prefixData+"set"+((i+1)%10)+"_exemples");
			}
			else
			{				
	//			int i = 0;
	//			learning_set.add("datasets/set1");
				for(int j = 0; j < 10; j++)
				{
					if(j != i)
						learning_set.add(prefixData+"set"+j+"_exemples");
				}
			}
//			learning_set.add("datasets/set"+i);
			lect.lectureCSV(prefixData+"set"+i+"_exemples");
			lect.lectureCSVordre(prefixData+"set"+i+"_scenario");
			if(contraintesPresentes)
			{
				contraintes.reinitialisation();
				contraintes.propagation();
			}

			recommandeur.apprendDonnees(learning_set, i);

//			System.out.println("Apprentissage : "+(System.currentTimeMillis() - avant));
			
//			for(int test=0; test<1; test++)
			for(int test=0; test<lect.nbligne; test++)
			{
//				avant = System.currentTimeMillis();
				variables.clear();
				solutions.clear();
				ordre.clear();
		
				for(int k=0; k<lect.nbvar; k++){
//					System.out.println("CSV : "+lect.var[k].trim()+" "+lect.domall[test][k].trim());
					variables.add(lect.var[k].trim());
					solutions.add(lect.domall[test][k].trim());
				}
				
				for(int k=0; k<lect.nbvar; k++){
					ordre.add(lect.ordre[test][k].trim());
				}
				
				recommandeur.oublieSession();
				//System.out.println("intro : "+(System.currentTimeMillis() - avant));
				for(int occu=0; occu<ordre.size(); occu++)
				{
					if(sleep)
						try {
							Thread.sleep(1000);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					

//					avant = System.currentTimeMillis();
					int k = variables.indexOf(ordre.get(occu));
//					System.out.println("Recherche de "+ordre.get(occu)+" : "+k);
					String v = variables.get(k);
					String solution = solutions.get(k);
					Set<String> values = null;
					int nbModalites = 0;
					
					if(contraintes != null)
					{
						values = contraintes.getCurrentDomainOf(v);						
						nbModalites = values.size();
					}
					
					if(recommandeur instanceof AlgoSaladdOubli)
						instancesRestantes[i] += ((AlgoSaladdOubli) recommandeur).count();
					
					if(contraintes != null && nbModalites == 1)
					{
						if(verbose)
						{
							ArrayList<String> values_array = new ArrayList<String>();
							values_array.addAll(values);
							System.out.println(occu+" variables connues. Possible: "+values.iterator().next()+". Scénario pour "+v+": "+solution+" (trivial)");
						}
						parposTrivial[occu]++;
						trivial++;
						recommandeur.setSolution(v, solution);
						if(contraintesPresentes)
							contraintes.assignAndPropagate(v, solution);
						//System.out.println("début trivial : "+(System.currentTimeMillis() - avant));
						continue;
					}
					
					parModaliteNb[nbModalites]++;
					ArrayList<String> values_array = null;
					
					if(contraintes != null)
					{
						values_array = new ArrayList<String>();
						values_array.addAll(values);
					}

					//System.out.println("début : "+(System.currentTimeMillis() - avant));
					avant = System.nanoTime();

					String r = recommandeur.recommande(v, values_array);
					
					duree += (System.nanoTime() - avant);
					
					//System.out.println("reco : "+(System.currentTimeMillis() - avant));
					if(verbose && contraintes != null)
						System.out.print(occu+" variables connues. "+values_array.size()+" possibles. ");
					if(verbose)
						System.out.print("Recommandation pour "+v+": "+r);
//					avant = System.currentTimeMillis();
					recommandeur.setSolution(v, solution);
					
					if(contraintesPresentes)
						contraintes.assignAndPropagate(v, solution);
					
//					matricesConfusion.get(v)
//						[contraintes.getVar(v).conv(solution)]
//						[contraintes.getVar(v).conv(r)]++;

					if(r != null && solution.compareTo(r)==0)
					{
						if(verbose)
							System.out.println(" (succès)");
						succes++;
						if(recommandeur instanceof AlgoSaladdOubli)
						{
							int nbOubli = ((AlgoSaladdOubli)recommandeur).getNbOublis();
							oubliparpos[occu] += nbOubli;
							parOubli[nbOubli]++;
							parOubliNb[nbOubli]++;
							if(occu != 0)
							{
								int taux = (int)(10.*nbOubli/occu);
								parTauxOubli[taux]++;
								parTauxOubliNb[taux]++;
							}
						}
						parModalite[nbModalites]++;
						parpos[occu]++;
					}
					else
					{
						if(verbose)
							System.out.println(" (échec, vraie valeur: "+solution+")");
						echec++;
						if(recommandeur instanceof AlgoSaladdOubli)
						{
							int nbOubli = ((AlgoSaladdOubli)recommandeur).getNbOublis();
							oubliparpos[occu] += nbOubli;
							parOubliNb[nbOubli]++;
							if(occu != 0)
							{
								int taux = (int)(10.*nbOubli/occu);
								parTauxOubliNb[taux]++;
							}
						}
					}
					parposnb[occu]++;
					if((echec+succes) % 500 == 0)
					{
						System.out.println(10*i+test*10./lect.nbligne+"%");
						System.out.println("Taux succès: "+100.*succes/(echec+succes));
						System.out.println("Taux trivial: "+100.*trivial/(echec+succes+trivial));
						System.out.println("Durée: "+(duree));
						System.out.println("Durée moyenne d'une recommandation en ms: "+((double)duree)/(1000000.*echec+succes));
						System.out.println("Succès par position: ");
						for(int l=0; l<ordre.size(); l++)
							System.out.print(((double)parpos[l])/parposnb[l]+", ");
						System.out.println();

						if(recommandeur instanceof AlgoSaladdOubli)
						{
							System.out.println("Oublis par position: ");
							for(int l=0; l<ordre.size(); l++)
								System.out.print(((double)oubliparpos[l])/parposnb[l]+", ");
							System.out.println();
						}

					}
					//System.out.println("après : "+(System.currentTimeMillis() - avant));
				}
//				avant = System.currentTimeMillis();
				if(contraintesPresentes)
				{
					contraintes.reinitialisation();
					contraintes.propagation();
				}
				//System.out.println("prog : "+(System.currentTimeMillis() - avant));
			}
		}
		recommandeur.termine();
/*
		for(String v: contraintes.getFreeVariables())
		{
			int domain = contraintes.getVar(v).domain;
			Integer[][] mat = matricesConfusion.get(v);
			int bon = 0, total = 0;
			for(int i = 0; i < domain; i++)
				for(int j = 0; j < domain; j++)
				{
					total += mat[i][j];
					if(i == j)
						bon += mat[i][j];
					System.out.println(v+" "+i+" "+j+" "+mat[i][j]);

				}
			System.out.println(v+" "+((double)bon)/total);
		}
		*/
		System.out.println("Fin du test de "+recommandeur);

		System.out.println("Exemples par position: ");
		for(int l=0; l<ordre.size(); l++)
			System.out.print(((double)instancesRestantes[l])/parposnb[0]+", ");
		System.out.println();
		
		System.out.println("Succès par position avec trivial: ");
		for(int l=0; l<ordre.size(); l++)
			System.out.print(((double)parpos[l] + parposTrivial[l])/parposnb[0]+", ");
		System.out.println();
		
		System.out.println("Succès par position: ");
		for(int l=0; l<ordre.size(); l++)
			System.out.print(((double)parpos[l])/parposnb[l]+", ");
		System.out.println();
		
		System.out.println("Succès par position: ");
		for(int l=0; l<ordre.size(); l++)
			System.out.print(((double)parpos[l])/parposnb[l]+", ");
		System.out.println();
		
		System.out.println("Succès par position: ");
		for(int occu=0; occu<ordre.size(); occu++)
			System.out.print(((double)parpos[occu])/parposnb[occu]+" ("+occu+", "+parposnb[occu]+"), ");
		System.out.println();

		for(int occu=0; occu<ordre.size(); occu++)
			System.out.print(" & "+(10000*parpos[occu]/parposnb[occu]/100.));
		System.out.println();

		
		if(recommandeur instanceof AlgoSaladdOubli)
		{
			System.out.println("Succès par nombre d'oubli: ");
			for(int occu=0; occu<lect.nbvar; occu++)
			{
				if(parOubliNb[occu] == 0)
					System.out.print("NA, ");
				else
					System.out.print(((double)parOubli[occu])/parOubliNb[occu]+" ("+occu+", "+parOubliNb[occu]+"), ");
			}
			System.out.println();
			
			for(int occu=0; occu<lect.nbvar; occu++)
			{
				if(parOubliNb[occu] < 1000)
					System.out.print(" & $\\star$ ");
				else
					System.out.print(" & "+(10000*parOubli[occu])/parOubliNb[occu]/100.);
			}
			System.out.println();
			
			System.out.println("Succès par taux d'oubli: ");
			for(int occu=0; occu<11; occu++)
			{
				if(parTauxOubliNb[occu] == 0)
					System.out.print("NA, ");
				else
					System.out.print(((double)parTauxOubli[occu])/parTauxOubliNb[occu]+" ("+10*occu+"% à "+(10*(occu+1)-0.01)+"%, "+parTauxOubliNb[occu]+"), ");
			}
			
			for(int occu=0; occu<11; occu++)
			{
				if(parTauxOubliNb[occu] < 1000)
					System.out.print(" & $\\star$ ");
				else
					System.out.print(" & "+(10000*parTauxOubli[occu])/parTauxOubliNb[occu]/100.);
			}
			System.out.println();
			
			System.out.println();
			System.out.println("Oublis par position: ");
			for(int occu=0; occu<ordre.size(); occu++)
				System.out.print(((double)oubliparpos[occu])/parposnb[occu]+" ("+occu+", "+parposnb[occu]+"), ");
			System.out.println();
		}
		System.out.println("Taux de réussite par modalités: ");
		for(int i = 2; i < 50; i++)
		{
			if(parModaliteNb[i] != 0)
				System.out.print(((double)parModalite[i])/parModaliteNb[i]+" ("+i+", "+parModaliteNb[i]+"), ");
		}
		System.out.println();

		for(int i = 2; i < 50; i++)
		{		
			if(parModaliteNb[i] < 1000)
				System.out.print(" & $\\star$ ");
			else
				System.out.print(" & "+(10000*parModalite[i])/parModaliteNb[i]/100.);
		}
		System.out.println();

		
		System.out.println("	Taux succès: "+100.*succes/(echec+succes));
		System.out.println("	Taux trivial: "+100.*trivial/(echec+succes+trivial));

		System.out.println("Durée totale: "+(System.currentTimeMillis() - toutDebut));
		System.out.println("Durée de la recommandation: "+duree);
		System.out.println("Nombre de recommandations: "+(echec+succes));
		System.out.println("Durée moyenne d'une recommandation: "+((double)duree)/(echec+succes));
		
	}

}
