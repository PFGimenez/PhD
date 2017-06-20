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

package utilitaires;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import compilateur.LecteurCdXml;

/**
 * Transforme les variables en V1, V2, etc. et les valeurs en 0, 1, 2, etc.
 * @author pgimenez
 *
 */

public class Anonymiser
{

	public static void main(String[] args)
	{
		if(args.length < 2)
		{
			System.out.println("Usage : Anonymiser input-file output-file");
		}
		boolean entete = true;
		
		LecteurCdXml lect=new LecteurCdXml();
		lect.lectureCSV(args[0], entete);

		@SuppressWarnings("unchecked")
		List<String>[] valeurs = (List<String>[]) new ArrayList[lect.nbvar-1];
			
		PrintWriter writer;
		try {
			writer = new PrintWriter(args[1], "UTF-8");		
			// l'entête
			for(int i = 0; i < lect.nbvar - 1; i++)
			{
				valeurs[i] = new ArrayList<String>();
				writer.print("v"+(i+1));
				if(i != lect.nbvar-2)
					writer.print(",");
			}
			writer.println();
			for(int test=0; test<lect.nbligne; test++)
			{
				for(int i = 0; i < Integer.parseInt(lect.domall[test][0]); i++)
				{
					for(int j = 1; j < lect.nbvar; j++)
					{
						int index = valeurs[j-1].indexOf(lect.domall[test][j]);
						if(index == -1)
						{
							index = valeurs[j-1].size();
							valeurs[j-1].add(lect.domall[test][j]);
						}
						writer.print(index);
						if(j != lect.nbvar-1)
							writer.print(",");
					}
					writer.println();
				}
			}
			writer.close();

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		System.out.println("C'est fini !");
	}
	
}
