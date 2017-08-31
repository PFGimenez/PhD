package recommandation;

import java.util.ArrayList;

import compilateurHistorique.DatasetInfo;
import compilateurHistorique.Neighborhood;

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
 * Recommandation basée sur le voisinage (naive bayes voter)
 * @author Pierre-François Gimenez
 *
 */

public class AlgoVoisinsNaive implements AlgoReco
{
	private Neighborhood voisins = new Neighborhood();
	private int[] conf;
	private int nbVoisins;
	
	public AlgoVoisinsNaive()
	{
		this(20);
	}
	
	public void describe()
	{
		System.out.println("Neighbourhood naive bayes");
		System.out.println("Neighbours : "+nbVoisins);
	}
	
	public AlgoVoisinsNaive(int nbVoisins)
	{
		this.nbVoisins = nbVoisins;
	}
	
/*	@Override
	public void apprendContraintes(SALADD contraintes)
	{}*/
	
	@Override
	public void apprendDonnees(DatasetInfo dataset, ArrayList<String> filename, int nbIter, boolean entete)
	{
		voisins.initVariables(dataset);
		voisins.compileHistorique(filename, entete);
		conf = voisins.getEmptyConf();
	}

	@Override
	public String recommande(String variable, ArrayList<String> possibles)
	{
		return voisins.naiveBayesVoter(conf, variable, nbVoisins);
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
}
