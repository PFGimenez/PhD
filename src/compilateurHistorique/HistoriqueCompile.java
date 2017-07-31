package compilateurHistorique;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import compilateur.LecteurCdXml;
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
	
	public static Instanciation[] readInstances(DatasetInfo dataset, List<String> filename, boolean entete)
	{
		Instanciation[] out = null;
		for(String s : filename)
		{
			LecteurCdXml lect = new LecteurCdXml();
			lect.lectureCSV(s, entete);

			int indiceMax;
			indiceMax = lect.nbligne;
			out = new Instanciation[indiceMax];
			
			for(int i = 0; i < indiceMax; i++)
			{
				out[i] = new Instanciation(dataset);
				for(int k = 0; k < lect.nbvar; k++)
				{
					String var = lect.var[k];
					assert dataset.vars[dataset.mapVar.get(var)].values.contains(lect.domall[i][k]) : "Valeur " + lect.domall[i][k] + " inconnue pour " + var + " !";
					out[i].conditionne(var, lect.domall[i][k]);
				}
			}
		}
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

	/**
	 * Retourne le nombre d'exemples pour chaque modalité
	 * @param variable
	 * @param possibles
	 * @return
	 */
	public HashMap<String, Integer> getNbInstancesToutesModalitees(String variable, /*ArrayList<String> possibles,*/ boolean withZero, Instanciation instance)
	{
		List<String> l = new ArrayList<String>();
		l.add(variable);
		HashMap<List<String>, Integer> tmp = getNbInstancesToutesModalitees(l, withZero, instance);
		HashMap<String, Integer> out = new HashMap<String, Integer>();
		for(List<String> list : tmp.keySet())
		{
			assert list.size() == 1;
			out.put(list.get(0), tmp.get(list));
		}
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
		
		if(withZero)
			for(List<String> s : combinaisons)
				out.put(s, 0);
		
		// TODO !
		HashMap<String, Integer> tmp = new HashMap<String, Integer>();
		arbre.getNbInstancesToutesModalitees(tmp, dataset.mapVar.get(variables.get(0)), instance.values, instance.nbVarInstanciees);

		return out;
	}

	public final int getNbInstances(Instanciation instance)
	{
		assert compileDone;
		return arbre.getNbInstances(instance.values, instance.nbVarInstanciees);
	}	

	public final int getNbInstances(Instanciation instance, int nbInst)
	{
		assert compileDone;
		return arbre.getNbInstances(instance.values, nbInst);
	}	

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
	
}
