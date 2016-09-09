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
import compilateurHistorique.MultiHistoComp;
import compilateurHistorique.InstanceMemoryManager;
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
	// Structure qui sauvegarde un graphe
	private static class Sauvegarde implements Serializable
	{
		private static final long serialVersionUID = 1L;
		public boolean lecture = false;
		public HashMap<Integer,ArrayList<String>> cutsets = new HashMap<Integer,ArrayList<String>>();
		public HashMap<Integer,ArrayList<String>> varsSousGraphe = new HashMap<Integer,ArrayList<String>>();
	}
	
	private static Sauvegarde sauv = new Sauvegarde();
	private static final long serialVersionUID = 1L;
	private ArrayList<String> cutset;
	private int[] cutsetIndice;
	private ArrayList<String> acutset;
	private ArrayList<String> context;
	private int[] contextIndice;
	private ArrayList<String> vars; // liste les variables du graphe et leurs parents
	private int[] varsIndice;
	private ArrayList<String> graphe;
	private int[] grapheIndice;
	private int[] varsMoinsGrapheIndice;
	private Graphe[] sousgraphes;
	private transient DTreeGenerator dtreegenerator;
//	private DSeparation dsep;
	private MultiHistoComp historique;
	private MultiHistoComp historique2;
	private HashMap<Integer, Double> cache;
//	private int[][] cacheNbInstances;
	private static int seuil;
	public static int nbS = 0;
	private int nb;
	private transient SALADD contraintes;
	private int tailleCache;
	private static HashMap<String, Integer> mapVar;
	private static boolean avecHisto;
//	private boolean utiliseHisto;
	private static double cacheFactor;
	private	final boolean utiliseCache;
//	private final boolean[] utiliseCacheInstances = new boolean[2];
	private Instanciation lastInstance;
	private ArrayList<String> filename, filenameInit;
	private boolean entete;
//	private int profondeurSiFeuille;
	private int profondeurDtree;
	private boolean compteFils[] = new boolean[2];
	private IteratorInstances iter;
	private HashMap<String, Double> proba = new HashMap<String, Double>();

	public static void config(int seuilP, boolean avecHistoP, double cacheFactorP)
	{
		seuil = seuilP;
		avecHisto = avecHistoP;
		cacheFactor = cacheFactorP;
	}
	
	public Graphe(SALADD contraintes, ArrayList<String> acutset, ArrayList<String> graphe, DTreeGenerator dtreegenerator, ArrayList<String> filename, ArrayList<String> filenameInit, boolean entete, int profondeurDtree)
	{
		this.profondeurDtree = profondeurDtree;
		this.filenameInit = filenameInit;
		this.filename = filename;
		this.entete = entete;
		this.contraintes = contraintes;
		nb = nbS;
		nbS++;
		
//		System.out.println(nb+"Création d'un graphe.");
//		this.dsep = dsep;

		vars = new ArrayList<String>();
		for(String s : graphe)
		{
			if(!vars.contains(s))
				vars.add(s);
			for(String p : dtreegenerator.getParents(s))
				if(!vars.contains(p))
					vars.add(p);
		}
		
		// context = acutset (intersection) vars
		context = new ArrayList<String>();
		
		for(String s : acutset)
			if(vars.contains(s))
				context.add(s);
		

		ArrayList<String> varsMoinsGraphe = new ArrayList<String>();
		varsMoinsGraphe.addAll(vars);
		varsMoinsGraphe.removeAll(graphe);
		
//		utiliseHisto = vars.size() < 50;
		if((avecHisto) || nb == 0)
		{
			historique = new MultiHistoComp(filenameInit, entete, vars);
			historique.compile(filename, entete, context);

			if(vars.size() != graphe.size())
			{
				historique2 = new MultiHistoComp(filenameInit, entete, varsMoinsGraphe);
				historique2.compile(filename, entete, context);
			}
//			if(historique.getNbNoeuds() < 200)
//				historique.printADD(nb);
		}

		this.acutset = acutset;

		this.graphe = graphe;
		
		if(mapVar == null)
			mapVar = MultiHistoComp.getMapVar();

		varsIndice = new int[vars.size()];
		for(int i = 0; i < varsIndice.length; i++)
			varsIndice[i] = mapVar.get(vars.get(i));
		
		varsMoinsGrapheIndice = new int[varsMoinsGraphe.size()];
		for(int i = 0; i < varsMoinsGrapheIndice.length; i++)
			varsMoinsGrapheIndice[i] = mapVar.get(varsMoinsGraphe.get(i));
		
		this.dtreegenerator = dtreegenerator;
				
		contextIndice = new int[context.size()];
		for(int i = 0; i < contextIndice.length; i++)
			contextIndice[i] = mapVar.get(context.get(i));

		grapheIndice = new int[graphe.size()];
		for(int i = 0; i < grapheIndice.length; i++)
			grapheIndice[i] = mapVar.get(graphe.get(i));

		tailleCache = Instanciation.getTailleCache(context, cacheFactor);
		if(tailleCache <= 0 || tailleCache > 5000000)
			tailleCache = 5000000;
		utiliseCache = true;
//		if(tailleCache == -1 || tailleCache > cacheFactor) // overflow de la taille
//			tailleCache = cacheFactor;
		
		System.out.println("Taille du cache : "+tailleCache);

		System.out.print("Création graphe "+nb);

		if(utiliseCache)
		{
			System.out.println(" avec cache");
			cache = new HashMap<Integer, Double>();
//			cache = new double[tailleCache];
			
//			for(int i = 0; i < tailleCache; i++)
//				cache[i] = -1;
		}
		else
			System.out.println(" sans cache");
		lastInstance = new Instanciation();
//		printGraphe();
/*
		System.out.print(nb+" Vars : ");
		for(String s : vars)
			System.out.print(s+" ");
		System.out.println();*/
//		if(graphe.size() == 1)
//			profondeurSiFeuille = mapVar.get(graphe.get(0));
	}
	
	public void save(String namefile)
	{
		System.out.println("Sauvegarde du dtree");
		ObjectOutputStream oos;
		try {
			oos = new ObjectOutputStream(new FileOutputStream(new File(namefile)));
			oos.writeObject(sauv);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static boolean load(String namefile)
	{
		ObjectInputStream ois;
		try {
			System.out.println("Chargement du dtree");
			ois = new ObjectInputStream(new FileInputStream(new File(namefile)));
			sauv = (Sauvegarde)ois.readObject();
			sauv.lecture = true;
			ois.close();
			return true;
		} catch (Exception e) {
			System.out.println("Lecture du dtree impossible");
			e.printStackTrace();
		}
		return false;
	}

	public void reinitCache()
	{
		if(utiliseCache)
			cache.clear();
//			for(int i = 0; i < tailleCache; i++)
//				cache[i] = -1;
		if(sousgraphes != null)
		{
			sousgraphes[0].reinitCache();
			sousgraphes[1].reinitCache();
		}
		lastInstance.deconditionneTout();
	}
	
	private void reinitCachePartiel(String variable)
	{
		if(vars.contains(variable) && !context.contains(variable))
		{
			if(utiliseCache)
				cache.clear();
//				for(int i = 0; i < tailleCache; i++)
//					cache[i] = -1;
			if(sousgraphes != null)
			{
				sousgraphes[0].reinitCachePartiel(variable);
				sousgraphes[1].reinitCachePartiel(variable);
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
		proba.clear();
		if(valeurs.size() == 1) // ne devrait pas arriver
		{
			proba.put(valeurs.get(0), 1.);
			return proba;
		}
		int seuilSave = seuil;
//		seuil = (valeurs.size()-1) * seuil;
		// Cas un peu particulier car on sait qu'on veut la distribution complète de varAReco
		boolean compte = avecHisto && historique.getNbInstances(instance) > seuil;

//		System.out.println(valeurs.size()+" valeurs");
		
		for(String s : valeurs)
		{
			instance.conditionne(variable, s);
//			reinitCache();
			proba.put(s, computeProbaUpdateCache(instance, compte));
//			reinitCachePartiel(variable);
			instance.deconditionne(variable);
		}
		seuil = seuilSave;
		return proba;
	}

	private double computeProbaUpdateCache(Instanciation instance, boolean compte)
	{
		if(utiliseCache)
		{
			ArrayList<String> diff = instance.getVarDiff(lastInstance);
			for(String s : diff)
				reinitCachePartiel(s);
			lastInstance = instance.clone();
		}
		if(compte)
			return lookUpHistory(instance);
		else
		{
			double out = computeProba(instance);
			InstanceMemoryManager.getMemoryManager().clearAll();
			return out;
		}
	}
	
	private double lookUpHistory(Instanciation instance)
	{
//		System.out.println("lookUpHistory de "+profondeurDtree);
		int indiceCache = -1;
		if(utiliseCache)
		{
			indiceCache = instance.getIndexCache(contextIndice);
			Double p = cache.get(indiceCache);
			if(p == null && cache.size() > tailleCache) // plus de place : on ne sauvegardera pas
				indiceCache = -1;
			else if(p != null) // p déjà calculé
				return p;
		}
		
//		subinstance.deconditionne(grapheIndice);
		instance.saveNbVarInstanciees();
/*		double nbToutConnuMoinsGraphe;
		if(varsIndice.length == grapheIndice.length)
			nbToutConnuMoinsGraphe = historique.getNbInstancesTotal();
		else
		{
			instance.updateNbVarInstanciees(varsMoinsGrapheIndice);
//			instance.updateNbVarInstancieesRetire(grapheIndice);
			nbToutConnuMoinsGraphe = historique2.getNbInstances(instance);
			instance.loadNbVarInstanciees();
		}

		double p;
		if(nbToutConnuMoinsGraphe == 0) // si on tombe sur un cas impossible
			p = 0.;
		else*/
//		{
			instance.updateNbVarInstanciees(varsIndice);
			double nbInstance = historique.getNbInstances(instance);
			double p = nbInstance;// / nbToutConnuMoinsGraphe;
			instance.loadNbVarInstanciees();
//		}

		if(utiliseCache && indiceCache >= 0)
			cache.put(indiceCache, p);
		
//		InstanceMemoryManager.getMemoryManager().clearFrom(subinstance);
		return p;
	}
	
	private double computeProba(Instanciation instance)
	{
//		System.out.println("Appel à "+profondeurDtree);

		int indiceCache = -1;
		if(utiliseCache)
		{
			indiceCache = instance.getIndexCache(contextIndice);
			Double p = cache.get(indiceCache);
			if(p == null && cache.size() > tailleCache) // plus de place : on ne sauvegardera pas
				indiceCache = -1;
			else if(p != null) // p déjà calculé
				return p;
		}

		Instanciation subinstance = instance.subInstanciation(varsIndice);

		double p = 0;
		
		if(avecHisto)
			for(int i = 0; i < sousgraphes.length; i++)
			{
				subinstance.saveNbVarInstanciees();
//				if(sousgraphes[i].historique.getNbInstancesTotal() < seuil)
//					compteFils[i] = false;
//				else
//				{
					subinstance.updateNbVarInstanciees(sousgraphes[i].varsIndice);
					compteFils[i] = sousgraphes[i].historique.getNbInstances(subinstance) > seuil;
					subinstance.loadNbVarInstanciees();
//				}
			}

		// Iteration sur toutes les valeurs non affectées du cutset
		iter.init(subinstance, cutsetIndice);

		// Ne plus utiliser subinstance ; il a été modifié par l'iterator
		while(iter.hasNext())
		{
			Instanciation c = iter.next();
//			if(contraintes != null && !c.isPossible(contraintes))
//				continue;
			double prod = 1;
			for(int i = 0; i < sousgraphes.length; i++)
			{
				if(avecHisto && compteFils[i])
					prod *= sousgraphes[i].lookUpHistory(c);
				else
					prod *= sousgraphes[i].computeProba(c);
				if(prod == 0)
					break;
			}
			p += prod;
		}
		
		if(utiliseCache && indiceCache >= 0)
			cache.put(indiceCache, p);
		
		// On libère la mémoire des instances du sous-graphe
		InstanceMemoryManager.getMemoryManager().clearFrom(subinstance);

		return p;
	}
	
	private void construitSousGraphes()
	{
		if(sauv.lecture)
			cutset = sauv.cutsets.get(nb);
		else
		{
			cutset = dtreegenerator.separateHyperGraphe(graphe);
			sauv.cutsets.put(nb, cutset);
		}
		
		ArrayList<ArrayList<String>> cluster = new ArrayList<ArrayList<String>>();
		
		ArrayList<String> vars_sauv = new ArrayList<String>();
		vars_sauv.addAll(vars);
		
		if(sauv.lecture)
		{
			cluster.add(sauv.varsSousGraphe.get(2 * nb));
			cluster.add(sauv.varsSousGraphe.get(2 * nb + 1));			
		}
		else
		{
			cluster.add(dtreegenerator.getSousGraphe(0));
			cluster.add(dtreegenerator.getSousGraphe(1));
			sauv.varsSousGraphe.put(2 * nb, dtreegenerator.getSousGraphe(0));
			sauv.varsSousGraphe.put(2 * nb + 1, dtreegenerator.getSousGraphe(1));
		}
		
		// Il est possible qu'il y ait des doublons…
		cutset.removeAll(acutset);
		
//		HashMap<String, Integer> mapVar = historique.getMapVar();

		cutsetIndice = new int[cutset.size()];
		for(int i = 0; i < cutsetIndice.length; i++)
			cutsetIndice[i] = mapVar.get(cutset.get(i));

		iter = new IteratorInstances(cutsetIndice.length);

		sousgraphes = new Graphe[cluster.size()];

		for(int i = 0; i < sousgraphes.length; i++)
		{
			ArrayList<String> acutsetSons = new ArrayList<String>();
			acutsetSons.addAll(acutset);
			acutsetSons.addAll(cutset);

			sousgraphes[i] = new Graphe(contraintes, acutsetSons, cluster.get(i), dtreegenerator, filename, filenameInit, entete, profondeurDtree+1);
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
			output.write("Contexte :");// ("+cache.length+") :");
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
			/*
			cacheNbInstances = new int[2][];
			for(int i = 0; i < 2; i++)
			{
				int tailleCacheInstances = Instanciation.getTailleCPT(sousgraphes[i].vars);
				utiliseCacheInstances[i] = tailleCacheInstances >= 0 && tailleCacheInstances < 10000;
				if(utiliseCacheInstances[i])
				{
					cacheNbInstances[i] = new int[tailleCacheInstances];
					for(int j = 0; j < tailleCacheInstances; j++)
						cacheNbInstances[i][j] = -1;
				}
			}*/
		}

	}

	public MultiHistoComp getHistorique()
	{
		return historique;
	}

}
