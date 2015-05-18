import heuristique_contraintes.HeuristiqueContraintesRien;
import heuristique_variable.HeuristiqueVariableMCSinvPlusUn;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;

import br4cp.SALADD;

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
 * Vérifier que des entrées respectent les contraintes. Utilisé pour la génération de données.
 * @author pgimenez
 *
 */

public class FiltrageContraintes {
	
	public static void main(String[] args) {
		SALADD contraintes = new SALADD();
		contraintes.compilation("medium.xml", true, new HeuristiqueVariableMCSinvPlusUn(), new HeuristiqueContraintesRien(), 0);
		contraintes.initialize();
	
		//lecture du fichier texte	
		try{
			InputStream ips=new FileInputStream("samples"); 
			File f = new File("samples_filtered");
			InputStreamReader ipsr=new InputStreamReader(ips);
			BufferedReader br=new BufferedReader(ipsr);
			int nb_lignes = Integer.parseInt(br.readLine().trim());
			int nb_vars = Integer.parseInt(br.readLine().trim());
		    PrintWriter pw = new PrintWriter (new BufferedWriter (new FileWriter (f)));
			String[] entree = new String[nb_vars];
			String[] var = new String[nb_vars];
			int total = 0;
			for(int j = 0; j < nb_vars; j++)
				var[j] = br.readLine().trim();
			for(int i = 0; i < nb_lignes; i++)
			{
				boolean respecte_contraintes = true;
				for(int j = 0; j < nb_vars; j++)
				{
					entree[j] = br.readLine().trim();
//					System.out.println(var[j]+" "+entree[j]);
//					respecte_contraintes = respecte_contraintes && contraintes.isPresentInCurrentDomain(var[j], entree[j]);
					if(entree[j].compareTo("999") == 0 || entree[j].compareTo("-1") == 0)
					{
						respecte_contraintes = false;
					}
					if(!respecte_contraintes)
					{
//						System.out.println("non");
						continue;
					}
//					contraintes.assignAndPropagate(var[j], entree[j]);
				}
				contraintes.reinitialisation();
				if(respecte_contraintes)
				{
					total++;
					if(total % 10 == 0)
						System.out.println(total);
//					System.out.println(i+": ok");
					for(int j = 0; j < nb_vars; j++)
					{
						pw.print(entree[j]);
						if(j != nb_vars-1)
							pw.print(", ");
						else
							pw.println();
					}
					
				}
//				else
//					System.out.println(i+": pas ok");
			}
			br.close(); 
			pw.close();
		}		
		catch (Exception e){
			e.printStackTrace();
		}
		
	}

}
