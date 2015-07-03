package recommandation;

import java.util.ArrayList;

/*   (C) Copyright 2015, Gimenez Pierre-François
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
 * Interface pour les algorithmes de recommandation
 * @author pgimenez
 *
 */

public interface AlgoReco {

	/**
	 * Initialisation de l'algorithme, appeler une fois au début
	 * @param variables
	 */
	public void initialisation(ArrayList<String> variables);
	
	/**
	 * Apprentissage des contraintes
	 * @param filename
	 */
	public void apprendContraintes(String filename);
	
	/**
	 * L'algorithme doit oublier les données précédentes et apprendre celles-ci.
	 * @param filename
	 */
	public void apprendDonnees(ArrayList<String> filename, int nbIter);

	/**
	 * recommande une valeur pour une variable sachant les valeurs possibles
	 * @param variable
	 */
	public String recommande(String variable, ArrayList<String> possibles);
	
	/**
	 * Récupération de la valeur effectivement choisie par l'utilisateur
	 * @param solution
	 */
	public void setSolution(String variable, String solution);
	
	/**
	 * Oublie les valeurs choisies par l'utilisateur.
	 */
	public void oublieSession();
	
	/**
	 * Appelé à la toute fin du protocole
	 */
	public void termine();

}
