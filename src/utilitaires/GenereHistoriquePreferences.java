package utilitaires;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Random;

import JSci.maths.statistics.GeometricDistribution;
import JSci.maths.statistics.ProbabilityDistribution;
import preferences.GenereLexTree;
import preferences.LexicographicStructure;
import preferences.LexicographicTree;

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
		int nbinstances = 10000;
		LexicographicTree arbre;
		System.out.println("Chargement en cours…");
		arbre = (LexicographicTree) LexicographicTree.load("datasets/lptree-relearning_geometric/LPtree_for_generation-"+nbVar);
		if(arbre == null)
		{
			System.out.println("Chargement échoué");
			System.out.println("Génération en cours…");
			arbre = GenereLexTree.genere(nbVar);
			System.out.println("Génération terminée");
			arbre.save("datasets/lptree-relearning_geometric/LPtree_for_generation-"+nbVar);
		}
		arbre.affiche("");
		ProbabilityDistribution p = new GeometricDistribution(0.1);
		Random rng = new Random();
		FileWriter fichier;
		BufferedWriter output;

		try {
			fichier = new FileWriter("datasets/lptree-relearning_geometric/set0_exemples.csv");
			output = new BufferedWriter(fichier);
			for(int i = 0; i < nbVar-1; i++)
				output.write("V"+i+",");
			output.write("V"+(nbVar-1));
			output.newLine();

			for(int i = 0; i < nbinstances; i++)
			{
				if(i%10==0)
					System.out.println(i);
				long rang = (long) p.inverse(rng.nextDouble());
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

	}
}
