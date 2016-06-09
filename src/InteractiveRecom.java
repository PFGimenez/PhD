import java.io.File;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.Set;

import compilateur.LecteurCdXml;
import compilateur.SALADD;
import compilateurHistorique.Variable;
import preferences.*;
import preferences.heuristiques.HeuristiqueEntropieNormalisee;
import recommandation.*;


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
 * Recommendation interactive. Utilisé par le démonstrateur
 * @author pgimenez
 *
 */

public class InteractiveRecom {

	// PARAM 1 : algo
	// PARAM 2 : dataset
	// PARAM 3 : entete (optionnel, par défaut false) -e
	
	public static void main(String[] args)
	{
/*
		args = new String[3];

		// Dataset
		args[0] = "naif";

		// Dataset
		args[1] = "renault_small_csv";
		
		// Entete
		args[2] = "-e";*/
		
		if(args.length < 1)
		{
			System.err.println("Interactive recommendation.");
			System.err.println("Usage : algo dataset [-e]");
			return;
		}

		final boolean entete = args.length > 2 && args[2].toLowerCase().contains("-e");

		final String dataset = args[1].trim();
		final String prefixData = "datasets/"+dataset+"/";

		if(!new File(prefixData).exists())		
		{
			System.err.println("Dataset inconnu : "+dataset);
			return;
		}
		
		boolean contraintesPresentes =  dataset.contains("contraintes") ;

		AlgoReco recommandeur;
		
		if(args[0].toLowerCase().contains("drc"))
			recommandeur = new AlgoDRC(10, 1);
		else if(args[0].toLowerCase().contains("rc"))
			recommandeur = new AlgoDRC(-1, 1);
		else if(args[0].toLowerCase().contains("jointree"))
			recommandeur = new AlgoRBJayes(prefixData);
		else if(args[0].toLowerCase().contains("v-maj"))
			recommandeur = new AlgoVoisinsMajorityVoter(199);
		else if(args[0].toLowerCase().contains("v-pop"))
			recommandeur = new AlgoVoisinsMostPopular(20);
		else if(args[0].toLowerCase().contains("v-naive"))
			recommandeur = new AlgoVoisinsNaive(20);
		else if(args[0].toLowerCase().contains("naif"))
			recommandeur = new AlgoRBNaif();
		else if(args[0].toLowerCase().contains("lextree"))
			recommandeur = new AlgoLexTree(new ApprentissageLexTree(300, 10, new HeuristiqueEntropieNormalisee()), prefixData);
		else
		{
			System.err.println("Algo inconnu : "+args[0]);
			return;
		}

		String fichierContraintes = prefixData+"contraintes.xml";
		
		SALADD contraintes, contraintes2;
		contraintes = null;
		contraintes2 = null;		

		if(contraintesPresentes && new File(fichierContraintes).exists())			
		{
			contraintes = new SALADD();
			contraintes.compilation(fichierContraintes, true, 4, 0, 0);
			contraintes.propagation();
			contraintes2 = new SALADD();
			contraintes2.compilation(fichierContraintes, true, 4, 0, 0);
			contraintes2.propagation();
		}
		else if(contraintesPresentes)
		{
			System.err.println("Pas de fichier de contraintes!");
			System.err.println("Veuillez relancez avec \"contraintesPresentes = false\"");
			return;
		}
	
		LecteurCdXml lect=new LecteurCdXml();
		// On lit le premier fichier afin de récupére le nombre de variables
		lect.lectureCSV(prefixData+"set0_exemples", entete);
		
		recommandeur.apprendContraintes(contraintes2);

		ArrayList<String> learning_set = new ArrayList<String>();

		for(int i = 0; i < 2; i++)
			learning_set.add(prefixData+"set"+i+"_exemples");

		Variable[] vars = initVariables(learning_set, entete);
		recommandeur.initHistorique(learning_set, entete);		
		
		if(contraintesPresentes)
		{
			contraintes.reinitialisation();
			contraintes.propagation();
		}

		recommandeur.apprendDonnees(learning_set, 2, entete);
		System.err.println("Test de "+recommandeur+" sur "+dataset);
		Scanner sc = new Scanner(System.in);
		for(int i = 0; i < vars.length-1; i++)
			System.out.print(vars[i].name+",");
		System.out.println(vars[vars.length-1].name);

		while(true)
		{
			recommandeur.oublieSession();
			//System.out.println("intro : "+(System.currentTimeMillis() - avant));
			
			while(true)
			{
				String input = sc.nextLine().trim();
				
				// Démarre une nouvelle session
				if(input.contains("reinit"))
				{
					System.err.println("Nouvelle session");
					break;
				}
				
				// Arrête le programme
				else if(input.contains("exit"))
				{
					sc.close();
					recommandeur.termine();
					System.err.println("Arrêt");
					return;
				}
				
				// Demande une recommandation
				else if(input.contains("reco"))
				{
					String v = sc.nextLine().trim();
					Set<String> values = null;
					int nbModalites = 0;
					ArrayList<String> values_array = new ArrayList<String>();

					if(contraintes != null)
					{
						values = contraintes.getCurrentDomainOf(v);						
						nbModalites = values.size();
						if(nbModalites == 0)
						{
							System.err.println("Aucune valeur possible !");
							int z = 0;
							z = 1/z;
						}
						values_array.addAll(values);
					}
					else
					{
						for(int i = 0; i < vars.length; i++)
							if(vars[i].name.equals(v))
							{
								values_array.addAll(vars[i].values);
								break;
							}
					}

					String r = recommandeur.recommande(v, values_array);
					
					values_array.remove(r);
					
					// On retourne la recommandation et les autres valeurs possibles
					System.out.println(r);
					for(int i = 0; i < values_array.size()-1; i++)
						System.out.print(values_array.get(i)+",");
					System.out.println(values_array.get(values_array.size()-1));
					
				}
				else if(input.contains("set"))
				{
					String var = sc.nextLine().trim();
					String solution = sc.nextLine().trim();

					recommandeur.setSolution(var, solution);
					
					if(contraintesPresentes)
						contraintes.assignAndPropagate(var, solution);
				}
				else
				{
					System.err.println("Commande inconnue : "+input);
				}
			}
			if(contraintesPresentes)
			{
				contraintes.reinitialisation();
				contraintes.propagation();
			}
		}

	}

	/**
	 * Initialise les valeurs et les domaines des variables.
	 * IL N'Y A PAS D'APPRENTISSAGE SUR LES VALEURS
	 * @param filename
	 * @param entete
	 * @return
	 */
	private static Variable[] initVariables(ArrayList<String> filename, boolean entete)
	{
		// Vérification de toutes les valeurs possibles pour les variables
		Variable[] vars = null;
		LecteurCdXml lect = null;
		
		int nbvar = 0;
		
		int[] conversion = null;
		for(String s : filename)
		{
			lect = new LecteurCdXml();
			lect.lectureCSV(s, entete);

			if(vars == null)
			{
				nbvar = lect.nbvar;
				vars = new Variable[nbvar];
				conversion = new int[lect.nbvar];
				int j = 0;
				for(int i = 0; i < lect.nbvar; i++)
				{
					conversion[i] = j;
					vars[j] = new Variable();
					vars[j].name = lect.var[i];
					vars[j].domain = 0;
					j++;
				}
			}

			for(int i = 0; i < lect.nbligne; i++)
			{
				for(int k = 0; k < lect.nbvar; k++)
				{
					if(conversion[k] == -1)
						continue;
					String value = lect.domall[i][k];
					if(!vars[conversion[k]].values.contains(value))
					{
						vars[conversion[k]].values.add(value);
						vars[conversion[k]].domain++;
					}
				}
			}
		}

		return vars;
	}
	
}
