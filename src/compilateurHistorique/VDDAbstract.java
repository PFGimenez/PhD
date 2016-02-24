package compilateurHistorique;

import java.util.ArrayList;
import java.util.HashMap;

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
 * Classe abstraite du VDD utilisé pour la compilation d'historique
 * @author pgimenez
 *
 */

public abstract class VDDAbstract
{
	public abstract int getNbInstances(Integer[] values, int nbVarInstanciees);
	public abstract void addInstanciation(Integer[] values);
	protected abstract void getNbInstancesToutesModalitees(HashMap<String, Integer> out, int nbVar, Integer[] values, ArrayList<String> possibles, int nbVarInstanciees);
	public abstract int getNbNoeuds();
}
