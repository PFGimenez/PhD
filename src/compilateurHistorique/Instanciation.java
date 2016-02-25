package compilateurHistorique;

import java.util.ArrayList;
import java.util.HashMap;

import compilateur.SALADD;
import compilateur.Var;

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
	Integer[] values;
	int nbVarInstanciees;

	static void setVars(Variable[] variables, HashMap<String, Integer> mapVariables)
	{
		vars = variables;
		mapVar = mapVariables;
	}
	
	public Instanciation()
	{
		values = new Integer[vars.length];
		nbVarInstanciees = 0;
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
	 * Génère un entier pour les variables du contexte.
	 * @return
	 */
	public int getIndexCache(int[] contextIndice)
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
	 * Renvoie la taille du cache, c'est-à-dire le nombre de modalité du contexte
	 * @param contexte
	 * @return
	 */
	public static int getTailleCache(ArrayList<String> contexte)
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
	public Instanciation subInstanciation(ArrayList<String> variables)
	{
// TODO
			
		Instanciation out = new Instanciation();
		out.nbVarInstanciees = 0;
		for(int i = 0; i < values.length; i++)
			if(variables.contains(vars[i].name))
			{
				out.values[i] = values[i];
				if(values[i] != null)
					out.nbVarInstanciees++;
			}
			else
				out.values[i] = null;

		return out;
	}
	
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

	public Integer[] getHash(int nb)
	{
		Integer[] out = new Integer[2*nb];
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
	
}
