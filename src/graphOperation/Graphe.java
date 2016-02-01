package graphOperation;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

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

public class Graphe
{
	private ArrayList<String> cutset;
	private ArrayList<String> acutset;
	private ArrayList<String> context;
	private ArrayList<String> vars; // liste les variables du graphe et leurs parents
	private ArrayList<String> graphe;
	private Graphe[] sousgraphes;
	private DTreeGenerator dtreegenerator;
	private DSeparation dsep;
	private HistoComp historique;
	private double[] cache;
	private static int seuil;
	private boolean sonsAllowed;
	private static int cacheFactor;
	private int tailleCache;
	public static int nbS = 0;
	private int nb;
	private Instanciation subinstance;
	
	public static void config(int seuilP, int cacheFactorP)
	{
		seuil = seuilP;
		cacheFactor = cacheFactorP;
	}
	
	public Graphe(boolean sonsAllowed, ArrayList<String> acutset, ArrayList<String> graphe, HistoComp historique, DTreeGenerator dtreegenerator, DSeparation dsep)
	{
		nb = nbS;
		nbS++;
		System.out.println(nb+"Création d'un graphe. Enfants autorisés : "+sonsAllowed);
		this.dsep = dsep;
		this.sonsAllowed = sonsAllowed;
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

		this.dtreegenerator = dtreegenerator;
		this.historique = historique;
		
		// context = acutset (intersection) vars
		context = new ArrayList<String>();

		for(String s : acutset)
			if(vars.contains(s))
				context.add(s);
		
		int tailleCache = Instanciation.getTailleCache(context);

		if(tailleCache == -1 || tailleCache > cacheFactor) // overflow de la taille
			tailleCache = cacheFactor;
		
//		System.out.println("Taille du cache : "+tailleCache);

		cache = new double[tailleCache];
		
		for(int i = 0; i < tailleCache; i++)
			cache[i] = -1;
				
		System.out.print(nb+" Vars : ");
		for(String s : vars)
			System.out.print(s+" ");
		System.out.println();
	}
	
	/**
	 * Calcule la probabilité de l'instance. Ne modifie pas instance
	 * @param instance
	 * @return
	 */
	public double computeProba(Instanciation instance)
	{
		
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		subinstance = instance.subInstanciation(vars);
		
//		System.out.print(".");
//		System.out.println();
		System.out.println(nb+" Appel de computeProba avec instance : "+subinstance);
		
//		System.out.println("Nb exemples : "+historique.getNbInstances(subinstance));

	
		System.out.print(nb+" Contexte : ");
		for(String s : context)
			System.out.print(s+" ");
		System.out.println();

		System.out.print(nb+" Variables : ");
		for(String s : vars)
			System.out.print(s+" ");
		System.out.println();

		// A-t-on déjà calculé cette valeur?
		int indice = subinstance.getIndexCache(context);
		boolean utiliseCache = indice < tailleCache && indice != -1;
		if(utiliseCache && cache[indice] >= 0)
		{
			System.out.println(nb+" Utilisation du cache, retourne "+cache[indice]);
			return cache[indice];
		}
		
		int nbInstance = historique.getNbInstances(subinstance);
		
//		System.out.println(nbInstance+" exemples");
		
		// Si on a assez d'exemples, pas besoin de redécouper
		// Si on ne peut plus découper… on peut plus découper
		if(!sonsAllowed || dtreegenerator.isFeuille(vars))
		{
			Instanciation sauv = historique.getCurrentState();
			historique.deconditionne(context);
			int normalisation = historique.getNbInstances();
			historique.loadSavedState(sauv);
			
//			System.out.println("nbInstance = "+nbInstance+", normalisation = "+normalisation);
//			System.out.println("Fin de récursivité");
			double p = ((double)nbInstance)/normalisation;// / nbInstancesNormalisation;
			if(utiliseCache)
				cache[indice] = p;
			System.out.println(nb+" retourne "+p);
			return p;
		}
		
		if(sousgraphes == null)
			construitSousGraphes(nbInstance < seuil);

		System.out.print(nb+" Cutset : ");
		for(String s : cutset)
			System.out.print(s+" ");
		System.out.println();
		
		double p = 0;
		// Comme les sous-graphes ont été construit, cutset a été calculé aussi
		for(String s : cutset)
			if(instance.getValue(s) != null)
				System.out.println("Erreur ! Une variable du cutset est déjà instanciée : "+s+" ("+instance.getValue(s)+")");
		
		IteratorInstances iter = historique.getIterator(instance, cutset);
		while(iter.hasNext())
		{
			Instanciation c = iter.next();
			double prod = 1;
			for(int i = 0; i < sousgraphes.length; i++)
				prod *= sousgraphes[i].computeProba(c);
			p += prod;
		}
		if(utiliseCache)
			cache[indice] = p;
		
		System.out.println(nb+" retourne "+p);
		return p;
	}
	
	private void construitSousGraphes(boolean sonsAllowed)
	{
		if(sousgraphes != null)
			System.out.println("Erreur : sous-graphes déjà construits");
		
		cutset = dtreegenerator.separateHyperGraphe(graphe);
		
		System.out.print(nb+" Découpe par : ");
		for(String u : cutset)
			System.out.print(u+" ");
		System.out.println();

		
/*		for(int i = 0; i < 2; i++)
		{
			int nb = 0;
			for(String s : historique.getVarConnues(subinstance))
				if(dtreegenerator.getSousGraphe(i).contains(s))
					nb++;
			System.out.println("Graphe "+i+" : "+nb);
		}*/
		
		ArrayList<String> done = new ArrayList<String>();
		ArrayList<ArrayList<String>> cluster = new ArrayList<ArrayList<String>>();
		
		ArrayList<String> vars_sauv = new ArrayList<String>();
		vars_sauv.addAll(vars);
		
		ArrayList<String> dsepConnues = new ArrayList<String>();
		dsepConnues.addAll(cutset);
		dsepConnues.addAll(historique.getVarConnues(subinstance));
		
		for(String s : vars_sauv)
			if(done.contains(s))
				continue;
			else
			{
				ArrayList<String> req = dsep.getRequisiteObservation(dsepConnues, s);
				
/*				System.out.print(nb+" Sous-graphe : ");
				for(String u : req)
					System.out.print(u+" ");
				System.out.println();*/

				cluster.add(req);
				for(String v : req)
					done.add(v);
			}

		cutset.removeAll(acutset);
		cutset.removeAll(historique.getVarConnues(subinstance));

		sousgraphes = new Graphe[cluster.size()];

		for(int i = 0; i < sousgraphes.length; i++)
		{
			ArrayList<String> acutsetSons = new ArrayList<String>();
			acutsetSons.addAll(acutset);
			acutsetSons.addAll(cutset);

			sousgraphes[i] = new Graphe(sonsAllowed, acutsetSons, cluster.get(i), historique, dtreegenerator, dsep);
		}
		if(nb == 0)
			print();
	}
	
	private void affichePrivate(BufferedWriter output) throws IOException
	{
		output.write(nb+" [label=\"");
		if(sousgraphes != null)
			for(String s : cutset)
				output.write(s+" ");
		else
			for(String s : vars)
				output.write(s+" ");
			
		output.write("("+nb+")\"];");
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
	
	public void print()
	{
		FileWriter fichier;
		BufferedWriter output;
		try {
			fichier = new FileWriter("affichage.dot");
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
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
