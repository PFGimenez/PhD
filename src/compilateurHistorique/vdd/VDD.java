package compilateurHistorique.vdd;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;

import compilateurHistorique.Instanciation;
import compilateurHistorique.Variable;


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
 * @author Pierre-François Gimenez
 *
 */

public class VDD extends VDDAbstract implements Serializable
{
	private static final long serialVersionUID = 1L;

	private Variable[] variables;
	private int[] variablesIndex;
	private int nbVarTotal;
	private Variable var;
	private int indiceVarDansOrdre;
	private VDDAbstract[] subtrees;
	
	/**
	 * Constructeur public, crée la racine
	 */
	public VDD(Variable[] ordre)
	{
		this(ordre, 0);
	}

	/**
	 * Constructeur. Prend en paramètre la profondeur du nœud
	 * @param indiceDansOrdre
	 */
	private VDD(Variable[] ordre, int indiceVarDansOrdre)
	{
		this.indiceVarDansOrdre = indiceVarDansOrdre;
		variables = ordre;
		nbVarTotal = ordre.length;
		this.var = variables[indiceVarDansOrdre];
		variablesIndex = new int[ordre.length];
		for(int i = 0; i < variablesIndex.length; i++)
			variablesIndex[i] = variables[i].index;
		subtrees = new VDDAbstract[var.domain];
	}
	
	public HashMap<String, Integer> getNbInstancesToutesModalitees(int nbVar, Instanciation instance)
	{
		// Initialisation de la hashmap
		HashMap<String, Integer> proba = new HashMap<String, Integer>();
		getNbInstancesToutesModalitees(proba, nbVar, instance);
		return proba;
	}
	
	@Override
	public void getNbInstancesToutesModalitees(HashMap<String, Integer> out, int nbVar, Instanciation instance)
	{
		getNbInstancesToutesModalitees(out, nbVar, instance, instance.getNbVarInstancieesOnSubvars(variablesIndex));
	}

	@Override
	protected void getNbInstancesToutesModalitees(HashMap<String, Integer> out, int nbVar, Instanciation instance, int nbVarInstanciees)
	{
		if(nbVar == var.index)
		{
			/**
			 * C'est cette variable qu'on doit partitionner
			 */
			for(int i = 0; i < var.domain; i++)
			{
				// ce fils n'existe pas
				if(subtrees[i] == null)
					continue;

				Integer precedenteValeur;						

				String value = var.values.get(i);
		
//				if(possibles != null && !possibles.contains(value))
//					continue;
				
				precedenteValeur = out.get(value);

				// Si cette valeur n'a encore jamais été vue
				if(precedenteValeur == null)
					precedenteValeur = 0;

				precedenteValeur += subtrees[i].getNbInstances(instance, nbVarInstanciees-1);

				// On s'assure que toutes les valeurs seront strictement positives
				if(precedenteValeur > 0)
					out.put(value, precedenteValeur);
			}
		}
		else if(instance.values[var.index] != null)
		{
			// cette variable est instanciée			
			int indice = instance.values[var.index];
			
			// Pas de sous-arbre avec cette instanciation? Alors il n'y a aucun exemple et on ne fait rien
			
			if(subtrees[indice] != null)
				subtrees[indice].getNbInstancesToutesModalitees(out, nbVar, instance, nbVarInstanciees-1);
		}
		else
		{
			// la variable n'est pas instanciée
			for(int i = 0; i < var.values.size(); i++)
				if(subtrees[i] != null)
					subtrees[i].getNbInstancesToutesModalitees(out, nbVar, instance, nbVarInstanciees);
		}
	}
	
	@Override
	public void addInstanciation(Integer[] values)
	{
		nbInstances++;
		int indice = values[var.index];
		if(subtrees[indice] == null)
		{
			// Si c'est la dernière variable, on construit une feuille
			if(indiceVarDansOrdre == nbVarTotal - 1)
				subtrees[indice] = new VDDLeaf();
			else
				subtrees[indice] = new VDD(variables, indiceVarDansOrdre + 1);
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

	private final static int tailleMemoire = 1 << 16;
	private static VDDAbstract[] memoire = new VDDAbstract[tailleMemoire];

	public int getNbInstances(Instanciation instance)
	{
		return getNbInstances(instance, instance.getNbVarInstancieesOnSubvars(variablesIndex));
	}
	
	@Override
	protected int getNbInstances(Instanciation instance, int nbVarInstanciees)
	{		
		Integer[] values = instance.values;
		
		int prochainLecture = 0, prochainEcriture = 0;
		
		memoire[prochainEcriture++] = this;
		this.nbVarInstanciees = nbVarInstanciees;
		
/*		int test = 0;
		for(int i = 0; i < values.length; i++)
			if(values[i] != null)
				System.out.println("Values : "+i);
		System.out.println("Compté : "+test+", normalement : "+nbVarInstanciees);
	
		for(int i = 0; i < ((VDD)vddDebut).variables.length; i++)
			System.out.println("Variables : "+((VDD)vddDebut).variables[i].profondeur);
		
		System.out.println("nbVarInstanciees : "+nbVarInstanciees+", taille ADD : "+((VDD)vddDebut).variables.length);
		*/
		int somme = 0;
		Integer indice;
		while(prochainLecture != prochainEcriture)
		{
			VDDAbstract vddabs = memoire[prochainLecture++];
			prochainLecture &= tailleMemoire - 1;
			
//			System.out.println(vddabs.nbVarInstanciees);

			if(vddabs.nbVarInstanciees == 0)
			{
				somme += vddabs.nbInstances;
				continue;
			}

			assert vddabs instanceof VDD : instance.toString();
			
			VDD vdd = (VDD) vddabs; // on est sûr que c'est pas une feuille
/*			if(vdd.lineaire)
			{
				System.out.println("C'est linéaire");
				boolean out = false;
				for(int i = 0; i < vdd.varsLineaire.length; i++)
				{
					int v = vdd.varsLineaire[i];
					if(values[v] != null && values[v] != vdd.valeursLineaire[i])
					{
						out = true;
						break;
					}
				}
				if(out)
					continue;
				somme += vddabs.nbInstances;
			}
			else*/
			//{				
				indice = values[vdd.var.index];
		
				// cette variable est instanciée
				if(indice != null)
				{
					// Pas de sous-arbre? Alors il n'y a aucun exemple
					if(vdd.subtrees[indice] != null)
					{
						memoire[prochainEcriture++] = vdd.subtrees[indice];
						prochainEcriture &= tailleMemoire - 1;
						assert prochainEcriture != prochainLecture;
						vdd.subtrees[indice].nbVarInstanciees = vdd.nbVarInstanciees - 1;
					}
				}
				else
				{
					for(int i = 0; i < vdd.var.values.size(); i++)
						if(vdd.subtrees[i] != null)
						{
							memoire[prochainEcriture++] = vdd.subtrees[i];
							prochainEcriture &= tailleMemoire - 1;
							assert prochainEcriture != prochainLecture;
							vdd.subtrees[i].nbVarInstanciees = vdd.nbVarInstanciees;
						}
				}
			//}
		}
		return somme;
	}
	
	protected void affichePrivate(BufferedWriter output) throws IOException
	{
		output.write(nb+" [label=\""+var.name+" "+indiceVarDansOrdre+"\"]");
		output.newLine();
		for(int i = 0; i < var.values.size(); i++)
		{
			if(subtrees[i] != null)
			{
				subtrees[i].affichePrivate(output);
				output.write(nb+" -> "+subtrees[i].nb+";");
				output.newLine();
			}
		}
	}
}
