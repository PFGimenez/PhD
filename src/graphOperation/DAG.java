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

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import compilateur.LecteurXML;

/**
 * Un graphe orienté acyclique
 * @author pf
 *
 */

public class DAG
{
	private static final int enfants = 1;
	private static final int parents = 0;
	public HashMap<String, ArrayList<String>>[] dag;
	
	public DAG(String bnFile)
	{
		dag = LecteurXML.lectureReseauBayesien(bnFile);
	}
	
	/**
	 * Construit un réseau bayésien
	 * @param var
	 * @param parent
	 */
	@SuppressWarnings("unchecked")
	public DAG(Set<String> vars, String parent)
	{
		// parent est le parent de toutes les variables (sauf de lui-même)
		dag = (HashMap<String, ArrayList<String>>[]) new HashMap[2];
		dag[0] = new HashMap<String, ArrayList<String>>();
		dag[1] = new HashMap<String, ArrayList<String>>();
		
		for(String s : vars)
		{
			if(s.equals(parent))
				continue;
			
			ArrayList<String> l = new ArrayList<String>();
			l.add(parent);
			dag[parents].put(s, l);
			dag[enfants].put(s, new ArrayList<String>());
		}
		
		ArrayList<String> e = new ArrayList<String>();
		e.addAll(vars);
		e.remove(parent);
		dag[parents].put(parent, new ArrayList<String>());
		dag[enfants].put(parent, e);
	}
	
	public void printGraphe(String filename)
	{
		try {
			
			FileWriter fichier;
			BufferedWriter output;
	
			fichier = new FileWriter(filename+".dot");
			output = new BufferedWriter(fichier);
			output.write("digraph G { ");
			output.newLine();
			output.write("ordering=out;");			
			output.newLine();
			
			for(String n : dag[enfants].keySet())
			{
				output.write(n+" [label="+n+"];");
				output.newLine();
				for(String v : dag[enfants].get(n))
				{
					output.write(n+" -> "+v+";");
					output.newLine();
				}
			}
			output.write("}");
			output.newLine();
			output.close();
		} catch (IOException e) {
			e.printStackTrace();
		}		
	}
}
