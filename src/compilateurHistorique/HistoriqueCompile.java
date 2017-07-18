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
		int var = dataset.mapVar.get(variable);
		
		assert instance.values[var] == null;
		if(instance.values[var] != null)
		{
			System.err.println("Attention, variable déjà instanciée");
			instance.deconditionne(var);
		}
		
		HashMap<String, Integer> out = new HashMap<String, Integer>();

		if(withZero)
			for(String s : variablesLocal[var].values)
				out.put(s, 0);
		
		arbre.getNbInstancesToutesModalitees(out, var, instance.values, possibles, instance.nbVarInstanciees);

		return out;
	}
	
	public int nbModalites(String v)
	{
		return variablesLocal[mapVarLocal.get(v)].domain;
	}

	public final int getNbInstances(Instanciation instance)
	{
		return arbre.getNbInstances(instance.values, instance.nbVarInstanciees);
	}	
	
	public int getNbNoeuds()
	{
		return arbre.getNbNoeuds();
	}
	
	/**
	 * Retourne le nombre total d'exemples
	 * @return
	 */
	public final int getNbInstancesTotal()
	{
		return arbre.nbInstances;
	}

	public final ArrayList<String> getValues(String variable)
	{
		return dataset.vars[dataset.mapVar.get(variable)].values;
	}
	
	public void printADD(int nb)
	{
		arbre.print(nb);
	}
	
}
