package graphOperation;

import java.util.ArrayList;

import compilateurHistorique.HistoComp;
import compilateurHistorique.Instanciation;
import compilateurHistorique.IteratorInstances;

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
 * Un sous-graphe, obtenu après partitionnement
 * @author pgimenez
 *
 */

public class Graphe
{
	private ArrayList<String> cutset;
	private ArrayList<String> acutset;
	private ArrayList<String> context;
	private ArrayList<String> vars;
	private Instanciation instance;
	private Graphe[] sousgraphes;
	private DTreeGenerator dtreegenerator;
	private HistoComp historique;
	private double[] cache;
	private static int seuil;
	
	public static void setSeuil(int seuilP)
	{
		seuil = seuilP;
	}
	
	/**
	 * Constructeur de la racine
	 * @param vars
	 * @param historique
	 * @param dtreegenerator
	 */
	public Graphe(ArrayList<String> vars, HistoComp historique, DTreeGenerator dtreegenerator)
	{
		this.acutset = new ArrayList<String>();
		this.vars = vars;
		this.dtreegenerator = dtreegenerator;
		this.historique = historique;
		context = new ArrayList<String>(); // comme acutset est vide, le contexte est vide

		cache = new double[1];
		cache[0] = -1;
	}
	
	private Graphe(ArrayList<String> acutset, ArrayList<String> vars, HistoComp historique, DTreeGenerator dtreegenerator)
	{
		this.acutset = acutset;
		this.vars = vars;
		this.dtreegenerator = dtreegenerator;
		this.historique = historique;
				
		// context = acutset (intersection) vars
		context = new ArrayList<String>();

		for(String s : acutset)
			if(vars.contains(s))
				context.add(s);
		
		cache = new double[instance.getTailleCache(context)];

		for(int i = 0; i < cache.length; i++)
			cache[i] = -1;
	}
	
	/**
	 * Calcule la probabilité de l'instance. Ne modifie pas instance
	 * @param instance
	 * @return
	 */
	public double computeProba(Instanciation instance)
	{
		// A-t-on déjà calculé cette valeur?
		int indice = instance.getIndexCache(context);
		if(cache[indice] >= 0)
			return cache[indice];
		
		Instanciation subinstance = instance.subInstanciation(vars);
		int nbInstance = historique.getNbInstances(subinstance);
		
		// Si on a assez d'exemples, pas besoin de redécouper
		if(nbInstance > seuil)
		{
			double p = ((double)nbInstance) / historique.getNbInstancesTotal();
			cache[indice] = p;
			return p;
		}
		
		if(sousgraphes == null)
			construitSousGraphes();

		double p = 0;
		// Comme les sous-graphes ont été construit, cutset a été calculé aussi
		for(String s : cutset)
			if(instance.getValue(s) != null)
				System.out.println("Erreur ! Une variable du cutset est déjà instanciée");
		
		IteratorInstances iter = historique.getIterator(cutset);
		while(iter.hasNext())
		{
			Instanciation c = iter.next();
			p += sousgraphes[0].computeProba(c) * sousgraphes[1].computeProba(c);
		}
		cache[indice] = p;
		return p; // TODO
	}
	
	private void construitSousGraphes()
	{
		if(sousgraphes != null)
			System.out.println("Erreur : sous-graphes déjà construits");

		sousgraphes = new Graphe[2];
		
		dtreegenerator.separateHyperGraphe(vars);
		cutset = dtreegenerator.getCutset();
		
		acutset.addAll(cutset);
		
		for(int i = 0; i < 2; i++)
		{
			sousgraphes[i] = new Graphe(acutset, dtreegenerator.getSousGraphe(i), historique, dtreegenerator);
		}

	}
	
}
