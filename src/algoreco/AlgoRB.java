package algoreco;

import heuristique_contraintes.HeuristiqueContraintesRien;
import heuristique_variable.HeuristiqueVariableMCSinv;

import java.util.ArrayList;
import java.util.Map;

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
 * Algorithme de recommandation avec les réseaux bayésiens
 * @author pgimenez
 *
 */

public class AlgoRB implements AlgoReco
{
	private String param;
	private SALADD x;
	
	public AlgoRB(String param)
	{
		this.param = param;
	}
	
	@Override
	public void initialisation(ArrayList<String> variables)
	{
		x = new SALADD();
	}
	
	@Override
	public String recommande(String variable, ArrayList<String> possibles)
	{
		Map<String, Double> recomandations=x.calculeDistributionAPosteriori(variable);
		String best="";
		double bestproba=-1;
		
		for(String value: possibles)
		{
			if(recomandations.get(value) == null)
				continue;
			if(recomandations.get(value)>bestproba){
				bestproba=recomandations.get(value);
				best=value;
			}
		}
		return best;
	}

	@Override
	public void setSolution(String variable, String solution)
	{
		x.assignAndPropagate(variable, solution);
	}

	@Override
	public void oublieSession()
	{
		x.reinitialisation();
	}

	@Override
	public void apprendContraintes(String filename)
	{}

	@Override
	public void apprendDonnees(ArrayList<String> filename, int nbIter) 
	{
		System.out.println("Compilation du réseau bayésien...");
		x.compilation("bn"+"_"+param+"_"+nbIter+".xml", true, false, new HeuristiqueVariableMCSinv(), new HeuristiqueContraintesRien(), 1);
//		x.compilation("not_filtered_bn"+nbIter+".xml", true, false, new HeuristiqueVariableMCSinv(), new HeuristiqueContraintesRien(), 3);
		x.initialize();
		System.out.println("Compilation terminée");
	}

	@Override
	public void termine()
	{}

}
