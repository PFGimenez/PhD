import java.io.File;
import java.util.ArrayList;

import compilateur.LecteurCdXml;
import compilateur.SALADD;
import preferences.Apprentissage;

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
		String dataset = "renault_small_sans_contraintes_preferences";
		String prefixData = "datasets/"+dataset+"/";

		Apprentissage algo = new Apprentissage();
		
		String fichierContraintes = prefixData+"contraintes.xml";
		
		SALADD contraintes;
		contraintes = new SALADD();
		System.out.print("Compilation...");

		if(new File(fichierContraintes).exists())
		{
			contraintes.compilation(fichierContraintes, true, 4, 0, 0);
			contraintes.propagation();
		}
		else
		{
			// TODO: et s'il n'y a pas de contraintes?
			int z=0;
			z = 1/z;
		}
		
		System.out.println(" finie");

		LecteurCdXml lect=new LecteurCdXml();
		lect.lectureCSV(prefixData+"set0_exemples");
		lect.lectureCSVordre(prefixData+"set0_scenario");
		
		ArrayList<String> variables_tmp = new ArrayList<String>();
		variables_tmp.addAll(contraintes.getFreeVariables());
		
		algo.initOrder(contraintes);

		ArrayList<String> element = new ArrayList<String>();
		ArrayList<String> ordre = new ArrayList<String>();

		ArrayList<String> learning_set = new ArrayList<String>();
		long[] rangs = new long[10*lect.nbligne];

//		int i = 0;
		for(int i = 0; i < 10; i++)
		{
			for(int j = 0; j < 10; j++)
			{
				if(j != i)
					learning_set.add(prefixData+"set"+j+"_exemples_pour_compilation");
			}
			lect.lectureCSV(prefixData+"set"+i+"_exemples");
			lect.lectureCSVordre(prefixData+"set"+i+"_scenario");
			algo.apprendDonnees(learning_set);
			ordre.clear();
			for(int k = 0; k < lect.nbvar; k++)
				ordre.add(lect.var[k]);

//			int test = 0;
			for(int test=0; test<lect.nbligne; test++)
			{
				element.clear();
				
				for(int k=0; k<lect.nbvar; k++)
					element.add(lect.domall[test][k].trim());
				
				rangs[i*lect.nbligne+test] = algo.infereRang(element, ordre);
			}
		}
		System.out.println("Rang moyen : "+aggregMoyenne(rangs)+". Pourcentage du rang max : "+100.*aggregMoyenne(rangs)/algo.rangMax());
	}
	
	/**
	 * Aggrégation par moyenne
	 * @param rangs
	 * @return
	 */
	private static long aggregMoyenne(long[] rangs)
	{
		long somme = 0;
		for(int i = 0; i < rangs.length; i++)
			somme += rangs[i];
		return somme / rangs.length;
	}

}
