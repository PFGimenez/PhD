package recommandation;

import java.util.ArrayList;
import java.util.Random;

import compilateurHistorique.MultiHistoComp;

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

public class AlgoRandom implements AlgoReco {

	private Random r = new Random();
	private ArrayList<String> filenameInit;
	private MultiHistoComp historique;
	
/*	@Override
	public void apprendContraintes(SALADD contraintes)
	{}*/
	
	public void describe()
	{
		System.out.println("Random recommander");
	}
	
	@Override
	public void apprendDonnees(ArrayList<String> filename, int nbIter, boolean entete)
	{
		historique.compile(filename, entete);
	}

	@Override
	public String recommande(String variable, ArrayList<String> possibles)
	{
		if(possibles == null)
			possibles = historique.getValues(variable);
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

	public String toString()
	{
		return getClass().getSimpleName();
	}
	
	public void initHistorique(ArrayList<String> filename, boolean entete)
	{
		historique = new MultiHistoComp(filename, entete, null);
	}

	@Override
	public void unassign(String variable)
	{}

}
