package graphOperation;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;

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
 * S'occupe de calculer un dtree à partir d'un DAG.
 * Fait un appel au programme hmetis qui fait du partitionnement d'hypergraphe
 * @author pgimenez
 *
 */

public class DTreeGenerator 
{
//	@SuppressWarnings("unchecked")

	@SuppressWarnings("unchecked")
	private ArrayList<String>[] partition = (ArrayList<String>[]) new ArrayList[2];

	private HashMap<String, ArrayList<String>>[] reseau;
	private static final int parents = 0;
//	private static final int enfants = 1;

	public DTreeGenerator(String prefixData, int nbIter)
	{
		partition[0] = new ArrayList<String>();
		partition[1] = new ArrayList<String>();
		LecteurXML xml = new LecteurXML();
		reseau = xml.lectureReseauBayesien(prefixData+"BN_"+nbIter+".xml");
	}
	
	/**
	 * Récupère le cutset de la dernière partition
	 * @return
	 */
/*	public ArrayList<String> getCutset()
	{
		ArrayList<String> out = new ArrayList<String>();
		for(String s : cutset)
			out.add(s);
		return out;
	}*/
	
	/**
	 * Recupère un des deux sous-graphes précédemment calculés. id = 0 ou 1
	 * @param id
	 * @return
	 */
	public ArrayList<String> getSousGraphe(int id)
	{
		ArrayList<String> out = new ArrayList<String>();
		for(String s : partition[id])
			out.add(s);
		return out;
	}
	
	/**
	 * Sépare le graphe en deux. requisiteNodes contient tous les nœuds non instanciés qui sont nécessaire au calcul des proba
	 * @param requisiteNodes
	 * @param connues
	 */
	public ArrayList<String> separateHyperGraphe(ArrayList<String> requisiteNodes)
	{
		ArrayList<String> cutset = new ArrayList<String>();

		partition[0].clear();
		partition[1].clear();
		
		if(requisiteNodes.size() == 2)
		{
			partition[0].add(requisiteNodes.get(0));
			partition[1].add(requisiteNodes.get(1));
			if(reseau[parents].get(requisiteNodes.get(0)).contains(requisiteNodes.get(1)))
				cutset.add(requisiteNodes.get(1));
			else
				cutset.add(requisiteNodes.get(0));
    		return cutset;
	
		}
		
		try {
//			System.out.println("Taille hypergraphe : "+requisiteNodes.size());
			PrintWriter writer = new PrintWriter("/tmp/hg", "UTF-8");
			writer.println(requisiteNodes.size()+" "+requisiteNodes.size());
//			System.out.println(requisiteNodes.size()+" "+requisiteNodes.size());
			for(int i = 0; i < requisiteNodes.size(); i++)
			{
				String s = requisiteNodes.get(i);
				for(String p : reseau[parents].get(s))
					if(requisiteNodes.contains(p))
					{
						writer.print((requisiteNodes.indexOf(p)+1)+" ");
//						System.out.print(requisiteNodes.indexOf(p)+" ("+p+") ");
					}
				writer.println(i+1);
//				System.out.println(i+" ("+s+")");
			}
			writer.close();
			
			Process proc = Runtime.getRuntime().exec("lib/hmetis-1.5-linux/shmetis /tmp/hg 2 1");
			BufferedReader input = new BufferedReader(new InputStreamReader(proc.getInputStream()));
			BufferedReader error = new BufferedReader(new InputStreamReader(proc.getErrorStream()));
            while ((input.readLine()) != null);
            while ((error.readLine()) != null);
            proc.waitFor();

            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream("/tmp/hg.part.2")));
            String line = br.readLine();
            int variable = 0;
            
    		@SuppressWarnings("unchecked")
			ArrayList<String>[] var = (ArrayList<String>[]) new ArrayList[2];
			var[0] = new ArrayList<String>();
			var[1] = new ArrayList<String>();

            while(line != null)
            {
            	int part = Integer.parseInt(line);

            	if(!var[part].contains(requisiteNodes.get(variable)))
            		var[part].add(requisiteNodes.get(variable));

            	for(String p : reseau[parents].get(requisiteNodes.get(variable)))
					if(requisiteNodes.contains(p) && !var[part].contains(p))
						var[part].add(p);
				partition[part].add(requisiteNodes.get(variable));
				variable++;
				line = br.readLine();
            }
            br.close();
            (new File("/tmp/hg.part.2")).delete();
            for(String s : var[0])
            	if(var[1].contains(s))
            		cutset.add(s);
            
//			System.out.println("Taille sous-graphes : "+partition[0].size()+", "+partition[1].size());

/*    		System.out.print("var 1 : ");
    		for(String s : var[0])
    			System.out.print(s+" ");
    		System.out.println();
    		
    		System.out.print("var 2 : ");
    		for(String s : var[1])
    			System.out.print(s+" ");
    		System.out.println();    		

    		System.out.print("cutset : ");
    		for(String s : cutset)
    			System.out.print(s+" ");
    		System.out.println();    		

    		System.out.print("Graphe 1 : ");
    		for(String s : partition[0])
    			System.out.print(s+" ");
    		System.out.println();
            
    		System.out.print("Graphe 2 : ");
    		for(String s : partition[1])
    			System.out.print(s+" ");
    		System.out.println();*/
            
    		return cutset;
    		
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Ces variables sont-elles une famille du réseau bayésien?
	 * Autrement dit, y a-t-il une variable qui soit la fille de toutes les autres?
	 * Attention! La famille peut être incomplète
	 * @param vars
	 * @return
	 */
/*	public boolean isFeuille(ArrayList<String> vars)
	{
//		for(String s : vars)
//			System.out.print(s+" ");
//		System.out.println(" est une feuille?");
		for(String s : vars)
		{
			if(reseau[parents].get(s).size()+1 < vars.size())
				continue;

			boolean nope = false;
			for(String v : vars)
			{
				if(v.equals(s))
					continue;
				else if(!reseau[parents].get(s).contains(v))
				{
//					System.out.println("Nope : "+v+" n'est pas un parent de "+s);
					nope = true;
					break;
				}
			}
			if(!nope)
				return true;
		}
		return false;
	}
	*/
	public ArrayList<String> getParents(String v)
	{
		return reseau[parents].get(v);
	}
	
}
