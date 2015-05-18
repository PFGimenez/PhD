import heuristique_contraintes.HeuristiqueContraintesRien;
import heuristique_variable.HeuristiqueVariableMCSinvPlusUn;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import methode_oubli.*;
import test_independance.*;
import algoreco.*;
import br4cp.LecteurCdXml;
import br4cp.SALADD;

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
 * Protocole d'évaluation
 * @author pgimenez
 *
 */

public class Recommandation {

	public static void main(String[] args)
	{
		final boolean verbose = false;
		
		AlgoReco recommandeur;
		
//		recommandeur = new AlgoRandom();			// Algorithme de choix aléatoire
//		recommandeur = new AlgoRBNaif("naif");		// Algorithme à réseau bayésien naïf
//		recommandeur = new AlgoRBNaif("tree");		// Algorithme à réseau bayésien naïf augmenté
//		recommandeur = new AlgoRB("tabu");			// Algorithme à réseau bayésien (tabu)
//		recommandeur = new AlgoRB("hc");			// Algorithme à réseau bayésien (hc)
		
		// Algorithmes à SLDD avec oubli par indépendance
		//
//		recommandeur = new AlgoSaladdOubli(new OubliParIndependance(new TestEcartMax()));	
//		recommandeur = new AlgoSaladdOubli(new OubliParIndependance(new TestEcartMax()));	
//		recommandeur = new AlgoSaladdOubli(new OubliParIndependance(new TestKhi2Statistique()));
//		recommandeur = new AlgoSaladdOubli(new OubliParIndependance(new TestG2Statistique()));
//		recommandeur = new AlgoSaladdOubli(new OubliParIndependance(new TestKhi2Correction()));
//		recommandeur = new AlgoSaladdOubli(new OubliParIndependance(new TestKhi2Max()));
//		recommandeur = new AlgoSaladdOubli(new OubliParIndependance(new TestG2()));
//		recommandeur = new AlgoSaladdOubli(new OubliParIndependance(new TestKhi2Max()));
//		recommandeur = new AlgoSaladdOubli(new OubliParIndependance(new Testmediane()));
//		recommandeur = new AlgoSaladdOubli(new OubliParIndependance(new Testl1mediane()));
//		recommandeur = new AlgoSaladdOubli(new OubliParIndependance(new TestSommeMediane()));
//		recommandeur = new AlgoSaladdOubli(new OubliParIndependance(new TestVariancePonderee()));
//		recommandeur = new AlgoSaladdOubli(new OubliParIndependance(new TestInformationMutuelle()));	

		recommandeur = new AlgoSaladdOubli(new OubliParEntropie2());
		
				// Algorithme à SLDD sans oubli
//		recommandeur = new AlgoSaladdOubli(new SansOubli());

		// Pas des algorithmes de recommandation mais de conversion vers XML
//		recommandeur = new XMLconverter();
//		recommandeur = new XMLconverter2();
		
		int echec = 0, succes = 0, trivial = 0;

		SALADD contraintes = new SALADD();
		contraintes.compilation("small.xml", true, new HeuristiqueVariableMCSinvPlusUn(), new HeuristiqueContraintesRien(), 0);
		contraintes.initialize();
		
		ArrayList<String> variables_tmp = new ArrayList<String>();
		variables_tmp.addAll(contraintes.getFreeVariables());
		recommandeur.initialisation(variables_tmp);

		ArrayList<String> memory=new ArrayList<String>();
		
		ArrayList<String> variables=new ArrayList<String>();
		ArrayList<String> solutions=new ArrayList<String>();
		ArrayList<String> ordre=new ArrayList<String>();
		
		HashMap<String,Integer[][]> matricesConfusion = new HashMap<String,Integer[][]>();
		
		for(String v: contraintes.getFreeVariables())
		{
			int domain = contraintes.getVar(v).domain;
			Integer[][] mat = new Integer[domain][domain];
			for(int i = 0; i < domain; i++)
				for(int j = 0; j < domain; j++)
					mat[i][j] = 0;
			matricesConfusion.put(v, mat);
		}

		LecteurCdXml lect=new LecteurCdXml();
		lect.lectureCSV("datasets/set0");
		lect.lectureCSVordre("datasets/scenario0");
		
		int[] oubliparpos = new int[lect.nbvar];
		int[] parpos = new int[lect.nbvar];
		int[] parposnb = new int[lect.nbvar];
		for(int i=0; i<parpos.length; i++){
			oubliparpos[i] = 0;
			parpos[i]=0;
			parposnb[i]=0;
		}
		
		recommandeur.apprendContraintes("small.xml");

		long debut = System.currentTimeMillis();

//		for(int i = 0; i < 10; i++)
		{
			int i = 0;
			ArrayList<String> learning_set = new ArrayList<String>();
			learning_set.add("datasets/set1");
/*			for(int j = 0; j < 10; j++)
			{
				if(j != i)
					learning_set.add("datasets/set"+j);
			}*/
			lect.lectureCSV("datasets/set"+i);
			lect.lectureCSVordre("datasets/scenario"+i);

			recommandeur.apprendDonnees(learning_set, i);
			
//			for(int test=0; test<lect.nbligne; test++)
			for(int test=0; test<lect.nbligne/10; test++)
			{
				memory.clear();
				variables.clear();
				solutions.clear();
				ordre.clear();
		
				for(int k=0; k<lect.nbvar; k++){
					variables.add(lect.var[k].trim());
					solutions.add(lect.domall[test][k].trim());
				}
				
				for(int k=0; k<lect.nbvar; k++){
					ordre.add(lect.ordre[test][k].trim());
				}
				
				recommandeur.oublieSession();
				
				for(int occu=0; occu<ordre.size(); occu++)
				{
					int k = variables.indexOf(ordre.get(occu));
					String v = variables.get(k);
					String solution = solutions.get(k);
					Set<String> values = contraintes.getCurrentDomainOf(v);
					
					if(values.size() == 1)
					{
						if(verbose)
							System.out.println("(trivial)");
						trivial++;
						recommandeur.setSolution(v, solution);
						contraintes.assignAndPropagate(v, solution);
						continue;
					}
					
					ArrayList<String> values_array = new ArrayList<String>();
					values_array.addAll(values);
					String r = recommandeur.recommande(v, values_array);
					if(recommandeur instanceof AlgoSaladdOubli)
						oubliparpos[occu] += ((AlgoSaladdOubli)recommandeur).getNbOublis();
					
					if(verbose)
						System.out.print(occu+" variables connues. "+values_array.size()+" possibles. Recommandation pour "+v+": "+r);

					recommandeur.setSolution(v, solution);
					contraintes.assignAndPropagate(v, solution);
					
					matricesConfusion.get(v)
						[contraintes.getVar(v).conv(solution)]
						[contraintes.getVar(v).conv(r)]++;
					if(solution.compareTo(r)==0)
					{
						if(verbose)
							System.out.println(" (succès)");
						succes++;
						parpos[occu]++;
					}
					else
					{
						if(verbose)
							System.out.println(" (échec, vraie valeur: "+solution+")");
						echec++;
					}
					parposnb[occu]++;
					if((echec+succes) % 10 == 0)
					{
						System.out.println(test*1000./lect.nbligne+"%");
						System.out.println("Taux succès: "+100.*succes/(echec+succes));
						System.out.println("Taux trivial: "+100.*trivial/(echec+succes+trivial));
						System.out.println("Durée: "+(System.currentTimeMillis()-debut));
						System.out.println("Succès par position: ");
						for(int l=0; l<ordre.size(); l++)
							System.out.print(((double)parpos[l])/parposnb[l]+", ");
						System.out.println();

						if(recommandeur instanceof AlgoSaladdOubli)
						{
							System.out.println("Oublis par position: ");
							for(int l=0; l<ordre.size(); l++)
								System.out.print(((double)oubliparpos[l])/parposnb[l]+", ");
							System.out.println();
						}

					}
				}
				
				contraintes.reinitialisation();

			}
		}

		for(String v: contraintes.getFreeVariables())
		{
			int domain = contraintes.getVar(v).domain;
			Integer[][] mat = matricesConfusion.get(v);
			int bon = 0, total = 0;
			for(int i = 0; i < domain; i++)
				for(int j = 0; j < domain; j++)
				{
					total += mat[i][j];
					if(i == j)
						bon += mat[i][j];
					System.out.println(v+" "+i+" "+j+" "+mat[i][j]);
				}
			System.out.println(v+" "+((double)bon)/total);
		}
		
		System.out.println("Succès par position: ");
		for(int occu=0; occu<ordre.size(); occu++)
			System.out.print(((double)parpos[occu])/parposnb[occu]+", ");
		System.out.println();

		if(recommandeur instanceof AlgoSaladdOubli)
		{
			System.out.println("Oublis par position: ");
			for(int occu=0; occu<ordre.size(); occu++)
				System.out.print(((double)oubliparpos[occu])/parposnb[occu]+", ");
			System.out.println();
		}

		System.out.println("Au final: ");
		System.out.println("	Taux succès: "+100.*succes/(echec+succes));
		System.out.println("	Taux trivial: "+100.*trivial/(echec+succes+trivial));

		System.out.println("Durée: "+(System.currentTimeMillis()-debut));
	}

}
