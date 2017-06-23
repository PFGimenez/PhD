package utilitaires;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

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

/**
 * Durcit des contraintes existantes
 * @author Pierre-François Gimenez
 *
 */

public class HardenConstraints
{
	public static void main(String[] args)
	{
		List<String> tuples = new ArrayList<String>();
		if(args.length < 3)
		{
			System.out.println("HardenConstraints contraintes-in.xml contraintes-out.xml durete");
		}
		Random r = new Random();
		double durete = Double.parseDouble(args[2]);
		try {
			BufferedReader reader = new BufferedReader(new FileReader(args[0]));
			BufferedWriter writer = new BufferedWriter(new FileWriter(args[1]));
			String l;
			while((l = reader.readLine()) != null)
			{
				if(l.contains("<relation name"))
				{
					tuples.add(l);
					while((l = reader.readLine()) != null)
					{
						tuples.add(l);
						if(l.contains("</relation>"))
							break;
					}
					Iterator<String> iter = tuples.iterator();
					while(iter.hasNext())
					{
						String s = iter.next();
						if(s.contains("<relation name") || s.contains("</relation>"))
							continue;
						if(r.nextDouble() < durete)
							iter.remove();
					}
					String first = tuples.get(0);
					writer.write(first.split("nbTuples")[0]+"nbTuples=\""+(tuples.size()-2)+"\" semantics=\"supports\">");
					writer.newLine();
					for(int i = 1; i < tuples.size()-1; i++)
					{
						String s = tuples.get(i);
						if(i == tuples.size()-2)
							s = s.split("\\|")[0];
						writer.write(s);
						writer.newLine();
					}
					writer.write("</relation>");
					writer.newLine();
					tuples.clear();
				}
				else
				{
					writer.write(l);
					writer.newLine();
				}
			}
			writer.close();
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
	
}
