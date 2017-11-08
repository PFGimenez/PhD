package recommandation;

import java.util.ArrayList;
import java.util.HashMap;

import compilateur.SALADD;
import compilateurHistorique.DatasetInfo;
import compilateurHistorique.HistoriqueCompile;
import compilateurHistorique.Instanciation;
import compilateurHistorique.Neighborhood;
import recommandation.parser.ParserProcess;

/*   (C) Copyright 2016, Gimenez Pierre-François
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
 * Recommandation basée sur le voisinage (most popular choice)
 * @author Pierre-François Gimenez
 *
 */

public class AlgoVoisinsMostPopular implements AlgoReco, Clusturable
{
	private Neighborhood voisins = new Neighborhood();
	private int[] conf;
	private int nbVoisins;
	private SALADD contraintes;
	
	public AlgoVoisinsMostPopular(ParserProcess pp)
	{
		nbVoisins = Integer.parseInt(pp.read());
	}
	
	public AlgoVoisinsMostPopular()
	{
		this(20);
	}
	
	public AlgoVoisinsMostPopular(int nbVoisins)
	{
		this.nbVoisins = nbVoisins;
	}
	
	public void describe()
	{
		System.out.println("Most popular");
		System.out.println("Neighbours : "+nbVoisins);
	}
	
	@Override
	public void apprendContraintes(SALADD contraintes)
	{
		this.contraintes = contraintes;
	}
	
	@Override
	public void apprendDonnees(DatasetInfo dataset, ArrayList<String> filename, int nbIter, boolean entete)
	{
		apprendDonnees(dataset, HistoriqueCompile.readPossibleInstances(dataset, filename, entete, contraintes), 0);
	}

	@Override
	public void apprendDonnees(DatasetInfo dataset, Instanciation[] instances, int code)
	{
		voisins.compileHistorique(dataset, instances);
		conf = voisins.getEmptyConf();
	}

	@Override
	public String recommande(String variable, ArrayList<String> possibles)
	{
		return voisins.mostPopularChoice(conf, variable, nbVoisins, possibles);
	}
	
	public boolean isOracle()
	{
		return false;
	}

	@Override
	public void setSolution(String variable, String solution)
	{
		voisins.set(conf, variable, solution);
	}

	@Override
	public void oublieSession()
	{
		conf = voisins.getEmptyConf();
	}

	@Override
	public void termine()
	{}

	@Override
	public void terminePli()
	{}

	public String toString()
	{
		return getClass().getSimpleName();
	}
	
	@Override
	public void unassign(String variable)
	{
		voisins.unset(conf, variable);
	}
	
	@Override
	public HashMap<String, Double> metricCoeff()
	{
		return new HashMap<String, Double>();
	}

	@Override
	public HashMap<String, Double> metric()
	{
		return new HashMap<String, Double>();
	}
}
