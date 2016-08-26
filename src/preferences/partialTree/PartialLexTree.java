package preferences.partialTree;

import java.util.ArrayList;

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
	private PartialLexTree[] enfants;
	private PartialLexTree parent;
	private Variable variable;
	protected ArrayList<String> ordrePref;
	private boolean complet;
	
	/**
	 * Constructeur de l'arbre avec seulement une racine
	 * @param historique
	 */
	public PartialLexTree(MultiHistoComp historique, String variable, ArrayList<String> ordrePref)
	{
		
	}

	/**
	 * Calcule la vraisemblance de cet arbre
	 * A n'utiliser que si l'arbre est complet
	 * @param loi
	 * @return
	 */
	public static double calculeVraisemblance(PartialLexTree tree, Loi loi)
	{
		if(!tree.isFinished())
			throw new IllegalArgumentException("L'arbre n'est pas terminé !");
		
		PartialLexTree racine = tree;
		while(racine.parent != null)
			racine = racine.parent;
		
		// Racine contient la racine de l'arbre donné en paramètre
		
		return 0;
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

	/**
	 * Cet arbre est-il fini ?
	 * @return
	 */
	public boolean isFinished()
	{
		return complet;
	}

	public PartialLexTree getClone()
	{
		// TODO Auto-generated method stub
		return null;
	}
	
}
