package preferences.multipleTree;

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
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import compilateurHistorique.Instanciation;
import preferences.completeTree.LexTreeInterface;

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
 * Arbre lexicographique incomplet
 * @author Pierre-François Gimenez
 *
 */

public class LexicographicMultipleTree implements Serializable, LexTreeInterface
{
	protected int nbMod; // = le nombre d'enfants
	protected ArrayList<List<String>> ordrePref;
	protected List<String> variables;
	protected BigInteger base;
	protected static int nbS = 0;
	protected int nb;
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
	
	public static LexicographicMultipleTree load(String namefile)
	{
		ObjectInputStream ois;
		try {
			ois = new ObjectInputStream(new FileInputStream(new File(namefile)));
			LexicographicMultipleTree out = (LexicographicMultipleTree)ois.readObject();
			nbS = out.getMaxNb();
			ois.close();
			return out;
		} catch (Exception e) {
			System.err.println(e);
		}
		return null;
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
				System.out.println("Sauvegarde annulée : le fichier affichage"+s+".dot existe déjà");
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
			e.printStackTrace();
		}

	}
	
	protected void affichePrivate(BufferedWriter output) throws IOException
	{
		int nbAffMax = 5;
		output.write(nb+" [label=\""+variables+"\"];");
		output.newLine();
		if(!split && enfants != null)
		{
			enfants[0].affichePrivate(output);
			output.write(nb+" -> "+enfants[0].nb+" [label=\"");
			for(int i = 0; i< Math.min(nbMod - 1, nbAffMax); i++)
				output.write(ordrePref.get(i)+">");
			if(nbMod <= nbAffMax)
				output.write(ordrePref.get(nbMod-1).toString());
			else
				output.write("...");
			output.write("\"];");
			output.newLine();
		}
		else if(enfants != null)
		{
			for(int i = 0; i<nbMod; i++)
			{
				enfants[i].affichePrivate(output);
				output.write(nb+" -> "+enfants[i].nb+" [label=\""+i+" : "+ordrePref.get(i)+"\"];");
				output.newLine();
			}
		}
		else
		{
			for(int i = 0; i< Math.min(nbMod, nbAffMax); i++)
			{
				output.write(++nbS+" [style=invisible];");				
				output.newLine();
				output.write(nb+" -> "+nbS+" [label=\""+ordrePref.get(i)+"\"];");
				output.newLine();
			}
			if(nbMod > nbAffMax)
			{
				output.write(++nbS+" [style=invisible];");				
				output.newLine();
				output.write(nb+" -> "+nbS+" [label=\"...\"];");
				output.newLine();
			}

		}
			
	}	
	/**
	 * Met à jour l'entropie et ordrePref.
	 * @param nbExemples
	 */
	public void setOrdrePref(Map<List<String>, Integer> nbExemples)
	{
//		heuristique = h.computeHeuristique(nbExemples);

		// Calcul de ordrePref
		
		// From http://stackoverflow.com/questions/109383/how-to-sort-a-mapkey-value-on-the-values-in-java
		LinkedList<Entry<List<String>, Integer>> list = new LinkedList<Map.Entry<List<String>,Integer>>(nbExemples.entrySet());
	     Collections.sort(list, new Comparator<Entry<List<String>, Integer>>() {
	          public int compare(Entry<List<String>, Integer> o1, Entry<List<String>, Integer> o2) {
	               return -o1.getValue()
	              .compareTo(o2.getValue());
	          }
	     });

	    for (Iterator<Entry<List<String>, Integer>> it = list.iterator(); it.hasNext();) {
	        Map.Entry<List<String>, Integer> entry = it.next();
	        ordrePref.add((List<String>) entry.getKey());
	    }
	    
	}
	/*
	public void setOrdrePrefRandom()
	{
		ordrePref = new ArrayList<List<String>>();
		ordrePref.add(new ArrayList<String>());
		for(String s : variables)
		{
			Variable v = dataset.vars[dataset.mapVar.get(s)];
			ArrayList<List<String>> next = new ArrayList<List<String>>();
			for(List<String> val : ordrePref)
				for(int i = 0; i < v.domain; i++)
				{
					List<String> l = new ArrayList<String>();
					l.addAll(val);
					l.add(v.values.get(i));
					next.add(l);
				}
			ordrePref = next;
		}

		Collections.shuffle(ordrePref);
	}*/

	
	protected int getPref(List<String> valeur)
	{
		for(int i = 0; i < nbMod - 1; i++)
			if(ordrePref.get(i).equals(valeur))
				return i;
		assert ordrePref.get(nbMod-1).equals(valeur);
		return nbMod - 1;
	}
	
	protected List<String> getPref(int indice)
	{
		assert indice >= 0 && indice < ordrePref.size();
		return ordrePref.get(indice);
	}
	
	public BigInteger getRangMax()
	{
		return base.multiply(BigInteger.valueOf(nbMod));
	}
	
	public BigInteger getBase()
	{
		return base;
	}

	/**
	 * Renvoie le plus petit rang r tel que this(r) != autre(r)
	 * @param autre
	 * @return
	 */
	public BigInteger firstDifferentRank(LexicographicMultipleTree autre)
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

	/*
	public double firstDifferentNode(LexicographicMultipleTree autre)
	{
		LinkedList<LexicographicMultipleTree> file1 = new LinkedList<LexicographicMultipleTree>();
		LinkedList<LexicographicMultipleTree> file2 = new LinkedList<LexicographicMultipleTree>();
		file1.add(this);
		file2.add(autre);
		int cpt = 0;
		
		while(!file1.isEmpty())
		{
			assert file1.size() == file2.size();
			LexicographicMultipleTree a = file1.poll(), b = file2.poll();
//			System.out.println(a.variable+" "+b.variable);
			if(!a.variables.equals(b.variables) || !a.ordrePref.equals(b.ordrePref))
				return cpt;
			cpt++;
			for(LexicographicMultipleTree e : a.getEnfants())
				file1.add(e);
			for(LexicographicMultipleTree e : b.getEnfants())
				file2.add(e);
		}
		return cpt;
	}*/

	/**
	 * Utilisé par les LP-tree générés dynamiquement
	 * @param base
	 */
	public void updateBaseNoRecursive(BigInteger base)
	{
		this.base = base.divide(BigInteger.valueOf(nbMod));
	}
	
	private static final long serialVersionUID = -2858953018327076982L;
	// un enfant peut être un LexicographicTree ou un LexicographicOrder
	private LexicographicMultipleTree[] enfants;
	
	public LexicographicMultipleTree(List<String> variables, int nbMod, boolean split)
	{
		this.nbMod = nbMod;
		this.variables = variables;		
		ordrePref = new ArrayList<List<String>>();
		nb = nbS;
		nbS++;
		enfants = null;
		this.split = split;
	}
	
/*	protected void affichePrivate(BufferedWriter output) throws IOException
	{
		output.write(nb+" [label="+variable+"];");
		output.newLine();
		if(!split && enfants != null)
		{
			enfants[0].affichePrivate(output);
			
			for(int i = 0; i<nbMod; i++)
			{
				output.write(nb+" -> "+enfants[0].nb+" [label=\""+ordrePref.get(i)+"\"];");
				output.newLine();
			}	
		}
		else if(enfants != null)
		{
			for(int i = 0; i<nbMod; i++)
			{
				enfants[i].affichePrivate(output);
				output.write(nb+" -> "+enfants[i].nb+" [label=\""+ordrePref.get(i)+"\"];");
				output.newLine();
			}
		}
		else
			for(int i = 0; i<nbMod; i++)
			{
				output.write(++nbS+" [style=invisible];");				
				output.newLine();
				output.write(nb+" -> "+nbS+" [label=\""+ordrePref.get(i)+"\"];");
				output.newLine();
			}
			
	}
		*/
	/**
	 * Met à jour la base des enfants existants
	 */
	public void updateBaseChildren()
	{
		if(enfants != null)
		{
			if(split)
			{
				for(LexicographicMultipleTree e : enfants)
					if(e != null)
						e.updateBaseNoRecursive(base);
			}
			else
				enfants[0].updateBaseNoRecursive(base);
		}
		else
			System.err.println("Enfants == null !");
	}
	
	public void updateBase(BigInteger base)
	{
		this.base = base.divide(BigInteger.valueOf(nbMod));
		if(enfants != null)
			if(split)
				for(LexicographicMultipleTree e : enfants)
					e.updateBase(this.base);
			else
				enfants[0].updateBase(this.base);
	}
	
	public void setEnfant(int indice, LexicographicMultipleTree enfant)
	{
		if(enfants == null)
			enfants = new LexicographicMultipleTree[nbMod];
		if(split)
			enfants[indice] = enfant;
		else // pas de split : tous les enfants sont les mêmes
		{
			for(int i = 0; i < enfants.length; i++)
				enfants[i] = enfant;
		}
	}
	
	@Override
	public BigInteger infereRang(List<String> val, List<String> var)
	{
		HashMap<String, String> map = new HashMap<String, String>();
		for(int i = 0; i < val.size(); i++)
			map.put(var.get(i), val.get(i));
		return infereRang(map);
	}

	private transient List<String> values;

	public BigInteger infereRang(HashMap<String, String> val)
	{
		if(values == null)
			values = new ArrayList<String>();
		
		values.clear();
		for(String v : variables)
			values.add(val.get(v));
		
		if(enfants == null)
			return base.multiply(BigInteger.valueOf(getPref(values)));
		else
		{
			int nbFils = ordrePref.indexOf(values);
			BigInteger tmp = enfants[nbFils].infereRang(val);
			return base.multiply(BigInteger.valueOf(getPref(values))).add(tmp);
		}
	}
	
	public String infereBest(String varARecommander, HashMap<String, String> valeurs)
	{
		int bestConsistent = -1;
		// peut-être que des variables du nœud sont instanciées
		for(int i = 0; i < nbMod; i++)
		{
			boolean possible = true;
			for(int k = 0; k < variables.size(); k++)
			{
				String val = valeurs.get(variables.get(k));
				if(val != null && !val.equals(getPref(i).get(k)))
				{
					possible = false;
					break;
				}
			}
			if(possible)
			{
				bestConsistent = i;
				break;
			}
		}
		
		assert bestConsistent >= 0 : "bestConsistent = "+bestConsistent+", val = "+valeurs+", variables = "+variables+", ordrePref = "+ordrePref;
		
		int index = variables.indexOf(varARecommander);
		if(index >= 0) // ça y est, on connaît la meilleure valeur
			return getPref(bestConsistent).get(index);

		return enfants[bestConsistent].infereBest(varARecommander, valeurs);
	}
/*
	public int getRessemblance(LexicographicMultipleTree other)
	{
//		if(other instanceof LexicographicOrder)
//			return other.getRessemblance(this);

		LexicographicMultipleTree otherT = (LexicographicMultipleTree) other;
		if(otherT.variable.equals(variable) && otherT.getPref(0).equals(getPref(0)))
		{
//			System.out.println("Egal");
			if(enfants == null || otherT.enfants == null)
				return 1;
			else
				return 1 + enfants[0].getRessemblance(otherT.enfants[0]);
		}
//		System.out.println(otherT.variable+" "+variable);
//		System.out.println(otherT.getPref(0)+" "+getPref(0));
//		System.out.println(otherT.variable.equals(variable)+" "+otherT.getPref(0).equals(getPref(0)));
		return 0;
	}*/
	/*
	public ArrayList<String> getVarOrdre()
	{
		ArrayList<String> out;
		if(enfants == null || !(enfants[0] instanceof LexicographicMultipleTree))
			out = new ArrayList<String>();
		else
			out = ((LexicographicMultipleTree)enfants[0]).getVarOrdre();
		out.add(0,variable);
		return out;
	}*/
	
	public HashMap<String, String> getConfigurationAtRank(BigInteger r)
	{
		// On est à la feuille
		if(enfants == null)
		{
			HashMap<String, String> out = new HashMap<String, String>();
			List<String> l = getPref(r.intValue());
			for(int i = 0; i < variables.size(); i++)
				out.put(variables.get(i), l.get(i));
			return out;
		}
		else
		{
			for(int i = 1; i <= nbMod; i++)
				if(r.compareTo(base.multiply(BigInteger.valueOf(i))) < 0)
				{
					HashMap<String, String> out = enfants[i-1].getConfigurationAtRank(r.mod(base));
					List<String> l = getPref(i-1);
					for(int k = 0; k < variables.size(); k++)
						out.put(variables.get(k), l.get(k));
					return out;
				}
			System.err.println("Rang trop grand");
			return null;
		}
	}
	
/*	public ArrayList<String> getPrefOrdre()
	{
		ArrayList<String> out;
		if(enfants == null || !(enfants[0] instanceof LexicographicMultipleTree))
			out = new ArrayList<String>();
		else
			out = ((LexicographicMultipleTree)enfants[0]).getVarOrdre();
		out.add(0,getPref(0));
		return out;
	}*/

	protected int getMaxNb()
	{
		if(enfants == null)
			return nb;
		else
		{
			int out = nb;
			if(split)
			{
				for(LexicographicMultipleTree e : enfants)
					out = Math.max(out, e.getMaxNb());
				return out;
			}
			else
				return Math.max(out, enfants[0].getMaxNb());
		}
	}

	public int getNbNoeuds()
	{
		if(enfants == null)
			return 1;
		else
		{
			if(split)
			{
				int out = 1;
				for(LexicographicMultipleTree e : enfants)
					out += e.getNbNoeuds();
				return out;
			}
			else
				return 1 + enfants[0].getNbNoeuds();
		}
	}

	protected ArrayList<LexicographicMultipleTree> getEnfants()
	{
		ArrayList<LexicographicMultipleTree> out = new ArrayList<LexicographicMultipleTree>();
		if(enfants != null)
		{
			if(split)
				for(LexicographicMultipleTree e : enfants)
					out.add(e);
			else
				for(int i = 0; i < nbMod; i++)
					out.add(enfants[0]);
		}
		return out;
	}
	
	public int getTaille()
	{
		if(enfants == null)
			return nbMod-1;
		else
		{
			if(split)
			{
				int out = nbMod-1;
				for(LexicographicMultipleTree e : enfants)
					out += e.getNbNoeuds();
				return out;
			}
			else
				return nbMod-1 + enfants[0].getNbNoeuds();
		}
	}

	public BigInteger sommeRang(Instanciation[] instances)
	{
		BigInteger out = BigInteger.ZERO;
		HashMap<String, String> map = new HashMap<String, String>();
		for(Instanciation i : instances)
		{
			List<String> var = i.getVarConditionees();
			for(String v : var)
				map.put(v, i.getValue(v));
			out = out.add(infereRang(map));
			out = out.add(BigInteger.ONE); // parce que infereRang commence à 0
		}
		return out;
	}
	
	public BigInteger rangMoyen(Instanciation[] instances)
	{
		return sommeRang(instances).divide(BigInteger.valueOf(instances.length));
	}

}
