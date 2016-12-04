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

package graphOperation;

import java.util.ArrayList;
import java.util.HashMap;

import compilateur.LecteurXML;

/**
 * Un graphe orienté acyclique
 * @author pf
 *
 */

public class DAG
{
//	private static final int parents = 0;
//	private static final int enfants = 1;
	public HashMap<String, ArrayList<String>>[] dag;
	
	public DAG(String bnFile)
	{
		dag = LecteurXML.lectureReseauBayesien(bnFile);
	}
	
}
