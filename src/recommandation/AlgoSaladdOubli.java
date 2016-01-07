package recommandation;

import java.util.ArrayList;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import compilateur.Var;

import compilateur.SALADD;
import recommandation.methode_oubli.MethodeOubli;

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
	private SALADD saladd, contraintes;
	private String dataset;
	
	public AlgoSaladdOubli(MethodeOubli oubli, String dataset)
	{
		this.oubli = oubli;
		this.dataset = dataset;
		saladd = new SALADD();
	}
	
	public void charge(String s)
	{
		saladd.chargement(s, 0);
	}
	
	public void save(String s)
	{
		saladd.save(s);
	}
	
	@Override
	public void initialisation(ArrayList<String> variables)
	{}

	@Override
	public void apprendContraintes(SALADD contraintes)
	{
		this.contraintes = contraintes;
	}

	@Override
	public void apprendDonnees(ArrayList<String> filename, int nbIter) {
		ArrayList<String> filename2 = new ArrayList<String>();
		System.out.println("Apprentissage de ");
		for(int i = 0; i < filename.size(); i++)
		{
			String s = filename.get(i);
			System.out.println("	"+s+".xml");
			filename2.add(s+".xml");
		}

		//		saladd.compilationDHistorique(filename2, 2, null);
		saladd.compilationDHistorique(filename2, 2, contraintes.getOrd());
		oubli.setNbIter(nbIter);
		oubli.learn(saladd, dataset); // apprentissage des indépendances
		saladd.propagation();
	}

	public String recommandeGeneration(String variable, ArrayList<String> possibles, SALADD contraintes)
	{
		Map<String, Double> recommandations=saladd.recomandation(variable, oubli, possibles, contraintes);

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
	public String recommande(String variable, ArrayList<String> possibles, SALADD contraintes)
	{
		Map<String, Double> recommandations=saladd.recomandation(variable, oubli, possibles, contraintes);
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
		saladd.assignAndPropagate(variable, solution);
	}

	@Override
	public void oublieSession() {
		saladd.reinitialisation();
		saladd.propagation();
	}

	@Override
	public void termine()
	{}

	public int getNbOublis()
	{
		return oubli.getNbOublis();
	}
	
	public String toString()
	{
		return oubli.toString();
	}

	public Set<String> getVariables() {
		return saladd.getFreeVariables();
	}
	
	// Juste pour un test
	public long count() {
		return saladd.getVDD().countingpondere();
	}

}
