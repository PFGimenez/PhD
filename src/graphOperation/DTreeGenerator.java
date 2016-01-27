package graphOperation;

import java.io.BufferedReader;
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
	private ArrayList<String> cutset = new ArrayList<String>();
	@SuppressWarnings("unchecked")
	ArrayList<String>[] partition = (ArrayList<String>[]) new ArrayList[2];

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
	public ArrayList<String> getCutset()
	{
		return cutset;
	}
	
	/**
	 * Recupère un des deux sous-graphes précédemment calculés. id = 0 ou 1
	 * @param id
	 * @return
	 */
	public ArrayList<String> getSousGraphe(int id)
	{
		return partition[id];
	}
	
	/**
	 * Sépare le graphe en deux. requisiteNodes contient tous les nœuds non instanciés qui sont nécessaire au calcul des proba
	 * @param requisiteNodes
	 * @param connues
	 */
	public void separateHyperGraphe(ArrayList<String> requisiteNodes)
	{
		try {
			PrintWriter writer = new PrintWriter("/tmp/hg", "UTF-8");
			writer.println(requisiteNodes.size()+" "+requisiteNodes.size());
			System.out.println(requisiteNodes.size()+" "+requisiteNodes.size());
			for(int i = 0; i < requisiteNodes.size(); i++)
			{
				String s = requisiteNodes.get(i);
				for(String p : reseau[parents].get(s))
					if(requisiteNodes.contains(p))
					{
						writer.print((requisiteNodes.indexOf(p)+1)+" ");
						System.out.print(requisiteNodes.indexOf(p)+" ("+p+") ");
					}
				writer.println(i+1);
				System.out.println(i+" ("+s+")");
			}
			writer.close();
			Process proc = Runtime.getRuntime().exec("lib/hmetis-1.5-linux/shmetis /tmp/hg 2 10");
			BufferedReader input = new BufferedReader(new InputStreamReader(proc.getInputStream()));
			BufferedReader error = new BufferedReader(new InputStreamReader(proc.getErrorStream()));
            while ((input.readLine()) != null);
            while ((error.readLine()) != null);
            proc.waitFor();

            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream("/tmp/hg.part.2")));
            String line = br.readLine();
            int variable = 0;
            
            cutset.clear();
    		@SuppressWarnings("unchecked")
			ArrayList<String>[] var = (ArrayList<String>[]) new ArrayList[2];
			var[0] = new ArrayList<String>();
			var[1] = new ArrayList<String>();

            while(line != null)
            {
            	int part = Integer.parseInt(line);
				for(String p : reseau[parents].get(requisiteNodes.get(variable)))
					if(requisiteNodes.contains(p) && !var[part].contains(p))
						var[part].add(p);
				partition[part].add(requisiteNodes.get(variable));
				variable++;
				line = br.readLine();
            }
            for(String s : var[0])
            	if(var[1].contains(s))
            		cutset.add(s);
            br.close();
            
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
}
