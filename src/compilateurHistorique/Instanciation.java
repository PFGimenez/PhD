package compilateurHistorique;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import compilateur.SALADD;
import compilateur.Var;

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
 * Une instanciation, complète ou partielle, des variables du VDD
 * @author Pierre-François Gimenez
 *
 */

public class Instanciation implements Serializable
{
	private static final long serialVersionUID = 1L;
	public transient DatasetInfo dataset;
//	public static Variable[] dataset.vars;
//	private static HashMap<String, Integer> dataset.mapVar;
	private static InstanceMemoryManager memory;
	public final int nbMemory;

	public Integer[] values;
	int nbVarInstanciees;
//	int nbVarInstancieesSave;
	
/*	public static void reinit()
	{
		vars = null;
	}

	public static void setVars(Variable[] variables)
	{
		if(dataset.vars == null)
		{
			vars = variables;
			dataset.mapVar = new HashMap<String, Integer>();
			for(int i = 0; i < variables.length; i++)
				dataset.mapVar.put(variables[i].name, i);
		}
	}
	*/
	public Instanciation(int nbMemory, DatasetInfo dataset)
	{
		this.dataset = dataset;
		values = new Integer[dataset.vars.length];
		nbVarInstanciees = 0;
		this.nbMemory = nbMemory;
	}
	
/*	public void saveNbVarInstanciees()
	{
		nbVarInstancieesSave = nbVarInstanciees;
	}

	public void loadNbVarInstanciees()
	{
		nbVarInstanciees = nbVarInstancieesSave;
	}*/

	/**
	 * An empty instanciation
	 */
	public Instanciation(DatasetInfo dataset)
	{
		this(-1, dataset);
	}
	
	static void setMemoryManager(InstanceMemoryManager memory)
	{
		Instanciation.memory = memory;
	}
	
	private boolean checkNbVarInstanciees()
	{
		int nb = 0;
		
		for(int i = 0; i < values.length; i++)
			if(values[i] != null)
				nb++;
		return nbVarInstanciees == nb;
	}
	
	public Instanciation cloneFromMemory()
	{
		Instanciation out = memory.getObject();
		for(int i = 0; i < values.length; i++)
			out.values[i] = values[i];
		out.nbVarInstanciees = nbVarInstanciees;
		assert out.checkNbVarInstanciees();
		return out;
	}
	
	public void copy(Instanciation out)
	{
		for(int i = 0; i < values.length; i++)
			out.values[i] = values[i];
		out.nbVarInstanciees = nbVarInstanciees;
		assert out.checkNbVarInstanciees();
	}
	
	public Instanciation clone()
	{
		Instanciation out = new Instanciation(dataset);
		for(int i = 0; i < values.length; i++)
			out.values[i] = values[i];
		out.nbVarInstanciees = nbVarInstanciees;
		assert out.checkNbVarInstanciees();
		return out;
	}
	
	public int getNbVarInstanciees()
	{
		return nbVarInstanciees;
	}
	
	/**
	 * Génère un entier pour les variables du CPT. Les variables peuvent ne pas être évaluées.
	 * @return
	 */
	public int getIndexCPT(int[] contextIndice)
	{
		int index = 0;
		for(int i = 0; i < contextIndice.length; i++)
		{
			int indice = contextIndice[i];
			Variable v = dataset.vars[indice];
			int valeur;
			if(values[indice] == null) // variable non évaluée
				valeur = v.domain;
			else
				valeur = values[indice];

			index = index * (v.domain+1) + valeur;
			
			// Overflow
			if(index < 0)
				return -1;
		}
		return index;
	}
	
	/**
	 * Génère un entier pour les variables du contexte. Toutes les variables du contexte sont forcément évaluées.
	 * @return
	 */
	public int getIndexCache(int[] contextIndice)
	{
		int index = 0;

		for(int i = 0; i < contextIndice.length; i++)
		{
			int indice = contextIndice[i];
			Variable v = dataset.vars[indice];
			int valeur = values[indice];

			index = index * v.domain + valeur;
			
			// Overflow
			if(index < 0)
				return -1;
		}
		return index;
	}
	
	/**
	 * Renvoie la taille du cache, c'est-à-dire le nombre de modalité du contexte
	 * @param contexte
	 * @return
	 */
	public static int getTailleCache(DatasetInfo dataset, List<String> contexte, double cacheFactor)
	{
		double taille = cacheFactor;
		for(String s : contexte)
		{
			taille *= dataset.vars[dataset.mapVar.get(s)].domain;
			if(taille < 0)
				return -1;
		}

		return (int) taille;
	}
	
	public static int getTailleCPT(DatasetInfo dataset, List<String> contexte)
	{
		int taille = 1;
		for(String s : contexte)
		{
			taille *= (dataset.vars[dataset.mapVar.get(s)].domain+1);
			if(taille < 0)
				return -1;
		}

		return taille;
	}
	
	/**
	 * Retourne l'instanciation projetée sur certaines variables
	 * @param dataset.vars
	 * @return
	 */
/*	public Instanciation subInstanciation(ArrayList<String> variables)
	{
//		Instanciation out = memory.getObject();
		Instanciation out = new Instanciation();
		out.nbVarInstanciees = 0;
		for(int i = 0; i < dataset.vars.length; i++)
			out.values[i] = null;
		for(String s : variables)
		{
			int i = dataset.mapVar.get(s);
			out.values[i] = values[i];
			if(values[i] != null)
				out.nbVarInstanciees++;
		}
		return out;
	}*/
	
	public Instanciation subInstanciation(int[] variables)
	{
		Instanciation out = memory.getObject();
//		Instanciation out = new Instanciation();
		out.nbVarInstanciees = 0;
		for(int i = 0; i < dataset.vars.length; i++)
			out.values[i] = null;

		for(int i = 0; i < variables.length; i++)
		{
			int indice = variables[i];
			out.values[indice] = values[indice];
			if(values[indice] != null)
				out.nbVarInstanciees++;
		}
		assert out.checkNbVarInstanciees();
		return out;
	}
	
	// On projette l'instanciation sur variables3 inter (variables union variables2)
	public Instanciation subInstanciation2(int[] variables, int[] variables2, int[] variables3)
	{
		Instanciation out = memory.getObject();
//		Instanciation out = new Instanciation();
		out.nbVarInstanciees = 0;
		for(int i = 0; i < dataset.vars.length; i++)
			out.values[i] = null;
		
		for(int i = 0; i < variables.length; i++)
		{
			int indice = variables[i];
			out.values[indice] = values[indice];
			if(values[indice] != null)
				out.nbVarInstanciees++;
		}
		
		for(int i = 0; i < variables2.length; i++)
		{
			int indice = variables2[i];
			if(out.values[indice] == null)
			{
				out.values[indice] = values[indice];
				if(values[indice] != null)
					out.nbVarInstanciees++;
			}
		}
		
		for(int i = 0; i < values.length; i++)
		{
			boolean conserve = false;
			for(int j = 0; j < variables3.length; j++)
				if(variables3[j] == i)
				{
					conserve = true;
					break;
				}
			if(!conserve && out.values[i] != null)
			{
				out.values[i] = null;
				out.nbVarInstanciees--;
			}	
		}
		
		assert out.checkNbVarInstanciees();
		return out;
	}
	/*
	public Instanciation subInstanciationRetire(int[] variables)
	{
		Instanciation out = memory.getObject();
//		Instanciation out = new Instanciation();
		out.nbVarInstanciees = 0;
		for(int i = 0; i < dataset.vars.length; i++)
			out.values[i] = values[i];

		out.nbVarInstanciees = nbVarInstanciees;

		for(int i = 0; i < variables.length; i++)
		{
			int indice = variables[i];
			if(values[indice] != null)
				out.nbVarInstanciees--;
			out.values[indice] = null;
		}
		return out;
	}*/
	
	@Override
	public String toString()
	{
		String out = "";
		for(int i = 0; i < dataset.vars.length; i++)
			if(values[i] != null)
				out += dataset.vars[i].name+" ("+dataset.vars[i].values.get(values[i])+") ";
		if(out.equals(""))
			return "(vide)";
		return out;
	}
	
	@Override
	public boolean equals(Object o)
	{
		if(!(o instanceof Instanciation))
			return false;
		for(int i = 0; i < dataset.vars.length; i++)
			if(values[i] != ((Instanciation)o).values[i])
				return false;
		return true;
	}
	
	@Override
	public int hashCode()
	{
		int out = 0;
		for(int i = 0; i < dataset.vars.length; i++)
			out += 32 * out + (values[i] == null ? 0 : (values[i] + 1)); // +1 sinon si ça vaut 0 c'est confondu avec l'absence de valeurs
		return out;
	}

	// Cette instance est-elle possible d'après les contraintes?
	public boolean isPossible(SALADD contraintes)
	{
		contraintes.getVDD().deconditionerAll();
		for(Variable v : dataset.vars)
			if(values[v.index] != null)
			{
				Var var = contraintes.getVDD().getVar(v.name);
				contraintes.getVDD().conditioner(var, var.conv(v.values.get(values[v.index])));
			}
		contraintes.propagation();
		return contraintes.isPossiblyConsistent();
	}
	

	public void conditionne(String v, String value)
	{
		conditionne(dataset.mapVar.get(v), value);
	}
	
	public void conditionne(String v, Integer value)
	{
		conditionne(dataset.mapVar.get(v), value);
	}

	void conditionne(int v, String value)
	{
		int index = dataset.vars[v].values.indexOf(value);
		assert index > -1;
//		if(index == -1)
//			System.out.println("Valeur inconnue pour "+vars[v].name+" : "+value);
		conditionne(v, index);
	}
	
	public void conditionne(int v, Integer value)
	{
		assert value != null;
		if(values[v] != value)
		{
			if(values[v] == null)
				nbVarInstanciees++;
			values[v] = value;
		}
		assert checkNbVarInstanciees();
	}
	
	public void deconditionne(int[] l)
	{
		for(int i = 0; i < l.length; i++)
			deconditionne(l[i]);
	}

	
	public void deconditionne(List<String> l)
	{
		for(String s : l)
			deconditionne(s);
	}

	public void deconditionne(String v)
	{
		deconditionne(dataset.mapVar.get(v));
	}
	
/*	public void deconditionne(Var v)
	{
		deconditionne(v.name);
	}*/

	public void deconditionne(int v)
	{
		if(values[v] != null)
		{
			nbVarInstanciees--;
			values[v] = null;
		}
		assert checkNbVarInstanciees();
	}
	
	public void deconditionneTout()
	{
		for(int i = 0; i < values.length; i++)
			values[i] = null;
		nbVarInstanciees = 0;
		assert checkNbVarInstanciees();
	}

	Integer[] out = new Integer[2*2];

	public Integer[] getHash(int nb)
	{
		out[2*(nb-1)] = null;
		int b = 0;
		int k = 0;
		while(out[2*(nb-1)] == null)
		{
			if(values[k] != null)
			{
				out[b++] = k;
				out[b++] = values[k];
			}
			k++;
		}
		return out;
	}
	
	public EnsembleVariables getEVConditionees()
	{
		EnsembleVariables out = new EnsembleVariables(nbVarInstanciees);
		int j = 0;
		for(int i = 0; i < values.length; i++)
			if(values[i] != null)
				out.vars[j++] = i;
		assert checkNbVarInstanciees();
		return out;
	}
	
	public List<String> getVarConditionees()
	{
		List<String> out = new ArrayList<String>();
		for(int i = 0; i < values.length; i++)
			if(values[i] != null)
				out.add(dataset.vars[i].name);
		return out;
	}
	
	public List<String> getVarDiff(Instanciation other)
	{
		List<String> out = new ArrayList<String>();
		for(int i = 0; i < values.length; i++)
			if(values[i] == null)
			{
				if(other.values[i] != null)
					out.add(dataset.vars[i].name);
				else
					continue;
			}
			else if(!values[i].equals(other.values[i]))
				out.add(dataset.vars[i].name);
		return out;
	}
/*
	public int getNbVarInstancieesSubInstanciation(int[] variables)
	{
		int out = 0;
		for(int i = 0; i < variables.length; i++)
		{
			int indice = variables[i];
			if(values[indice] != null)
				out++;
		}
		return out;
	}*/

	public int getNbVarInstancieesOnSubvars(int[] variables)
	{
		int nbVarInstanciees = 0;
		for(int i = 0; i < variables.length; i++)
		{
			int indice = variables[i];
			if(values[indice] != null)
				nbVarInstanciees++;
		}
		assert nbVarInstanciees <= this.nbVarInstanciees;
		return nbVarInstanciees;
	}
	

	public boolean isConditionne(String var)
	{
		return values[dataset.mapVar.get(var)] != null;
	}

	public boolean isConditionne(int i)
	{
		return values[i] != null;
	}
	
	public Integer getValue(int var)
	{
		return values[var];
	}
	
	public String getValue(String var)
	{
		return dataset.vars[dataset.mapVar.get(var)].values.get(values[dataset.mapVar.get(var)]);
	}

/*	public static int getNbVars()
	{
		return vars.length;
	}*/
	
	public int distance(Instanciation other)
	{
		int out = 0;
		
		for(int i = 0; i < dataset.vars.length; i++)
			if(values[i] != null && other.values[i] != null && !values[i].equals(other.values[i]))
				out++;

		return out;
	}
	
	public boolean isCompatible(Instanciation other)
	{
		for(int i = 0; i < dataset.vars.length; i++)
			if(values[i] != null && other.values[i] != null && !values[i].equals(other.values[i]))
				return false;
		return true;
	}
	
}
