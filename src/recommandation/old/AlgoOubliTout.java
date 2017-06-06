package recommandation.old;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import compilateur.SALADD;
import compilateur.VDD;
import recommandation.AlgoReco;

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
 * Algorithme de recommandation avec SLDD. Utilise la loi a priori.
 * @author Pierre-François Gimenez
 *
 */

public class AlgoOubliTout implements AlgoReco
{
	private SALADD saladd, contraintes;
	private Map<String, Map<String, Double>> table = new HashMap<String, Map<String, Double>>();
	
	public AlgoOubliTout()
	{
		saladd = new SALADD();
	}
	
	public void describe()
	{
		System.out.println("A priori recommender");
	}
	
	public void charge(String s)
	{
		saladd.chargement(s, 0);
	}
	
	public void save(String s)
	{
		saladd.save(s);
	}

	public void apprendContraintes(SALADD contraintes)
	{
		this.contraintes = contraintes;
	}

	@Override
	public void apprendDonnees(ArrayList<String> filename, int nbIter, boolean entete) {
		ArrayList<String> filename2 = new ArrayList<String>();
		System.out.println("Apprentissage de ");
		for(int i = 0; i < filename.size(); i++)
		{
			String s = filename.get(i);
			System.out.println("	"+s+".xml");
			filename2.add(s+".xml");
		}
		saladd.compilationDHistorique(filename2, 2, contraintes.getOrd());
		saladd.propagation();
		Set<String> vars = saladd.getFreeVariables();
		VDD vdd = saladd.getVDD();
		table.clear();
		for(String var : vars)
		{
			ArrayList<String> domaine = new ArrayList<String>(saladd.getDomainOf(var));
			table.put(var, vdd.countingpondereOnPossibleDomain(vdd.getVar(var), domaine));
		}
	}

	public String recommandeGeneration(String variable, ArrayList<String> possibles, SALADD contraintes)
	{		
		Map<String, Double> recommandations = table.get(variable);

		double choix = (new Random()).nextDouble();
		double total = 0;
		double normalisation = 0;
		
		for(String value: possibles)
		{
			normalisation += recommandations.get(value);
//			System.out.println("Proba de "+value+" : "+recommandations.get(value));
		}
				
		// Si aucun cas n'est rencontré, on renvoie une valeur au hasard (uniformément tirée)
		if(normalisation == 0)
			return possibles.get((new Random()).nextInt(possibles.size()));

		choix = choix * normalisation;

		for(String value: possibles)
		{
			if(recommandations.get(value) == null)
				continue;
			total += recommandations.get(value);
			if(choix <= total)
				return value;
		}
		System.out.println("Erreur! "+choix+" "+total);
		return null;
	}
	
	@Override
	public String recommande(String variable, ArrayList<String> possibles)
	{
		Map<String, Double> recommandations = table.get(variable);
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
//		System.out.println((int)(10000*bestproba)/100.+"%");
		return best;
	}

	@Override
	public void setSolution(String variable, String solution)
	{
//		saladd.assignAndPropagate(variable, solution);
	}

	@Override
	public void oublieSession() {
//		saladd.reinitialisation();
//		saladd.propagation();
	}

	@Override
	public void termine()
	{}

	public String toString()
	{
		return getClass().getSimpleName();
	}

	public Set<String> getVariables()
	{
		return saladd.getFreeVariables();
	}
	
	public void initHistorique(ArrayList<String> filename, boolean entete)
	{}
	
	@Override
	public void unassign(String variable)
	{}

}
