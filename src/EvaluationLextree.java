/*   (C) Copyright 2016, Gimenez Pierre-François 
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

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import compilateurHistorique.Instanciation;
import compilateurHistorique.Variable;
import preferences.*;
import preferences.GeometricDistribution;
import preferences.compare.*;
import preferences.completeTree.*;
import preferences.heuristiques.*;
import preferences.penalty.*;

/**
 * Génère un historique à partir d'un LP-tree + évaluation
 * @author Pierre-François Gimenez
 *
 */

public class EvaluationLextree
{
	private static Random rng = new Random();

	static class SplitVar
	{		
		public SplitVar(double coeffSplit, int nbVar)
		{
			this.coeffSplit = coeffSplit;
			this.nbVar = nbVar;
		}
		double coeffSplit;
		int nbVar;
	}
	
	public static class EvaluationResults
	{
		/**
		 * 1er indice : nombre de variables
		 * 2e indice : l'indice de la taille du jeu de données
		 * Valeurs de la liste : divergence KL avec le vrai lextree
		 */
		ArrayList<Double>[][] data;
	
		/**
		 * 1er indice : split*10
		 * 2e indice : l'indice de la taille du jeu de données
		 * Valeurs de la liste : divergence KL avec le vrai lextree
		 */
		ArrayList<Double>[][] dataSplit;
		
		/**
		 * 1er indice : nombre de variables
		 * 2e indice : l'indice de la taille du jeu de données
		 * Valeurs de la liste : taille de l'arbre
		 */
		ArrayList<Integer>[][] tailleArbre;
		
		/**
		 * 1er indice : nombre de variables
		 * 2e indice : l'indice de la taille du jeu de données
		 * Valeurs de la liste : temps d'apprentissage en ms
		 */
		ArrayList<Double>[][] tempsAppr;

		@SuppressWarnings("unchecked")
		public EvaluationResults(int n)
		{
			data = (ArrayList<Double>[][]) new ArrayList[100][n];
			for(int j = 0; j < n; j++)
				for(int i = 0; i < 100; i++)
					data[i][j] = new ArrayList<Double>();

			dataSplit = (ArrayList<Double>[][]) new ArrayList[100][n];
			for(int j = 0; j < n; j++)
				for(int i = 0; i <= 10; i++)
					dataSplit[i][j] = new ArrayList<Double>();
			
			tailleArbre = (ArrayList<Integer>[][]) new ArrayList[100][n];
			for(int j = 0; j < n; j++)
				for(int i = 0; i < 100; i++)
					tailleArbre[i][j] = new ArrayList<Integer>();
			
			tempsAppr = (ArrayList<Double>[][]) new ArrayList[100][n];
			for(int j = 0; j < n; j++)
				for(int i = 0; i < 100; i++)
					tempsAppr[i][j] = new ArrayList<Double>();
		}
	}
	
	public static void main(String[] args)
	{
		int[] nbVartab = {10};//, /*13, 15, 18, */20/*, 25, 30*/};
		double[] splitCoefftab = {0.2};//, 0.2};
		
		List<SplitVar> splitvar = new ArrayList<SplitVar>();
		
		for(double d : splitCoefftab)
			for(int n : nbVartab)
				splitvar.add(new SplitVar(d, n));
		
/*		SplitVar[] splitvar = {new SplitVar(0.8, 28), new SplitVar(0, 10), new SplitVar(0.1, 10), new SplitVar(0.2, 10), new SplitVar(0.3, 10), new SplitVar(0.4, 10),
				new SplitVar(0.1, 15), new SplitVar(0.12, 15), new SplitVar(0.15, 15),
				new SplitVar(0.1, 18), new SplitVar(0.12, 18), new SplitVar(0.15, 18), new SplitVar(0.17, 18), new SplitVar(0.2, 18),
				new SplitVar(0.1, 20), new SplitVar(0.12, 20), new SplitVar(0.15, 20), new SplitVar(0.17, 20), new SplitVar(0.2, 20),
				new SplitVar(0.1, 22),
				new SplitVar(0.01, 25), new SplitVar(0.05, 25), new SplitVar(0.1, 25),
				new SplitVar(0.01, 28), new SplitVar(0.05, 28), new SplitVar(0.1, 28),
				new SplitVar(0.005, 30), new SplitVar(0.01, 30),
				new SplitVar(0.005, 35), new SplitVar(0.01, 35),
				new SplitVar(0.005, 40), new SplitVar(0.01, 40)};*/
		
//		double[] coeffSplitTab = {0.05, 0.1, 0.11, 0.12, 0.13, 0.14, .15, 0.2, 0.25, 0.3, 0.35, 0.4};

		int[] nbinstancetab = {10, 20, 50, 100, 200, 500, 1000, 2000, 5000, 10000, 20000, 50000};
//		int[] nbinstancetab = {10, 100, 1000, 2000, 10000, 25000, 100000};
		
		ApprentissageGloutonLexTree algo = new ApprentissageGloutonLexTree(300, 20, new HeuristiqueDuel());
		ApprentissageGloutonLexOrder algoLinear = new ApprentissageGloutonLexOrder(new HeuristiqueDuel());
		
//		int[] nbVarTab = {10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20};
		Variable[] vars = null;

		EvaluationResults data = new EvaluationResults(nbinstancetab.length);		
		EvaluationResults dataPrune = new EvaluationResults(nbinstancetab.length);
		EvaluationResults dataOrder = new EvaluationResults(nbinstancetab.length);
		
//		Comparison[] comptab = {/*new SpearmanCorrComparison(), new InverseComparison(new SpearmanCorrComparison()),*/ new KLComparison()/*, new SpearmanMetricComparison()*/};
		Comparison comp = new KLComparison();
//		Comparison comp = new FirstDifferentNodeComparison();

		PrintWriter writer = null, writerTaille = null, writerTemps = null;
//		ProbabilityDistributionLog p = new LinearDistribution(Math.pow(2, nbVar), 0);
//		System.out.println("Distribution de probabilité : "+p.getClass().getSimpleName());
		String dataset = "datasets/lptree-relearning3";

		int nbIterMax = 500;
		for(SplitVar s : splitvar)
		{
			int nbVar = s.nbVar;
			double coeffSplit = s.coeffSplit;

			String resultatFile = dataset+"/result-"+comp.getClass().getSimpleName()+"-"+nbVar+"-"+coeffSplit+".csv";
			String resultatTailleFile = dataset+"/taille-"+comp.getClass().getSimpleName()+"-"+nbVar+"-"+coeffSplit+".csv";
			String resultatTempsFile = dataset+"/temps-"+comp.getClass().getSimpleName()+"-"+nbVar+"-"+coeffSplit+".csv";

			if(!loadResults(nbinstancetab.length, resultatFile, resultatTailleFile, resultatTempsFile, nbVar, coeffSplit, data, dataPrune, dataOrder, nbIterMax))
			{
				System.out.println("Le chargement a échoué : "+resultatFile);

				try {
					writer = new PrintWriter(new BufferedWriter(new FileWriter(resultatFile))); // on ajoute juste
				} catch (Exception e1) {
					e1.printStackTrace();
					writer.close();
					return;
				}
				
				try {
					writerTaille = new PrintWriter(new BufferedWriter(new FileWriter(resultatTailleFile))); // on ajoute juste
				} catch (Exception e1) {
					e1.printStackTrace();
					writerTaille.close();
					writer.close();
					return;
				}
	
				try {
					writerTemps = new PrintWriter(new BufferedWriter(new FileWriter(resultatTempsFile))); // on ajoute juste
				} catch (Exception e1) {
					e1.printStackTrace();
					writerTaille.close();
					writer.close();
					return;
				}
				
				for(int iter = 0; iter < nbIterMax; iter++)
				{
					// si le chargement des résultats a échoué
					
					// VARIABLES
					vars = generateVariables(nbVar);
					System.out.println("Itération : "+iter);
					System.out.println("Nb var : "+nbVar);
					System.out.println("Coeff split : "+coeffSplit);					

					// ARBRE
					DynamicallyGeneratedLexTree arbre = new DynamicallyGeneratedLexTree(vars, coeffSplit);
					
					System.out.println("Rang max = "+new BigDecimal(arbre.getRangMax()));
					
					// le rang moyen sera (taille du domaine) / 4
					BigDecimal param_p = BigDecimal.valueOf(4.).divide(new BigDecimal(arbre.getRangMax()), 250, RoundingMode.HALF_EVEN);
					BigDecimal log_p = BigDecimal.valueOf(Math.log(4.)).subtract(new BigDecimal(arbre.getRangMaxLog()));
					ProbabilityDistributionLog p = new GeometricDistribution(param_p, log_p);

					String valLextree = "", valPrune = "", valLin = "";
					String tailleLextree = "", taillePrune = "";
					String tempsLextree = "", tempsPrune = "", tempsLin = "";
					
					/*
					 * Évaluation : LP-tree, LP-tree + prune, linear LP-tree
					 */
					for(int n = 0; n < nbinstancetab.length; n++)
					{
						double val;
						int taille;
						int nbinstances = nbinstancetab[n];
						String exemplesFile = "/tmp/exemples";
						new File(exemplesFile+".csv").delete(); // le fichier d'exemples ne sert plus à rien
						generateExamples(exemplesFile+".csv", nbVar, vars, nbinstances, p, arbre);
						BigInteger[] rangs = prepareEvaluate(p, arbre);
						
						/*
						 * LP-tree
						 */
						long avant = System.nanoTime();
						LexicographicStructure arbreAppris = learn(vars, exemplesFile, algo);
						long apres = System.nanoTime();
						val = comp.compare(arbreAppris, arbre, rangs, p);
						taille = arbreAppris.getNbNoeuds();
						System.out.println(algo.getClass().getSimpleName()+" "+comp.getClass().getSimpleName()+" : "+val+" "+taille+" "+(apres-avant)/100000.);
						valLextree += val;
						tailleLextree += taille;
						tempsLextree += (double) (apres-avant)/1000000.;
						
						/*
						 * LP-tree + prune
						 */
						long avant2 = System.nanoTime();
						algo.pruneFeuille(new AIC(0.8), p);
						long apres2 = System.nanoTime();
						val = comp.compare(arbreAppris, arbre, rangs, p);
						taille = arbreAppris.getNbNoeuds();
						System.out.println(algo.getClass().getSimpleName()+" "+comp.getClass().getSimpleName()+" : "+val+" (prune) "+taille+" "+(apres-avant+apres2-avant2)/100000.);
						valPrune += val;
						taillePrune += taille;
						tempsPrune += (double) (apres-avant+apres2-avant2)/1000000.;

						/*
						 * Linear LP-tree
						 */
						long avant3 = System.nanoTime();
						arbreAppris = learn(vars, exemplesFile, algoLinear);
						long apres3 = System.nanoTime();
						val = comp.compare(arbreAppris, arbre, rangs, p);
						taille = arbreAppris.getNbNoeuds();
						System.out.println(algo.getClass().getSimpleName()+" "+comp.getClass().getSimpleName()+" : "+val+" "+taille+" "+(apres3-avant3)/100000.);
						valLin += val;
						tempsLin += (double) (apres3-avant3)/1000000.;
						
						if(n < nbinstancetab.length - 1)
						{
							valLextree += ",";
							valPrune += ",";
							valLin += ",";
							tailleLextree += ",";
							taillePrune += ",";
							tempsLextree += ",";
							tempsPrune += ",";
							tempsLin += ",";
						}
					}
					writer.println(valLextree);
					writer.println(valPrune);
					writer.println(valLin);
					writerTaille.println(tailleLextree);
					writerTaille.println(taillePrune);
					writerTemps.println(tempsLextree);
					writerTemps.println(tempsPrune);
					writerTemps.println(tempsLin);
				}
				writer.close();
				writerTaille.close();
				writerTemps.close();
			}
			
			loadResults(nbinstancetab.length, resultatFile, resultatTailleFile, resultatTempsFile, nbVar, coeffSplit, data, dataPrune, dataOrder, nbIterMax);
		}
		
		System.out.println("Génération / chargement terminé");
	
		resultatTauxBonApprentissageFonctionDeTailleJeuNbVar(nbinstancetab, dataset, splitvar, data, dataPrune);
		resultatTauxBonApprentissageFonctionDeTailleJeuSplit(nbinstancetab, dataset, splitvar, data, dataPrune);
		resultatDivergenceMoyenneFonctionDeTailleJeu(nbinstancetab, dataset, splitvar, data, dataPrune);
		resultatGainPruningEnTailleFonctionDeTailleJeu(nbinstancetab, dataset, splitvar, data, dataPrune);
		resultatTauxBonApprentissageFonctionDeTailleJeuLPTreeLPTreePruneLinearLPTree(nbinstancetab, dataset, splitvar, data, dataPrune, dataOrder);
		moyenneKLFonctionDeTailleJeuLPTreeLPTreePruneLinearLPTree(nbinstancetab, dataset, splitvar, data, dataPrune, dataOrder);
		tempsApprentissageFonctionTailleJeuLPTreeLPTreePruneLinearLPTree(nbinstancetab, dataset, splitvar, data, dataPrune, dataOrder);
		tailleModeleFonctionTailleJeuLPTreeLPTreePruneLinearLPTree(nbinstancetab, dataset, splitvar, data, dataPrune, dataOrder);
		System.out.println("Compilation des résultats terminée");
	}

	/**
	 * Génère des variables
	 * @param varsFile
	 * @param nbVar
	 * @return
	 */
	private static Variable[] generateVariables(int nbVar)
	{
		Variable[] vars = new Variable[nbVar];
		System.out.println("Création des variables");
		for(int i = 0; i < nbVar; i++)
		{
			vars[i] = new Variable();
			vars[i].domain = rng.nextInt(5) + 2;
			vars[i].name = "V"+i;
			System.out.println(vars[i].name+" : "+vars[i].domain+" modalités");
			vars[i].values = new ArrayList<String>();
			for(int j = 0; j < vars[i].domain; j++)
				vars[i].values.add(Integer.toString(j));
		}
		return vars;
	}
	
	/**
	 * Charge les résultats. Renvoie "vrai" si le chargement a réussi.
	 * @param resultatFile
	 * @param resultatTailleFile
	 * @param nbVar
	 * @param nbJeux
	 * @param dataVar
	 * @param dataVarPrune
	 * @param nbNoeuds
	 * @return
	 */
	private static boolean loadResults(int expectedValues, String resultatFile, String resultatTailleFile, String resultatTempsFile, int nbVar, double coeffSplit, EvaluationResults dataVar, EvaluationResults dataVarPrune, EvaluationResults dataVarOrder, int nbIterMax)
	{
		BufferedReader reader, readerTaille, readerTemps;
		// on a déjà les résultats de cette expérience
		if(new File(resultatFile).exists() && new File(resultatTailleFile).exists() && new File(resultatTempsFile).exists())
		{
			try {
				reader = new BufferedReader(new FileReader(new File(resultatFile)));
				readerTaille = new BufferedReader(new FileReader(new File(resultatTailleFile)));
				readerTemps = new BufferedReader(new FileReader(new File(resultatTempsFile)));
				boolean error = false;
				
				for(int k = 0; k < nbIterMax; k++)
				{
					String[][] tab = new String[3][], tabTaille = new String[2][], tabTemps = new String[3][];
					String l;
					/*
					 * Lecture des divergences KL
					 */
					for(int i = 0; i < 3; i++)
					{

						/*
						 * 1e ligne : lextree sans prune
						 * 2e ligne : lextree avec prune
						 * 3e ligne : linear lextree
						 */
						l = reader.readLine();
						if(l != null)
						{
							tab[i] = l.split(",");
							error |= tab[i].length != expectedValues;
						}
						else
							error = true;
					}
					
					/*
					 * Lecture des tailles des lextree
					 */
					for(int i = 0; i < 2; i++)
					{
						/*
						 * 1e ligne : sans prune
						 * 2e ligne : avec prune
						 */
						l = readerTaille.readLine();
						if(l != null)
						{
							tabTaille[i] = l.split(",");
							error |= tabTaille[i].length != expectedValues;
						}
						else
							error = true;
					}
		
					/*
					 * Lecture des temps
					 */
					for(int i = 0; i < 3; i++)
					{
						/*
						 * 1e ligne : lextree sans prune
						 * 2e ligne : lextree avec prune
						 * 3e ligne : linear lextree
						 */
						l = readerTemps.readLine();
						if(l != null)
						{
							tabTemps[i] = l.split(",");
							error |= tabTemps[i].length != expectedValues;
						}
						else
							error = true;
					}
					
					/*
					 * Pas de chargement partiel : s'il manque quelque chose on recommence tout
					 */
					if(error)
					{
						reader.close();
						readerTaille.close();
						readerTemps.close();
						return false;
					}
	
					/*
					 * Le chargement s'est bien passé, on peut tout sauvegarder en mémoire
					 */
					for(int i = 0; i < 3; i++)
					{
						ArrayList<Double>[][] data;
						ArrayList<Double>[][] dataSplit;
						if(i == 0)
						{
							data = dataVar.data;
							dataSplit = dataVar.dataSplit;
						}
						else if(i == 1)
						{
							data = dataVarPrune.data;
							dataSplit = dataVarPrune.dataSplit;
						}
						else
						{
							data = dataVarOrder.data;
							dataSplit = dataVarOrder.dataSplit;
						}
	
						for(int n = 0; n < tab[i].length; n++)
						{
							data[nbVar][n].add(Double.valueOf(tab[i][n]));
							dataSplit[(int)(coeffSplit*10)][n].add(Double.valueOf(tab[i][n]));
						}
					}
	
					for(int n = 0; n < tabTemps[0].length; n++)
						dataVar.tempsAppr[nbVar][n].add(Double.valueOf(tabTemps[0][n]));

					for(int n = 0; n < tabTemps[1].length; n++)
						dataVarPrune.tempsAppr[nbVar][n].add(Double.valueOf(tabTemps[1][n]));

					for(int n = 0; n < tabTemps[2].length; n++)
						dataVarOrder.tempsAppr[nbVar][n].add(Double.valueOf(tabTemps[2][n]));

					
					for(int n = 0; n < tabTaille[0].length; n++)
						dataVar.tailleArbre[nbVar][n].add(Integer.valueOf(tabTaille[0][n]));
	
					for(int n = 0; n < tabTaille[1].length; n++)
						dataVarPrune.tailleArbre[nbVar][n].add(Integer.valueOf(tabTaille[1][n]));
				}
				
				reader.close();
				readerTaille.close();
				readerTemps.close();

				return true;
			} catch (FileNotFoundException e) {
				e.printStackTrace();
				return false;
			} catch (IOException e) {
				e.printStackTrace();
				return false;
			}
		}
		
		// fichier introuvable
		return false;
	}
	
	/**
	 * Génère des exemples (s'ils n'existent pas déjà)
	 * @param exemplesFileCsv
	 * @param nbVar
	 * @param vars
	 * @param nbinstances
	 * @param p
	 * @param arbre
	 */
	private static void generateExamples(String exemplesFileCsv, int nbVar, Variable[] vars, int nbinstances, ProbabilityDistributionLog p, DynamicallyGeneratedLexTree arbre)
	{
		FileWriter fichier;
		BufferedWriter output;
		
		if(!new File(exemplesFileCsv).exists())
		{	
			System.out.println("	Génération de "+nbinstances+" exemples.");
			try {
				fichier = new FileWriter(exemplesFileCsv);
				output = new BufferedWriter(fichier);
				for(int i = 0; i < nbVar-1; i++)
					output.write(vars[i].name+",");
				output.write(vars[nbVar-1].name);
				output.newLine();
	
				for(int i = 0; i < nbinstances; i++)
				{
//						if(i%10==0)
//							System.out.println(i);
					BigInteger rang = p.inverseBigInteger(rng.nextDouble());
					
//					System.out.println("Rang généré : "+rang);
//						System.out.println(rang);
					// rang hors de portée
					if(rang.compareTo(arbre.getRangMax()) >= 0)
					{
						i--;
						continue;
					}
					
					rang = rang.subtract(BigInteger.ONE);
					HashMap<String, String> instance = arbre.getConfigurationAtRank(rang);
					
					for(int j = 0; j < nbVar-1; j++)
						output.write(instance.get(vars[j].name)+",");
					output.write(instance.get(vars[nbVar-1].name));
					output.newLine();
				}
				output.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	private static BigInteger[] prepareEvaluate(ProbabilityDistributionLog p, DynamicallyGeneratedLexTree arbre)
	{
		int nbExemplesEvaluation = 100000;

		BigInteger[] rangs = new BigInteger[nbExemplesEvaluation];
		for(int i = 0; i < nbExemplesEvaluation; i++)
		{
			rangs[i] = p.inverseBigInteger(rng.nextDouble());
			if(rangs[i].compareTo(arbre.getRangMax()) > 0)
				i--; // si on a généré un rang trop grand…
		}
		return rangs;
	}
	
	private static LexicographicStructure learn(Variable[] vars, String exemplesFile, ApprentissageGloutonLexStructure algo)
	{
		ArrayList<String> filename = new ArrayList<String>();
		filename.add(exemplesFile);
		Instanciation.reinit();
		algo.apprendDomainesVariables(vars);
		LexicographicStructure arbreAppris = algo.apprendDonnees(filename, true);
		return arbreAppris;
	}
	
	/**
	 * Sauvegarde les résultats dans les fichiers prune-results. Format :
	 * taille jeu, gain taille (%)
	 * taille jeu, gain taille (%)
	 * etc.
	 * @param nbinstancetab
	 * @param dataset
	 * @param splitvar
	 * @param dataVar
	 * @param dataVarPrune
	 */
	public static void resultatGainPruningEnTailleFonctionDeTailleJeu(int[] nbinstancetab, String dataset, List<SplitVar> splitvar, EvaluationResults dataVar, EvaluationResults dataVarPrune)
	{
		PrintWriter writer = null;

		for(SplitVar s : splitvar)
		{
			String completeResultFile = dataset+"/prune-results-"+s.nbVar+"-"+s.coeffSplit+".csv";
			try {
				writer = new PrintWriter(completeResultFile, "UTF-8");
			} catch (FileNotFoundException e1) {
				e1.printStackTrace();
				writer.close();
				return;
			} catch (UnsupportedEncodingException e1) {
				e1.printStackTrace();
				writer.close();
				return;
			}
			
			for(int n = 0; n < nbinstancetab.length; n++)
			{
				double gainTaille = 0;
				int nb = dataVar.tailleArbre[s.nbVar][n].size();
				for(int i = 0; i < nb; i++)
				{
//					System.out.println("Avant : "+dataVar.tailleArbre[s.nbVar][n].get(i)+", après : "+dataVarPrune.tailleArbre[s.nbVar][n].get(i));
					gainTaille += (dataVar.tailleArbre[s.nbVar][n].get(i) - dataVarPrune.tailleArbre[s.nbVar][n].get(i)) / ((double)dataVar.tailleArbre[s.nbVar][n].get(i));
				}

				writer.println(nbinstancetab[n]+","+gainTaille/nb);
			}
			
			writer.close();
		}
	}
	
	/**
	 * Sauvegarde les résultats dans les fichiers vars-results. Format :
	 * taille jeu, KL moyen
	 * taille jeu, KL moyen
	 * etc.
	 * @param nbinstancetab
	 * @param dataset
	 * @param splitvar
	 * @param dataVar
	 * @param dataVarPrune
	 */
	public static void resultatTauxBonApprentissageFonctionDeTailleJeuNbVar(int[] nbinstancetab, String dataset, List<SplitVar> splitvar, EvaluationResults dataVar, EvaluationResults dataVarPrune)
	{
		boolean[] prunetab = {true, false};
		PrintWriter writer = null;

		for(boolean prune : prunetab)
		{
			for(SplitVar s : splitvar)
			{
//				if(s.coeffSplit != coeffsplit)
//					continue;
				String completeResultFile = dataset+"/vars-results-"+s.nbVar+"-"+prune+".csv";
				ArrayList<Double>[][] tab;
				if(prune)
					tab = dataVarPrune.data;
				else
					tab = dataVar.data;
				try {
					writer = new PrintWriter(completeResultFile, "UTF-8");
				} catch (FileNotFoundException e1) {
					e1.printStackTrace();
					writer.close();
					return;
				} catch (UnsupportedEncodingException e1) {
					e1.printStackTrace();
					writer.close();
					return;
				}
				
				for(int n = 0; n < nbinstancetab.length; n++)
				{
					int nb = 0;
					double pc = 0;
					for(double d : tab[s.nbVar][n])
					{
						pc += d;
//						if(d < epsilon)
//							pc++;
						nb++;
					}
					writer.println(nbinstancetab[n]+","+(pc / nb));
				}
				
				writer.close();
			}
		}
	}
	
	/**
	 * Sauvegarde les résultats dans les fichiers split-results. Format :
	 * taille jeu, KL moyen
	 * taille jeu, KL moyen
	 * etc.
	 * @param nbinstancetab
	 * @param dataset
	 * @param splitvar
	 * @param dataVar
	 * @param dataVarPrune
	 */
	public static void resultatTauxBonApprentissageFonctionDeTailleJeuSplit(int[] nbinstancetab, String dataset, List<SplitVar> splitvar, EvaluationResults dataVar, EvaluationResults dataVarPrune)
	{
		boolean[] prunetab = {true, false};
		PrintWriter writer = null;

		for(boolean prune : prunetab)
		{
			for(SplitVar s : splitvar)
			{
//				if(s.coeffSplit != coeffsplit)
//					continue;
				String completeResultFile = dataset+"/split-results-"+s.coeffSplit+"-"+prune+".csv";
				ArrayList<Double>[][] tab;
				if(prune)
					tab = dataVarPrune.dataSplit;
				else
					tab = dataVar.dataSplit;
				try {
					writer = new PrintWriter(completeResultFile, "UTF-8");
				} catch (FileNotFoundException e1) {
					e1.printStackTrace();
					writer.close();
					return;
				} catch (UnsupportedEncodingException e1) {
					e1.printStackTrace();
					writer.close();
					return;
				}
				
				for(int n = 0; n < nbinstancetab.length; n++)
				{
					int nb = 0;
					double pc = 0;
					for(double d : tab[(int)(s.coeffSplit*10)][n])
					{
						pc += d;
//						if(d < epsilon)
//							pc++;
						nb++;
					}
					writer.println(nbinstancetab[n]+","+(pc / nb));
				}
				
				writer.close();
			}
		}
	}
	
	/**
	 * Compare les taux de réussite du LP-tree, du LP-tree pruné et du LP-tree linéaire
	 * Sauvegarde les résultats dans les fichiers struct-lp-results, struct-prune-results, struct-lin-results. Format :
	 * taille jeu, KL moyen
	 * taille jeu, KL moyen
	 * etc.
	 * @param nbinstancetab
	 * @param dataset
	 * @param splitvar
	 * @param dataVar
	 * @param dataVarPrune
	 */
	public static void resultatTauxBonApprentissageFonctionDeTailleJeuLPTreeLPTreePruneLinearLPTree(int[] nbinstancetab, String dataset, List<SplitVar> splitvar, EvaluationResults dataVar, EvaluationResults dataVarPrune, EvaluationResults dataVarOrder)
	{
		PrintWriter writer = null;
		ArrayList<Double>[][] tab;

		for(int i = 0; i < 3; i++)
		{
			if(i == 0)
				tab = dataVar.data;
			else if(i == 1)
				tab = dataVarPrune.data;
			else
				tab = dataVarOrder.data;

			String completeResultFile;
			if(i == 0)
				completeResultFile = dataset+"/struct-lp-results.csv";
			else if(i == 1)
				completeResultFile = dataset+"/struct-prune-results.csv";
			else
				completeResultFile = dataset+"/struct-lin-results.csv";
			try {
				writer = new PrintWriter(completeResultFile, "UTF-8");
			} catch (FileNotFoundException e1) {
				e1.printStackTrace();
				writer.close();
				return;
			} catch (UnsupportedEncodingException e1) {
				e1.printStackTrace();
				writer.close();
				return;
			}
			
			for(int n = 0; n < nbinstancetab.length; n++)
			{
				int nb = 0;
				double pc = 0;
				for(SplitVar s : splitvar)
				{
					for(double d : tab[s.nbVar][n])
					{
						pc += d;
//						if(d < epsilon)
//							pc++;
						nb++;
					}
				}
				
				writer.println(nbinstancetab[n]+","+(pc / nb));
			}
			writer.close();
		}
	}

	/**
	 * Compare les tailles du LP-tree et du LP-tree pruné.
	 * Sauvegarde les résultats dans les fichiers taille-lp-results, taille-prune-results. Format :
	 * taille jeu, taille moyenne
	 * taille jeu, taille moyenne
	 * etc.
	 * @param nbinstancetab
	 * @param dataset
	 * @param splitvar
	 * @param dataVar
	 * @param dataVarPrune
	 * @param dataVarOrder
	 */
	public static void tailleModeleFonctionTailleJeuLPTreeLPTreePruneLinearLPTree(int[] nbinstancetab, String dataset, List<SplitVar> splitvar, EvaluationResults dataVar, EvaluationResults dataVarPrune, EvaluationResults dataVarOrder)
	{
		PrintWriter writer = null;
		ArrayList<Integer>[][] tab;

		for(int i = 0; i < 2; i++)
		{
			if(i == 0)
				tab = dataVar.tailleArbre;
			else
				tab = dataVarPrune.tailleArbre;

			String completeResultFile;
			if(i == 0)
				completeResultFile = dataset+"/temps-lp-results.csv";
			else
				completeResultFile = dataset+"/temps-prune-results.csv";
			try {
				writer = new PrintWriter(completeResultFile, "UTF-8");
			} catch (FileNotFoundException e1) {
				e1.printStackTrace();
				writer.close();
				return;
			} catch (UnsupportedEncodingException e1) {
				e1.printStackTrace();
				writer.close();
				return;
			}
			
			for(int n = 0; n < nbinstancetab.length; n++)
			{
				int nb = 0;
				double pc = 0;
				for(SplitVar s : splitvar)
				{
					for(double d : tab[s.nbVar][n])
					{
						pc += d;
						nb++;
					}
				}
				writer.println(nbinstancetab[n]+","+(pc / nb));
			}
			writer.close();
		}
	}
	
	/**
	 * Compare les temps d'apprentissage du LP-tree, du LP-tree pruné et du LP-tree linéaire.
	 * Sauvegarde les résultats dans les fichiers temps-lp-results, temps-prune-results, temps-lin-results. Format :
	 * taille jeu, temps moyen
	 * taille jeu, temps moyen
	 * etc.
	 * @param nbinstancetab
	 * @param dataset
	 * @param splitvar
	 * @param dataVar
	 * @param dataVarPrune
	 * @param dataVarOrder
	 */
	public static void tempsApprentissageFonctionTailleJeuLPTreeLPTreePruneLinearLPTree(int[] nbinstancetab, String dataset, List<SplitVar> splitvar, EvaluationResults dataVar, EvaluationResults dataVarPrune, EvaluationResults dataVarOrder)
	{
		PrintWriter writer = null;
		ArrayList<Double>[][] tab;

		for(int i = 0; i < 3; i++)
		{
			if(i == 0)
				tab = dataVar.tempsAppr;
			else if(i == 1)
				tab = dataVarPrune.tempsAppr;
			else
				tab = dataVarOrder.tempsAppr;

			String completeResultFile;
			if(i == 0)
				completeResultFile = dataset+"/temps-lp-results.csv";
			else if(i == 1)
				completeResultFile = dataset+"/temps-prune-results.csv";
			else
				completeResultFile = dataset+"/temps-lin-results.csv";
			try {
				writer = new PrintWriter(completeResultFile, "UTF-8");
			} catch (FileNotFoundException e1) {
				e1.printStackTrace();
				writer.close();
				return;
			} catch (UnsupportedEncodingException e1) {
				e1.printStackTrace();
				writer.close();
				return;
			}
			
			for(int n = 0; n < nbinstancetab.length; n++)
			{
				int nb = 0;
				double pc = 0;
				for(SplitVar s : splitvar)
				{
					for(double d : tab[s.nbVar][n])
					{
						pc += d;
						nb++;
					}
				}
				writer.println(nbinstancetab[n]+","+(pc / nb));
			}
			writer.close();
		}
	}
	
	/**
	 * Compare les taux de réussite du LP-tree, du LP-tree pruné et du LP-tree linéaire
	 * Sauvegarde les résultats dans les fichiers kl-lp-results, kl-prune-results, kl-lin-results. Format :
	 * taille jeu, moyenne KL
	 * taille jeu, moyenne KL
	 * etc.
	 * @param nbinstancetab
	 * @param dataset
	 * @param splitvar
	 * @param dataVar
	 * @param dataVarPrune
	 */
	public static void moyenneKLFonctionDeTailleJeuLPTreeLPTreePruneLinearLPTree(int[] nbinstancetab, String dataset, List<SplitVar> splitvar, EvaluationResults dataVar, EvaluationResults dataVarPrune, EvaluationResults dataVarOrder)
	{
		PrintWriter writer = null;
		ArrayList<Double>[][] tab;

		for(int i = 0; i < 3; i++)
		{
			if(i == 0)
				tab = dataVar.data;
			else if(i == 1)
				tab = dataVarPrune.data;
			else
				tab = dataVarOrder.data;

			String completeResultFile;
			if(i == 0)
				completeResultFile = dataset+"/kl-lp-results.csv";
			else if(i == 1)
				completeResultFile = dataset+"/kl-prune-results.csv";
			else
				completeResultFile = dataset+"/kl-lin-results.csv";
			try {
				writer = new PrintWriter(completeResultFile, "UTF-8");
			} catch (FileNotFoundException e1) {
				e1.printStackTrace();
				writer.close();
				return;
			} catch (UnsupportedEncodingException e1) {
				e1.printStackTrace();
				writer.close();
				return;
			}
			
			for(int n = 0; n < nbinstancetab.length; n++)
			{			
				int nb = 0;
				double pc = 0;
				for(SplitVar s : splitvar)
				{
					if(i == 2)
						System.out.println(tab[s.nbVar][n].size());
					for(double d : tab[s.nbVar][n])
					{
						pc += d;
						nb++;
					}
				}
				writer.println(nbinstancetab[n]+","+(pc / nb));
			}
			writer.close();
		}
	}
	
	/**
	 * Sauvegarde les résultats dans les fichiers moyenne-results. Format :
	 * taille jeu, KL
	 * taille jeu, KL
	 * etc.
	 * @param nbinstancetab
	 * @param dataset
	 * @param splitvar
	 * @param dataVar
	 * @param dataVarPrune
	 */
	private static void resultatDivergenceMoyenneFonctionDeTailleJeu(int[] nbinstancetab, String dataset, List<SplitVar> splitvar, EvaluationResults dataVar, EvaluationResults dataVarPrune)
	{
		boolean[] prunetab = {true, false};
		PrintWriter writer = null;

		for(boolean prune : prunetab)
		{
			for(SplitVar s : splitvar)
			{
				String completeResultFile = dataset+"/moyenne-results-"+s.nbVar+"-"+prune+".csv";
				try {
					writer = new PrintWriter(completeResultFile, "UTF-8");
				} catch (FileNotFoundException e1) {
					e1.printStackTrace();
					writer.close();
					return;
				} catch (UnsupportedEncodingException e1) {
					e1.printStackTrace();
					writer.close();
					return;
				}
				
				ArrayList<Double>[][] tab;
				if(prune)
					tab = dataVarPrune.data;
				else
					tab = dataVar.data;
				
				for(int n = 0; n < nbinstancetab.length; n++)
				{
					int nb = tab[s.nbVar][n].size();
					double pc = 0;
					for(double d : tab[s.nbVar][n])
						pc += d;
	
					writer.println(nbinstancetab[n]+","+(pc / nb));
				}
				
				writer.close();
			}
		}
	}
}
