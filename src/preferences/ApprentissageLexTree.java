package preferences;

import java.util.ArrayList;

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
 * Apprentissage d'un arbre lexicographique incomplet
 * @author pgimenez
 *
 */

public class ApprentissageLexTree extends ApprentissageLexStructure
{
	private SALADD contraintes;

	/**
	 * Initialise l'ordre grâce aux infos sur les variables
	 * @param fichierContraintes
	 */
	public void initOrder(SALADD contraintes)
	{
		super.initOrder(contraintes);
		this.contraintes = contraintes;
	}
	
	private LexicographicStructure apprendRecursif(SALADD saladd)
	{
		VDD vdd = saladd.getVDD();
		LexicographicTree best = null;
		double bestEntropie = 1;
	
		ArrayList<String> variablesTmp = new ArrayList<String>();
		variablesTmp.addAll(saladd.getFreeVariables());
	
		for(String var : variablesTmp)
		{
			LexicographicTree tmp = new LexicographicTree(var, contraintes.getSizeOfCurrentDomainOf(var));
			tmp.setNbExemples(vdd.countingpondereOnFullDomain(vdd.getVar(var)));
			double entropie = tmp.getEntropie();
			if(best == null || entropie < bestEntropie)
			{
				best = tmp;
				bestEntropie = entropie;
			}
		}
		
		// Si c'était la dernière variable, alors c'est une feuille
		if(variablesTmp.size() == 1)
			return best;
		
		variablesTmp.remove(best.getVar());
		int nbMod = best.getNbMod();
		Var var = vdd.getVar(best.getVar());
		for(int i = 0; i < nbMod; i++)
		{
			// On conditionne par une certaine valeur
			vdd.conditioner(var, var.conv(best.getPref(i)));
			if(vdd.countingpondere() > 500)
				best.setEnfant(i, apprendRecursif(saladd));
			else
				best.setEnfant(i, apprendOrdre(saladd));
			vdd.deconditioner(var);
		}
		// A la fin, le VDD est conditionné de la même manière qu'à l'appel
		return best;
	}
	
	public void apprendDonnees(ArrayList<String> filename)
	{
		super.apprendDonnees(filename);
		struct = apprendRecursif(saladd);
	}

}
