package preferences.partialTree;

import java.math.BigInteger;
import java.util.ArrayList;

import compilateurHistorique.Instanciation;
import compilateurHistorique.HistoriqueCompile;
import compilateurHistorique.Variable;
import preferences.loiProbabilite.Loi;

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
 * Un arbre lexicographique partiel
 * N'EST PAS UTILISABLE
 * @author Pierre-François Gimenez
 *
 */

public class PartialLexTree
{
	private boolean canHaveChildren;
	private Instanciation instance;
	private PartialLexTree[] enfants;
	private PartialLexTree parent;
	private Variable variable;
	protected ArrayList<String> ordrePref;
	private BigInteger base;
	private static BigInteger nbFeuillesMax;
	public int nbMod; // le nombre de modalités de la variable de ce nœud
	private static int seuilNbExemples = 20;
	
	private static HistoriqueCompile historique;
	
	/**
	 * Constructeur de l'arbre avec seulement une racine
	 * @param historique
	 */
	public PartialLexTree(HistoriqueCompile historique)
	{
		parent = null;
		instance = new Instanciation();
		canHaveChildren = historique.getNbInstances(instance) > seuilNbExemples;
		if(canHaveChildren)
			enfants = new PartialLexTree[nbMod];
	}

	/**
	 * Calcule la vraisemblance de cet arbre
	 * A n'utiliser que si l'arbre est complet
	 * @param loi
	 * @return
	 */
	public double calculeVraisemblance(Loi loi)
	{
//		if(!isFinished())
//			throw new IllegalArgumentException("L'arbre n'est pas terminé !");
		
		PartialLexTree racine = this;
		while(racine.parent != null)
			racine = racine.parent;
		
		// Racine contient la racine de l'arbre donné en paramètre
		
		// On met à jour "base" pour pouvoir calculer les rang
		racine.computeBase(nbFeuillesMax);
		return racine.calculeVraisemblanceLocale(BigInteger.ZERO, loi);
	}
	
	/**
	 * Calcule la vraisemblance d'un sous-arbre
	 */
	private double calculeVraisemblanceLocale(BigInteger sofar, Loi loi)
	{
		double out = 0;
		/**
		 * On a des enfants : on leur demande leur vraisemblance
		 */
		if(enfants != null)
		{
			for(int i = 0; i < nbMod; i++)
				out += enfants[i].calculeVraisemblanceLocale(sofar.add(base.multiply(BigInteger.valueOf(i))), loi);
			return out;
		}
		
		else
			// nb exemple * log(p(rang))
			return historique.getNbInstances(instance)*loi.getVraisemblance(sofar.add(base.divide(BigInteger.valueOf(2)))); // rang moyen
	}
	
	private void computeBase(BigInteger base)
	{
		this.base = base.divide(BigInteger.valueOf(nbMod));
		if(enfants != null)
			for(PartialLexTree e : enfants)
				e.computeBase(this.base);
	}
	
	/**
	 * Calcule un majorant de la vraisemblance
	 * @param loi
	 * @return
	 */
	public double majoreVraisemblance(Loi loi)
	{
		return 0;
	}

	public boolean computeComplet()
	{
		if(enfants == null) // si on a pas d'enfants, soit c'est qu'on ne peut pas en avoir (et on est complet), soit c'est qu'on est incomplet
			return !canHaveChildren;
		else
		{
			// un nœud est complet si tous ses enfants le sont
			for(int i = 0; i < nbMod; i++)
				if(!enfants[i].computeComplet())
					return false;
			return true;
		}
	}
	
	public void setVariable(Variable v, ArrayList<String> ordrePref)
	{
		nbMod = v.domain;
		variable = v;
		this.ordrePref = ordrePref;
	}
	
	/**
	 * Constructeur d'un enfant
	 * @param parent
	 * @param v
	 * @param valueParent
	 * @param ordrePref
	 */
	public PartialLexTree(PartialLexTree parent, String valueParent)
	{
		this.parent = parent;
		instance = parent.instance.clone();
		instance.conditionne(parent.variable.name, valueParent);
		canHaveChildren = historique.getNbInstances(instance) > seuilNbExemples;
		if(canHaveChildren)
			enfants = new PartialLexTree[nbMod];
	}

	public PartialLexTree(PartialLexTree incompleteTree)
	{
		this.parent = incompleteTree.parent;
		instance = incompleteTree.instance.clone();
		canHaveChildren = incompleteTree.canHaveChildren;		
		if(canHaveChildren)
			enfants = new PartialLexTree[nbMod];	
	}
	
	public static void setHistorique(HistoriqueCompile historique2)
	{
		if(historique == null)
			historique = historique2;		
	}

	public ArrayList<Variable> getFreeVariables()
	{
		ArrayList<Variable> free = historique.getVar();
		free.removeAll(historique.getVarConnues(parent.instance));
		return free;
	}
	
}
