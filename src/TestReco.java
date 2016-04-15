import java.io.File;
import java.util.ArrayList;
import java.util.Random;
import java.util.Set;

import compilateur.LecteurCdXml;
import compilateur.SALADD;
import compilateur.Var;
import compilateur.test_independance.*;
import preferences.*;
import recommandation.*;
import recommandation.methode_oubli.*;

public class TestReco {

	/**
	 * Les fichiers .csv sont utilisés pour la gestion de la recommandation, indépendamment de l'algorithme qui recommande
	 * Les fichiers setX_exemples.csv sont aussi utilisés pour l'apprentissage bayésien avec BNlearn et la compilation d'historique avec VDD
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
		final boolean oracle = false;		
//		final boolean testRapide = false;
		final boolean sleep = false;
		final boolean entete = false;
		
		// La seule différence entre la version avec contraintes et la version sans est l'affectation (ou non) dans le SLDD des contraintes
		final boolean contraintesPresentes = false;
		int echec = 0, succes = 0, trivial = 0;

		String dataset = "petitChampi";
		String prefixData = "datasets/"+dataset+"/";
		
		Random randomgenerator = new Random(0);
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
		recommandeur = new AlgoARC(50, 1.);
		

	
		LecteurCdXml lect=new LecteurCdXml();
		// On lit le premier fichier afin de récupére le nombre de variables
		lect.lectureCSV(prefixData+"set0_exemples", entete);
		
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
		
		long duree = 0;
		long avant;
		
		for(int i = 0; i < 10; i++)
//		for(int i = 9; i < 10; i++)
		{
			
			// Si le fichier de test n'existe pas, on passe au suivant
			if(!new File(prefixData+"set"+i+"_exemples.csv").exists())
				continue;
			
			// Si le fichier d'ordre des variables n'existe pas, l'ordre sera improvisé
			boolean randomOrder = !new File(prefixData+"set"+i+"_scenario.csv").exists();
				
				
//			avant = System.currentTimeMillis();
			ArrayList<String> learning_set = new ArrayList<String>();
			if(oracle)
			{
				learning_set.add(prefixData+"set"+i+"_exemples");
			}
/*			else if(testRapide) // on apprend un seul jeu d'exemple, mais pas celui sur lequel on sera évalué
			{
				learning_set.add(prefixData+"set"+((i+1)%10)+"_exemples");
			}*/
			else
			{				
	//			int i = 0;
	//			learning_set.add("datasets/set1");
				for(int j = 0; j < 10; j++)
				{
					if(j != i)
					{
						String fichier = prefixData+"set"+j+"_exemples";
						if(new File(fichier+".csv").exists())
							learning_set.add(fichier);
					}
				}
			}
//			learning_set.add("datasets/set"+i);
			lect.lectureCSV(prefixData+"set"+i+"_exemples", entete);

			if(!randomOrder)
				lect.lectureCSVordre(prefixData+"set"+i+"_scenario");
			

			recommandeur.apprendDonnees(learning_set, i, entete);

//			System.out.println("Apprentissage : "+(System.currentTimeMillis() - avant));
			
//			for(int test=0; test<1; test++)
			for(int test=0; test<lect.nbligne; test++)
			{
//				avant = System.currentTimeMillis();
				variables.clear();
				solutions.clear();
		
				for(int k=0; k<lect.nbvar; k++){
//					System.out.println("CSV : "+lect.var[k].trim()+" "+lect.domall[test][k].trim());
//					System.out.println(k+" "+lect.var[k]);
					variables.add(lect.var[k]);
					solutions.add(lect.domall[test][k]);
				}
				
				
				recommandeur.oublieSession();
				//System.out.println("intro : "+(System.currentTimeMillis() - avant));
				for(int occu=0; occu<1; occu++)
				{

					int k = 0;
					recommandeur.setSolution(variables.get(6), solutions.get(6));

//					System.out.println("Recherche de "+ordre.get(occu)+" : "+k);
					String v = variables.get(k);
					String solution = solutions.get(k);
					Set<String> values = null;
					int nbModalites = 0;
					

					
					if(recommandeur instanceof AlgoSaladdOubli)
						instancesRestantes[i] += ((AlgoSaladdOubli) recommandeur).count();
					
					
					parModaliteNb[nbModalites]++;
					ArrayList<String> values_array = null;
					

					//System.out.println("début : "+(System.currentTimeMillis() - avant));
					avant = System.nanoTime();

					String r = recommandeur.recommande(v, values_array);
					
					duree += (System.nanoTime() - avant);
					
					//System.out.println("reco : "+(System.currentTimeMillis() - avant));
					if(verbose)
						System.out.print("Recommandation pour "+v+": "+r);
//					avant = System.currentTimeMillis();
					recommandeur.setSolution(v, solution);
					
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
					if((echec+succes) % 50000 == 0)
					{
						System.out.println("Pli "+i+" à "+test*100./lect.nbligne+"%");
						System.out.println("Taux succès: "+100.*succes/(echec+succes));
						if(contraintesPresentes)
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

/*		System.out.println("Exemples par position: ");
		for(int l=0; l<ordre.size(); l++)
			System.out.print(((double)instancesRestantes[l])/parposnb[0]+", ");
		System.out.println();*/
		
		if(contraintesPresentes)
		{
			System.out.println("Succès par position avec trivial: ");
			for(int l=0; l<ordre.size(); l++)
				System.out.print(((double)parpos[l] + parposTrivial[l])/parposnb[0]+", ");
			System.out.println();
		}
		
		System.out.println("Succès par position: ");
		for(int l=0; l<ordre.size(); l++)
		{
			System.out.print(((double)parpos[l])/parposnb[l]);
			if(l < ordre.size()-1)
			System.out.print(", ");
		}
		System.out.println();
		
/*		System.out.println("Succès par position: ");
		for(int occu=0; occu<ordre.size(); occu++)
			System.out.print(((double)parpos[occu])/parposnb[occu]+" ("+occu+", "+parposnb[occu]+"), ");
		System.out.println();*/

/*		for(int occu=0; occu<ordre.size(); occu++)
			System.out.print(" & "+(10000*parpos[occu]/parposnb[occu]/100.));
		System.out.println();*/

		/*
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

		*/
		System.out.println("Taux succès: "+100.*succes/(echec+succes));
		if(contraintesPresentes)
			System.out.println("Taux trivial: "+100.*trivial/(echec+succes+trivial));

		System.out.println("Durée de la recommandation: "+duree);
		System.out.println("Nombre de recommandations: "+(echec+succes));
		System.out.println("Durée moyenne d'une recommandation: "+((double)duree)/(echec+succes));
	}

}
