package utilitaires;
import java.io.File;
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

public class Generation {

	public static void main(String[] args) throws Exception
	{	
		final boolean contraintesPresentes = false;
//		long dureeTotale = 0;
//		long dureeTmp;
//		int nbFait = 0;
		Random randomgenerator = new Random();
		int nbGenere = 1000;
		String dataset = "renault_small_sans_contraintes_preferences";
//		String dataset = "renault_small";
		String prefixData = "datasets/"+dataset+"/";
//		String cheminBif = prefixData+"rb.xml";
		
		AlgoRBJayes generateur;
		XMLcreator scenario_xml;
		XMLcreator exemple_xml;

		//		generateur = new AlgoRB(cheminBif);			// Algorithme à réseau bayésien (hc)
		generateur = new AlgoRBJayes(prefixData);
//		generateur = new AlgoSaladdOubli(new OubliParDSeparation(100, new TestEcartMax(), prefixData));
//		generateur = new AlgoSaladdOubli(new OubliParIndependance(50, new TestEcartMax()));
		scenario_xml = new XMLcreator(prefixData, true);
		exemple_xml = new XMLcreator(prefixData, false);
		
		String fichierContraintes = prefixData+"contraintes.xml";

		SALADD contraintes = new SALADD();
//		if(new File(prefixData+"saveContraintes.sldd.dot").exists())
//			contraintes.chargement(prefixData+"saveContraintes.sldd", 0);
		/*else*/ if(new File(fichierContraintes).exists())
		{
			System.out.println("Apprentissage des contraintes");
//			contraintes.compilation(fichierContraintes, true, 4, 0, 0);
			contraintes.compilation(fichierContraintes, true, 4, 0, 0);
			System.out.println("fini");
			contraintes.propagation();
//			contraintes.save(prefixData+"saveContraintes.sldd");
		}
		else
			throw new Exception();
		
		ArrayList<String> variables = new ArrayList<String>();
		variables.addAll(contraintes.getFreeVariables());
		System.out.println("Nb variables : "+variables.size());
//		generateur.initialisation(variables);
		ArrayList<String> chemin = new ArrayList<String>();
		chemin.add(prefixData+"bigHistory");
//		if(new File(prefixData+"saveHistorique.sldd.dot").exists())
//			generateur.charge(prefixData+"saveHistorique.sldd");
//		else
		{
			System.out.println("Apprentissage de l'historique");
			generateur.apprendDonnees(chemin, 0, true);
			variables = new ArrayList<String>();
			variables.addAll(generateur.getVariables());
			System.out.println("Nb variables : "+variables.size());

//			generateur.save(prefixData+"saveHistorique.sldd");
		}
		

/*
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
*/
		int nbVar = variables.size();

		System.out.println("Génération");
		for(int k = 0; k < 10; k++)
		{
			scenario_xml.open(k);
			exemple_xml.open(k);
			
			for(int test=0; test<nbGenere; test++)
			{
				if((test % 10) == 0)
					System.out.println("ensemble "+k+", génération "+test);
				
				generateur.oublieSession();
				scenario_xml.debutSession();
				exemple_xml.debutSession();
				
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
				
				
//				for(int occu=0; occu<variables.size(); occu++)
//				{
//					String v = variables.get(occu);
//					Set<String> values = contraintes.getCurrentDomainOf(v);
//					System.out.println("INIT : "+v+" : "+values.size());
//				}
				
//				System.out.println("Tirage fait");
				for(int occu=0; occu<ordre.size(); occu++)
				{
					String v = ordre.get(occu), r;
					Set<String> values = contraintes.getCurrentDomainOf(v);
					
					ArrayList<String> values_array = new ArrayList<String>();
					values_array.addAll(values);
//					System.out.println("nb valeurs possibles : "+values.size()+" pour la variable "+v);
					if(values.size() == 1)
						r = values_array.get(0);
					else
					{
//						dureeTmp = System.currentTimeMillis();
						r = generateur.recommandeGeneration(v, values_array);
//						dureeTotale += System.currentTimeMillis() - dureeTmp;
//						nbFait++;
//						System.out.println("Durée moyenne : "+(System.currentTimeMillis() - dureeTmp));
					}

					scenario_xml.setPossibles(values_array);
					scenario_xml.setSolution(v, r);
					exemple_xml.setSolution(v, r);
					
					generateur.setSolution(v, r);
					generateur.setSolution(v, r);
					if(contraintesPresentes)
						contraintes.assignAndPropagate(v, r);
//					System.out.println(contraintes.getCurrentDomainOf(v).size()+" = 1");
				}
				scenario_xml.finSession();
				exemple_xml.finSession();

				contraintes.reinitialisation();
				contraintes.propagation();
			}
			scenario_xml.close();
			exemple_xml.close();
		}
	}

}
