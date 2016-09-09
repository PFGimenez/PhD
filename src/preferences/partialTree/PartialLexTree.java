package preferences.partialTree;

import java.math.BigInteger;
import java.util.ArrayList;

import compilateurHistorique.Instanciation;
import compilateurHistorique.MultiHistoComp;
import compilateurHistorique.Variable;
import preferences.loiProbabilite.Loi;

/**
 * Un arbre lexicographique partiel
 * @author pgimenez
 *
 */

public class PartialLexTree
{
	private boolean canHaveChildren; // TODO à la construction, mettre à jour
	private Instanciation instance;
	private PartialLexTree[] enfants;
	private PartialLexTree parent;
	private Variable variable;
	protected ArrayList<String> ordrePref;
	private BigInteger base;
	private static BigInteger nbFeuillesMax;
	public int nbMod; // le nombre de modalités de la variable de ce nœud
	private static int seuilNbExemples = 20;
	
	private static MultiHistoComp historique;
	
	/**
	 * Constructeur de l'arbre avec seulement une racine
	 * @param historique
	 */
	public PartialLexTree(MultiHistoComp historique)
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
	
	public static void setHistorique(MultiHistoComp historique2)
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
