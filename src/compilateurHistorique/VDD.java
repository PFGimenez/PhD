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

	private Variable[] variables;
	private int nbVarTotal;
	private Variable var;
	private int indiceVarDansOrdre;
	private VDDAbstract[] subtrees;
//	private ArrayList<String> subtreeIndexToValue = new ArrayList<String>();
//	protected boolean lineaire;
//	protected int[] varsLineaire;
//	protected int[] valeursLineaire;

	/**
	 * Constructeur public, crée la racine
	 */
	public VDD(Variable[] ordre)
	{
		this(ordre, 0);
//		for(int i= 0; i < ordre.length; i++)
//			System.out.println(ordre[i].profondeur);
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
		subtrees = new VDDAbstract[var.domain];
	}
	
	public HashMap<String, Integer> getNbInstancesToutesModalitees(int nbVar, Integer[] values, ArrayList<String> possibles, int nbVarInstanciees)
	{
		// Initialisation de la hashmap
		HashMap<String, Integer> proba = new HashMap<String, Integer>();
		getNbInstancesToutesModalitees(proba, nbVar, values, possibles, nbVarInstanciees);
		return proba;
	}
	
	@Override
	protected void getNbInstancesToutesModalitees(HashMap<String, Integer> out, int nbVar, Integer[] values, ArrayList<String> possibles, int nbVarInstanciees)
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

				Integer precedenteValeur;						

				String value = var.values.get(i);
		
				if(possibles != null && !possibles.contains(value))
					continue;
				
				precedenteValeur = out.get(value);

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
			int indice = values[var.profondeur];
			
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
	public int getNbInstances(Integer[] values, int nbVarInstanciees)
	{
		if(nbVarInstanciees == 0)
			return nbInstances;
		
		Integer indice = values[var.profondeur];

		// cette variable est instanciée
		if(indice != null)
		{
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
	
/*	public boolean computeLineaire()
	{
		int nbFils = 0;
		for(int i = 0; i < var.values.size(); i++)
			if(subtrees[i] != null)
				nbFils++;
		
		// On a un seul fils. On est linéaire si le fils l'est
		if(nbFils == 1)
		{
			for(int i = 0; i < var.values.size(); i++)
				if(subtrees[i] != null)
					lineaire = subtrees[i].computeLineaire();
		}
		else // On a plusieurs fils (donc on n'est pas linéaire)
		{
			lineaire = false;
			for(int i = 0; i < var.values.size(); i++)
				if(subtrees[i] != null && subtrees[i].computeLineaire() && !(subtrees[i] instanceof VDDLeaf))
				{
					// Ce fils est linéaire. Il faut calculer ce qu'il faut
					VDD fils = (VDD)subtrees[i];
//					System.out.println(fils.var.profondeur);
					fils.varsLineaire = new int[variables.length-fils.var.profondeur];
					fils.valeursLineaire = new int[variables.length-fils.var.profondeur];
					VDD tmp = fils;
					// Le premier cas est particulier, car this a plusieurs enfants
					for(int j = 0; j < variables.length-fils.var.profondeur; j++)
					{
						fils.varsLineaire[j] = tmp.var.profondeur;
						int k;
						for(k = 0; k < tmp.var.values.size(); k++)
							if(tmp.subtrees[k] != null)
							{
								if(tmp.subtrees[k] instanceof VDD)
									tmp = (VDD) tmp.subtrees[k];
								break;
							}
						fils.valeursLineaire[j] = k;
					}
				}
		}
		return lineaire;
	}
	*/
	@Override
	public void addInstanciation(Integer[] values)
	{
		nbInstances++;
		int indice = values[var.profondeur];
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

	// TODO valeur moins grande, plus adaptée
	private final static int tailleMemoire = 1 << 14;
	private static VDDAbstract[] memoire = new VDDAbstract[tailleMemoire];
	
	public static int getNbInstancesStatic(VDDAbstract vddDebut, Integer[] values, int nbVarInstanciees)
	{
//		System.out.println("Instance :");
//		for(int i = 0; i < values.length; i++)
//			if(values[i] != null)
//				System.out.println(values[i]);
		int prochainLecture = 0, prochainEcriture = 0;
		memoire[prochainEcriture++] = vddDebut;
		vddDebut.nbVarInstanciees = nbVarInstanciees;
		
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
			{				
				Integer indice = values[vdd.var.profondeur];
		
				// cette variable est instanciée
				if(indice != null)
				{
					// Pas de sous-arbre? Alors il n'y a aucun exemple
					if(vdd.subtrees[indice] != null)
					{
						memoire[prochainEcriture++] = vdd.subtrees[indice];
						prochainEcriture &= tailleMemoire - 1;
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
							vdd.subtrees[i].nbVarInstanciees = vdd.nbVarInstanciees;
						}
				}
			}
		}
		return somme;
	}
}
