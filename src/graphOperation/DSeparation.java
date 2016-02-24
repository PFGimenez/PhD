package graphOperation;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;

import compilateur.LecteurXML;

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
 * S'occupe des calculs de d-séparation. Utilise l'algorithme Bayes-Ball
 * @author pgimenez
 *
 */

public class DSeparation implements Serializable
{
	private static final long serialVersionUID = 1L;
	private static final int parents = 0;
	private static final int enfants = 1;

	// Utilisé dans la liste
	private class CoupleVarOrigine
	{
		public String var;
		public int origine;
		public CoupleVarOrigine(String var, int origine)
		{
			this.var = var;
			this.origine = origine;
		}
		
	}

	private ArrayList<String> top = new ArrayList<String>();
	private ArrayList<String> bottom = new ArrayList<String>();
	private ArrayList<String> visited = new ArrayList<String>();
	
	private HashMap<String, ArrayList<String>>[] reseau;
	private Queue<CoupleVarOrigine> liste = new LinkedList<CoupleVarOrigine>();
	
	public DSeparation(String prefixData, int nbIter)
	{
		LecteurXML xml = new LecteurXML();
		reseau = xml.lectureReseauBayesien(prefixData+"BN_"+nbIter+".xml");
		ArrayList<String> nodes = new ArrayList<String>();
		nodes.addAll(reseau[0].keySet());
		printSousGraphes(nodes, 100);
	}

	public ArrayList<String> getRequisiteObservation(ArrayList<String> connues, String probaACalculer)
	{
		ArrayList<String> tmp = new ArrayList<String>();
		tmp.add(probaACalculer);
		return getRequisiteObservation(connues, tmp);
	}

	/**
	 * Retourne les nœuds nécessaires au calcul de "probaACalculer" sachant "connues"
	 * @param connues
	 * @param probaACalculer
	 * @return
	 */
	public ArrayList<String> getRequisiteObservation(ArrayList<String> connues, ArrayList<String> probaACalculer)
	{
//		System.out.print("Connues : ");
//		for(String s : connues)
//			System.out.print(s+" ");
//		System.out.println();
		
		ArrayList<String> noeudsDeterministes = new ArrayList<String>();
		top.clear();
		bottom.clear();
		visited.clear();
		liste.clear();
		for(String v : probaACalculer)
			liste.add(new CoupleVarOrigine(v, enfants));
		
		while(!liste.isEmpty())
		{
			CoupleVarOrigine node = liste.poll();
			
			if(!visited.contains(node.var))
				visited.add(node.var);
//			System.out.println("Node : "+node.var);

			boolean estConnu = connues.contains(node.var);
			if(!estConnu && node.origine == enfants)
			{
				if(!top.contains(node.var))
				{
//					System.out.println("Top1");
					top.add(node.var);
					ArrayList<String> noeudsParents = reseau[parents].get(node.var);
					for(String parent : noeudsParents)
						liste.add(new CoupleVarOrigine(parent, enfants));
				}
				if(!noeudsDeterministes.contains(node.var) && !bottom.contains(node.var))
				{
//					System.out.println("Bottom1");
					bottom.add(node.var);
					ArrayList<String> noeudsEnfants = reseau[enfants].get(node.var);
					for(String enfant : noeudsEnfants)
						liste.add(new CoupleVarOrigine(enfant, parents));
				}
			}
			else if(node.origine == parents)
			{
				if(estConnu && !top.contains(node.var))
				{
				//	System.out.println("Top2");
					top.add(node.var);
					ArrayList<String> noeudsParents = reseau[parents].get(node.var);
					for(String parent : noeudsParents)
						liste.add(new CoupleVarOrigine(parent, enfants));
				}
				else if(!estConnu && !bottom.contains(node.var))
				{
				//	System.out.println("Bottom2");
					bottom.add(node.var);
					ArrayList<String> noeudsEnfants = reseau[enfants].get(node.var);
					for(String enfant : noeudsEnfants)
						liste.add(new CoupleVarOrigine(enfant, parents));	
				}
			}
		}
		
		ArrayList<String> out = new ArrayList<String>();
		for(String s : visited)
			out.add(s);
		return out;
	}
	

	public void printSousGraphes(ArrayList<String> nodes, int nbNom)
	{
		try {
	
			FileWriter fichier;
			BufferedWriter output;
	
			fichier = new FileWriter("affichage-"+nbNom+".dot");
			output = new BufferedWriter(fichier);
			output.write("digraph G { ");
			output.newLine();
			output.write("ordering=out;");			
			output.newLine();
			for(int i = 0; i < nodes.size(); i++)
			{
				output.write(i+" [label="+nodes.get(i)+"];");
				output.newLine();			
			}
			for(int i = 0; i < nodes.size(); i++)
			{
				for(int j = 0; j < reseau[enfants].get(nodes.get(i)).size(); j++)
				{
					int nb = nodes.indexOf(reseau[enfants].get(nodes.get(i)).get(j));
					if(nb == -1)
						continue;
					output.write(i+" -> "+nb);
					output.newLine();
				}
			}
			output.write("}");
			output.newLine();
			output.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
}
