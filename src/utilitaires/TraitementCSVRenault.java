package utilitaires;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

import compilateur.LecteurCdXml;
import compilateur.SALADD;
import compilateur.VDD;

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
 * Traitement des .csv de Renault où il faut dupliquer certaines valeurs
 * @author Pierre-François Gimenez
 *
 */

public class TraitementCSVRenault
{
	public static void main(String[] args)
	{
		boolean checkContraintes = false;
		String filename = "sorted_renault_small";
		boolean entete = true;
		String prefixData = "experiments/exp4/";
		
		LecteurCdXml lect=new LecteurCdXml();
		try {
			lect.lectureCSV(prefixData+filename, entete);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		String fichierContraintes = prefixData+"contraintes.xml";
		
		SALADD contraintes;
		contraintes = null;
		VDD x = null;

		if(checkContraintes)
		{
			contraintes = new SALADD();
			contraintes.compilation(fichierContraintes, true, 4, 0, 0, true);
			contraintes.propagation();
			x = contraintes.getVDD();
		}
		
		PrintWriter writer;
		try {
			writer = new PrintWriter(prefixData+"out.txt", "UTF-8");		
			for(int test=0; test<lect.nbligne; test++)
			{
				if(checkContraintes)
				{
					x.deconditionerAll();
					for(int j = 1; j < lect.nbvar; j++)
						x.conditioner(x.getVar(lect.var[j]), x.getVar(lect.var[j]).conv(lect.domall[test][j]));
					contraintes.propagation();
				
					// Si ça ne satisfait pas les contraintes, on vire
					if(!contraintes.isPossiblyConsistent())
						continue;
				}
				
				for(int i = 0; i < Integer.parseInt(lect.domall[test][0]); i++)
				{
					for(int j = 1; j < lect.nbvar-1; j++)
						writer.print(lect.domall[test][j]+",");
					writer.println(lect.domall[test][lect.nbvar-1]);
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
