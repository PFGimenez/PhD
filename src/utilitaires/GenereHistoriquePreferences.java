package utilitaires;

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

import JSci.maths.statistics.*;
import compilateurHistorique.Variable;
import preferences.*;
import preferences.compare.*;
import preferences.completeTree.*;
import preferences.heuristiques.*;
import preferences.heuristiques.simple.*;

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

public class GenereHistoriquePreferences
{
	public static void main(String[] args)
	{
		Random rng = new Random();

		int[] nbinstancetab = {10, 100, 1000, 10000, 100000};
		ApprentissageGloutonLexStructure[] algotab = {new ApprentissageGloutonLexTree(300, 20, new VieilleHeuristique(new HeuristiqueRandom())),
				new ApprentissageGloutonLexTree(300, 20, new VieilleHeuristique(new HeuristiqueEntropieNormalisee())),
				new ApprentissageGloutonLexTree(300, 20, new VieilleHeuristique(new HeuristiqueProbaMax())),
				new ApprentissageGloutonLexTree(300, 20, new HeuristiqueDuel())
		};
		int nbExemplesEvaluation = 10000;
		int nbVar = 10;
		Variable[] vars = null;
		
		System.out.println("Nb var : "+nbVar);
		
//		ProbabilityDistribution p = new LinearDistribution(Math.pow(2, nbVar), 0);
		ProbabilityDistribution p = new GeometricDistribution(0.01);
//		Comparison comp = new SpearmanComparison();
		Comparison comp = new KLComparison();
		
		System.out.println("Distribution de probabilité : "+p.getClass().getSimpleName());
		
		LexicographicTree arbre;
		System.out.println("Chargement du lextree en cours…");
		arbre = (LexicographicTree) LexicographicTree.load("datasets/lptree-relearning_"+p.getClass().getSimpleName()+"/LPtree_for_generation-"+nbVar);

		if(arbre == null)
		{
			System.out.println("Chargement échoué");
			System.out.println("Génération en cours…");
			vars = new Variable[nbVar];
			for(int i = 0; i < nbVar; i++)
			{
				vars[i] = new Variable();
				vars[i].domain = rng.nextInt(8) + 2;
				System.out.println(vars[i].domain+" modalités");
				vars[i].name = "V"+i;
				vars[i].values = new ArrayList<String>();
				for(int j = 0; j < vars[i].domain; j++)
					vars[i].values.add(Integer.toString(j));
			}
			
			arbre = GenereLexTree.genere(vars);
			System.out.println("Génération terminée");
			arbre.save("datasets/lptree-relearning_"+p.getClass().getSimpleName()+"/LPtree_for_generation-"+nbVar);
			
			// sauvegarde des variables
			ObjectOutputStream oos;
			try {
				oos = new ObjectOutputStream(new FileOutputStream(new File("datasets/lptree-relearning_"+p.getClass().getSimpleName()+"/vars-"+nbVar)));
				oos.writeObject(vars);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		else
		{
			// Chargement des variables
			ObjectInputStream ois;
			try {
				ois = new ObjectInputStream(new FileInputStream(new File("datasets/lptree-relearning_"+p.getClass().getSimpleName()+"/vars-"+nbVar)));
				vars = (Variable[])ois.readObject();
				
				for(int i = 0; i < nbVar; i++)
					System.out.println(vars[i].domain+" modalités");
				
				ois.close();
			} catch (Exception e) {
				System.err.println(e);
			}
			System.out.println("Chargement terminé");
		}
		
		FileWriter fichier;
		BufferedWriter output;

		for(int k = 0; k < nbinstancetab.length; k++)
		{
		
			int nbinstances = nbinstancetab[k];
		
			if(!new File("datasets/lptree-relearning_"+p.getClass().getSimpleName()+"/exemples-"+nbVar+"-"+nbinstances+".csv").exists())
			{	
				System.out.println("Génération du fichier d'exemples");
				try {
					fichier = new FileWriter("datasets/lptree-relearning_"+p.getClass().getSimpleName()+"/exemples-"+nbVar+"-"+nbinstances+".csv");
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
						
						HashMap<String, String> instance = arbre.getConfigurationAtRank(BigInteger.valueOf(rang));
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
			filename.add("datasets/lptree-relearning_"+p.getClass().getSimpleName()+"/exemples-"+nbVar+"-"+nbinstances);

			System.out.println("	Nombre d'instances : "+nbinstances);
			
			
			long[] rangs = new long[nbExemplesEvaluation];
			for(int i = 0; i < 10000; i++)
				rangs[i] = Math.round(p.inverse(rng.nextDouble()));
			
			for(int i = 0; i < algotab.length; i++)
			{
				ApprentissageGloutonLexStructure algo = algotab[i];
				algo.apprendDomainesVariables(vars);
				LexicographicStructure arbreAppris = algo.apprendDonnees(filename, true);
				
//				arbreAppris.affiche("Appris");
				
				System.out.println(algo.getHeuristiqueName()+" : "+comp.compare(arbreAppris, arbre, rangs, p));
			}
			System.out.println();
		}
	}
}
