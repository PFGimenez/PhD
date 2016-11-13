package preferences.completeTree;

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
 * @author Pierre-François Gimenez
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
//	private transient HeuristiqueComplexe h; // utilisé seulement pour l'apprentissage, donc pas besoin de le sauvegarder
//	private double heuristique;
	protected boolean split;
	
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
			LexicographicStructure out = (LexicographicStructure)ois.readObject();
			nbS = out.getMaxNb();
			ois.close();
			return out;
		} catch (Exception e) {
			System.err.println(e);
		}
		return null;
	}
	
	protected abstract int getMaxNb();

	public LexicographicStructure(String variable, int nbMod)
	{
		this.nbMod = nbMod;
		this.variable = variable;		
		ordrePref = new ArrayList<String>();
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
			if(new File("affichage"+s+".dot").exists())
			{
				System.out.println("Sauvegarde annulée : le fichier .dot existe déjà");
				return;
			}
			System.out.println("Enregistrement de l'arbre…");
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
			System.out.println("Enregistrement terminé");
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
	public void setOrdrePref(Map<String, Integer> nbExemples)
	{
//		heuristique = h.computeHeuristique(nbExemples);

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
	
	public void setOrdrePrefRandom()
	{
		for(int i = 0; i < nbMod; i++)
		{
			ordrePref.add(Integer.toString(i));
		}
		Collections.shuffle(ordrePref);
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

	public abstract void updateBase(BigInteger base);
	
	public abstract BigInteger infereRang(ArrayList<String> element, ArrayList<String> ordreVariables);

	public abstract String infereBest(String varARecommander, ArrayList<String> possibles, HashMap<String, String> valeurs);
	
	public abstract int getRessemblance(LexicographicStructure other);

	public abstract HashMap<String, String> getConfigurationAtRank(BigInteger r);

	public abstract int getNbNoeuds();
	
	protected abstract ArrayList<LexicographicStructure> getEnfants();
	
	/**
	 * Renvoie le plus petit rang r tel que this(r) != autre(r)
	 * @param autre
	 * @return
	 */
	public BigInteger firstDifferentRank(LexicographicStructure autre)
	{
		BigInteger r = BigInteger.ZERO, r2;
		BigInteger max = getRangMax();
		
		do
		{
			r = r.add(BigInteger.ONE);
			HashMap<String, String> o = getConfigurationAtRank(r);
			ArrayList<String> val = new ArrayList<String>();
			ArrayList<String> var = new ArrayList<String>();
			
			for(String s : o.keySet())
			{
				var.add(s);
				val.add(o.get(s));
			}
			
			r2 = autre.infereRang(val, var);
		} while(r.compareTo(max) < 0 && r.compareTo(r2) == 0);
		
		return r; // soit le rang max, soit le premier rang différent
	}

	
	public double firstDifferentNode(LexicographicStructure autre)
	{
		LinkedList<LexicographicStructure> file1 = new LinkedList<LexicographicStructure>();
		LinkedList<LexicographicStructure> file2 = new LinkedList<LexicographicStructure>();
		file1.add(this);
		file2.add(autre);
		int cpt = 0;
		
		while(!file1.isEmpty())
		{
			if(file1.size() != file2.size())
			{
				int z = 0;
				z = 1 / z;
			}
			LexicographicStructure a = file1.poll(), b = file2.poll();
//			System.out.println(a.variable+" "+b.variable);
			if(!a.variable.equals(b.variable) || !a.ordrePref.equals(b.ordrePref))
				return cpt;
			cpt++;
			for(LexicographicStructure e : a.getEnfants())
				file1.add(e);
			for(LexicographicStructure e : b.getEnfants())
				file2.add(e);
		}
		return cpt;
	}

}
