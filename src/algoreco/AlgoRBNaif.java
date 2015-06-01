package algoreco;

import heuristique_contraintes.HeuristiqueContraintesRien;
import heuristique_variable.HeuristiqueVariableMCSinv;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
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
 * Algorithme de recommandation pour les réseaux bayésiens naïfs et naïfs augmentés
 * @author pgimenez
 *
 */

public class AlgoRBNaif implements AlgoReco
{
	private String param;
	private HashMap<String, SALADD> x = new HashMap<String, SALADD>();
	private ArrayList<String> variables;
	private HashMap<String, String> assignement = new HashMap<String, String>();
	
	public AlgoRBNaif(String param)
	{
		this.param = param;
	}
	
	@Override
	public void initialisation(ArrayList<String> variables)
	{
		this.variables = variables;
	}
	
	@Override
	public String recommande(String variable, ArrayList<String> possibles)
	{
		for(String v: variables)
			if(assignement.get(v) != null)
				x.get(variable).assignAndPropagate(v, assignement.get(v));
		Map<String, Double> recomandations=x.get(variable).calculeDistributionAPosteriori(variable, possibles);
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
		assignement.put(variable, solution);
	}

	@Override
	public void oublieSession()
	{
		assignement.clear();
		for(String v: variables)
			if(x.get(v) != null)
				x.get(v).reinitialisation();
	}

	@Override
	public void apprendContraintes(String filename)
	{}

	@Override
	public void apprendDonnees(ArrayList<String> filename, int nbIter) 
	{
		System.out.println("Compilation des réseaux bayésiens...");
		for(String v: variables)
		{
			String file = "bn"+"_"+param+"_"+v+"_"+nbIter+".xml";
			if(new File(file).exists())
			{
				SALADD s = new SALADD();
				s.compilation(file, true, false, new HeuristiqueVariableMCSinv(), new HeuristiqueContraintesRien(), 3);
				s.initialize();
				x.put(v, s);
				System.out.println(v+": ok");
			}
			else
				System.out.println(file+" introuvable");
		}
		System.out.println("Compilation terminée");
	}

	@Override
	public void termine()
	{}

}
