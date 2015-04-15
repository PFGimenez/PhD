package br4cp;

import java.util.ArrayList;

public interface TestIndependance {

	/**
	 * Renvoie un tableau de statistiques
	 * @param v
	 * @param graph
	 * @return
	 */
	public double[][] getIndependancy(ArrayList<Var> v, VDD graph);
	
	/**
	 * Doit renvoyer vrai si le couple de variables avec la statistique "valeur1" est plus
	 * indépendant que celui avec la statistique "valeur 2"
	 * @param valeur1
	 * @param valeur2
	 * @return
	 */
	public boolean estPlusIndependantQue(double valeur1, double valeur2);
	
}
