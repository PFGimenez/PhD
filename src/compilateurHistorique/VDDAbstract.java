package compilateurHistorique;

import java.util.HashMap;

/**
 * Classe abstraite de l'arbre de compilation
 * @author pgimenez
 *
 */

public abstract class VDDAbstract
{
	public abstract int getNbInstances(String[] values, int nbVarInstanciees);
	public abstract void addInstanciation(String[] values);
	protected abstract void getNbInstancesToutesModalitees(HashMap<String, Integer> out, int nbVar, String[] values, int nbVarInstanciees);
}
