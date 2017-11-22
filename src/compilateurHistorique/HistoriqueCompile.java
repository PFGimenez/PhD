package compilateurHistorique;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import compilateur.LecteurCdXml;
import compilateur.SALADD;
import compilateurHistorique.vdd.VDD;

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
 * Un historique compilé. Surcouche de VDD, qui permet de faire facilement des calculs de comptage et de probabilité.
 * @author Pierre-François Gimenez
 *
 */

public class HistoriqueCompile implements Serializable
{
	private static final long serialVersionUID = 1L;
	private VDD arbre;
	private Variable[] variablesLocal;
	private HashMap<String, Integer> mapVarLocal;
	private DatasetInfo dataset;
	private boolean compileDone = false;

	/**
	 * Initialise les variables à partir d'un bif xml de réseau bayésien
	 * @param rbfile
	 */
	public HistoriqueCompile(DatasetInfo dataset, Variable[] variablesAConsiderer)
	{
		this.dataset = dataset;
		if(variablesAConsiderer == null)
			variablesAConsiderer = dataset.vars;
		triSimpleVariablesLocal(variablesAConsiderer);
		arbre = new VDD(variablesLocal);
	}
	
	public HistoriqueCompile(DatasetInfo dataset)
	{
		this(dataset, null);
	}
	
	/**
	 * On met les variables avec le plus de valuations en bas de l'arbre afin de limiter le nombre de nœuds
	 */
	private void triSimpleVariablesLocal(Variable[] vars)
	{
		variablesLocal = new Variable[vars.length];
		for(int i = 0; i < vars.length; i++)
			variablesLocal[i] = vars[i];
		
		for(int i = 0; i < variablesLocal.length; i++)
		{
			int indicemax = 0;
			for(int j = 1; j < variablesLocal.length-i; j++) // j cpmmence à 1 car indicemax vaut déjà 0
			{
				if(variablesLocal[j].domain > variablesLocal[indicemax].domain)
					indicemax = j;
			}
			// On ne fait l'échange que s'il y a besoin
			if(variablesLocal[variablesLocal.length-1-i].domain != variablesLocal[indicemax].domain)
			{
				Variable tmp = variablesLocal[variablesLocal.length-1-i];
				variablesLocal[variablesLocal.length-1-i] = variablesLocal[indicemax];
				variablesLocal[indicemax] = tmp;
			}
		}
		
		mapVarLocal = new HashMap<String, Integer>();
		
		for(int i = 0; i < variablesLocal.length; i++)
			mapVarLocal.put(variablesLocal[i].name, i);
		
		for(int i = 0; i < variablesLocal.length; i++)
			variablesLocal[i].index = dataset.mapVar.get(variablesLocal[i].name);
	}
	
	/**
	 * Returns only the possible instances
	 * @param dataset
	 * @param filename
	 * @param entete
	 * @param contraintes
	 * @return
	 */
	public static Instanciation[] readPossibleInstances(DatasetInfo dataset, List<String> filename, boolean entete, SALADD contraintes)
	{
		Instanciation[] instances = readInstances(dataset, filename, entete);
		if(contraintes != null)
		{
			System.out.println("Reading the possible instances only… (this may take a moment)");
			contraintes.reinitialisation();
			contraintes.propagation();
			List<Instanciation> inst = new ArrayList<Instanciation>();
			for(int i = 0; i < instances.length; i++)
			{
				boolean possible = true;
				for(Variable v : dataset.vars)
				{
					if(!contraintes.isPresentInCurrentDomain(v.name, instances[i].getValue(v.name)))
					{
						possible = false;
						break;
					}
					contraintes.assignAndPropagate(v.name, instances[i].getValue(v.name));
					if(!contraintes.isPossiblyConsistent())
					{
						possible = false;
						break;
					}
				}
				if(possible)
					inst.add(instances[i]);
				contraintes.reinitialisation();
				contraintes.propagation();
			}
			instances = new Instanciation[inst.size()];
			for(int i = 0; i < instances.length; i++)
				instances[i] = inst.get(i);
		}
		return instances;
	}
	
	public static Instanciation[] readInstances(DatasetInfo dataset, List<String> filename, boolean entete)
	{
		List<Instanciation> tmp = new ArrayList<Instanciation>();
		for(String s : filename)
		{
			LecteurCdXml lect = new LecteurCdXml();
			lect.lectureCSV(s, entete);

			int indiceMax = lect.nbligne;
			
			for(int i = 0; i < indiceMax; i++)
			{
				Instanciation inst = new Instanciation(dataset);
				for(int k = 0; k < lect.nbvar; k++)
				{
					String var = lect.var[k];
					assert dataset.mapVar.get(var) != null : "Variable inconnue : "+var;
					assert dataset.vars[dataset.mapVar.get(var)].values.contains(lect.domall[i][k]) : "Valeur " + lect.domall[i][k] + " inconnue pour " + var + " !";
					inst.conditionne(var, lect.domall[i][k]);
				}
				tmp.add(inst);
			}
		}
		Instanciation[] out = new Instanciation[tmp.size()];
		for(int i = 0; i < out.length; i++)
			out[i] = tmp.get(i);
		return out;
	}

	public void compile(Instanciation[] exemples)
	{
		assert !compileDone;
		compileDone = true;
		for(Instanciation i : exemples)
			arbre.addInstanciation(i.values);
	}
	
	public void compile(List<String> filename, boolean entete)
	{
		compile(readInstances(dataset, filename, entete));
	}
	
	/**
	 * Calcule les probabilités pour toutes les modalités d'une variable sachant "instance"
	 * La somme de ces probabilités fait donc 1.
	 * @param variable
	 * @param possibles
	 * @param withZero faut-il explicitement inclure des valeurs dont la probabilité est nulle ?
	 * @param instance
	 * @return
	 */
	public HashMap<String, Double> getProbaToutesModalitees(String variable, /*ArrayList<String> possibles, */boolean withZero, Instanciation instance)
	{
		HashMap<String, Double> out = new HashMap<String, Double>();
		HashMap<String, Integer> exemples = getNbInstancesToutesModalitees(variable, withZero, instance);
		
		double somme = 0.;
		for(Integer i : exemples.values())
			somme += i;
		
		for(String s : exemples.keySet())
			out.put(s, exemples.get(s) / somme);
		
		return out;
	}

	public HashMap<String, Integer> getNbInstancesToutesModalitees(String variable, Instanciation instance)
	{
		return getNbInstancesToutesModalitees(variable, false, instance);
	}
	
	private boolean checkSum(HashMap<String, Integer> map, int total)
	{
		int sum = 0;
		for(Integer i : map.values())
			sum += i;
		return sum == total;
	}

	/**
	 * Retourne le nombre d'exemples pour chaque modalité
	 * @param variable
	 * @param possibles
	 * @return
	 */
	public HashMap<String, Integer> getNbInstancesToutesModalitees(String variable, /*ArrayList<String> possibles,*/ boolean withZero, Instanciation instance)
	{
		assert compileDone;
		
		List<String> l = new ArrayList<String>();
		l.add(variable);
		HashMap<List<String>, Integer> tmp = getNbInstancesToutesModalitees(l, withZero, instance);
		HashMap<String, Integer> out = new HashMap<String, Integer>();
		for(List<String> list : tmp.keySet())
		{
			assert list.size() == 1;
			out.put(list.get(0), tmp.get(list));
		}
		assert checkSum(out, getNbInstances(instance));

		return out;
	}
	
	/**
	 * Retourne le nombre d'exemples pour chaque modalité
	 * @param variable
	 * @param possibles
	 * @return
	 */
	public HashMap<List<String>, Integer> getNbInstancesToutesModalitees(List<String> variables, /*ArrayList<String> possibles,*/ boolean withZero, Instanciation instance)
	{
		assert compileDone;

		int[] vars = new int[variables.size()];
		for(int i = 0; i < vars.length; i++)
		{
			vars[i] = dataset.mapVar.get(variables.get(i));
			assert instance.values[vars[i]] == null;
			if(instance.values[vars[i]] != null)
			{
				System.err.println("Attention, variable déjà instanciée : "+variables.get(i));
				instance.deconditionne(vars[i]);
			}
		}
		
		HashMap<List<String>, Integer> out = new HashMap<List<String>, Integer>();

		/*
		 * "combinaisons" est l'ensemble des combinaisons de valeurs des variables données, par exemple ( (0,A), (0,B), (1,A), (1,B) )
		 */
		
		List<List<String>> combinaisons = new ArrayList<List<String>>();
		combinaisons.add(new ArrayList<String>());
		for(String s : variables)
		{
			Variable v = dataset.vars[dataset.mapVar.get(s)];
			ArrayList<List<String>> next = new ArrayList<List<String>>();
			for(List<String> val : combinaisons)
				for(int i = 0; i < v.domain; i++)
				{
					List<String> l = new ArrayList<String>();
					l.addAll(val);
					l.add(v.values.get(i));
					next.add(l);
				}
			combinaisons = next;
		}
				
		HashMap<String, Integer> tmp = new HashMap<String, Integer>();

		for(List<String> s : combinaisons)
		{
			// déjà calculé
			if(out.get(s) != null)
				continue;
			for(int i = 0; i < variables.size(); i++)
				instance.conditionne(variables.get(i), s.get(i));
			tmp.clear();
			arbre.getNbInstancesToutesModalitees(tmp, dataset.mapVar.get(variables.get(0)), instance);
			for(int i = 0; i < variables.size(); i++)
				instance.deconditionne(variables.get(i));
			for(String val : tmp.keySet())
			{
				List<String> s2 = new ArrayList<String>();
				s2.add(val);
				for(int i = 1; i < variables.size(); i++)
					s2.add(s.get(i));
				out.put(s2, tmp.get(val));
			}
		}

		if(withZero)
			for(List<String> s : combinaisons)
				if(out.get(s) == null)
					out.put(s, 0);

		return out;
	}

	public final int getNbInstances(Instanciation instance)
	{
		assert compileDone;
		return arbre.getNbInstances(instance);
	}

/*	public final int getNbInstances(Instanciation instance, int nbInst)
	{
		assert compileDone;
		return arbre.getNbInstances(instance.values, nbInst);
	}	*/

	public int getNbNoeuds()
	{
		assert compileDone;
		return arbre.getNbNoeuds();
	}
	
	/**
	 * Retourne le nombre total d'exemples
	 * @return
	 */
	public final int getNbInstancesTotal()
	{
		assert compileDone;
		return arbre.nbInstances;
	}
	
	public void printADD(int nb)
	{
		assert compileDone;
		arbre.print(nb);
	}

	public List<String> computeOrder(String variable, Instanciation instance)
	{
		HashMap<String, Integer> nbInstances = getNbInstancesToutesModalitees(variable, instance);
		List<String> ordrePref = new ArrayList<String>();
		
		LinkedList<Entry<String, Integer>> list = new LinkedList<Map.Entry<String,Integer>>(nbInstances.entrySet());
	     Collections.sort(list, new Comparator<Entry<String, Integer>>() {
	          public int compare(Entry<String, Integer> o1, Entry<String, Integer> o2) {
	               return -o1.getValue()
	              .compareTo(o2.getValue());
	          }
	     });
	
	    for (Iterator<Entry<String, Integer>> it = list.iterator(); it.hasNext();) {
	        Map.Entry<String, Integer> entry = it.next();
	        ordrePref.add((String) entry.getKey());
	    }
	    return ordrePref;
	}
	
}
