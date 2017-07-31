package compilateurHistorique.vdd;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;

import compilateurHistorique.Instanciation;


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
 * Feuille du VDD
 * @author Pierre-François Gimenez
 *
 */

public class VDDLeaf extends VDDAbstract implements Serializable
{
	private static final long serialVersionUID = 1L;

	@Override
	public void addInstanciation(Integer[] values)
	{
		nbInstances++;
	}
	
	protected void affichePrivate(BufferedWriter output) throws IOException
	{
		output.write(nb+" [label="+nbInstances+"]");
		output.newLine();
	}
	
	@Override
	protected int getNbInstances(Instanciation instance, int nbVarInstanciee)
	{
		return nbInstances;
	}
	
	@Override
	public int getNbInstances(Instanciation instance)
	{
		return nbInstances;
	}
	
	@Override
	public void getNbInstancesToutesModalitees(HashMap<String, Integer> out, int nbVar, Instanciation instance)
	{}
	
	@Override
	protected void getNbInstancesToutesModalitees(HashMap<String, Integer> out, int nbVar, Instanciation instance, int nbVarInstanciees)
	{}
	
	@Override
	public int getNbNoeuds()
	{
		return 1;
	}

}
