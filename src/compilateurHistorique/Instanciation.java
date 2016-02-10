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
	 * Visibitilé package. Ainsi, HistComp peut l'utiliser mais pas les autres
	 */
	private static Variable[] vars;
	private static HashMap<String, Integer> mapVar;
	String[] values;
	int nbVarInstanciees;

	static void setVars(Variable[] variables, HashMap<String, Integer> mapVariables)
	{
		vars = variables;
		mapVar = mapVariables;
	}
	
	public Instanciation()
	{
		values = new String[vars.length];
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
	public int getIndexCache(ArrayList<String> contexte)
	{
		int index = 0;
		for(String s : contexte)
		{
			int indice = mapVar.get(s);
			Variable v = vars[indice];
			int valeur;
			if(values[indice] == null) // variable non évaluée
				valeur = v.domain;
			else
			{
				valeur = v.values.indexOf(values[indice]);
				if(valeur == -1)
					System.out.println("Erreur fatale dans getIndexCache! (Instanciation.java) : valeur "+values[indice]+" inconnu dans la variable "+v.name);
			}

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
		Instanciation out = new Instanciation();
		out.nbVarInstanciees = 0;
		for(int i = 0; i < values.length; i++)
			if(variables.contains(vars[i].name))
			{
				out.values[i] = values[i];
				out.nbVarInstanciees++;
			}
			else
				out.values[i] = null;

		return out;
	}

	public String getValue(String variable)
	{
		return values[mapVar.get(variable)];
	}
	
	@Override
	public String toString()
	{
		String out = "";
		for(int i = 0; i < vars.length; i++)
			if(values[i] != null)
				out += vars[i].name+" ("+values[i]+") ";
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
				contraintes.getVDD().conditioner(var, var.conv(values[v.profondeur]));
			}
		contraintes.propagation();
		return contraintes.isPossiblyConsistent();
	}
	

	public void conditionne(String v, String value)
	{
		conditionne(mapVar.get(v), value);
	}
	
	public void conditionne(String v, int value)
	{
		int var = mapVar.get(v);
		conditionne(var, vars[var].values.get(value));
	}

	void conditionne(int v, String value)
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
	
}
