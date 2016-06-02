package recommandation;

import java.util.ArrayList;

import compilateur.SALADD;
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
 * Recommandation basée sur le voisinage
 * @author pgimenez
 *
 */

public class AlgoVoisins implements AlgoReco
{
	private Neighborhood voisins = new Neighborhood();
	private int[] conf;
	private int nbVoisins;
	
	public AlgoVoisins(int nbVoisins)
	{
		this.nbVoisins = nbVoisins;
	}
	
	@Override
	public void apprendContraintes(SALADD contraintes)
	{}
	
	@Override
	public void apprendDonnees(ArrayList<String> filename, int nbIter, boolean entete)
	{
		voisins.compileHistorique(filename, entete);
		conf = voisins.getEmptyConf();
	}

	@Override
	public String recommande(String variable, ArrayList<String> possibles)
	{
		return voisins.weightedMajorityVoter(conf, variable, nbVoisins);
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

	public String toString()
	{
		return getClass().getSimpleName();
	}
	
	public void initHistorique(ArrayList<String> filename, boolean entete)
	{
		voisins.initVariables(filename, entete);
	}

}
