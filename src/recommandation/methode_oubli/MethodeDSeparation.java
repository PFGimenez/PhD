package recommandation.methode_oubli;

import java.util.ArrayList;
import java.util.HashMap;

import compilateur.LecteurXML;
import compilateur.MethodeOubliRestauration;
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

	public MethodeDSeparation(int seuil, TestIndependance test, String prefixData)
	{
		super(seuil, test);
		LecteurXML xml=new LecteurXML();
		reseau = xml.lectureReseauBayesien(prefixData+"rb_"+nbIter+".xml");
	}
	
	/**
	 * Calcul de D-séparation
	 * @param connues
	 * @param v
	 * @param vientDeParent
	 * @param distance
	 */
	protected void rechercheEnProfondeur(ArrayList<String> connues, String v, boolean vientDeParent, int distance)
	{
/*		if(!distances.containsKey(v))
			distances.put(v, distance);
		else if(distances.get(v) > distance)
		{
			distances.remove(v);
			distances.put(v, distance);
		}
			*/
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
 		if(!connues.contains(v))
			for(String enf: listeEnfants)
				rechercheEnProfondeur(connues, enf, true, distance + 1);
		
		boolean aUnEnfantConnu = false;
		for(String enf: listeEnfants)
			if(connues.contains(enf))
			{
				aUnEnfantConnu = true;
				break;
			}
		
		/**
		 * La recherche des parents est un peu plus complexe, et prend compte des V-structure
		 */
		if(!(connues.contains(v) || aUnEnfantConnu) && vientDeParent)
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
			if(!connues.contains(v))
				for(String par: listeParents)
					if(!done.contains(par))
						rechercheEnProfondeur(connues, par, false, distance + 1);
		}
		else
		{
			/**
			 * Dernière possibilité: on vient d'un parent et on (ou un de nos fils) est connu.
			 * Dans ce cas, la V-structure est ignoré.
			 */
			for(String par: listeParents)
				if(!done.contains(par))
					rechercheEnProfondeur(connues, par, false, distance + 1);
		}
	}

}
