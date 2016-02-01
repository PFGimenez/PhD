import java.util.ArrayList;
import java.util.Arrays;

import compilateur.LecteurCdXml;
import heuristiques.*;
import preferences.*;

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
		String dataset = "champi";
		boolean entete = false;
		String prefixData = "datasets/"+dataset+"/";

//		ApprentissageLexStructure algo = new ApprentissageLexOrder();
//		ApprentissageLexStructure algo = new ApprentissageLexTree(20, 100, new HeuristiqueEntropie());
//		ApprentissageLexStructure algo = new ApprentissageLexTree(20, 100, new HeuristiqueProbaMax());
//		ApprentissageLexStructure algo = new ApprentissageLexTree(20, 100, new HeuristiqueProbaMin());
//		ApprentissageLexStructure algo = new ApprentissageLexTree(20, 100, new HeuristiqueNbMod());
		ApprentissageLexStructure algo = new ApprentissageLexTree(20, 100, new HeuristiqueProbaMaxMod());
		
		LecteurCdXml lect=new LecteurCdXml();
		lect.lectureCSV(prefixData+"set0_exemples", entete);
		
		ArrayList<String> element = new ArrayList<String>();
		ArrayList<String> ordre = new ArrayList<String>();

		ArrayList<String> learning_set = new ArrayList<String>();
		long[] rangs = new long[2*lect.nbligne];

		for(int i = 0; i < 2; i++)
		{
			learning_set.add(prefixData+"set"+i+"_exemples");
		}
		
		algo.apprendDomainesVariables(learning_set, entete);
		
		for(int i = 0; i < 2; i++)
		{
			learning_set.clear();
			for(int j = 0; j < 2; j++)
			{
				if(j != i)
					learning_set.add(prefixData+"set"+j+"_exemples");
			}
			lect.lectureCSV(prefixData+"set"+i+"_exemples", entete);

			algo.apprendDonnees(learning_set, entete);

			algo.affiche();
			
			for(int test=0; test<lect.nbligne; test++)
			{
				ordre.clear();
				for(int k = 0; k < lect.nbvar; k++)
					ordre.add(lect.var[k].trim());
				
				element.clear();
				for(int k=0; k<lect.nbvar; k++)
					element.add(lect.domall[test][k].trim());
				
				rangs[i*lect.nbligne+test] = algo.infereRang(element, ordre);
//				System.out.println(rangs[i*lect.nbligne+test]);
			}
		}
		double score = aggregMediane(rangs);
		System.out.println("Rang médian : "+score+". Pourcentage du rang max : "+100.*score/algo.rangMax());
		score = aggregMoyenne(rangs);
		System.out.println("Rang moyen : "+score+". Pourcentage du rang max : "+100.*score/algo.rangMax());
		score = aggregMax(rangs);
		System.out.println("Rang max : "+score+". Pourcentage du rang max : "+100.*score/algo.rangMax());
		score = aggregMin(rangs);
		System.out.println("Rang min : "+score+". Pourcentage du rang max : "+100.*score/algo.rangMax());
	}
	
	/**
	 * Aggrégation par moyenne
	 * @param rangs
	 * @return
	 */
	private static double aggregMoyenne(long[] rangs)
	{
		double somme = 0.;
		for(int i = 0; i < rangs.length; i++)
		{
			somme += rangs[i];			
			if(somme < 0)
				throw new ArithmeticException();
		}
//		System.out.println("Somme : "+somme);
		return somme / rangs.length;
	}

	private static long aggregMediane(long[] rangs)
	{
		Arrays.sort(rangs);
		long out = rangs[rangs.length/2];
//		System.out.println("Rang median : "+out);
		return out;
	}
	
	private static long aggregMax(long[] rangs)
	{
		long max = rangs[0];
		for(int i = 1; i < rangs.length; i++)
			max = Math.max(max, rangs[i]);
//		System.out.println("Rang max atteint : "+max);
		return max;
	}

	private static long aggregMin(long[] rangs)
	{
		long min = rangs[0];
		for(int i = 1; i < rangs.length; i++)
			min = Math.min(min, rangs[i]);
//		System.out.println("Rang min atteint : "+min);
		return min;
	}
	
}
