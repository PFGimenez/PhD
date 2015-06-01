package algoreco;

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
 * Utilisé afin de générer les fichiers d'apprentissage sous forme XML
 * @author pf
 *
 */

public class XMLconverter implements AlgoReco
{
	private FileWriter fichier;
	private BufferedWriter output;
	private ArrayList<String> possibles;
	
	boolean firstTime;
	
	@Override
	public void initialisation(ArrayList<String> variables) {
	}

	@Override
	public void apprendContraintes(String filename)
	{}

	@Override
	public void apprendDonnees(ArrayList<String> filename, int nbIter)
	{
		try {
			if(output != null)
			{
				output.write("</session>");
				output.newLine();
				output.close();
			}
			fichier = new FileWriter("datasets/set"+nbIter+"_scenario.xml");
			output = new BufferedWriter(fichier);
			output.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
			output.newLine();
			output.newLine();
		} catch (IOException e) {
			e.printStackTrace();
		}
		firstTime = true;
	}

	@Override
	public String recommande(String variable, ArrayList<String> possibles)
	{		
		this.possibles = possibles;
		return possibles.get(0);
	}

	@Override
	public void setSolution(String variable, String solution) {
		try {
			output.write("	<affect var=\""+variable+"\" val=\""+solution+"\" domain=\"");
			boolean first = true;
			for(String s: possibles)
			{
				if(!first)
					output.write(" ");
				output.write(s);
				first = false;
			}
			output.write("\">");
			output.newLine();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

	@Override
	public void oublieSession() {
		try {
			if(!firstTime)
			{
				output.write("</session>");
				output.newLine();
				output.newLine();
			}
			firstTime = false;
			output.write("<session>");
			output.newLine();
			output.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void termine()
	{
		try {
			output.write("</session>");
			output.newLine();
			output.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
