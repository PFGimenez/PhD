package compilateurHistorique;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

import compilateur.LecteurCdXml;


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
 * Classe pour manipuler un historique compilé
 * @author pgimenez
 *
 */

public class HistoComp implements Serializable
{
	private static final long serialVersionUID = 1L;
	private VDD arbre;
	private Variable[] variables;
	private HashMap<String, Integer> mapVar; // associe au nom d'une variable sa position dans values
	
//	private HashMap<Integer, Integer>[][][] nbInstancesTriplet;
	private HashMap<Integer, Integer>[][] nbInstancesPaire;
	private HashMap<Integer, Integer>[] nbInstancesPriori;
//	private int[] stat;
	
	// Ces deux variables ne sont utilisées que quand un réseau bayésien est utilisé
	private HashMap<String,HashMap<Integer, Integer>> cpt;
	private HashMap<String,int[]> famille;
	
/*	public HistoComp(String[] ordre, ArrayList<String> filename, boolean entete)
	{
		mapVar = new HashMap<String, Integer>();

		for(int i = 0; i < ordre.length; i++)
			mapVar.put(ordre[i], i);
		
		VDD.setOrdreVariables(ordre.length);
		instance = new Instanciation(ordre.length);
		arbre = new VDD();
//		values = new String[ordre.length];
		deconditionneTout();
		
		compileHistorique(filename, entete);
	}*/
	
	@SuppressWarnings("unchecked")
	public HistoComp(ArrayList<String> filename, boolean entete)
	{
		variables = initVariables(filename, entete);
//		stat = new int[variables.length+1];
//		nbInstancesTriplet = (HashMap<Integer, Integer>[][][]) new HashMap[variables.length][variables.length][variables.length];
		nbInstancesPaire = (HashMap<Integer, Integer>[][]) new HashMap[variables.length][variables.length];
		nbInstancesPriori = (HashMap<Integer, Integer>[]) new HashMap[variables.length];
	}
	
	public void compile(ArrayList<String> filename, boolean entete)
	{
		compile(filename, entete, -1);
	}
	
	public void initCPT(HashMap<String,ArrayList<String>> famille)
	{
		System.out.println("Apprentissage des CPT");
		this.famille = new HashMap<String,int[]>();
		for(String s : famille.keySet())
		{
			int[] list = new int[famille.get(s).size()];
			int k = 0;
			for(String p : famille.get(s))
				list[k++] = mapVar.get(p);
			this.famille.put(s, list);
		}
		cpt = new HashMap<String,HashMap<Integer, Integer>>();
		for(String s : famille.keySet())
		{
			HashMap<Integer, Integer> tmp = new HashMap<Integer, Integer>();
			IteratorInstancesPartielles iter = new IteratorInstancesPartielles(new Instanciation(), variables, mapVar, famille.get(s));
			int k = 0;
			while(iter.hasNext())
			{
				Instanciation instance = iter.next();
//				System.out.println(k+" "+instance.getIndexCache(this.famille.get(s)));
				tmp.put(k++, VDD.getNbInstancesStatic(arbre, instance.values, instance.nbVarInstanciees));
			}
			cpt.put(s, tmp);
		}

	}

	public void compile(ArrayList<String> filename, boolean entete, int nbExemplesMax)	
	{
		/**
		 * On met les variables avec le plus de valuations en bas de l'arbre afin de limiter le nombre de nœuds
		 */
		for(int i = 0; i < variables.length-1; i++)
		{
			int indicemax = 0;
			for(int j = 1; j < variables.length-i; j++)
			{
				if(variables[j].domain > variables[indicemax].domain)
					indicemax = j;
			}
			// On ne fait l'échange que s'il y a besoin
			if(variables[variables.length-1-i].domain != variables[indicemax].domain)
			{
				Variable tmp = variables[variables.length-1-i];
				variables[variables.length-1-i] = variables[indicemax];
				variables[indicemax] = tmp;
			}
		}

		for(int i = 0; i < variables.length; i++)
			variables[i].profondeur = i;

		mapVar = new HashMap<String, Integer>();

		for(int i = 0; i < variables.length; i++)
			mapVar.put(variables[i].name, i);

		VDD.setOrdreVariables(variables);
		arbre = new VDD();
//		values = new String[ordre.length];
		
		Instanciation.setVars(variables, mapVar);
//		instance = new Instanciation();
//		deconditionneTout();
		
		compileHistorique(filename, entete, nbExemplesMax);
		
		for(int i = 0; i < variables.length; i++)
		{
			nbInstancesPriori[i] = new HashMap<Integer,Integer>();
			for(int vi = 0; vi < variables[i].domain; vi++)
			{
				Instanciation val = new Instanciation();
				val.conditionne(i, vi);
				nbInstancesPriori[i].put(vi, arbre.getNbInstances(val.values, val.nbVarInstanciees));
			}
		}

		System.out.println("Apprentissage des paires…");
		
		for(int i = 0; i < variables.length - 1; i++)
			for(int j = i + 1; j < variables.length; j++)
			{
				nbInstancesPaire[i][j] = new HashMap<Integer,Integer>();
				for(int vi = 0; vi < variables[i].domain; vi++)
					for(int vj = 0; vj < variables[j].domain; vj++)
					{
						Instanciation val = new Instanciation();
						val.conditionne(i, vi);
						val.conditionne(j, vj);
						nbInstancesPaire[i][j].put(vi*variables[j].domain+vj, arbre.getNbInstances(val.values, val.nbVarInstanciees));
					}
			}
		
/*		System.out.println("Apprentissage des triplets…");
		for(int i = 0; i < variables.length - 1; i++)
			for(int j = i + 1; j < variables.length; j++)
				for(int k = j + 1; k < variables.length; k++)
				{
					nbInstancesTriplet[i][j][k] = new HashMap<Integer,Integer>();
					for(int vi = 0; vi < variables[i].domain; vi++)
						for(int vj = 0; vj < variables[j].domain; vj++)
							for(int vk = 0; vk < variables[k].domain; vk++)
							{
								Instanciation val = new Instanciation();
								val.conditionne(i, vi);
								val.conditionne(j, vj);
								val.conditionne(k, vk);
								nbInstancesTriplet[i][j][k].put((vi*variables[j].domain+vj)*variables[k].domain+vk, arbre.getNbInstances(val.values, val.nbVarInstanciees));
							}
				}*/

	}
	
	/**
	 * Initialise les valeurs et les domaines des variables.
	 * IL N'Y A PAS D'APPRENTISSAGE SUR LES VALEURS
	 * @param filename
	 * @param entete
	 * @return
	 */
	private Variable[] initVariables(ArrayList<String> filename, boolean entete)
	{
		// Vérification de toutes les valeurs possibles pour les variables
		Variable[] vars = null;
		LecteurCdXml lect = null;
		
		for(String s : filename)
		{
			lect = new LecteurCdXml();
			lect.lectureCSV(s, entete);

			if(vars == null)
			{
				vars = new Variable[lect.nbvar];
				for(int i = 0; i < lect.nbvar; i++)
				{
					vars[i] = new Variable();
					vars[i].name = lect.var[i];
					vars[i].domain = 0;
				}
			}

			for(int i = 0; i < lect.nbligne; i++)
			{
				for(int k = 0; k < lect.nbvar; k++)
				{
					String value = lect.domall[i][k];
					if(!vars[k].values.contains(value))
					{
						vars[k].values.add(value);
						vars[k].domain++;
					}
				}
			}
		}
		System.out.print(lect.nbvar+ " variables. Domaines :");
		System.out.print(" "+vars[0].domain);
		for(int k = 1; k < lect.nbvar; k++)
			System.out.print(", "+vars[k].domain);
		System.out.println();
		return vars;
	}
	
	private void compileHistorique(ArrayList<String> filename, boolean entete, int nbExemplesMax)
	{
		for(String s : filename)
		{
			LecteurCdXml lect = new LecteurCdXml();
			lect.lectureCSV(s, entete);
			Integer[] values = new Integer[lect.nbvar];
//			System.out.println(lect.nbligne+" exemples");
			int indiceMax;
			if(nbExemplesMax == -1)
				indiceMax = lect.nbligne;
			else
				indiceMax = Math.min(nbExemplesMax, lect.nbligne);
			
			for(int i = 0; i < indiceMax; i++)
			{
				for(int k = 0; k < lect.nbvar; k++)
				{
					String var = lect.var[k];	
//					System.out.print(var+" ("+lect.domall[i][k]+"), ");
					values[mapVar.get(var)] = variables[mapVar.get(var)].values.indexOf(lect.domall[i][k]);
				}
//				System.out.println();
				arbre.addInstanciation(values);
			}
		}
	}

	/**
	 * Sauvegarde par sérialisation
	 */
	public void save(String s)
	{
		File fichier =  new File(s+".sav") ;
		ObjectOutputStream oos;
		try {
			oos = new ObjectOutputStream(new FileOutputStream(fichier));
			oos.writeObject(this);
			oos.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Chargement d'un historique compilé sauvegardé
	 * @return
	 */
	public static HistoComp load(String s)
	{
		System.out.println("Chargement d'un historique");
		HistoComp out;
		File fichier =  new File(s+".sav") ;
		ObjectInputStream ois;
		try {
			ois = new ObjectInputStream(new FileInputStream(fichier));
			out = (HistoComp)ois.readObject() ;
			ois.close();
			return out;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * Retourne des proba (entre 0 et 1 donc)
	 * @param variable
	 * @param possibles
	 * @return
	 */
	public HashMap<String, Double> getProbaToutesModalitees(String variable, ArrayList<String> possibles, boolean withZero, Instanciation instance)
	{
		HashMap<String, Double> out = new HashMap<String, Double>();
		HashMap<String, Integer> exemples = getNbInstancesToutesModalitees(variable, possibles, withZero, instance);
		
		double somme = 0.;
		for(Integer i : exemples.values())
			somme += i;
		
		for(String s : exemples.keySet())
			out.put(s, exemples.get(s) / somme);
		
		return out;
	}

	public HashMap<String, Integer> getNbInstancesToutesModalitees(String variable, Instanciation instance)
	{
		return getNbInstancesToutesModalitees(variable, null, false, instance);
	}

	/**
	 * Retourne le nombre d'exemples pour chaque modalité
	 * @param variable
	 * @param possibles
	 * @return
	 */
	public HashMap<String, Integer> getNbInstancesToutesModalitees(String variable, ArrayList<String> possibles, boolean withZero, Instanciation instance)
	{
		int var = mapVar.get(variable);
		if(instance.values[var] != null)
		{
			System.out.println("Attention, variable déjà instanciée");
			instance.deconditionne(var);
		}
		
		HashMap<String, Integer> out = new HashMap<String, Integer>();;

		if(withZero)
			for(String s : variables[var].values)
				out.put(s, 0);
		
//		System.out.println("Nb exemples : " + arbre.getNbInstances(values, nbVarInstanciees));
		

/*		if(possibles != null) // commenté car pas du tout efficace en temps d'exécution
		{
			out = new HashMap<String, Integer>();
			for(String p : possibles)
			{
				values[var] = p;
				out.put(p, arbre.getNbInstances(values, nbVarInstanciees + 1));
			}
			values[var] = null;
		}
		else
		{*/
			arbre.getNbInstancesToutesModalitees(out, var, instance.values, possibles, instance.nbVarInstanciees);
/*			int somme = 0;
			for(Integer i : out.values())
				somme += i;
			if(somme != arbre.getNbInstances(values, nbVarInstanciees))
				System.out.println("Erreur de calcul du nombre d'instances!");*/
//		}
		
//		conditionne(var, sauv);
		return out;
	}
	
	public int nbModalites(String v)
	{
		return variables[mapVar.get(v)].domain;
	}

	public int getNbInstancesCPT(Instanciation instance, String var)
	{
		return cpt.get(var).get(instance.getIndexCache(famille.get(var)));
	}

	int delay = 0;
	
	public int getNbInstances(Instanciation instance)
	{
/*		stat[instance.nbVarInstanciees]++;
		
		if(delay++ % 10000 == 0)
		{
			for(int i = 0; i < variables.length; i++)
				System.out.print(stat[i]+", ");
			System.out.println();
		}*/
		
		if(instance.nbVarInstanciees == 1)
		{
			Integer[] t = instance.getHash(1);
//			System.out.println("i : "+t[0]+", vi : "+t[1]+", j : "+t[2]+", vj : "+t[3]);
			return nbInstancesPriori[t[0]].get(t[1]);
		}
		else if(instance.nbVarInstanciees == 2)
		{
			Integer[] t = instance.getHash(2);
//			System.out.println("i : "+t[0]+", vi : "+t[1]+", j : "+t[2]+", vj : "+t[3]);
			return nbInstancesPaire[t[0]][t[2]].get(t[1]*variables[t[2]].domain+t[3]);
		}
/*		else if(instance.nbVarInstanciees == 3)
		{
			Integer[] t = instance.getHash(3);
//			System.out.println("i : "+t[0]+", vi : "+t[1]+", j : "+t[2]+", vj : "+t[3]);
			return nbInstancesTriplet[t[0]][t[2]][t[4]].get((t[1]*variables[t[2]].domain+t[3])*variables[t[4]].domain+t[5]);
		}*/
		return VDD.getNbInstancesStatic(arbre, instance.values, instance.nbVarInstanciees);
	}	
	
	public int getNbInstancesAncien(Instanciation instance)
	{
		if(instance.nbVarInstanciees == 1)
		{
			Integer[] t = instance.getHash(1);
//			System.out.println("i : "+t[0]+", vi : "+t[1]+", j : "+t[2]+", vj : "+t[3]);
			return nbInstancesPriori[t[0]].get(t[1]);
		}
		else if(instance.nbVarInstanciees == 2)
		{
			Integer[] t = instance.getHash(2);
//			System.out.println("i : "+t[0]+", vi : "+t[1]+", j : "+t[2]+", vj : "+t[3]);
			return nbInstancesPaire[t[0]][t[2]].get(t[1]*variables[t[2]].domain+t[3]);
		}
		return arbre.getNbInstances(instance.values, instance.nbVarInstanciees);
	}
	
	public int getNbNoeuds()
	{
		return arbre.getNbNoeuds();
	}
	
	public ArrayList<String> getVarConnues(Instanciation instance)
	{
		ArrayList<String> varConnues = new ArrayList<String>();
		for(int i = 0; i < variables.length; i++)
			if(instance.values[i] != null)
				varConnues.add(variables[i].name);
		return varConnues;
	}
	/*
	public IteratorInstances getIterator(String var, Instanciation instance)
	{
		ArrayList<String> cutset = new ArrayList<String>();
		cutset.add(var);
		return new IteratorInstances(instance, variables, mapVar, cutset);
	}
*/
	public IteratorInstances getIterator(Instanciation instance, int[] cutset)
	{
		return new IteratorInstances(instance, variables, cutset);
	}

	/**
	 * Retourne le nombre total d'exemples
	 * @return
	 */
	public int getNbInstancesTotal()
	{
		return arbre.getNbInstances(null, 0);
	}

	public ArrayList<String> getValues(String variable)
	{
		return variables[mapVar.get(variable)].values;
	}
	
	public HashMap<String, Integer> getMapVar()
	{
		return mapVar;
	}
	
}
