package utilitaires;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import recommandation.methode_oubli.OubliParIndependance;
import recommandation.old.AlgoSaladdOubli;
import compilateur.test_independance.TestEcartMax;


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
 * Génération d'ordre sous en format CSV. La génération est équiprobable
 * @author Pierre-François Gimenez
 *
 */

public class GenereOrdreCSV {

	public static void main(String[] args)
	{	
		Random randomgenerator = new Random();
		int nbGenere = 1000;
		String dataset = "renault_big_court";
		String prefixData = "datasets/"+dataset+"/";
		
		AlgoSaladdOubli generateur;

		ArrayList<String> variables = new ArrayList<String>();
		generateur = new AlgoSaladdOubli(new OubliParIndependance(50, new TestEcartMax()),prefixData);
		ArrayList<String> chemin = new ArrayList<String>();
		chemin.add(prefixData+"bigHistory");

		generateur.apprendDonnees(chemin, 0, true);
		variables = new ArrayList<String>();
		variables.addAll(generateur.getVariables());
		System.out.println("Nb variables : "+variables.size());

		variables.remove("v27");
		variables.remove("v28");
		variables.remove("v29");
		variables.remove("v30");
		variables.remove("v31");
		
		variables.remove("v34");
		variables.remove("v35");

		variables.remove("v43");
		
		variables.remove("v74");
		variables.remove("v75");
		variables.remove("v76");
		variables.remove("v77");
		variables.remove("v78");
		
		variables.remove("v81");
		
		variables.remove("v90");
		variables.remove("v91");
		
		variables.remove("v98");
		
		variables.remove("v104");
		variables.remove("v105");
		variables.remove("v106");
		variables.remove("v107");
		variables.remove("v108");
		variables.remove("v109");
		variables.remove("v110");
		variables.remove("v111");
		variables.remove("v112");
		variables.remove("v113");
		variables.remove("v114");
		variables.remove("v115");
		variables.remove("v116");
		variables.remove("v117");
		variables.remove("v118");
		variables.remove("v119");
		variables.remove("v120");
		variables.remove("v121");

		variables.remove("v189");

		int nbVar = variables.size();

		System.out.println("Génération");
		for(int k = 0; k < 10; k++)
		{
			try {
				FileWriter fichier = new FileWriter(prefixData+"set"+k+"_scenario.csv");
				BufferedWriter output = new BufferedWriter(fichier);
				for(int test=0; test<nbGenere; test++)
				{
					System.out.println(k*nbGenere+test);

					if((test % 10) == 0)
						System.out.println("ensemble "+k+", génération "+test);
					
					boolean[] dejaTire = new boolean[nbVar];
					for(int i = 0; i < nbVar; i++)
						dejaTire[i] = false;
									
					int n;
					for(int i = 0; i < nbVar; i++)
					{
						do {
							n = randomgenerator.nextInt(nbVar);
						} while(dejaTire[n]);
						output.write(variables.get(n));
						if(i < nbVar - 1)
							output.write(",");
						else
							output.newLine();
						dejaTire[n] = true;
					}
				}
				output.close();
			} catch (IOException e) {
				e.printStackTrace();
			}

		}
	}

}
