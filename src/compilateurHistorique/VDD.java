package compilateurHistorique;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

import compilateur.Var;

/**
 * Arbre de compilation
 * @author pgimenez
 *
 */

public class VDD extends VDDAbstract implements Serializable
{
	private static final long serialVersionUID = 1L;

	private static int[] domainVariables;
	private int profondeur;
	private static int nbVarTotal;
	private VDDAbstract[] subtrees;
	private ArrayList<String> subtreeIndexToValue = new ArrayList<String>();
	private int nbInstances = 0;
	private static final int tailleDomaineParDefaut = 100; // on doit s'assurer que c'est plus grand que tous les domaines
	
	/**
	 * Constructeur public, créé la racine
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
		this.profondeur = profondeur;
		subtrees = new VDDAbstract[domainVariables[profondeur]];
	}
	
	/**
	 * A appeler avant de construire un arbre. L'arbre va fonder l'ordre sur l'attribut "pos" des variables.
	 * Le tri est faitpar HistoComp
	 * @param ordreVariables
	 */
	public static void setOrdreVariables(Var[] ordre)
	{
		nbVarTotal = ordre.length;
		domainVariables = new int[nbVarTotal];
		for(int i = 0; i < ordre.length; i++)
			domainVariables[i] = ordre[i].domain;
	}
	
	public static void setOrdreVariables(int nbVarTotalP)
	{
		nbVarTotal = nbVarTotalP;
		domainVariables = new int[nbVarTotal];
		for(int i = 0; i < nbVarTotal; i++)
			domainVariables[i] = tailleDomaineParDefaut;
	}
	
	public HashMap<String, Integer> getNbInstancesToutesModalitees(int nbVar, String[] values, int nbVarInstanciees)
	{
		// Initialisation de la hashmap
		HashMap<String, Integer> proba = new HashMap<String, Integer>();
		getNbInstancesToutesModalitees(proba, nbVar, values, nbVarInstanciees);
		return proba;
	}
	
	@Override
	protected void getNbInstancesToutesModalitees(HashMap<String, Integer> out, int nbVar, String[] values, int nbVarInstanciees)
	{
		if(nbVar == profondeur)
		{
			/**
			 * C'est cette variable qu'on doit partitionner
			 */
			for(int i = 0; i < subtreeIndexToValue.size(); i++)
			{
				String value = subtreeIndexToValue.get(i);
				Integer precedenteValeur = out.get(value);

				// Si cette valeur n'a encore jamais été vue
				if(precedenteValeur == null)
					precedenteValeur = 0;

				out.put(value, subtrees[i].getNbInstances(values, nbVarInstanciees) + precedenteValeur);
			}
		}
		else if(values[profondeur] != null)
		{
			// cette variable est instanciée
			int indice = subtreeIndexToValue.indexOf(values[nbVar]);
			
			// Pas de sous-arbre avec cette instanciation? Alors il n'y a aucun exemple et on ne fait rien
			
			if(indice != -1)
				subtrees[indice].getNbInstancesToutesModalitees(out, nbVar, values, nbVarInstanciees - 1);
		}
		else
		{
			// la variable n'est pas instanciée
			for(int i = 0; i < subtreeIndexToValue.size(); i++)
				subtrees[i].getNbInstancesToutesModalitees(out, nbVar, values, nbVarInstanciees);
		}
	}
	
	@Override
	public int getNbInstances(String[] values, int nbVarInstanciees)
	{
		if(nbVarInstanciees == 0)
			return nbInstances;
		
		String value = values[profondeur];

		// cette variable est instanciée
		if(value != null)
		{
			int indice = subtreeIndexToValue.indexOf(value);
			
			// Pas de sous-arbre? Alors il n'y a aucun exemple
			if(indice == -1)
				return 0;
			else
				return subtrees[indice].getNbInstances(values, nbVarInstanciees - 1);
		}
		else
		{
			int somme = 0;
			for(int i = 0; i < subtreeIndexToValue.size(); i++)
				somme += subtrees[i].getNbInstances(values, nbVarInstanciees);
			return somme;
		}
	}
	
	@Override
	public void addInstanciation(String[] values)
	{
		nbInstances++;
		String value = values[profondeur];
		int indice = subtreeIndexToValue.indexOf(value);

		if(indice == -1)
		{
			// on doit créer un sous-arbre
			indice = subtreeIndexToValue.size();
			subtreeIndexToValue.add(value);
			
			// Si c'est la dernière variable, on construit une feuille
			if(profondeur == nbVarTotal - 1)
				subtrees[indice] = new VDDLeaf();
			else
				subtrees[indice] = new VDD(profondeur + 1);
		}
		
		subtrees[indice].addInstanciation(values);			
	}

}
