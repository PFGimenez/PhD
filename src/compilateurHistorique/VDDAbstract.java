package compilateurHistorique;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
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
	private static int nbS = 0;
	
	public VDDAbstract()
	{
		nb = nbS++;
	}
	
	protected int nbInstances = 0;
	public int nbVarInstanciees;
	protected int nb;
	
//	public abstract boolean computeLineaire();
	public abstract int getNbInstances(Integer[] values, int nbVarInstanciees);
	public abstract void addInstanciation(Integer[] values);
	protected abstract void getNbInstancesToutesModalitees(HashMap<String, Integer> out, int nbVar, Integer[] values, ArrayList<String> possibles, int nbVarInstanciees);
	public abstract int getNbNoeuds();
	
	protected abstract void affichePrivate(BufferedWriter output) throws IOException;
	
	public void print(int nb)
	{
		FileWriter fichier;
		BufferedWriter output;
		try {
			fichier = new FileWriter("tmp/affichageVDD-"+nb+".dot");
			output = new BufferedWriter(fichier);
			output.write("digraph G { ");
			output.newLine();
			output.write("ordering=out;");			
			output.newLine();
			affichePrivate(output);
			output.write("}");
			output.newLine();
			output.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
