package preferences;

import java.util.ArrayList;

import compilateur.SALADD;

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

public class ApprentissageLexOrder extends ApprentissageLexStructure<LexicographicOrder>
{
	private LexicographicOrder[] all; // tableau des nœuds
	
	public ApprentissageLexOrder()
	{}
	
	/**
	 * Initialise l'ordre grâce aux infos sur les variables
	 * @param fichierContraintes
	 */
	public void initOrder(SALADD contraintes)
	{
		super.initOrder(contraintes);
		all = new LexicographicOrder[nbVar];
		int i = 0;
		for(String var : variables)
		{
			int nbMod = contraintes.getSizeOfDomainOf(var);
			all[i++] = new LexicographicOrder(var, nbMod);
		}
	}
	
	/**
	 * Compilation de l'historique dans un SLDD.
	 * Apprend l'ordre des variables
	 * @param filename
	 */
	public void apprendDonnees(ArrayList<String> filename) {
		super.apprendDonnees(filename);
		
		for(LexicographicOrder node : all)
		{
			node.setNbExemples(vdd.countingpondereOnFullDomain(vdd.getVar(node.getVar())));
		}

		LexicographicOrder tmp;
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

//		System.out.println("Base : "+base);
		struct.updateBase(base);
		
//		for(int i = 0; i < nbVar; i++)
//			System.out.println("Nœud " + i + " : "+all[i].getVar()+", base : "+all[i].getBase());
	}

}
