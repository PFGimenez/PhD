package utilitaires;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import JSci.maths.statistics.*;
import preferences.GenereLexTree;
import preferences.LinearDistribution;
import preferences.compare.Comparison;
import preferences.compare.SpearmanComparison;
import preferences.completeTree.ApprentissageGloutonLexStructure;
import preferences.completeTree.ApprentissageGloutonLexTree;
import preferences.completeTree.LexicographicStructure;
import preferences.completeTree.LexicographicTree;
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
		int nbVar = 20;
		int nbinstances = 1000;
		
//		ApprentissageGloutonLexStructure algo = new ApprentissageGloutonLexTree(300, 20, new VieilleHeuristique(new HeuristiqueEntropieNormalisee()));
		ApprentissageGloutonLexStructure algo = new ApprentissageGloutonLexTree(300, 20, new HeuristiqueDuel());
//		ProbabilityDistribution p = new LinearDistribution(Math.pow(2, nbVar), 0);
		ProbabilityDistribution p = new GeometricDistribution(0.01);
		Comparison comp = new SpearmanComparison(p);
		
		LexicographicTree arbre;
		System.out.println("Chargement en cours…");
		arbre = (LexicographicTree) LexicographicTree.load("datasets/lptree-relearning_"+p.getClass().getSimpleName()+"/LPtree_for_generation-"+nbVar);
		if(arbre == null)
		{
			System.out.println("Chargement échoué");
			System.out.println("Génération en cours…");
			arbre = GenereLexTree.genere(nbVar);
			System.out.println("Génération terminée");
			arbre.save("datasets/lptree-relearning_"+p.getClass().getSimpleName()+"/LPtree_for_generation-"+nbVar);
		}
		else
			System.out.println("Chargement terminé");
		
		Random rng = new Random();
		FileWriter fichier;
		BufferedWriter output;

		try {
			fichier = new FileWriter("datasets/lptree-relearning_"+p.getClass().getSimpleName()+"/set0_exemples.csv");
			output = new BufferedWriter(fichier);
			for(int i = 0; i < nbVar-1; i++)
				output.write("V"+i+",");
			output.write("V"+(nbVar-1));
			output.newLine();

			for(int i = 0; i < nbinstances; i++)
			{
				if(i%10==0)
					System.out.println(i);
				long rang = Math.round(p.inverse(rng.nextDouble()));
				
				System.out.println(rang);
				// rang hors de portée
				if(rang >= arbre.getRangMax().longValue())
				{
					i--;
					continue;
				}
				
				HashMap<String, String> instance = arbre.getConfigurationAtRank(BigInteger.valueOf(rang));
				for(int j = 0; j < nbVar-1; j++)
					output.write(instance.get("V"+j)+",");
				output.write(instance.get("V"+(nbVar-1)));
				output.newLine();
			}
			output.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		ArrayList<String> filename = new ArrayList<String>();
		filename.add("datasets/lptree-relearning_"+p.getClass().getSimpleName()+"/set0_exemples");
		algo.apprendDomainesVariables(nbVar, 2);
		LexicographicStructure arbreAppris = algo.apprendDonnees(filename, true);
		
		arbreAppris.affiche("Appris");
		
		System.out.println("Évalution en cours…");
		System.out.println(comp.getClass().getSimpleName()+" : "+comp.compare(arbreAppris, arbre, 10000));
	}
}
