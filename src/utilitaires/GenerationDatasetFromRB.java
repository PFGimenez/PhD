package utilitaires;
import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Random;
import java.util.Set;

import recommandation.*;
import compilateur.SALADD;


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
 * Génération de données par simulation de session de configuration.
 * @author pgimenez
 *
 */

public class GenerationDatasetFromRB {

	public static void main(String[] args) throws Exception
	{	
		final boolean contraintesPresentes = false;
		Random randomgenerator = new Random();
		int nbGenere = 1000;
		String dataset = "renault_small_header_contraintes";
		String prefixData = "datasets/"+dataset+"/";
		
		AlgoRBJayes generateur = new AlgoRBJayes(prefixData);
		
		String fichierContraintes = prefixData+"randomCSP-0.2-0.1.xml";

		SALADD contraintes = new SALADD();

		if(new File(fichierContraintes).exists())
		{
			System.out.println("Apprentissage des contraintes");
			contraintes.compilation(fichierContraintes, true, 4, 0, 0, true);
			System.out.println("fini");
			contraintes.propagation();
		}
		else
			throw new Exception();
		
		ArrayList<String> variables = new ArrayList<String>();
		variables.addAll(contraintes.getFreeVariables());
		System.out.println("Nb variables : "+variables.size());
		System.out.println("Apprentissage du réseau bayésien");
		generateur.apprendDonnees(null, 0, true);
		variables = new ArrayList<String>();
		variables.addAll(generateur.getVariables());
		System.out.println("Nb variables : "+variables.size());
		
		int nbVar = variables.size();

		System.out.println("Génération");
		for(int k = 0; k < 2; k++)
		{
		    PrintWriter writer = new PrintWriter("set"+k+"ABWABWA_exemples", "UTF-8");

    		writer.print(variables.get(0));
    		for(int i = 1; i < variables.size(); i++)
	    		writer.print(","+variables.get(i));
		    String[] ligne = new String[variables.size()];
		    
			for(int test=0; test<nbGenere; test++)
			{
				if((test % 10) == 0)
					System.out.println("ensemble "+k+", génération "+test);
				
				generateur.oublieSession();
			    writer.println();
				
				boolean[] dejaTire = new boolean[nbVar];
				for(int i = 0; i < nbVar; i++)
					dejaTire[i] = false;
				

				// L'ordre des variables n'influence pas la distribution générée
				// De plus, le SLDD compte mieux si les variables conditionnées sont regroupées
				
				int n;
				ArrayList<String> ordre = new ArrayList<String>();
				for(int i = 0; i < nbVar; i++)
				{
					do {
						n = randomgenerator.nextInt(nbVar);
					} while(dejaTire[n]);
					ordre.add(variables.get(n));
					dejaTire[n] = true;
				}
								
//				System.out.println("Tirage fait");
				for(int occu=0; occu<ordre.size(); occu++)
				{
					String v = ordre.get(occu), r;
					Set<String> values = contraintes.getCurrentDomainOf(v);
					
					ArrayList<String> values_array = new ArrayList<String>();
					values_array.addAll(values);
					if(values.size() == 1)
						r = values_array.get(0);
					else
						r = generateur.recommandeGeneration(v, values_array);

					ligne[variables.indexOf(v)] = r;
					
					generateur.setSolution(v, r);
					if(contraintesPresentes)
						contraintes.assignAndPropagate(v, r);
//					System.out.println(contraintes.getCurrentDomainOf(v).size()+" = 1");
				}
				
				writer.print(ligne[0]);
			    for(int i = 1; i < variables.size(); i++)
		    		writer.print(","+ligne[i]);

				contraintes.reinitialisation();
				contraintes.propagation();
			}
			writer.close();
		}
	}

}
