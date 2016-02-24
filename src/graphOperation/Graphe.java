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
	private ArrayList<String> acutset;
	private ArrayList<String> context;
	private ArrayList<String> vars; // liste les variables du graphe et leurs parents
	private ArrayList<String> graphe;
	private Graphe[] sousgraphes;
	private transient DTreeGenerator dtreegenerator;
	private DSeparation dsep;
	private HistoComp historique;
	private double[] cache;
	private static int seuil;
	public static int nbS = 0;
	private int nb;
	private transient SALADD contraintes;
	private int tailleCache;
	
	public static void config(int seuilP)
	{
		seuil = seuilP;
	}
	
	public Graphe(SALADD contraintes, ArrayList<String> acutset, ArrayList<String> graphe, HistoComp historique, DTreeGenerator dtreegenerator, DSeparation dsep)
	{
		this.contraintes = contraintes;
		nb = nbS;
		nbS++;
//		System.out.println(nb+"Création d'un graphe.");
		this.dsep = dsep;
		
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
		
		tailleCache = Instanciation.getTailleCache(context);

//		if(tailleCache == -1 || tailleCache > cacheFactor) // overflow de la taille
//			tailleCache = cacheFactor;
		
//		System.out.println("Taille du cache : "+tailleCache);

		cache = new double[tailleCache];
		
		for(int i = 0; i < tailleCache; i++)
			cache[i] = -1;
				
		printGraphe();
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
		for(int i = 0; i < tailleCache; i++)
			cache[i] = -1;
		if(sousgraphes != null)
		{
			sousgraphes[0].reinitCache();
			sousgraphes[1].reinitCache();
		}
	}
	
	/**
	 * Calcule la probabilité de l'instance.
	 * @param instance
	 * @return
	 */
	public double computeProba(Instanciation instance, String variableAReco)
	{
		/*
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		*/
		Instanciation subinstance = instance.subInstanciation(vars);
		
//		System.out.print(".");
//		System.out.println();
//		System.out.println(nb+" Appel de computeProba avec instance : "+subinstance);
		
//		System.out.println("Nb exemples : "+historique.getNbInstances(subinstance));

	/*
		System.out.print(nb+" Contexte : ");
		for(String s : context)
			System.out.print(s+" ");
		System.out.println();
		*/
/*
		System.out.print(nb+" Variables : ");
		for(String s : vars)
			System.out.print(s+" ");
		System.out.println();
*/
		// A-t-on déjà calculé cette valeur?
//		boolean utiliseCache = indice < tailleCache && indice != -1;
		Instanciation test = subinstance.clone();
		test.deconditionne(context);
		boolean utiliseCache = test.getNbVarInstanciees() == 0;
		utiliseCache = true;
		int indiceCache = -1;
		if(utiliseCache)
			indiceCache = subinstance.getIndexCache(context);
		if(utiliseCache && cache[indiceCache] >= 0)
		{
//			System.out.println(nb+" Utilisation du cache, retourne "+cache[indiceCache]+", "+indiceCache);
			return cache[indiceCache];
		}
		
		ArrayList<String> connues = historique.getVarConnues(subinstance);
		boolean ok = false;
		for(String s : graphe)
			if(connues.contains(s))
				ok = true;
		if(!ok)
		{
			if(utiliseCache)
			{
//				System.out.println("Sauvegarde cache : "+indiceCache+": "+1);
				cache[indiceCache] = 1.;
			}
			return 1.;
		}

//		double nbToutConnu = historique.getNbInstances(subinstance);
//		System.out.println("nbInstance : "+nbInstance+", "+subinstance);
		Instanciation sauv = subinstance.clone();
		sauv.deconditionne(variableAReco);
		double nbExemplesCritereArret = historique.getNbInstances(sauv);
//		System.out.println("nbExemplesCritereArret : "+nbExemplesCritereArret+", "+historique);
//		System.out.println("nbToutConnuMoinsGraphe : "+nbToutConnuMoinsGraphe+", "+historique);

//		System.out.println(nbInstance+" exemples");
		
		// Si on a assez d'exemples, pas besoin de redécouper
		// Si on ne peut plus découper… on peut plus découper
		if(nbExemplesCritereArret >= seuil || graphe.size() == 1)
		{
			sauv.deconditionne(graphe);
			double nbToutConnuMoinsGraphe = historique.getNbInstances(sauv);

			double nbInstance = historique.getNbInstances(subinstance);

//			System.out.println(nbExemplesCritereArret);
			double p;
//			if(nbToutConnuMoinsGraphe == 0)
//				p = 0.;
//			else
				p = (nbInstance+1)/(nbToutConnuMoinsGraphe+tailleCache);// / nbInstancesNormalisation;
			if(utiliseCache)
			{
//				System.out.println("Sauvegarde cache : "+indiceCache+": "+p);
				cache[indiceCache] = p;
			}
//			System.out.println(nb+" retourne "+p);
			return p;
		}
		
//		System.out.println("REC!");
		
		if(sousgraphes == null)
			construitSousGraphes();

		ArrayList<String> cutsetvarlibre = new ArrayList<String>();
		cutsetvarlibre.addAll(cutset);
		cutsetvarlibre.removeAll(historique.getVarConnues(subinstance));
/*		System.out.print(nb+" Variables libres du cutset : ");
		for(String s : cutsetvarlibre)
			System.out.print(s+" ");
		System.out.println();
	*/	
		double p = 0;
		// Comme les sous-graphes ont été construit, cutset a été calculé aussi
//		for(String s : cutsetvarlibre)
//			if(instance.getValue(s) != null)
//				System.out.println("Erreur ! Une variable du cutset est déjà instanciée : "+s+" ("+instance.getValue(s)+")");
		
		IteratorInstances iter = historique.getIterator(instance, cutsetvarlibre);
		while(iter.hasNext())
		{
			Instanciation c = iter.next();
			if(contraintes != null && !c.isPossible(contraintes))
				continue;
			double prod = 1;
			for(int i = 0; i < sousgraphes.length; i++)
				prod *= sousgraphes[i].computeProba(c,variableAReco);
			p += prod;
		}
		if(utiliseCache)
		{
//			System.out.println("Sauvegarde cache : "+indiceCache+": "+p);
			cache[indiceCache] = p;
		}
		
//		System.out.println(nb+" retourne "+p);
		return p;
	}
	
	private void construitSousGraphes()
	{
//		System.out.println("Taille graphe : "+graphe.size());
		cutset = dtreegenerator.separateHyperGraphe(graphe);
		
/*		System.out.print(nb+" Découpe par : ");
		for(String u : cutset)
			System.out.print(u+" ");
		System.out.println();
*/
		
/*		for(int i = 0; i < 2; i++)
		{
			int nb = 0;
			for(String s : historique.getVarConnues(subinstance))
				if(dtreegenerator.getSousGraphe(i).contains(s))
					nb++;
			System.out.println("Graphe "+i+" : "+nb);
		}*/
		
		ArrayList<ArrayList<String>> cluster = new ArrayList<ArrayList<String>>();
		
		ArrayList<String> vars_sauv = new ArrayList<String>();
		vars_sauv.addAll(vars);
		
		cluster.add(dtreegenerator.getSousGraphe(0));
		cluster.add(dtreegenerator.getSousGraphe(1));
		// Il est possible qu'il y ait des doublons…
		
/*		for(String s : vars_sauv)
		{
			ArrayList<String> ajout = new ArrayList<String>();
			ajout.add(s);
			for(String p : dtreegenerator.getParents(s))
				if(!cutset.contains(p))
					ajout.add(p);
			
			for(ArrayList<String> c : cluster)
			{
				ArrayList<ArrayList<String>> fusion = new ArrayList<ArrayList<String>>();
	
				for(String a : ajout)
					if(c.contains(a))
					{
						fusion.add(c);
						break;
					}
			}
			if(fusion.isEmpty())
				cluster.add(ajout);
			else
			{
				for(String a : ajout)
					if(!fusion.contains(a))
						fusion.add(a);		
			}
		}*/
		
/*		for(String s : vars_sauv)
			if(done.contains(s) || cutset.contains(s))
				continue;
			else
			{
				ArrayList<String> req = dsep.getRequisiteObservation(dsepConnues, s);
				
				cluster.add(req);
				for(String v : req)
					done.add(v);
			}*/

		cutset.removeAll(acutset);
//		cutset.removeAll(historique.getVarConnues(subinstance));

		sousgraphes = new Graphe[cluster.size()];

		for(int i = 0; i < sousgraphes.length; i++)
		{
			ArrayList<String> acutsetSons = new ArrayList<String>();
			acutsetSons.addAll(acutset);
			acutsetSons.addAll(cutset);

			sousgraphes[i] = new Graphe(contraintes, acutsetSons, cluster.get(i), historique, dtreegenerator, dsep);
		}
		if(nb == 0)
			printTree();
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
		output.write("Contexte ("+cache.length+") :");
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
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void printGraphe()
	{
		dsep.printSousGraphes(graphe, nb);
	}

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
