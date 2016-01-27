package compilateurHistorique;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;


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
 * VDD utilisé pour la compilation d'historique
 * @author pgimenez
 *
 */

public class VDD extends VDDAbstract implements Serializable
{
	private static final long serialVersionUID = 1L;

	private static Variable[] variables;
	private static int nbVarTotal;
	private Variable var;
	private VDDAbstract[] subtrees;
//	private ArrayList<String> subtreeIndexToValue = new ArrayList<String>();
	private int nbInstances = 0;
	
	/**
	 * Constructeur public, crée la racine
	 */
	public VDD()
	{
		this(0);
	}

	/**
	 * Constructeur. Prend en paramètre la profondeur du nœud
	 * @param indiceDansOrdre
	 */
	private VDD(int profondeur)
	{
		this.var = variables[profondeur];
		subtrees = new VDDAbstract[var.domain];
	}
	
	/**
	 * A appeler avant de construire un arbre. L'arbre va fonder l'ordre sur l'attribut "pos" des variables.
	 * Le tri est faitpar HistoComp
	 * @param ordreVariables
	 */
	public static void setOrdreVariables(Variable[] ordre)
	{
		variables = ordre;
		nbVarTotal = ordre.length;
	}

	public HashMap<String, Integer> getNbInstancesToutesModalitees(int nbVar, String[] values, ArrayList<String> possibles, int nbVarInstanciees)
	{
		// Initialisation de la hashmap
		HashMap<String, Integer> proba = new HashMap<String, Integer>();
		getNbInstancesToutesModalitees(proba, nbVar, values, possibles, nbVarInstanciees);
		return proba;
	}
	
	@Override
	protected void getNbInstancesToutesModalitees(HashMap<String, Integer> out, int nbVar, String[] values, ArrayList<String> possibles, int nbVarInstanciees)
	{
		if(nbVar == var.profondeur)
		{
			/**
			 * C'est cette variable qu'on doit partitionner
			 */
			for(int i = 0; i < var.domain; i++)
			{
				// ce fils n'existe pas
				if(subtrees[i] == null)
					continue;
				
				String value = var.values.get(i);
		
				if(possibles != null && !possibles.contains(value))
					continue;
				
				Integer precedenteValeur = out.get(value);

				// Si cette valeur n'a encore jamais été vue
				if(precedenteValeur == null)
					precedenteValeur = 0;

				precedenteValeur += subtrees[i].getNbInstances(values, nbVarInstanciees);

				// On s'assure que toutes les valeurs seront strictement positives
				if(precedenteValeur > 0)
					out.put(value, precedenteValeur);
			}
		}
		else if(values[var.profondeur] != null)
		{
			// cette variable est instanciée			
			int indice = var.values.indexOf(values[var.profondeur]);
			
			// Pas de sous-arbre avec cette instanciation? Alors il n'y a aucun exemple et on ne fait rien
			
			if(subtrees[indice] != null)
				subtrees[indice].getNbInstancesToutesModalitees(out, nbVar, values, possibles, nbVarInstanciees - 1);
		}
		else
		{
			// la variable n'est pas instanciée
			for(int i = 0; i < var.values.size(); i++)
				if(subtrees[i] != null)
					subtrees[i].getNbInstancesToutesModalitees(out, nbVar, values, possibles, nbVarInstanciees);
		}
	}
	
	@Override
	public int getNbInstances(String[] values, int nbVarInstanciees)
	{
		if(nbVarInstanciees == 0)
			return nbInstances;
		
		String value = values[var.profondeur];

		// cette variable est instanciée
		if(value != null)
		{
			int indice = var.values.indexOf(value);
			
			// Pas de sous-arbre? Alors il n'y a aucun exemple
			if(subtrees[indice] == null)
				return 0;
			else
				return subtrees[indice].getNbInstances(values, nbVarInstanciees - 1);
		}
		else
		{
			int somme = 0;
			for(int i = 0; i < var.values.size(); i++)
				if(subtrees[i] != null)
					somme += subtrees[i].getNbInstances(values, nbVarInstanciees);
			return somme;
		}
	}
	
	@Override
	public void addInstanciation(String[] values)
	{
		nbInstances++;
		int indice = var.values.indexOf(values[var.profondeur]);

		if(subtrees[indice] == null)
		{
			// Si c'est la dernière variable, on construit une feuille
			if(var.profondeur == nbVarTotal - 1)
				subtrees[indice] = new VDDLeaf();
			else
				subtrees[indice] = new VDD(var.profondeur + 1);
		}
		
		subtrees[indice].addInstanciation(values);			
	}

	@Override
	public int getNbNoeuds()
	{
		int somme = 1;
		for(int i = 0; i < var.values.size(); i++)
			if(subtrees[i] != null)
				somme += subtrees[i].getNbNoeuds();
		return somme;
	}
}
