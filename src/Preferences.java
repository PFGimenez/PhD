import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;

import compilateur.LecteurCdXml;
import preferences.completeTree.ApprentissageGloutonLexStructure;
import preferences.completeTree.ApprentissageGloutonLexTree;
import preferences.completeTree.LexicographicStructure;
import preferences.heuristiques.*;
import preferences.heuristiques.simple.HeuristiqueProbaMaxMod;

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
 * Protocole d'évaluation de l'apprentissage de préférence
 * @author pgimenez
 *
 */

public class Preferences
{
	public static void main(String[] args)
	{
		
		String dataset = "renault_medium_csv";
		boolean entete = dataset.contains("renault") || dataset.contains("lptree");
		String prefixData = "datasets/"+dataset+"/";
		int nbIter = 1;
		
//		ApprentissageLexStructure algo = new ApprentissageLexTree(100, 100, new HeuristiqueAutreEntropie());
//		ApprentissageLexStructure algo = new ApprentissageLexTree(100, 100, new HeuristiqueEntropieNormalisee());
//		ApprentissageLexStructure algo = new ApprentissageLexTree(100, 100, new HeuristiqueEntropie());
		ApprentissageGloutonLexStructure algo = new ApprentissageGloutonLexTree(100, 100, new VieilleHeuristique(new HeuristiqueProbaMaxMod()));
//		ApprentissageLexStructure algo = new ApprentissageLexTree(100, 100, new HeuristiqueProbaMax());
//		ApprentissageLexStructure algo = new ApprentissageLexTree(100, 100, new HeuristiqueProbaMin());
//		ApprentissageLexStructure algo = new ApprentissageLexTree(100, 100, new HeuristiqueRangOptimiste());
//		ApprentissageLexStructure algo = new ApprentissageLexTree(100, 100, new HeuristiqueNbMod());
//		ApprentissageLexStructure algo = new ApprentissageLexTree(100, 100, new HeuristiqueRandom());
		
		System.out.println(dataset+" entete: "+entete);
		LecteurCdXml lect=new LecteurCdXml();
		lect.lectureCSV(prefixData+"set0_exemples", entete);
		
		ArrayList<String> element = new ArrayList<String>();
		ArrayList<String> ordre = new ArrayList<String>();

		ArrayList<String> learning_set = new ArrayList<String>();
		BigInteger[] rangs = new BigInteger[2*lect.nbligne];
//		int granularite = 100;
//		int[] nbTrouves = new int[granularite];

		for(int i = 0; i < 2; i++)
			learning_set.add(prefixData+"set"+i+"_exemples");
		algo.apprendDomainesVariables(learning_set, entete);
		
		System.out.println("Nb instances : "+lect.nbligne);
		
		BigInteger scoreMediane = BigInteger.ZERO;
		BigInteger scoreMoyenne = BigInteger.ZERO;
		for(int l = 0; l < nbIter; l++)
		{
			if(l % 10 == 0 && nbIter > 1)
				System.out.println(100.*l/nbIter);
			for(int i = 0; i < 2; i++)
			{
				learning_set.clear();
				for(int j = 0; j < 2; j++)
				{
					if(j != i)
						learning_set.add(prefixData+"set"+j+"_exemples");
				}
				lect.lectureCSV(prefixData+"set"+i+"_exemples", entete);
				
				if(nbIter == 1)
					System.out.println("Apprentissage…");
		
				LexicographicStructure struct = algo.apprendDonnees(learning_set, entete, 10000);
				
				if(nbIter == 1)
					System.out.println("Apprentissage terminé");
				
//				algo.affiche("");
				
				for(int test=0; test<lect.nbligne; test++)
				{
					ordre.clear();
					for(int k = 0; k < lect.nbvar; k++)
						ordre.add(lect.var[k].trim());
					
					element.clear();
					for(int k=0; k<lect.nbvar; k++)
						element.add(lect.domall[test][k].trim());
					
					BigInteger rang = struct.infereRang(element, ordre);
					rangs[i*lect.nbligne+test] = rang;
//					System.out.println(rang);
//					nbTrouves[(int) (granularite*rang/algo.rangMax())]++;
				}
				
//				LexicographicTree modele = (LexicographicTree) LexicographicStructure.load(prefixData+"LPtree_for_generation");
//				if(modele != null)
//				{
//					System.out.println("Ressemblance : "+struct.getRessemblance(modele));
//				}

			}
			scoreMediane = scoreMediane.add(aggregMediane(rangs));
			scoreMoyenne = scoreMoyenne.add(aggregMoyenne(rangs));
			
		}
//		for(int i = 0; i < granularite; i++)
//			System.out.println("Entre "+100/granularite*i+"% et "+100/granularite*(i+1)+"% : "+nbTrouves[i]);
//			System.out.print(", "+nbTrouves[i]);
//		System.out.println();
		scoreMediane = scoreMediane.divide(BigInteger.valueOf(nbIter));
		scoreMoyenne = scoreMoyenne.divide(BigInteger.valueOf(nbIter));;
		System.out.println("Rang max : "+algo.rangMax());
		System.out.println("Rang médian : "+scoreMediane+". Pourcentage du rang max : "+(scoreMediane.multiply(BigInteger.valueOf(100000000000000000L)).divide(algo.rangMax())).longValue()/1000000000000000.);
		System.out.println("Rang moyen : "+scoreMoyenne+". Pourcentage du rang max : "+(scoreMoyenne.multiply(BigInteger.valueOf(100000000000000000L)).divide(algo.rangMax())).longValue()/1000000000000000.);
	}
	
	/**
	 * Aggrégation par moyenne
	 * @param rangs
	 * @return
	 */
	private static BigInteger aggregMoyenne(BigInteger[] rangs)
	{
		BigInteger somme = BigInteger.ZERO;
		for(int i = 0; i < rangs.length; i++)
		{
			somme = somme.add(rangs[i]);
//			if(somme < 0)
//				throw new ArithmeticException();
		}
//		System.out.println("Somme : "+somme);
		return somme.divide(BigInteger.valueOf(rangs.length));
	}

	private static BigInteger aggregMediane(BigInteger[] rangs)
	{
		Arrays.sort(rangs);
		BigInteger out = rangs[rangs.length/2];
//		System.out.println("Rang median : "+out);
		return out;
	}
	
}
