package utilitaires;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

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
 * Utilisé afin de générer les fichiers sous forme XML pour la recommandation
 * @author Pierre-François Gimenez
 *
 */

public class XMLcreator
{
	private FileWriter fichier;
	private BufferedWriter output;
	private String dossier;
	private ArrayList<String> possibles;
	private boolean avecPossibles;
	
	public XMLcreator(String dossier, boolean avecPossibles)
	{
		this.dossier = dossier;
		this.avecPossibles = avecPossibles;
	}
	
	public void open(int nbIter)
	{
		try {
			if(avecPossibles)
				fichier = new FileWriter(dossier+"/set"+nbIter+"_scenario.xml");
			else
				fichier = new FileWriter(dossier+"/set"+nbIter+"_exemples.xml");
			output = new BufferedWriter(fichier);
			output.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
			output.newLine();
			output.newLine();
			if(avecPossibles)
				output.write("<scenarios>");
			else
				output.write("<exemples>");
			output.newLine();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void close()
	{
		try {
			if(avecPossibles)
				output.write("</scenarios>");
			else
				output.write("</exemples>");
			output.newLine();
			output.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void setSolution(String variable, String solution) {
		try {
			if(avecPossibles)
			{
				output.write("	<affect var=\""+variable+"\" val=\""+solution+"\" domain=\"");
				boolean first = true;
				for(String s: possibles)
				{
					if(!first)
						output.write(" ");
					output.write(s);
					first = false;
				}
				output.write("\"/>");
			}
			else
				output.write("	<value var=\""+variable+"\" val=\""+solution+"\"/>");
			output.newLine();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

	public void debutSession()
	{
		try {
			if(avecPossibles)
				output.write("<session>");
			else
				output.write("<exemple>");
			output.newLine();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void finSession()
	{
		try {
			if(avecPossibles)
				output.write("</session>");
			else
				output.write("</exemple>");
			output.newLine();
			output.newLine();
			output.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void setPossibles(ArrayList<String> values_array)
	{
		if(!avecPossibles)
			System.out.println("Valeurs possibles non utilisées");
		this.possibles = values_array;
	}


}
