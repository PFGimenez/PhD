package compilateurHistorique;

import java.util.ArrayList;
import java.util.HashMap;

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
 * @author pgimenez
 *
 */

public class Instanciation
{
	/**
	 * Visibilité package. Ainsi, HistComp peut l'utiliser mais pas les autres
	 */
	private static Variable[] vars;
	private static HashMap<String, Integer> mapVar;
	private static InstanceMemoryManager memory;
	public final int nbMemory;

	Integer[] values;
	int nbVarInstanciees;
	int nbVarInstancieesSave;

	static void setVars(Variable[] variables, HashMap<String, Integer> mapVariables)
	{
		if(vars == null)
		{
			vars = variables;
			mapVar = mapVariables;
		}
	}
	
	public Instanciation(int nbMemory)
	{
		values = new Integer[vars.length];
		nbVarInstanciees = 0;
		this.nbMemory = nbMemory;
	}
	
	public void saveNbVarInstanciees()
	{
		nbVarInstancieesSave = nbVarInstanciees;
	}

	public void loadNbVarInstanciees()
	{
		nbVarInstanciees = nbVarInstancieesSave;
	}

	public Instanciation()
	{
		this(-1);
	}
	
	static void setMemoryManager(InstanceMemoryManager memory)
	{
		Instanciation.memory = memory;
	}
	
	public Instanciation clone()
	{
		Instanciation out = new Instanciation();
		for(int i = 0; i < values.length; i++)
			out.values[i] = values[i];
		out.nbVarInstanciees = nbVarInstanciees;
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
			Variable v = vars[indice];
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
			Variable v = vars[indice];
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
	public static int getTailleCache(ArrayList<String> contexte, double cacheFactor)
	{
		double taille = cacheFactor;
		for(String s : contexte)
		{
			taille *= vars[mapVar.get(s)].domain;
			if(taille < 0)
				return -1;
		}

		return (int) taille;
	}
	
	public static int getTailleCPT(ArrayList<String> contexte)
	{
		int taille = 1;
		for(String s : contexte)
		{
			taille *= (vars[mapVar.get(s)].domain+1);
			if(taille < 0)
				return -1;
		}

		return taille;
	}
	
	/**
	 * Retourne l'instanciation projetée sur certaines variables
	 * @param vars
	 * @return
	 */
/*	public Instanciation subInstanciation(ArrayList<String> variables)
	{
//		Instanciation out = memory.getObject();
		Instanciation out = new Instanciation();
		out.nbVarInstanciees = 0;
		for(int i = 0; i < vars.length; i++)
			out.values[i] = null;
		for(String s : variables)
		{
			int i = mapVar.get(s);
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
		for(int i = 0; i < vars.length; i++)
			out.values[i] = null;

		for(int i = 0; i < variables.length; i++)
		{
			int indice = variables[i];
			out.values[indice] = values[indice];
			if(values[indice] != null)
				out.nbVarInstanciees++;
		}
		return out;
	}
	
	// On projette l'instanciation sur variables3 inter (variables union variables2)
	public Instanciation subInstanciation2(int[] variables, int[] variables2, int[] variables3)
	{
		Instanciation out = memory.getObject();
//		Instanciation out = new Instanciation();
		out.nbVarInstanciees = 0;
		for(int i = 0; i < vars.length; i++)
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
		
		return out;
	}
	/*
	public Instanciation subInstanciationRetire(int[] variables)
	{
		Instanciation out = memory.getObject();
//		Instanciation out = new Instanciation();
		out.nbVarInstanciees = 0;
		for(int i = 0; i < vars.length; i++)
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
		for(int i = 0; i < vars.length; i++)
			if(values[i] != null)
				out += vars[i].name+" ("+vars[i].values.get(values[i])+") ";
		return out;
	}
	
	@Override
	public boolean equals(Object o)
	{
		if(!(o instanceof Instanciation))
			return false;
		for(int i = 0; i < vars.length; i++)
			if(values[i] != ((Instanciation)o).values[i])
				return false;
		return true;
	}
	
	@Override
	public int hashCode()
	{
		int out = 0;
		for(int i = 0; i < vars.length; i++)
			if(values[i] != null)
				out = 2*out + values[i];
		return out;
	}

	// Cette instance est-elle possible d'après les contraintes?
	public boolean isPossible(SALADD contraintes)
	{
		contraintes.getVDD().deconditionerAll();
		for(Variable v : vars)
			if(values[v.profondeur] != null)
			{
				Var var = contraintes.getVDD().getVar(v.name);
				contraintes.getVDD().conditioner(var, var.conv(v.values.get(values[v.profondeur])));
			}
		contraintes.propagation();
		return contraintes.isPossiblyConsistent();
	}
	

	public void conditionne(String v, String value)
	{
		conditionne(mapVar.get(v), value);
	}
	
	public void conditionne(String v, Integer value)
	{
		conditionne(mapVar.get(v), value);
	}

	void conditionne(int v, String value)
	{
		conditionne(v, vars[v].values.indexOf(value));
	}
	
	public void conditionne(int v, Integer value)
	{
		if(values[v] != value)
		{
			if(values[v] == null)
				nbVarInstanciees++;
			values[v] = value;
		}
	}

	public void deconditionne(int[] l)
	{
		for(int i = 0; i < l.length; i++)
			deconditionne(l[i]);
	}

	
	public void deconditionne(ArrayList<String> l)
	{
		for(String s : l)
			deconditionne(s);
	}

	public void deconditionne(String v)
	{
		deconditionne(mapVar.get(v));
	}
	
/*	public void deconditionne(Var v)
	{
		deconditionne(v.name);
	}*/

	void deconditionne(int v)
	{
		if(values[v] != null)
		{
			nbVarInstanciees--;
			values[v] = null;
		}
	}
	
	public void deconditionneTout()
	{
		for(int i = 0; i < values.length; i++)
			values[i] = null;
		nbVarInstanciees = 0;
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
	
	public ArrayList<String> getVarConditionees()
	{
		ArrayList<String> out = new ArrayList<String>();
		for(int i = 0; i < values.length; i++)
			if(values[i] != null)
				out.add(vars[i].name);
		return out;
	}
	
	public ArrayList<String> getVarDiff(Instanciation other)
	{
		ArrayList<String> out = new ArrayList<String>();
		for(int i = 0; i < values.length; i++)
			if(values[i] == null)
			{
				if(other.values[i] != null)
					out.add(vars[i].name);
				else
					continue;
			}
			else if(!values[i].equals(other.values[i]))
				out.add(vars[i].name);
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

	public void updateNbVarInstanciees(int[] variables)
	{
		nbVarInstanciees = 0;
		for(int i = 0; i < variables.length; i++)
		{
			int indice = variables[i];
			if(values[indice] != null)
				nbVarInstanciees++;
		}
	}
	

	public boolean isConditionne(int i)
	{
		return values[i] != null;
	}
	
}
