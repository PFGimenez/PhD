/*   (C) Copyright 2017, Gimenez Pierre-François 
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

package recommandation;

import java.util.ArrayList;

import compilateurHistorique.Clusters;
import compilateurHistorique.DatasetInfo;
import compilateurHistorique.Instanciation;

/**
 * Adapte un algorithme existant à l'utilisation d'un cluster
 * @author pgimenez
 *
 */

public class AlgoClustered implements AlgoReco
{
	private Clusturable[] clusters;
	private Instanciation instanceReco;
	private Clusters c;
	
	public AlgoClustered(Class<? extends Clusturable> c, int nbClusters)
	{
		clusters = new Clusturable[nbClusters];
		for(int i = 0; i < nbClusters; i++)
			try {
				clusters[i] = c.newInstance();
			} catch (InstantiationException | IllegalAccessException e) {
				e.printStackTrace();
			}
	}

	@Override
	public void apprendDonnees(DatasetInfo dataset, ArrayList<String> filename, int nbIter, boolean entete)
	{
		instanceReco = new Instanciation(dataset);
		c = new Clusters(clusters.length, filename, entete);
		for(int i = 0; i < clusters.length; i++)
			clusters[i].apprendDonnees(dataset, c.getCluster(i));
	}

	@Override
	public String recommande(String variable, ArrayList<String> possibles)
	{
		return clusters[c.getNearestCluster(instanceReco)].recommande(variable, possibles);
	}

	@Override
	public void setSolution(String variable, String solution)
	{
		instanceReco.conditionne(variable, solution);
		for(int i = 0; i < clusters.length; i++)
			clusters[i].setSolution(variable, solution);
	}

	@Override
	public void unassign(String variable)
	{
		instanceReco.deconditionne(variable);
		for(int i = 0; i < clusters.length; i++)
			clusters[i].unassign(variable);
	}

	@Override
	public void oublieSession()
	{
		instanceReco.deconditionneTout();
		for(int i = 0; i < clusters.length; i++)
			clusters[i].oublieSession();
	}

	@Override
	public void termine()
	{
		for(int i = 0; i < clusters.length; i++)
			clusters[i].termine();
	}

	@Override
	public void describe()
	{
		System.out.println("Clusters : ");
		for(int i = 0; i < clusters.length; i++)
			clusters[i].describe();
	}

}
