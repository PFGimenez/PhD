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
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import compilateurHistorique.Instanciation;
import compilateurHistorique.Variable;
import preferences.*;
import preferences.GeometricDistribution;
import preferences.compare.*;
import preferences.completeTree.*;
import preferences.heuristiques.*;
import preferences.penalty.BIC;

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
		 * 1er indice : nombre de variables
		 * 2e indice : l'indice de la taille du jeu de données
		 * Valeurs de la liste : divergence KL avec le vrai lextree
		 */
		ArrayList<Double>[][] dataOrder;
		
		/**
		 * 1er indice : nombre de variables
		 * 2e indice : l'indice de la taille du jeu de données
		 * Valeurs de la liste : taille de l'arbre
		 */
		ArrayList<Integer>[][] tailleArbre;

		/**
		 * Indice du tableau : l'indice de la taille du jeu de données
		 * Entier (clé) du HashMap : nombre de nœuds
		 * Double (valeur) du HashMap : divergence KL avec le vrai lextree
		 */
		HashMap<Integer, ArrayList<Double>>[] dataJeu;
		
		/**
		 * Indice du tableau : l'indice de la taille du jeu de données
		 * Entier (clé) du HashMap : nombre de nœuds
		 * Double (valeur) du HashMap : divergence KL avec le vrai lextree
		 */
		HashMap<Integer, ArrayList<Double>>[] dataJeuOrder;

		@SuppressWarnings("unchecked")
		public EvaluationResults(int n)
		{
			dataJeu = (HashMap<Integer, ArrayList<Double>>[]) new HashMap[n];
			for(int i = 0; i < n; i++)
				dataJeu[i] = new HashMap<Integer, ArrayList<Double>>();

			dataJeuOrder = (HashMap<Integer, ArrayList<Double>>[]) new HashMap[n];
			for(int i = 0; i < n; i++)
				dataJeuOrder[i] = new HashMap<Integer, ArrayList<Double>>();
			
			data = (ArrayList<Double>[][]) new ArrayList[100][n];
			for(int j = 0; j < n; j++)
				for(int i = 0; i < 100; i++)
					data[i][j] = new ArrayList<Double>();
			
			dataOrder = (ArrayList<Double>[][]) new ArrayList[100][n];
			for(int j = 0; j < n; j++)
				for(int i = 0; i < 100; i++)
					dataOrder[i][j] = new ArrayList<Double>();
			
			tailleArbre = (ArrayList<Integer>[][]) new ArrayList[100][n];
			for(int j = 0; j < n; j++)
				for(int i = 0; i < 100; i++)
					tailleArbre[i][j] = new ArrayList<Integer>();
		}
	}
	
	public static void main(String[] args)
	{
		double paramp = 0.001;

//		SplitVar[] splitvar = {new SplitVar(0.4, 10)};

		SplitVar[] splitvar = {new SplitVar(0, 10), new SplitVar(0.1, 10), new SplitVar(0.2, 10), new SplitVar(0.3, 10), new SplitVar(0.4, 10),
				new SplitVar(0.1, 15),// new SplitVar(0.12, 15), new SplitVar(0.15, 15),
				new SplitVar(0.1, 18),// new SplitVar(0.12, 18), new SplitVar(0.15, 18), new SplitVar(0.17, 18), new SplitVar(0.2, 18),
				new SplitVar(0.1, 20),// new SplitVar(0.12, 20), new SplitVar(0.15, 20), new SplitVar(0.17, 20), new SplitVar(0.2, 20),
				new SplitVar(0.1, 22),
				/*new SplitVar(0.01, 25), new SplitVar(0.05, 25),*/ new SplitVar(0.1, 25),
				/*new SplitVar(0.01, 28), new SplitVar(0.05, 28),*/ new SplitVar(0.1, 28)};
//				new SplitVar(0.005, 30), new SplitVar(0.01, 30),
//				new SplitVar(0.005, 35), new SplitVar(0.01, 35),
//				new SplitVar(0.005, 40), new SplitVar(0.01, 40)};
		
//		double[] coeffSplitTab = {0.05, 0.1, 0.11, 0.12, 0.13, 0.14, .15, 0.2, 0.25, 0.3, 0.35, 0.4};

//		int[] nbinstancetab = {10, 25, 100, 250, 600, 1000, 1500, 2000, 2500, 10000, 25000, 100000};
		int[] nbinstancetab = {10, 100, 1000, 2000, 10000, 25000, 100000};
		
		ApprentissageGloutonLexTree algo = new ApprentissageGloutonLexTree(300, 20, new HeuristiqueDuel());
		ApprentissageGloutonLexOrder algoLinear = new ApprentissageGloutonLexOrder(new HeuristiqueDuel());
		
//		int[] nbVarTab = {10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20};
		Variable[] vars = null;

		EvaluationResults data = new EvaluationResults(nbinstancetab.length);		
		EvaluationResults dataPrune = new EvaluationResults(nbinstancetab.length);
		
//		Comparison[] comptab = {/*new SpearmanCorrComparison(), new InverseComparison(new SpearmanCorrComparison()),*/ new KLComparison()/*, new SpearmanMetricComparison()*/};
		Comparison comp = new KLComparison();
//		Comparison comp = new FirstDifferentNodeComparison();

		PrintWriter writer = null, writerTaille = null;
		ProbabilityDistributionLog p = new GeometricDistribution(paramp);
//		ProbabilityDistributionLog p = new LinearDistribution(Math.pow(2, nbVar), 0);
		System.out.println("Distribution de probabilité : "+p.getClass().getSimpleName());
		String dataset = "datasets/lptree-relearning_"+p.getClass().getSimpleName()+"_"+paramp;

		for(SplitVar s : splitvar)
		{
			for(int iter = 0; iter < 50; iter++)
//			for(int iter = 0; iter < 500; iter++)
			{
			
				int nbVar = s.nbVar;
				double coeffSplit = s.coeffSplit;
							
				String arbreFile = dataset+"/LPtree_for_generation-"+nbVar+"-"+coeffSplit+"-"+iter;
				String varsFile = dataset+"/vars-"+nbVar+"-"+coeffSplit+"-"+iter;
				String resultatFile = dataset+"/result-"+comp.getClass().getSimpleName()+"-"+nbVar+"-"+coeffSplit+"-"+iter+".csv";
				String resultatTailleFile = dataset+"/taille-"+comp.getClass().getSimpleName()+"-"+nbVar+"-"+coeffSplit+"-"+iter+".csv";
				// VARIABLES
				vars = getVariables(varsFile, nbVar);
				
				// ARBRE
				LexicographicTree arbre = getArbre(arbreFile, coeffSplit, vars);
				int nbNoeuds = arbre.getNbNoeuds();
	
				System.out.println("Nb var : "+nbVar);
				System.out.println("Coeff split : "+coeffSplit);
				System.out.println("Rang max : "+arbre.getRangMax());
				System.out.println("Nb nœuds : "+nbNoeuds);
	//				if(coeffSplit < 0.5)
	//					arbre.affiche("-Reel-"+p.getClass().getSimpleName()+"-"+nbVar+"-"+coeffSplit);
		
				// si le chargement est partiel
				if(!loadResults(nbinstancetab.length, resultatFile, resultatTailleFile, nbVar, data, dataPrune, nbNoeuds))
				{
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
	
					BiString line = new BiString(), lineTaille = new BiString();
					BiString lineOrder = new BiString(), lineTailleOrder = new BiString();
					
					for(int n = 0; n < nbinstancetab.length; n++)
					{		
						int nbinstances = nbinstancetab[n];
						System.out.println("	Nombre d'instances : "+nbinstances);
						String exemplesFile = dataset+"/exemples-"+nbVar+"-"+nbinstances+"-"+coeffSplit+"-"+iter;
						generateExamples(exemplesFile+".csv", nbVar, vars, nbinstances, p, arbre);
						evaluate(n, nbinstances, exemplesFile, nbVar, vars, p, arbre, algo, comp, data, dataPrune, nbNoeuds, line, lineTaille);
						evaluate(n, nbinstances, exemplesFile, nbVar, vars, p, arbre, algoLinear, comp, data, dataPrune, nbNoeuds, lineOrder, lineTailleOrder);
					}
					writer.println(line.line1);
					System.out.println("1 "+line.line1);
					System.out.println("2 "+line.line2);
					System.out.println("3 "+lineOrder.line1);
					writer.println(line.line2);
					writer.println(lineOrder.line1);
					writerTaille.println(lineTaille.line1);
					writerTaille.println(lineTaille.line2);
					writer.close();
					writerTaille.close();
				}
			}
			
		}
		
		/*
		double[] epsilontab = {0.01, 0.1, 1, 10};
		
		for(double epsilon : epsilontab)
		{
			String completeResultFile = dataset+"/eps-results-"+epsilon+".csv";
			try {
				writer = new PrintWriter(completeResultFile, "UTF-8");
			} catch (FileNotFoundException e1) {
				e1.printStackTrace();
				return;
			} catch (UnsupportedEncodingException e1) {
				e1.printStackTrace();
				return;
			}
			
			for(int n = 0; n < nbinstancetab.length; n++)
			{
				int nb = 0;
				double pc = 0;
				for(ArrayList<Double> l : data[n].values())
					for(double d : l)
					{
						if(d < epsilon)
							pc++;
						nb++;
					}
				writer.println(nbinstancetab[n]+","+(pc / nb));
			}
			
			writer.close();
		}*/
		
		resultatTauxBonApprentissageFonctionDeTailleJeu(nbinstancetab, dataset, splitvar, data, dataPrune);
		resultatDivergenceMoyenneFonctionDeTailleJeu(nbinstancetab, dataset, splitvar, data, dataPrune);
		resultatGainPruningEnTailleFonctionDeTailleJeu(nbinstancetab, dataset, splitvar, data, dataPrune);
		resultatTauxBonApprentissageFonctionDeTailleJeuLPTreeLPTreePruneLinearLPTree(nbinstancetab, dataset, splitvar, data, dataPrune);
		/*
		double seuil = 0.1;
		int[] frontieres = {0,20,60,100,200,300,400,500};
		int pc = 0;
		int tmpInd = 0;
		double nb = 0;
		
		for(int n = 0; n < nbinstancetab.length; n++)
		{
			int indiceFront = 0;
			String completeResultFile = dataset+"/final-results-"+nbinstancetab[n]+".csv";

			try {
				writer = new PrintWriter(completeResultFile, "UTF-8");
			} catch (FileNotFoundException e1) {
				e1.printStackTrace();
				return;
			} catch (UnsupportedEncodingException e1) {
				e1.printStackTrace();
				return;
			}
			
			System.out.println("Nb exemples : "+nbinstancetab[n]);

			LinkedList<Entry<Integer, ArrayList<Double>>> list = new LinkedList<Map.Entry<Integer, ArrayList<Double>>>(data[n].entrySet());
		     Collections.sort(list, new Comparator<Entry<Integer, ArrayList<Double>>>() {
		          public int compare(Entry<Integer, ArrayList<Double>> o1, Entry<Integer, ArrayList<Double>> o2) {
		               return o1.getKey()
		              .compareTo(o2.getKey());
		          }
		     });

		    for (Iterator<Entry<Integer, ArrayList<Double>>> it = list.iterator(); it.hasNext();)
		    {
		        Map.Entry<Integer, ArrayList<Double>> entry = it.next();
		        ArrayList<Double> l = entry.getValue();
		        if(entry.getKey() >= 1000)
		        	break;
		        boolean save = false;
		        while(indiceFront < frontieres.length - 1 && entry.getKey() >= frontieres[indiceFront+1])
		        {
		        	save = true;
		        	indiceFront++;
		        }
		        
		        if(save && nb != 0)
		        {
		        	System.out.println((tmpInd / nb)+","+(pc / nb)+" ("+nb+")");
		        	writer.println((tmpInd / nb)+","+(pc / nb));
		        	pc = 0;
			        nb = 0;
			        tmpInd = 0;
		        }
		        
		        for(int i = 0; i < l.size(); i++)
		        {
		        	if(l.get(i) < seuil)
		        		pc++;
		        	tmpInd += entry.getKey();
		        	nb++;
		        }
		        
		    }
	        if(nb != 0)
	        {
	        	System.out.println((tmpInd / nb)+","+(pc / nb)+" ("+nb+")");
	        	writer.println((tmpInd / nb)+","+(pc / nb));
	        	pc = 0;
		        nb = 0;
		        tmpInd = 0;
	        }
		    
			writer.close();

		}*/
	}

	private static Variable[] getVariables(String varsFile, int nbVar)
	{
		ObjectInputStream ois;
		Variable[] vars;
		try {
			ois = new ObjectInputStream(new FileInputStream(new File(varsFile)));
			vars = (Variable[])ois.readObject();
			
//				System.out.println("Variables chargées : "+varsFile);

//				for(int i = 0; i < nbVar; i++)
//					System.out.println(vars[i].name+" : "+vars[i].domain+" modalités");
		
			ois.close();
		} catch (Exception e) {
			vars = new Variable[nbVar];
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
				
				// sauvegarde des variables
				ObjectOutputStream oos;
				try {
					oos = new ObjectOutputStream(new FileOutputStream(new File(varsFile)));
					oos.writeObject(vars);
				} catch (FileNotFoundException e2) {
					e2.printStackTrace();
				} catch (IOException e2) {
					e2.printStackTrace();
				}
			}
		}
		return vars;
	}
	
	private static LexicographicTree getArbre(String arbreFile, double coeffSplit, Variable[] vars)
	{
		System.out.println("Chargement du lextree en cours : "+arbreFile);
		LexicographicTree arbre = (LexicographicTree) LexicographicTree.load(arbreFile);

		if(arbre == null)
		{
			System.out.println("Chargement échoué");
			System.out.println("Génération en cours…");

			
			arbre = GenereLexTree.genere(vars, coeffSplit);
			System.out.println("Génération terminée");
			arbre.save(arbreFile);
			
		}
		return arbre;
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
	private static boolean loadResults(int expectedValues, String resultatFile, String resultatTailleFile, int nbVar, EvaluationResults dataVar, EvaluationResults dataVarPrune, int nbNoeuds)
	{
		BufferedReader reader, readerTaille;
		// on a déjà les résultats de cette expérience
		if(new File(resultatFile).exists() && new File(resultatTailleFile).exists())
		{
			System.out.println(resultatFile);
			System.out.println(resultatTailleFile);
			try {
				reader = new BufferedReader(new FileReader(new File(resultatFile)));
				readerTaille = new BufferedReader(new FileReader(new File(resultatTailleFile)));
				String l = reader.readLine();
				
				boolean error = false;
				String[][] tab = new String[3][], tabTaille = new String[2][];
				
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

				reader.close();
				readerTaille.close();

				/*
				 * Pas de chargement partiel : s'il manque quelque chose on recommence tout
				 */
				if(error)
					return false;

				/*
				 * Le chargement s'est bien passé, on peut tout sauvegarder en mémoire
				 */
				for(int i = 0; i < 3; i++)
				{
					HashMap<Integer, ArrayList<Double>>[] dataJeu;
					ArrayList<Double>[][] data;
					if(i == 0)
					{
						dataJeu = dataVar.dataJeu;
						data = dataVar.data;
					}
					else if(i == 1)
					{
						dataJeu = dataVarPrune.dataJeu;
						data = dataVarPrune.data;
					}
					else
					{
						dataJeu = dataVar.dataJeuOrder;
						data = dataVar.dataOrder;
					}

					for(int n = 0; n < tab[i].length; n++)
					{
						if(dataJeu[n].get(nbNoeuds) == null)
							dataJeu[n].put(nbNoeuds, new ArrayList<Double>());
						dataJeu[n].get(nbNoeuds).add(Double.valueOf(tab[i][n]));
						data[nbVar][n].add(Double.valueOf(tab[i][n]));
					}
				}

				for(int n = 0; n < tabTaille[0].length; n++)
					dataVar.tailleArbre[nbVar][n].add(Integer.valueOf(tabTaille[0][n]));

				for(int n = 0; n < tabTaille[1].length; n++)
					dataVarPrune.tailleArbre[nbVar][n].add(Integer.valueOf(tabTaille[1][n]));

			} catch (FileNotFoundException e) {
				e.printStackTrace();
				return false;
			} catch (IOException e) {
				e.printStackTrace();
				return false;
			}
		}
		
		return true;
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
	private static void generateExamples(String exemplesFileCsv, int nbVar, Variable[] vars, int nbinstances, ProbabilityDistributionLog p, LexicographicTree arbre)
	{
		FileWriter fichier;
		BufferedWriter output;
		
		if(!new File(exemplesFileCsv).exists())
		{	
			System.out.println("Génération du fichier d'exemples : "+exemplesFileCsv);
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
					long rang = Math.round(p.inverse(rng.nextDouble()));
					
//						System.out.println(rang);
					// rang hors de portée
					if(rang >= arbre.getRangMax().longValue())
					{
						i--;
						continue;
					}
					
					HashMap<String, String> instance = arbre.getConfigurationAtRank(BigInteger.valueOf(rang-1));
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

	private static class BiString
	{
		public String line1 = "", line2 = "";
	}
	
	/**
	 * Évalue un arbre (et sa version prunée) par rapport à la solution.
	 * Estime une divergence KL
	 * @param n
	 * @param nbinstances
	 * @param exemplesFile
	 * @param nbVar
	 * @param vars
	 * @param p
	 * @param arbre
	 * @param algo
	 * @param comp
	 * @param dataVar
	 * @param dataVarPrune
	 * @param nbNoeuds
	 * @param line
	 * @param lineTaille
	 * @return
	 */
	private static BiString evaluate(int n, int nbinstances, String exemplesFile, int nbVar, Variable[] vars, ProbabilityDistributionLog p, LexicographicTree arbre, ApprentissageGloutonLexStructure algo, Comparison comp, EvaluationResults dataVar, EvaluationResults dataVarPrune, int nbNoeuds, BiString line, BiString lineTaille)
	{
		int nbExemplesEvaluation = 100000;

		ArrayList<String> filename = new ArrayList<String>();
		filename.add(exemplesFile);
		
		long[] rangs = new long[nbExemplesEvaluation];
		for(int i = 0; i < nbExemplesEvaluation; i++)
		{
			rangs[i] = Math.round(p.inverse(rng.nextDouble()));
			if(rangs[i] > arbre.getRangMax().longValue())
				i--; // si on a généré un rang trop grand…
		}
		
		Instanciation.reinit();
		algo.apprendDomainesVariables(vars);
		LexicographicStructure arbreAppris = algo.apprendDonnees(filename, true);

//		System.out.println("  "+algo.getHeuristiqueName());
		double val = comp.compare(arbreAppris, arbre, rangs, p);
		int taille = arbreAppris.getNbNoeuds();
		
		System.out.println(algo.getClass().getSimpleName()+" "+comp.getClass().getSimpleName()+" : "+val+" "+taille);

		if(dataVar.dataJeu[n].get(nbNoeuds) == null)
			dataVar.dataJeu[n].put(nbNoeuds, new ArrayList<Double>());
		dataVar.dataJeu[n].get(nbNoeuds).add(val);
		dataVar.data[nbVar][n].add(Double.valueOf(val));
		dataVar.tailleArbre[nbVar][n].add(taille);

		if(n == 0)
		{
			lineTaille.line1 += taille;
			line.line1 += val;
		}
		else
		{
			lineTaille.line1 += ","+taille;
			line.line1 += ","+val;
		}
		
		if(algo instanceof ApprentissageGloutonLexTree)
		{
			// on élague l'arbre
			((ApprentissageGloutonLexTree)algo).prune(new BIC(), p);
			int taillePrune = arbreAppris.getNbNoeuds();
			double valPrune = comp.compare(arbreAppris, arbre, rangs, p);
			
			System.out.println(algo.getClass().getSimpleName()+" "+comp.getClass().getSimpleName()+" : "+valPrune+" (prune) "+taillePrune);
	
			if(dataVarPrune.dataJeu[n].get(nbNoeuds) == null)
				dataVarPrune.dataJeu[n].put(nbNoeuds, new ArrayList<Double>());
			dataVarPrune.dataJeu[n].get(nbNoeuds).add(valPrune);
			dataVarPrune.data[nbVar][n].add(Double.valueOf(valPrune));
			dataVar.tailleArbre[nbVar][n].add(taillePrune);
			
			if(n == 0)
			{
				lineTaille.line2 += taillePrune;
				line.line2 += valPrune;
			}
			else
			{
				lineTaille.line2 += ","+taillePrune;
				line.line2 += ","+valPrune;
			}
		}

		return line;
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
	public static void resultatGainPruningEnTailleFonctionDeTailleJeu(int[] nbinstancetab, String dataset, SplitVar[] splitvar, EvaluationResults dataVar, EvaluationResults dataVarPrune)
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
	 * taille jeu, proportion KL < 1
	 * taille jeu, proportion KL < 1
	 * etc.
	 * @param nbinstancetab
	 * @param dataset
	 * @param splitvar
	 * @param dataVar
	 * @param dataVarPrune
	 */
	public static void resultatTauxBonApprentissageFonctionDeTailleJeu(int[] nbinstancetab, String dataset, SplitVar[] splitvar, EvaluationResults dataVar, EvaluationResults dataVarPrune)
	{
//		double coeffsplit = 0.1;
		double epsilon = 1;
		boolean[] prunetab = {true, false};
		PrintWriter writer = null;

		for(boolean prune : prunetab)
		{
			for(SplitVar s : splitvar)
			{
//				if(s.coeffSplit != coeffsplit)
//					continue;
				String completeResultFile = dataset+"/vars-results-"+s.nbVar+"-"+s.coeffSplit+"-"+prune+".csv";
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
						if(d < epsilon)
							pc++;
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
	 * taille jeu, proportion KL < 1
	 * taille jeu, proportion KL < 1
	 * etc.
	 * @param nbinstancetab
	 * @param dataset
	 * @param splitvar
	 * @param dataVar
	 * @param dataVarPrune
	 */
	public static void resultatTauxBonApprentissageFonctionDeTailleJeuLPTreeLPTreePruneLinearLPTree(int[] nbinstancetab, String dataset, SplitVar[] splitvar, EvaluationResults dataVar, EvaluationResults dataVarPrune)
	{
//		double coeffsplit = 0.1;
		double epsilon = 1;
		PrintWriter writer = null;
		ArrayList<Double>[][] tab;

		for(int i = 0; i < 3; i++)
		{
			if(i == 0)
				tab = dataVar.data;
			else if(i == 1)
				tab = dataVarPrune.data;
			else
				tab = dataVar.dataOrder;

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
						if(d < epsilon)
							pc++;
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
	private static void resultatDivergenceMoyenneFonctionDeTailleJeu(int[] nbinstancetab, String dataset, SplitVar[] splitvar, EvaluationResults dataVar, EvaluationResults dataVarPrune)
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
