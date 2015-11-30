package preferences;

import java.util.ArrayList;
import java.util.Iterator;

import compilateur.SALADD;
import compilateur.VDD;
import compilateur.Var;

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
 * Apprentissage d'un ordre lexicographique
 * @author pgimenez
 *
 */

public class Apprentissage 
{
	private SALADD saladd;
	private LexicographicOrder order; // liste chaînée
	private LexicographicOrder[] all; // tableau des nœuds
	private VDD vdd;
	private int nbVar;
	
	public Apprentissage()
	{
		saladd = new SALADD();
		vdd = saladd.getVDD();
	}
	
	/**
	 * Initialise l'ordre grâce aux infos sur les variables
	 * @param fichierContraintes
	 */
	public void initOrder(SALADD contraintes)
	{
/*		SALADD contraintes;
		contraintes = new SALADD();
		System.out.print("Compilation...");

		if(new File(fichierContraintes).exists())
		{
			contraintes.compilation(fichierContraintes, true, 4, 0, 0);
			contraintes.propagation();
		}*/
		
		ArrayList<String> variables = new ArrayList<String>();
		variables.addAll(contraintes.getFreeVariables());
		
		/**
		 * Construction de l'arbre lexicographique par défaut.
		 * L'ordre est arbitraire
		 */
		
		Iterator<String> it = variables.iterator();

		// Tri pour small
		while(it.hasNext())
			if(it.next().contains("_"))
				it.remove();
		variables.remove("v55");
		variables.remove("v54");
		variables.remove("v0");
		variables.remove("v50");
		variables.remove("v92");
		variables.remove("v20");
		variables.remove("v22");
		variables.remove("v47");
		variables.remove("v48");
		variables.remove("v49");
		
		/*
		variables.remove("v0");
		
		variables.remove("v27");
		variables.remove("v28");
		variables.remove("v29");
		variables.remove("v30");
		variables.remove("v31");
		
		variables.remove("v34");
		variables.remove("v35");

		variables.remove("v43");
		
		variables.remove("v74");
		variables.remove("v75");
		variables.remove("v76");
		variables.remove("v77");
		variables.remove("v78");
		
		variables.remove("v81");
		
		variables.remove("v90");
		variables.remove("v91");
		
		variables.remove("v98");
		
		variables.remove("v104");
		variables.remove("v105");
		variables.remove("v106");
		variables.remove("v107");
		variables.remove("v108");
		variables.remove("v109");
		variables.remove("v110");
		variables.remove("v111");
		variables.remove("v112");
		variables.remove("v113");
		variables.remove("v114");
		variables.remove("v115");
		variables.remove("v116");
		variables.remove("v117");
		variables.remove("v118");
		variables.remove("v119");
		variables.remove("v120");
		variables.remove("v121");

		variables.remove("v189");
		*/
		nbVar = variables.size();
		System.out.println(nbVar+" variables");
		all = new LexicographicOrder[nbVar];
		order = null;		
		int i = 0;
		for(String var : variables)
		{
			order = new LexicographicOrder(var, contraintes.getSizeOfDomainOf(var), order);
			all[i++] = order;
		}
	}
	
	/**
	 * Compilation de l'historique dans un SLDD.
	 * Apprend l'ordre des variables
	 * @param filename
	 */
	public void apprendDonnees(ArrayList<String> filename) {
		ArrayList<String> filename2 = new ArrayList<String>();
		System.out.println("Apprentissage de ");
		for(int i = 0; i < filename.size(); i++)
		{
			String s = filename.get(i);
			System.out.println("	"+s+".xml");
			filename2.add(s+".xml");
		}
		saladd.compilationDHistorique(filename2, 2);
		saladd.propagation();
		vdd = saladd.getVDD();
		
		for(LexicographicOrder node : all)
			node.setNbExemples(vdd.countingpondereOnFullDomain(vdd.getVar(node.getVar())));

		LexicographicOrder tmp;
		for(int i = 0; i < nbVar-1; i++)
		{
			for(int j = 0; j < i; j++)
				if(all[i].getEntropie() > all[i+1].getEntropie())
				{
					tmp = all[i];
					all[i] = all[j];
					all[j] = tmp;
				}
		}

		order = null;
		for(int i = nbVar - 1; i >= 0; i--)
		{
			all[i].setEnfant(order);
			order = all[i];
		}

		
		order.updateBase();
		
		for(int i = 0; i < nbVar; i++)
			System.out.println("Nœud " + i + " : "+all[i].getVar());
	}

	/**
	 * Infère le rang d'un élément à partir de l'ordre
	 * @param element
	 * @param ordreVariables
	 * @return
	 */
	public long infereRang(ArrayList<String> element, ArrayList<String> ordreVariables)
	{
		long rang = 1; // car la meilleure préférence a le rang 1 (et pas 0)
		for(int i = 0; i < nbVar; i++)
			for(int j = 0; j < nbVar; j++)
				if(all[j].getVar().equals(ordreVariables.get(i)))
				{
					System.out.println("Pour variable " + ordreVariables.get(i) + ", pref : "+all[j].getPref(element.get(i)) + ", base : "+ all[j].getBase());
					rang += all[j].getPref(element.get(i))*all[j].getBase();
					continue;
				}
		System.out.println("Rang : "+rang);
		return rang; 
	}

	public long rangMax()
	{
		long rang = 1;
		for(int i = 0; i < nbVar; i++)
			rang += (all[i].getNbMod()-1)*all[i].getBase();
		return rang;
	}
	
}
