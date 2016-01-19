package compilateurHistorique;

import java.io.Serializable;
import java.util.HashMap;

/**
 * Feuille de l'arbre
 * @author pgimenez
 *
 */

public class VDDLeaf extends VDDAbstract implements Serializable
{
	private static final long serialVersionUID = 1L;
	private int nbInstances = 0;

	@Override
	public void addInstanciation(String[] values)
	{
		nbInstances++;
	}
	
	@Override
	public int getNbInstances(String[] values, int nbVarInstanciees)
	{
		if(nbVarInstanciees > 0)
			System.out.println("Erreur! "+nbVarInstanciees+" != 0");
		return nbInstances;
	}
	
	@Override
	protected void getNbInstancesToutesModalitees(HashMap<String, Integer> out, int nbVar, String[] values, int nbVarInstanciees)
	{}
}
