package algoreco;

import heuristique_contraintes.HeuristiqueContraintesRien;
import heuristique_variable.HeuristiqueVariableMCSinvPlusUn;

import java.util.ArrayList;
import java.util.Map;

import methode_oubli.MethodeOubli;
import br4cp.SALADD;

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
 * Algorithme de recommandation avec SLDD utilisant une méthode d'oubli
 * @author pgimenez
 *
 */

public class AlgoSaladdOubli implements AlgoReco
{
	private MethodeOubli oubli;
	private SALADD saladd;
	
	public AlgoSaladdOubli(MethodeOubli oubli)
	{
		this.oubli = oubli;
		saladd = new SALADD();
	}
	
	@Override
	public void initialisation(ArrayList<String> variables)
	{
	}

	@Override
	public void apprendContraintes(String filename)
	{
		saladd.compilation("small.xml", false, true, new HeuristiqueVariableMCSinvPlusUn(), new HeuristiqueContraintesRien(), 0);
	}

	@Override
	public void apprendDonnees(ArrayList<String> filename, int nbIter) {
		saladd.compilationDHistorique(filename, 2);
		saladd.initialize();		
	}

	@Override
	public String recommande(String variable, ArrayList<String> possibles) {
		Map<String, Double> recommandations=saladd.reco(variable, oubli);
		String best="";
		double bestproba=-1;
		
		for(String value: possibles)
		{
			if(recommandations.get(value) == null)
				continue;
			if(recommandations.get(value)>bestproba){
				bestproba=recommandations.get(value);
				best=value;
			}
		}
//		System.out.println("Recommandation pour "+variable+": "+best);
		return best;
	}

	@Override
	public void setSolution(String variable, String solution)
	{
		saladd.assignAndPropagate(variable, solution);
	}

	@Override
	public void oublieSession() {
		saladd.reinitialisation();
	}

	@Override
	public void termine()
	{}

}
