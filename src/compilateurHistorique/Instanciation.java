package compilateurHistorique;

import java.util.ArrayList;
import java.util.HashMap;

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
	
	Instanciation(int length)
	{
		values = new String[length];
	}
	
	public Instanciation clone()
	{
		Instanciation out = new Instanciation(values.length);
		for(int i = 0; i < values.length; i++)
			out.values[i] = values[i];
		out.nbVarInstanciees = nbVarInstanciees;
		return out;
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
			int valeur = v.values.indexOf(values[indice]);

			if(valeur == -1)
				System.out.println("Erreur fatale dans getIndexCache! (Instanciation.java)");

			index = index * v.domain + valeur;
		}
		return index;
	}
	
	/**
	 * Renvoie la taille du cache, c'est-à-dire le nombre de modalité du contexte
	 * @param contexte
	 * @return
	 */
	public int getTailleCache(ArrayList<String> contexte)
	{
		int taille = 1;
		for(String s : contexte)
			taille *= vars[mapVar.get(s)].domain;

		return taille;
	}
	
	/**
	 * Retourne l'instanciation projetée sur certaines variables
	 * @param vars
	 * @return
	 */
	public Instanciation subInstanciation(ArrayList<String> variables)
	{
		Instanciation out = new Instanciation(values.length);

		for(int i = 0; i < values.length; i++)
			if(variables.contains(vars[i].name))
				out.values[i] = values[i];
			else
				out.values[i] = null;

		out.nbVarInstanciees = variables.size();

		return out;
	}

	public String getValue(String variable)
	{
		return values[mapVar.get(variable)];
	}

}
