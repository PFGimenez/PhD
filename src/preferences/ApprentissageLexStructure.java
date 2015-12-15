package preferences;

import java.util.ArrayList;

import compilateur.SALADD;
import compilateur.VDD;

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
 * Apprentissage d'une structure basée sur l'ordre lexicographique
 * @author pgimenez
 *
 */

public abstract class ApprentissageLexStructure
{
	protected SALADD saladd;
	protected VDD vdd;
	protected int nbVar;
	protected long base;
	protected ArrayList<String> variables;
	protected LexicographicStructure struct;

	public ApprentissageLexStructure()
	{
		saladd = new SALADD();
	}
	
	/**
	 * Initialise l'ordre grâce aux infos sur les variables
	 * @param fichierContraintes
	 */
	public void initOrder(SALADD contraintes)
	{
		variables = new ArrayList<String>();
		variables.addAll(contraintes.getFreeVariables());
		
		/**
		 * Construction de l'arbre lexicographique par défaut.
		 * L'ordre est arbitraire
		 */
		
//		Iterator<String> it = variables.iterator();
/*
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
		*/
		
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
		
		nbVar = variables.size();
		base = 1;
		for(String var : variables)
		{
			base *= contraintes.getSizeOfDomainOf(var);
		}

	}
	
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
	}
	
	/**
	 * Renvoie le meilleur élément qui vérifie les variables déjà fixées
	 * @param element
	 * @param ordreVariables
	 * @return
	 */
	public String infereBest(String varARecommander, ArrayList<String> possibles, ArrayList<String> element, ArrayList<String> ordreVariables)
	{
		return struct.infereBest(varARecommander, possibles, element, ordreVariables);
	}
	
	public long infereRang(ArrayList<String> element, ArrayList<String> ordreVariables)
	{
		// +1 car les rangs commencent à 1 et pas à 0
		long rang = struct.infereRang(element, ordreVariables) + 1;
		System.out.println(rang);
		return rang;
	}

	public long rangMax()
	{
		return base;
	}

	protected LexicographicOrder apprendOrdre(VDD vdd, ArrayList<String> variablesRestantes)
	{
		ArrayList<String> variables = new ArrayList<String>();
		variables.addAll(variablesRestantes);
		int nbVar = variables.size();
		LexicographicOrder[] all = new LexicographicOrder[nbVar];

		int k = 0;
		for(String var : variables)
		{
			int nbMod = saladd.getSizeOfDomainOf(var);
			all[k++] = new LexicographicOrder(var, nbMod);
		}
		
		for(LexicographicOrder node : all)
			node.setNbExemples(vdd.countingpondereOnFullDomain(vdd.getVar(node.getVar())));

		// Tri à bulles sur les entropies
		LexicographicOrder tmp, struct;
		for(int i = 0; i < nbVar-1; i++)
		{
			for(int j = 0; j < i; j++)
				if(all[i].getEntropie() < all[i+1].getEntropie())
				{
					tmp = all[i];
					all[i] = all[j];
					all[j] = tmp;
				}
		}

		struct = null;
		for(int i = 0; i < nbVar; i++)
		{
			all[i].setEnfant(struct);
			struct = all[i];
		}

		return struct;
	}

	public void affiche()
	{
		struct.affiche();
	}

	public void save(String filename)
	{
		struct.save(filename);
	}

	public boolean load(String filename)
	{
		struct = LexicographicStructure.load(filename);
		if(struct == null)
			System.out.println("Le chargement a échoué");
		return struct != null;
	}
	
	@Override
	public String toString()
	{
		return getClass().getSimpleName();
	}

}
