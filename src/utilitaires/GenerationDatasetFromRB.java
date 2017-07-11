package utilitaires;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Random;
import java.util.Set;

import org.eclipse.recommenders.jayes.util.NumericalInstabilityException;

import recommandation.*;
import compilateur.SALADD;
import compilateurHistorique.DatasetInfo;
import contraintes.RandomCSP;


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
 * Génération de données par simulation de session de configuration avec contraintes.
 * @author Pierre-François Gimenez
 *
 */

public class GenerationDatasetFromRB {

	public static void main(String[] args) throws Exception
	{	
		Random randomgenerator = new Random();
		int nbGenere = 1000;
		int nbDataset = 3;
		String prefixData = "experiments/exp9/";
		String RBfile = prefixData+"alarm.xml";
		AlgoRBJayes generateur = new AlgoRBJayes();
				
		System.out.println("Apprentissage du réseau bayésien");
		generateur.apprendRB(RBfile);
		ArrayList<String> variables = new ArrayList<String>();
		variables.addAll(generateur.getVariables());
		System.out.println("Nb variables : "+variables.size());
		
		int nbVar = variables.size();
		double connectivite = 0.1, durete;
		DatasetInfo datasetinfo = new DatasetInfo(RBfile);

		for(int s = 0; s < nbDataset; s++)
		{
			durete = s*0.05;
			for(int c = 0; c < 5; c++)
			{
				try {				

					String fichierContraintes = prefixData+"randomCSP-"+s+"_"+c+".xml";
					boolean exception;
					RandomCSP csp = null;
					SALADD contraintes = null;
					do {
						try {
							exception = false;
							csp = new RandomCSP(datasetinfo.vars, 2, connectivite, durete);
							System.out.println("Génération du CSP "+fichierContraintes);
				
							csp.save(fichierContraintes);
							
							contraintes = new SALADD();
				
							System.out.print("Apprentissage des contraintes… ");
							contraintes.compilation(fichierContraintes, true, 4, 0, 0, true);
							System.out.println("fini");
							contraintes.propagation();
						} catch(Exception e)
						{
							e.printStackTrace();
							System.out.println("Erreur contraintes : on relance");
							exception = true;
//							return;
						}
					} while(exception);
					
					System.out.println("Génération");
					for(int k = 0; k < 10; k++)
					{
						String file = prefixData+"csp"+s+"_"+c+"_set"+k+"_exemples.csv";
						System.out.println("Fichier "+file);
					    PrintWriter writer = new PrintWriter(file, "UTF-8");
			
			    		writer.print(variables.get(0));
			    		for(int i = 1; i < variables.size(); i++)
				    		writer.print(","+variables.get(i));
					    String[] ligne = new String[variables.size()];
					    
						for(int test=0; test<nbGenere; test++)
						{
							generateur.oublieSession();
							
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
		//						System.out.println(values+" ---- "+contraintes.getDomainOf(v)+" : "+r);
		
								ligne[variables.indexOf(v)] = r;
								
								generateur.setSolution(v, r);
								contraintes.assignAndPropagate(v, r);
							}
							
						    writer.println();
							writer.print(ligne[0]);
						    for(int i = 1; i < variables.size(); i++)
					    		writer.print(","+ligne[i]);
						    
							if(((test+1) % 100) == 0)
								System.out.println("Générés : "+(test+1));
							
							contraintes.reinitialisation();
							contraintes.propagation();
							
						}
						writer.close();
					}

				} catch(NumericalInstabilityException e)
				{
					System.out.println(e.getMessage());
					c--;
				}
			}
				
		}
	}

}
