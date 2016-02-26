package graphOperation;

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
import java.util.ArrayList;
import java.util.HashMap;

import compilateur.SALADD;
import compilateurHistorique.HistoComp;
import compilateurHistorique.Instanciation;
import compilateurHistorique.IteratorInstances;

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
 * Un sous-graphe, obtenu après partitionnement
 * @author pgimenez
 *
 */

public class Graphe implements Serializable
{
	private static final long serialVersionUID = 1L;
	private ArrayList<String> cutset;
	private int[] cutsetIndice;
	private ArrayList<String> acutset;
	private ArrayList<String> context;
	private int[] contextIndice;
	private ArrayList<String> vars; // liste les variables du graphe et leurs parents
	private int[] varsIndice;
	private ArrayList<String> graphe;
	private Graphe[] sousgraphes;
	private transient DTreeGenerator dtreegenerator;
//	private DSeparation dsep;
	private HistoComp historique;
	private double[] cache;
	private static int seuil;
	public static int nbS = 0;
	private int nb;
	private transient SALADD contraintes;
	private int tailleCache;
	private static boolean avecHisto;
	private	boolean utiliseCache;
	
	public static void config(int seuilP, boolean avecHistoP)
	{
		seuil = seuilP;
		avecHisto = avecHistoP;		
	}
	
	public Graphe(SALADD contraintes, ArrayList<String> acutset, ArrayList<String> graphe, HistoComp historique, DTreeGenerator dtreegenerator)
	{
		this.contraintes = contraintes;
		nb = nbS;
		nbS++;
//		System.out.println(nb+"Création d'un graphe.");
//		this.dsep = dsep;
		
		this.acutset = acutset;
		this.graphe = graphe;

		vars = new ArrayList<String>();
		for(String s : graphe)
		{
			if(!vars.contains(s))
				vars.add(s);
			for(String p : dtreegenerator.getParents(s))
				if(!vars.contains(p))
					vars.add(p);
		}
		
		HashMap<String, Integer> mapVar = historique.getMapVar();

		varsIndice = new int[vars.size()];
		for(int i = 0; i < varsIndice.length; i++)
			varsIndice[i] = mapVar.get(vars.get(i));

		this.dtreegenerator = dtreegenerator;
		
		this.historique = historique;
		
		// context = acutset (intersection) vars
		context = new ArrayList<String>();

		for(String s : acutset)
			if(vars.contains(s))
				context.add(s);
				
		contextIndice = new int[context.size()];
		for(int i = 0; i < contextIndice.length; i++)
			contextIndice[i] = mapVar.get(context.get(i));
			
		
		tailleCache = Instanciation.getTailleCache(context);
		utiliseCache = tailleCache > 0;
//		if(tailleCache == -1 || tailleCache > cacheFactor) // overflow de la taille
//			tailleCache = cacheFactor;
		
//		System.out.println("Taille du cache : "+tailleCache);

		if(utiliseCache)
		{
			cache = new double[tailleCache];
			
			for(int i = 0; i < tailleCache; i++)
				cache[i] = -1;
		}
//		printGraphe();
/*
		System.out.print(nb+" Vars : ");
		for(String s : vars)
			System.out.print(s+" ");
		System.out.println();*/
	}
	
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
	
	public static Graphe load(String namefile)
	{
		ObjectInputStream ois;
		try {
			ois = new ObjectInputStream(new FileInputStream(new File(namefile)));
			Graphe out = (Graphe)ois.readObject() ;
			ois.close();
			return out;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public void reinitCache()
	{
		if(utiliseCache)
		{
			for(int i = 0; i < tailleCache; i++)
				cache[i] = -1;
			if(sousgraphes != null)
			{
				sousgraphes[0].reinitCache();
				sousgraphes[1].reinitCache();
			}
		}
	}

	public void reinitCachePartiel(String variable)
	{
		if(utiliseCache)
		{
			if(vars.contains(variable)/* && !context.contains(variable)*/)
			{
				for(int i = 0; i < tailleCache; i++)
					cache[i] = -1;
				if(sousgraphes != null)
				{
					sousgraphes[0].reinitCachePartiel(variable);
					sousgraphes[1].reinitCachePartiel(variable);
				}
			}
		}
	}
	
	/**
	 * Calcule la probabilité de l'instance.
	 * @param instance
	 * @return
	 */
	public HashMap<String, Double> computeToutesProba(Instanciation instance, String variable, ArrayList<String> valeurs)
	{
		HashMap<String, Double> proba = new HashMap<String, Double>();
		if(valeurs.size() == 1) // ne devrait pas arriver
		{
			proba.put(valeurs.get(0), 1.);
			return proba;
		}
		int seuilSave = seuil;
		seuil = (valeurs.size()-1) * seuil;
		// Cas un peu particulier car on sait qu'on veut la distribution complète de varAReco
		boolean compte = avecHisto && historique.getNbInstances(instance) > seuil;
		for(String s : valeurs)
		{
			instance.conditionne(variable, s);
//			reinitCache();
			proba.put(s, computeProba(instance, compte));
			reinitCachePartiel(variable);
			instance.deconditionne(variable);
		}
		seuil = seuilSave;
		return proba;
	}
	
	private double computeProba(Instanciation instance, boolean compte)
	{
		Instanciation subinstance = instance.subInstanciation(varsIndice);
		
		int indiceCache = -1;
		if(utiliseCache)
			indiceCache = subinstance.getIndexCache(contextIndice);

		if(utiliseCache && cache[indiceCache] >= 0)
			return cache[indiceCache];
		
		// Si on a assez d'exemples, pas besoin de redécouper
		// Si on ne peut plus découper… on peut plus découper
		if(compte || (avecHisto && subinstance.getNbVarInstanciees() <= 2) || graphe.size() == 1)
		{
			double nbInstance, nbToutConnuMoinsGraphe;
			
			// Cas particulier des CPT déjà calculées
			if(graphe.size() == 1)
			{
				nbInstance = historique.getNbInstancesCPT(subinstance, graphe.get(0));
				subinstance.deconditionne(graphe);
				nbToutConnuMoinsGraphe = historique.getNbInstancesCPT(subinstance, graphe.get(0));
			}
			else
			{
				nbInstance = historique.getNbInstances(subinstance);
				subinstance.deconditionne(graphe);
				nbToutConnuMoinsGraphe = historique.getNbInstances(subinstance);
			}

			double p;
			if(nbToutConnuMoinsGraphe == 0) // si on tombe sur un cas impossible
				p = 0.;
			else
				p = nbInstance/nbToutConnuMoinsGraphe;

			if(utiliseCache)
				cache[indiceCache] = p;
			return p;
		}
		
		if(sousgraphes == null)
			construitSousGraphes();

//		ArrayList<String> cutsetvarlibre = new ArrayList<String>();
//		cutsetvarlibre.addAll(cutset);
//		cutsetvarlibre.removeAll(historique.getVarConnues(subinstance));

		double p = 0;
		
		boolean compteFils[] = new boolean[sousgraphes.length];
		if(avecHisto)
			for(int i = 0; i < sousgraphes.length; i++)
			{
				Instanciation subsub = subinstance.subInstanciation(sousgraphes[i].vars);
				compteFils[i] = historique.getNbInstances(subsub) > seuil;
			}

		IteratorInstances iter = historique.getIterator(subinstance, cutsetIndice);
//		IteratorInstances iter = historique.getIterator(subinstance, cutsetvarlibre);
		while(iter.hasNext())
		{
			Instanciation c = iter.next();
			if(contraintes != null && !c.isPossible(contraintes))
				continue;
			double prod = 1;
			for(int i = 0; i < sousgraphes.length; i++)
				prod *= sousgraphes[i].computeProba(c, avecHisto && compteFils[i]);
			p += prod;
		}
		if(utiliseCache)
			cache[indiceCache] = p;
		
		return p;
	}
	
	private void construitSousGraphes()
	{
		cutset = dtreegenerator.separateHyperGraphe(graphe);
		
		ArrayList<ArrayList<String>> cluster = new ArrayList<ArrayList<String>>();
		
		ArrayList<String> vars_sauv = new ArrayList<String>();
		vars_sauv.addAll(vars);
		
		cluster.add(dtreegenerator.getSousGraphe(0));
		cluster.add(dtreegenerator.getSousGraphe(1));
		// Il est possible qu'il y ait des doublons…
		
		cutset.removeAll(acutset);
		
		HashMap<String, Integer> mapVar = historique.getMapVar();

		cutsetIndice = new int[cutset.size()];
		for(int i = 0; i < cutsetIndice.length; i++)
			cutsetIndice[i] = mapVar.get(cutset.get(i));

		sousgraphes = new Graphe[cluster.size()];

		for(int i = 0; i < sousgraphes.length; i++)
		{
			ArrayList<String> acutsetSons = new ArrayList<String>();
			acutsetSons.addAll(acutset);
			acutsetSons.addAll(cutset);

			sousgraphes[i] = new Graphe(contraintes, acutsetSons, cluster.get(i), historique, dtreegenerator);
		}
//		if(nb == 0)
//			printTree();
	}
	
	private void affichePrivate(BufferedWriter output) throws IOException
	{
		output.write(nb+" [label=\"");
		if(sousgraphes != null)
		{
			output.write("Cutset :");
			for(String s : cutset)
				output.write(" "+s);
			output.write("\n");
		}
		if(utiliseCache)
			output.write("Contexte ("+cache.length+") :");
		else
			output.write("Contexte :");
		for(String s : context)
			output.write(" "+s);
		output.write("\n");
		output.write("V :");
		for(String s : vars)
			output.write(" "+s);
		output.write("\nG :");
		for(String s : graphe)
			output.write(" "+s);
		output.write(" ("+nb+")\"];");
		output.newLine();
		if(sousgraphes != null)
		{
			for(int i = 0; i < sousgraphes.length; i++)
			{				
				sousgraphes[i].affichePrivate(output);
				output.write(nb+" -> "+sousgraphes[i].nb+";");
				output.newLine();
			}
		}
	}
	
	public void printTree()
	{
		FileWriter fichier;
		BufferedWriter output;
		try {
			fichier = new FileWriter("affichageTree.dot");
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
	/*
	public void printGraphe()
	{
		dsep.printSousGraphes(graphe, nb);
	}
*/
	public void construct()
	{
		if(graphe.size() != 1)
		{
			construitSousGraphes();
			sousgraphes[0].construct();
			sousgraphes[1].construct();
		}

	}

}
