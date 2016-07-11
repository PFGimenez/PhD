package recommandation;


import java.util.ArrayList;
import java.util.Map;

import compilateur.SALADD;
import compilateur.heuristique_contraintes.HeuristiqueContraintesRien;
import compilateur.heuristique_variable.HeuristiqueVariableMCSinv;


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
//	private String param;
	private SALADD x;
	
//	public AlgoRB(String param)
//	{
//		this.param = param;
//	}
	
	public AlgoRB()
	{
		x = new SALADD();
	}
	
	@Override
	public String recommande(String variable, ArrayList<String> possibles)
	{
		Map<String, Double> recomandations=x.calculeDistributionAPosteriori(variable, possibles);
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
		x.propagation();
	}

	@Override
	public void apprendContraintes(SALADD contraintes)
	{}

	@Override
	public void apprendDonnees(ArrayList<String> filename, int nbIter, boolean entete) 
	{
		System.out.println("Compilation du réseau bayésien...");
		x.compilation("BN"+"_"+nbIter+".xml", false, new HeuristiqueVariableMCSinv(), new HeuristiqueContraintesRien(), 3, true);
//		x.compilation("not_filtered_bn"+nbIter+".xml", true, false, new HeuristiqueVariableMCSinv(), new HeuristiqueContraintesRien(), 3);
//		x.compilation("bn_hc_medium0.xml", true, false, new HeuristiqueVariableMCSinv(), new HeuristiqueContraintesRien(), 3);		x.initialize();
		System.out.println("Compilation terminée");
		x.propagation();
	}
	
	public void apprendDonneesPourGeneration(String filename) 
	{
		System.out.println("Compilation du réseau bayésien...");
		x.compilation(filename, false, new HeuristiqueVariableMCSinv(), new HeuristiqueContraintesRien(), 3, true);
//		x.compilation("not_filtered_bn"+nbIter+".xml", true, false, new HeuristiqueVariableMCSinv(), new HeuristiqueContraintesRien(), 3);
//		x.compilation("bn_hc_medium0.xml", true, false, new HeuristiqueVariableMCSinv(), new HeuristiqueContraintesRien(), 3);		x.initialize();
		System.out.println("Compilation terminée");
		x.propagation();
	}

	@Override
	public void termine()
	{}
	
	public void initHistorique(ArrayList<String> filename, boolean entete)
	{}

	@Override
	public void unassign(String variable)
	{
		// TODO
	}

}
