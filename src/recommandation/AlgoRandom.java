package recommandation;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import compilateurHistorique.DatasetInfo;
import compilateurHistorique.Instanciation;
import recommandation.parser.ParserProcess;

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
 * Recommandation aléatoire
 * @author Pierre-François Gimenez
 *
 */

public class AlgoRandom extends Clusturable
{

	private Random r = new Random();
	
	public void describe()
	{
		System.out.println("Random recommander");
	}
	
	public AlgoRandom(ParserProcess pp)
	{}

	@Override
	public String recommande(String variable, ArrayList<String> possibles)
	{
		if(possibles == null)
			possibles = dataset.vars[dataset.mapVar.get(variable)].values;
		return possibles.get(r.nextInt(possibles.size()));
	}

	@Override
	public void setSolution(String variable, String solution)
	{}

	@Override
	public void oublieSession()
	{}

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
	{}

	@Override
	public void apprendDonnees(DatasetInfo dataset, List<Instanciation> instances, long code)
	{}


}
