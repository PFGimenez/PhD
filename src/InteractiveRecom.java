import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
import java.util.Set;

import compilateur.LecteurCdXml;
import compilateur.SALADD;
import compilateurHistorique.Variable;
import preferences.completeTree.ApprentissageGloutonLexTree;
import preferences.heuristiques.VieilleHeuristique;
import preferences.heuristiques.simple.HeuristiqueEntropieNormalisee;
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
	
	public static void main(String[] args)
	{

		args = new String[2];

		// Algo
		args[0] = "naif";

		// Dataset
		args[1] = "renault_medium_header_contraintes";
		
		if(args.length < 1)
		{
			System.err.println("Interactive recommendation.");
			System.err.println("Usage : algo dataset");
			return;
		}

		HashMap<String, String> vars_instanciees = new HashMap<String, String>();
		ArrayList<String> vars_choisies = new ArrayList<String>(); // liste des variables choisies explicitement par l'utilisateur
		ArrayList<String> modif = new ArrayList<String>(); // Toutes les variables modifiées par un set
		
		final String dataset = args[1].trim();
		final String prefixData = "datasets/"+dataset+"/";

		if(!new File(prefixData).exists())		
		{
			System.err.println("Dataset inconnu : "+dataset);
			return;
		}
		
		boolean entete = dataset.contains("header");
		boolean contraintesPresentes = dataset.contains("contraintes");

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
		else if(args[0].toLowerCase().contains("v-nai"))
			recommandeur = new AlgoVoisinsNaive(20);
		else if(args[0].toLowerCase().contains("nai"))
			recommandeur = new AlgoRBNaif();
		else if(args[0].toLowerCase().contains("lextree"))
			recommandeur = new AlgoLexTree(new ApprentissageGloutonLexTree(300, 10, new VieilleHeuristique(new HeuristiqueEntropieNormalisee())), prefixData);
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
			System.err.println("Chargement des contraintes : "+fichierContraintes);
			contraintes = new SALADD();
			contraintes.compilation(fichierContraintes, true, 4, 0, 0, true);
			contraintes.propagation();
			contraintes2 = new SALADD();
			contraintes2.compilation(fichierContraintes, true, 4, 0, 0, true);
			contraintes2.propagation();
		}
		else if(contraintesPresentes)
		{
			System.err.println("Pas de fichier de contraintes!");
//			System.err.println("Veuillez relancez avec \"contraintesPresentes = false\"");
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
			System.out.println("v30 : "+contraintes.getSizeOfCurrentDomainOf("v30")); // TODO
		}

		recommandeur.apprendDonnees(learning_set, 2, entete);
		System.err.println("Lancement de "+recommandeur+" sur "+dataset);
		Scanner sc = new Scanner(System.in);

		recommandeur.oublieSession();
		
		System.out.println("ready");
		System.err.println("System ready");

		while(true)
		{
			while(!sc.hasNextLine())
			{
				try {
					Thread.sleep(5);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			String input = sc.nextLine().trim();
			
			// Démarre une nouvelle session
			if(input.contains("reinit-all"))
			{
				vars_instanciees.clear();
				vars_choisies.clear();
				if(contraintesPresentes)
				{
					contraintes.reinitialisation();
					contraintes.propagation();
				}
				recommandeur.oublieSession();
				System.err.println("New configuration session.");
			}

			// Désinstancie une variable
/*				else if(input.contains("reinit"))
				{
					while(!sc.hasNextLine())
					{
						try {
							Thread.sleep(5);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
					String v = sc.nextLine().trim();
					vars_instanciees.remove(v);
				}*/

			// Arrête le programme
			else if(input.contains("exit"))
			{
				sc.close();
				recommandeur.termine();
				System.err.println("Shutdown");
				return;
			}
			
			// Demande des variables
			else if(input.contains("vars"))
			{
				for(int i = 0; i < vars.length-1; i++)
					System.out.print(vars[i].name+",");
				System.out.println(vars[vars.length-1].name);
			}
			
			// Demande les valeurs possibles pour toutes les variables
			else if(input.contains("possible-all"))
			{
				for(int i = 0; i < vars.length-1; i++)
					System.out.print(vars[i].name+";");
				System.out.println(vars[vars.length-1].name);

				for(int k = 0; k < vars.length; k++)
				{
					String v = vars[k].name;
					Set<String> values = null, values2 = null;
					int nbModalites = 0;
					ArrayList<String> values_array = new ArrayList<String>();
					ArrayList<String> values_array_interdites = new ArrayList<String>();
	
					// variable affectée, on renvoie une ligne vide
					if(vars_instanciees.get(v) != null)
						System.out.println();
					else
					{
						if(contraintes != null)
						{
							values = contraintes.getCurrentDomainOf(v);						
							values2 = contraintes.getDomainOf(v);
							
							nbModalites = values.size();
							if(nbModalites == 0)
							{
								System.err.println("No possible value for "+v+" (nb max : "+values2.size()+")");
								int z = 0;
								z = 1/z;
							}
							values_array.addAll(values);
							values_array_interdites.addAll(values2);
							values_array_interdites.removeAll(values);
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
		
						for(int i = 0; i < values_array.size()-1; i++)
							System.out.print(values_array.get(i)+",");
						if(values_array.size() > 0)
							System.out.print(values_array.get(values_array.size()-1));
						if(k < vars.length - 1)
							System.out.print(";");
					}
				}
				System.out.println();
			}

			// Demande les valeurs possibles
			else if(input.contains("possible"))
			{
				while(!sc.hasNextLine())
				{
					try {
						Thread.sleep(5);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				String v = sc.nextLine().trim();
				Set<String> values = null, values2 = null;
				int nbModalites = 0;
				ArrayList<String> values_array = new ArrayList<String>();
				ArrayList<String> values_array_interdites = new ArrayList<String>();

				// variable affectée, on renvoie une ligne vide
				if(vars_instanciees.get(v) != null)
					System.out.println();
				else
				{
					if(contraintes != null)
					{
						values = contraintes.getCurrentDomainOf(v);						
						values2 = contraintes.getDomainOf(v);
						
						nbModalites = values.size();
						if(nbModalites == 0)
						{
							System.err.println("No possible value !");
							int z = 0;
							z = 1/z;
						}
						values_array.addAll(values);
						values_array_interdites.addAll(values2);
						values_array_interdites.removeAll(values);
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
	
					for(int i = 0; i < values_array.size()-1; i++)
						System.out.print(values_array.get(i)+",");
					if(values_array.size() > 0)
						System.out.print(values_array.get(values_array.size()-1));
					System.out.println();
				}
			}
			
			// Demande une recommandation
			else if(input.contains("reco"))
			{
				while(!sc.hasNextLine())
				{
					try {
						Thread.sleep(5);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				String v = sc.nextLine().trim();
				Set<String> values = null, values2 = null;
				int nbModalites = 0;
				ArrayList<String> values_array = new ArrayList<String>();
				ArrayList<String> values_array_interdites = new ArrayList<String>();

				if(contraintes != null)
				{
					values = contraintes.getCurrentDomainOf(v);
					values2 = contraintes.getDomainOf(v);
					
					nbModalites = values.size();
					if(nbModalites == 0)
					{
						System.err.println("No possible value !");
						int z = 0;
						z = 1/z;
					}
					values_array.addAll(values);
					values_array_interdites.addAll(values2);
					values_array_interdites.removeAll(values);
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
				if(values_array.size() > 0)
					System.out.print(values_array.get(values_array.size()-1));
				System.out.println();
				
				for(int i = 0; i < values_array_interdites.size()-1; i++)
					System.out.print(values_array_interdites.get(i)+",");
				if(values_array_interdites.size() > 0)
					System.out.print(values_array_interdites.get(values_array_interdites.size()-1));
				System.out.println();
				
			}
/*			else if(input.contains("value-all"))
			{
				Iterator<String> iter = vars_instanciees.keySet().iterator();
				while(iter.hasNext())
				{
					System.out.print(iter.next());
					if(iter.hasNext())
						System.out.print(",");
				}
				System.out.println();
				
				iter = vars_instanciees.keySet().iterator();
				while(iter.hasNext())
				{
					System.out.print(vars_instanciees.get(iter.next()));
					if(iter.hasNext())
						System.out.print(",");
				}
				System.out.println();
			}*/
			else if(input.contains("value-all"))
			{
				for(int i = 0; i < vars.length-1; i++)
					System.out.print(vars[i].name+",");
				System.out.println(vars[vars.length-1].name);

				for(int i = 0; i < vars.length-1; i++)
					System.out.print(vars_instanciees.get(vars[i].name)+",");
				System.out.println(vars_instanciees.get(vars[vars.length-1].name));
			}
			else if(input.contains("value"))
			{
				while(!sc.hasNextLine())
				{
					try {
						Thread.sleep(5);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				String var = sc.nextLine().trim();
				System.out.println(vars_instanciees.get(var));
			}
			else if(input.contains("isset"))
			{
				while(!sc.hasNextLine())
				{
					try {
						Thread.sleep(5);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				String var = sc.nextLine().trim();
				System.out.println(vars_instanciees.containsKey(var)?"true":"false");
			}
			else if(input.contains("set"))
			{
				while(!sc.hasNextLine())
				{
					try {
						Thread.sleep(5);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				String var = sc.nextLine().trim();
				
				while(!sc.hasNextLine())
				{
					try {
						Thread.sleep(5);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				String solution = sc.nextLine().trim();

				vars_instanciees.put(var, solution);
				vars_choisies.add(var);
				recommandeur.setSolution(var, solution);
				
				if(contraintesPresentes)
				{
					contraintes.assignAndPropagate(var, solution);
/*					ArrayList<String> values = new ArrayList<String>();
					for(int i = 0; i < vars.length; i++)
					{
						values.clear();
						values.addAll(contraintes.getCurrentDomainOf(vars[i].name));
						if(values.size() == 1)
						{
							if(!vars[i].name.equals(var))
								 modif.add(vars[i].name);
							vars_instanciees.put(vars[i].name, values.get(0));
							recommandeur.setSolution(vars[i].name, values.get(0));
						}
					}*/
				}
			}
			else if(input.contains("unassign"))
			{
				while(!sc.hasNextLine())
				{
					try {
						Thread.sleep(5);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				String var = sc.nextLine().trim();
				ArrayList<String> unset = new ArrayList<String>();
				unset.add(var);
				vars_instanciees.remove(var);
				vars_choisies.remove(var);
				
				if(contraintesPresentes)
				{
					contraintes.unassignAndRestore(var);
					/*
					for(int i = 0; i < vars.length; i++)
					{
						if(!vars[i].name.equals(var) && !vars_choisies.contains(vars[i].name) && contraintes.getCurrentDomainOf(vars[i].name).size() > 1)
						{
								if(!vars[i].name.equals(var))
									 modif.add(vars[i].name);
								vars_instanciees.remove(vars[i].name);
								unset.add(vars[i].name);
						}
					}*/
				}
				
				recommandeur.unassign(var);
				
				for(int i = 0; i < unset.size()-1; i++)
					System.out.print(unset.get(i)+",");
				System.out.println(unset.get(unset.size()-1));
			}
			else if(input.contains("modif"))
			{
				for(int i = 0; i < modif.size()-1; i++)
					System.out.print(modif.get(i)+",");
				if(modif.size() > 0)
					System.out.print(modif.get(modif.size()-1));
				System.out.println();

				for(int i = 0; i < modif.size()-1; i++)
					System.out.print(vars_instanciees.get(modif.get(i))+",");
				if(modif.size() > 0)
					System.out.print(vars_instanciees.get(modif.get(modif.size()-1)));
				System.out.println();
				modif.clear();
			}
			else
			{
				System.err.println("Unknown command : "+input);
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
