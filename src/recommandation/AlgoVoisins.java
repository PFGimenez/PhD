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

import java.util.List;

import compilateurHistorique.DatasetInfo;
import compilateurHistorique.Instanciation;
import compilateurHistorique.Neighborhood;
import compilateurHistorique.neighbohroodSize.ConstantNeighborhood;
import compilateurHistorique.neighbohroodSize.InverseNeighborhood;
import compilateurHistorique.neighbohroodSize.NeighborhoodSizeComputer;
import compilateurHistorique.neighbohroodSize.QuadraticInverseNeighborhood;
import recommandation.parser.ParserProcess;

/**
 * Classe abstraite pour les algos basés sur voisins
 * @author pgimenez
 *
 */

public abstract class AlgoVoisins extends Clusturable
{
	protected Neighborhood voisins = new Neighborhood();
	protected int[] conf;
	protected NeighborhoodSizeComputer computer;
	
	public AlgoVoisins(ParserProcess pp)
	{
		String comp = pp.read();
		int param = Integer.parseInt(pp.read());
		if(comp.equals("constant"))
			computer = new ConstantNeighborhood(param);
		else if(comp.equals("inverse"))
			computer = new InverseNeighborhood(param);
		else if(comp.equals("quadratic"))
			computer = new QuadraticInverseNeighborhood(param);
		else
		{
			System.out.println("Unknown neighborhood computer : "+comp+"\\Possible values : constant, inverse, quadratic.");
		}
	}

	public AlgoVoisins()
	{
		this(20);
	}
	
	public AlgoVoisins(int nbVoisins)
	{
		computer = new ConstantNeighborhood(nbVoisins);
	}
	
	@Override
	public void apprendDonnees(DatasetInfo dataset, List<Instanciation> instances, long code)
	{
		voisins.compileHistorique(dataset, instances);
		conf = voisins.getEmptyConf();
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

	@Override
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
