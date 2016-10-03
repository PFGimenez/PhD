

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import compilateurHistorique.Variable;
import preferences.*;
import preferences.GeometricDistribution;
import preferences.compare.*;
import preferences.completeTree.*;
import preferences.heuristiques.*;

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

/**
 * Génère un historique à partir d'un LP-tree
 * @author pgimenez
 *
 */

public class EvaluationLextree
{
	public static void main(String[] args)
	{
		Random rng = new Random();
		double[] coeffSplitTab = {0.05, 0.1, .15, 0.2};

		int[] nbinstancetab = {10, 100, 1000, 10000, 100000};
//		ApprentissageGloutonLexStructure[] algotab = {new ApprentissageGloutonLexTree(300, 20, new VieilleHeuristique(new HeuristiqueRandom())),
				/*new ApprentissageGloutonLexTree(300, 20, new VieilleHeuristique(new HeuristiqueEntropieNormalisee())),
				new ApprentissageGloutonLexTree(300, 20, new VieilleHeuristique(new HeuristiqueProbaMax())),*/
//				new ApprentissageGloutonLexTree(300, 20, new HeuristiqueDuel())
//		};
		ApprentissageGloutonLexStructure algo = new ApprentissageGloutonLexTree(300, 20, new HeuristiqueDuel());
		
		int nbExemplesEvaluation = 100000;
		int[] nbVarTab = {10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20};
		Variable[] vars = null;
		
		@SuppressWarnings("unchecked")
		HashMap<Integer, Double>[] data = (HashMap<Integer, Double>[]) new HashMap[nbinstancetab.length];
		
		for(int n = 0; n < nbinstancetab.length; n++)
			data[n] = new HashMap<Integer, Double>();
		
//		Comparison[] comptab = {/*new SpearmanCorrComparison(), new InverseComparison(new SpearmanCorrComparison()),*/ new KLComparison()/*, new SpearmanMetricComparison()*/};
		Comparison comp = new KLComparison();
		
//		ProbabilityDistribution p = new LinearDistribution(Math.pow(2, nbVar), 0);
		ProbabilityDistributionLog p = new GeometricDistribution(0.01);
		System.out.println("Distribution de probabilité : "+p.getClass().getSimpleName());
		
		for(int nbVar : nbVarTab)
		{
			System.out.println("Nb var : "+nbVar);
						
			for(double coeffSplit : coeffSplitTab)
			{
				System.out.println("Coeff split : "+coeffSplit);
				String dataset = "datasets/lptree-relearning_"+p.getClass().getSimpleName();
				String arbreFile = dataset+"/LPtree_for_generation-"+nbVar+"-"+coeffSplit;
				String varsFile = dataset+"/vars-"+nbVar+"-"+coeffSplit;
				vars = null;
				// VARIABLES
				ObjectInputStream ois;
				try {
					ois = new ObjectInputStream(new FileInputStream(new File(varsFile)));
					vars = (Variable[])ois.readObject();
					
					System.out.println("Variables chargées : "+varsFile);
		
					for(int i = 0; i < nbVar; i++)
						System.out.println(vars[i].name+" : "+vars[i].domain+" modalités");
				
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
				
				// ARBRE
				LexicographicTree arbre;
				System.out.println("Chargement du lextree en cours : "+arbreFile);
				arbre = (LexicographicTree) LexicographicTree.load(arbreFile);
		
				if(arbre == null)
				{
					System.out.println("Chargement échoué");
					System.out.println("Génération en cours…");
		
					
					arbre = GenereLexTree.genere(vars, coeffSplit);
					System.out.println("Génération terminée");
					arbre.save(arbreFile);
					
				}
				System.out.println("Rang max : "+arbre.getRangMax());
				System.out.println("Nb nœuds : "+arbre.getNbNoeuds());
				if(coeffSplit < 0.5)
					arbre.affiche("-Reel-"+p.getClass().getSimpleName()+"-"+nbVar+"-"+coeffSplit);
				
				FileWriter fichier;
				BufferedWriter output;
		
				for(int n = 0; n < nbinstancetab.length; n++)
				{			
					int nbinstances = nbinstancetab[n];
					String exemplesFile = dataset+"/exemples-"+nbVar+"-"+nbinstances+"-"+coeffSplit;
					String exemplesFileCsv = exemplesFile+".csv";
					
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
					
					ArrayList<String> filename = new ArrayList<String>();
					filename.add(exemplesFile);
		
					System.out.println("	Nombre d'instances : "+nbinstances);
					
					
					long[] rangs = new long[nbExemplesEvaluation];
					for(int i = 0; i < nbExemplesEvaluation; i++)
					{
						rangs[i] = Math.round(p.inverse(rng.nextDouble()));
						if(rangs[i] > arbre.getRangMax().longValue())
							i--; // si on a généré un rang trop grand…
					}
					
//					for(int i = 0; i < algotab.length; i++)
//					{
//						ApprentissageGloutonLexStructure algo = algotab[i];
						algo.apprendDomainesVariables(vars);
						LexicographicStructure arbreAppris = algo.apprendDonnees(filename, true);
						
//						arbreAppris.affiche("-"+algo.getHeuristiqueName()+"-"+p.getClass().getSimpleName()+"-"+nbVar+"-"+coeffSplit+"-"+nbinstances);
		//				arbreAppris.affiche("Appris");
						
						System.out.println("  "+algo.getHeuristiqueName());
//						for(int j = 0; j < comptab.length; j++)
//							System.out.println(comptab[j].getClass().getSimpleName()+" : "+comptab[j].compare(arbreAppris, arbre, rangs, p));
						double val = comp.compare(arbreAppris, arbre, rangs, p);
						System.out.println(comp.getClass().getSimpleName()+" : "+val);
						System.out.println();
//					}
					data[n].put(arbre.getNbNoeuds(), val);
					System.out.println();
				}
			}
		}
		
		for(int n = 0; n < nbinstancetab.length; n++)
		{
			System.out.println("Nb exemples : "+nbinstancetab[n]);
			for(Integer i : data[n].keySet())
				System.out.println(i+" : "+data[n].get(i));
		}
	}
	
}
