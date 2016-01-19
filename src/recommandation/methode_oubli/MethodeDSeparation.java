package recommandation.methode_oubli;

import java.util.ArrayList;
import java.util.HashMap;

import compilateur.LecteurXML;
import compilateur.SALADD;
import compilateur.test_independance.TestIndependance;

/**
 * Classe abstraite pour les méthodes d'oubli à base de D-séparation et de restauration
 * @author pgimenez
 *
 */

public abstract class MethodeDSeparation extends MethodeOubliRestauration
{
	private static final int parents = 0;
	private static final int enfants = 1;

	private HashMap<String, ArrayList<String>>[] reseau;
	protected ArrayList<String> done = new ArrayList<String>();
	private String prefixData;

	public MethodeDSeparation(int seuil, TestIndependance test, String prefixData)
	{
		super(seuil, test);
		this.prefixData = prefixData;
	}
	
	public void learn(SALADD saladd, String prefix_file_name)
	{
		super.learn(saladd, prefix_file_name);
		LecteurXML xml=new LecteurXML();
		reseau = xml.lectureReseauBayesien(prefixData+"BN_"+nbIter+".xml");
	}
	
	/**
	 * Calcul de d-séparation. La valeur de retour est contenu dans l'attribut "done"
	 * @param connues
	 * @param v
	 * @param vientDeParent
	 */
	protected void rechercheEnProfondeur(ArrayList<String> connues, String v, boolean vientDeParent, SALADD contraintes)
	{
		/**
		 * Algorithme de d-séparation déterministe
		 * Recherche de X ; Y | Z
		 * Z+ = Z
		 * Pour tout U tel que
		 * 	* U est déterminé de manière déterministe
		 *  * Les parents de U sont dans Z
		 *  Alors Z+ = Z u {U}
		 *  
		 *  
		 *  Sinon: voir Identifying independence in Bayesian Networks, page 10
		 *  ou: Conditional independence and its representations, page 7
		 */
		
		
		// TODO : distance inutile
/*		if(!distances.containsKey(v))
			distances.put(v, distance);
		else if(distances.get(v) > distance)
		{
			distances.remove(v);
			distances.put(v, distance);
		}
			*/
		if(!done.contains(v))
			done.add(v);
		ArrayList<String> listeParents = reseau[parents].get(v);
		ArrayList<String> listeEnfants = reseau[enfants].get(v);

		/**
		 * D'abord, on étend la recherche aux enfants.
		 * Il n'y a pas de problème de collision.
		 * Si v est connu, on bloque la recherche.
		 * On ne vérifie pas qu'ils sont "done" parce que:
		 * - par acyclicité, il n'y aura pas de boucle infinie
		 * - c'est nécessaire dans le cas d'une V-structure qui peut être "done" et pourtant
		 * peut avoir des parents à explorer
		 */
//		System.out.println("Variable considérée : "+v);
		
 		if(!isKnown(connues, v, contraintes))
			for(String enf: listeEnfants)
				rechercheEnProfondeur(connues, enf, true, contraintes);
		
		boolean aUnEnfantConnu = false;
		for(String enf: listeEnfants)
			if(isKnown(connues, enf, contraintes))
			{
				aUnEnfantConnu = true;
				break;
			}
		
		/**
		 * La recherche des parents est un peu plus complexe, et prend compte des V-structure
		 */
		if(!(isKnown(connues, v, contraintes) || aUnEnfantConnu) && vientDeParent)
		{
			/**
			 * Si on n'est pas connu (et qu'aucun de nos enfants ne l'est)
			 * et qu'on provient d'un parent, on a une V-structure: on s'arrête là.
			 */
			return;
		}
		else if(!vientDeParent)
		{
			/**
			 * Si on vient d'un fils, alors il n'y a pas de V-structure.
			 * On peut propager à conditionner de ne pas être connu.
			 */
			if(!isKnown(connues, v, contraintes))
				for(String par: listeParents)
					if(!done.contains(par))
						rechercheEnProfondeur(connues, par, false, contraintes);
		}
		else
		{
			/**
			 * Dernière possibilité: on vient d'un parent et on (ou un de nos fils) est connu.
			 * Dans ce cas, la V-structure est ignoré.
			 */
			for(String par: listeParents)
				if(!done.contains(par))
					rechercheEnProfondeur(connues, par, false, contraintes);
		}
	}
	
	public String toString()
	{
		return getClass().getSimpleName() + " avec test " + test.getClass().getSimpleName() + " et seuil " + seuil;
	}

	private boolean isKnown(ArrayList<String> connues, String v, SALADD contraintes)
	{
		return connues.contains(v);// || contraintes.getSizeOfCurrentDomainOf(v) == 1;
	}
	
}
