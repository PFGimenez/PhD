package preferences;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;

import preferences.heuristiques.HeuristiqueOrdre;

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
 * Structure qui se base sur l'ordre lexicographique. Classe abstraite
 * @author pgimenez
 *
 */

public abstract class LexicographicStructure implements Serializable
{
	private static final long serialVersionUID = 1L;
	protected int nbMod;
	protected ArrayList<String> ordrePref;
	protected String variable;
//	private double entropie;
	protected BigInteger base;
	protected static int nbS = 0;
	protected int nb;
	private transient HeuristiqueOrdre h; // utilisé seulement pour l'apprentissage, donc pas besoin de le sauvegarder
	private double heuristique;
	
	public void save(String namefile)
	{
		ObjectOutputStream oos;
		try {
			oos = new ObjectOutputStream(new FileOutputStream(new File(namefile)));
			oos.writeObject(this);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static LexicographicStructure load(String namefile)
	{
		ObjectInputStream ois;
		try {
			ois = new ObjectInputStream(new FileInputStream(new File(namefile)));
			LexicographicStructure out = (LexicographicStructure)ois.readObject() ;
			ois.close();
			return out;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public LexicographicStructure(String variable, int nbMod, HeuristiqueOrdre h)
	{
		this.nbMod = nbMod;
		this.variable = variable;		
		ordrePref = new ArrayList<String>();
		this.h = h;
		nb = nbS;
		nbS++;
	}

	public void affiche()
	{
		affiche("");
	}
	
	public void affiche(String s)
	{
		FileWriter fichier;
		BufferedWriter output;

		try {
			fichier = new FileWriter("affichage"+s+".dot");
			output = new BufferedWriter(fichier);
			output.write("digraph G { ");
			output.newLine();
			output.write("ordering=out;");			
			output.newLine();
			affichePrivate(output);
			output.write("}");
			output.newLine();
			output.close();
//			Runtime.getRuntime().exec("dot -Tpdf affichage.dot -O");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	protected abstract void affichePrivate(BufferedWriter output) throws IOException;
		
	/**
	 * Met à jour l'entropie et ordrePref.
	 * @param nbExemples
	 */
	public void setNbExemples(Map<String, Integer> nbExemples)
	{
		heuristique = h.computeHeuristique(nbExemples);

		// Calcul de ordrePref
		
		// From http://stackoverflow.com/questions/109383/how-to-sort-a-mapkey-value-on-the-values-in-java
		LinkedList<Entry<String, Integer>> list = new LinkedList<Map.Entry<String,Integer>>(nbExemples.entrySet());
	     Collections.sort(list, new Comparator<Entry<String, Integer>>() {
	          public int compare(Entry<String, Integer> o1, Entry<String, Integer> o2) {
	               return -o1.getValue()
	              .compareTo(o2.getValue());
	          }
	     });

	    for (Iterator<Entry<String, Integer>> it = list.iterator(); it.hasNext();) {
	        Map.Entry<String, Integer> entry = it.next();
	        ordrePref.add((String) entry.getKey());
	    }
	    
	}
	
	protected int getPref(String valeur)
	{
		for(int i = 0; i < nbMod - 1; i++)
			if(ordrePref.get(i).equals(valeur))
				return i;
		return nbMod - 1;
	}
	
	protected String getPref(int indice)
	{
		return ordrePref.get(indice);
	}
	
	public int getNbMod()
	{
		return nbMod;
	}
	
	public BigInteger getRangMax()
	{
		return base.multiply(BigInteger.valueOf(nbMod));
	}
	
	public BigInteger getBase()
	{
		return base;
	}
	
	public String getVar()
	{
		return variable;
	}

	public double getHeuristique()
	{
		return heuristique;
	}
	
	public abstract void updateBase(BigInteger base);
	
	public abstract BigInteger infereRang(ArrayList<String> element, ArrayList<String> ordreVariables);

	public abstract String infereBest(String varARecommander, ArrayList<String> possibles, HashMap<String, String> valeurs);
	
	public abstract int getRessemblance(LexicographicStructure other);

	public abstract HashMap<String, String> getConfigurationAtRank(BigInteger r);

}
