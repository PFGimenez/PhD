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

package compilateurHistorique;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

/**
 * Un ensemble d'historique réparti en cluster
 * @author Pierre-François Gimenez
 *
 */

public class Clusters implements Serializable
{
	private static final long serialVersionUID = -6131188925308709820L;
	private int k;
	private transient Instanciation[] instanciations;
	private Instanciation[] centres;
	private int nbVars;
	private DatasetInfo dataset;
	private List<Instanciation>[] clusters;
	private transient List<Instanciation>[] clustersTmp;
	
	@SuppressWarnings("unchecked")
	public Clusters(int k, DatasetInfo dataset, ArrayList<String> filename, boolean entete, boolean verbose)
	{
		this.k = k;
		Random r = new Random();
//		HistoriqueCompile[] historiques = new HistoriqueCompile[k];
		this.dataset = dataset;
		instanciations = HistoriqueCompile.readInstances(dataset, filename, entete);
		nbVars = instanciations[0].values.length;
		int nbInstances = instanciations.length;
		centres = new Instanciation[k];
		clusters = (List<Instanciation>[]) new List[k];
		clustersTmp = (List<Instanciation>[]) new List[k];

		for(int i = 0; i < k; i++)
		{
			clusters[i] = new ArrayList<Instanciation>();
			clustersTmp[i] = new ArrayList<Instanciation>();
		}
		
		Instanciation[] bestCentres = new Instanciation[k];
		double tiniestClustersDistance = Double.MAX_VALUE;
		
		// On tente plusieurs fois car cet algo ne trouve qu'un minimum local
		for(int j = 0; j < 500; j++)
		{			
			if(k == 1) // un seul cluster = trivial
				j = 1000;
			for(int i = 0; i < k; i++)
				centres[i] = instanciations[r.nextInt(nbInstances)];
	
			boolean change;
			
			// Calcul de k-means
			do {
				change = updateClusters();
				if(change)
					updateCentres();
			} while(change);
			
			// Si le cluster est trop petit, on l'ignore
//			if(isThereSmallCluster(nbInstances/(3*k)))
//				j--;

//			System.out.println(j);
//			for(int i = 0; i < k; i++)
//				System.out.println("Cluster "+i+" : "+clusters[i].size());

			if(!isThereEmptyCluster())
			{
				double sum = sumDistance();
				if(sum < tiniestClustersDistance)
				{
					tiniestClustersDistance = sum;
					for(int i = 0; i < k; i++)
						bestCentres[i] = centres[i].clone();
				}
			}
		}
		
		if(bestCentres[0] != null)
		{
			// On utilise les meilleurs centres trouvés
			for(int i = 0; i < k; i++)
				centres[i] = bestCentres[i];
			updateClusters();
		}
		
//		if(verbose)
		{
			for(int i = 0; i < k; i++)
				System.out.println("Cluster "+i+" : "+clusters[i].size());
		}

		/*
		 * Création des historiques pour chaque cluster
		 */
/*		for(int i = 0; i < k; i++)
		{
			historiques[i] = new HistoriqueCompile(dataset);
			Instanciation[] part = new Instanciation[clusters[i].size()];
			for(int j = 0; j < clusters[i].size(); j++)
				part[j] = clusters[i].get(j);
			historiques[i].compile(part);
		}*/
		System.out.println("Clusters appris");

	}
	
	private boolean isThereEmptyCluster()
	{
		for(List<Instanciation> l : clusters)
			if(l.isEmpty())
				return true;
		return false;
	}
	/*
	private boolean isThereSmallCluster(int seuil)
	{
		for(List<Instanciation> l : clusters)
			if(l.size() < seuil)
			{
//				System.out.println("Taille : "+l.size()+" < "+seuil);
				return true;
			}
		return false;
	}
	*/
	/**
	 * Met à jour les clusters.
	 * Renvoie "vrai" si les clusters ont effectivement changé
	 * @return
	 */
	private boolean updateClusters()
	{
		boolean change = false;
		for(int i = 0; i < k; i++)
			clustersTmp[i].clear();
		for(Instanciation e : instanciations)
		{
			int k = getNearestCluster(e);
			if(!change && !clusters[k].contains(e)) // si e a changé de cluster
				change = true;
			clustersTmp[k].add(e);
		}
		for(int i = 0; i < k; i++)
		{
			clusters[i].clear();
			clusters[i].addAll(clustersTmp[i]);
		}
		return change;
	}
	
	/**
	 * Renvoie le cluster qui correspond à l'instanciation
	 * @param e
	 * @return
	 */
	public int getNearestCluster(Instanciation e)
	{
		int min = centres[0].distance(e);
		int argmin = 0;
		for(int i = 1; i < k; i++)
		{
			int minTmp = centres[i].distance(e);
			if(minTmp < min)
			{
				min = minTmp;
				argmin = i;
			}
		}
		return argmin;
	}

	private List<Integer> argmin = new ArrayList<Integer>();
	
	/**
	 * Renvoie le cluster qui correspond à l'instanciation.
	 * En cas d'égalité, renvoie un cluster au hasard.
	 * Attention ! Cet aléatoire doit être déterministe par rapport à e,
	 * sinon les clusters ne convergent plus nécessairement…
	 * @param e
	 * @return
	 */
	public int getNearestClusterRandom(Instanciation e)
	{
		int min = centres[0].distance(e);
		argmin.clear();
		argmin.add(0);
		for(int i = 1; i < k; i++)
		{
			int minTmp = centres[i].distance(e);
			if(minTmp < min)
				argmin.clear();
			
			if(minTmp <= min)
			{
				min = minTmp;
				argmin.add(i);
			}
		}
		return argmin.get(e.hashCode() % argmin.size());
	}
	
	/**
	 * Recalcule les centres des clusters
	 */
	private void updateCentres()
	{
		HashMap<Integer, HashMap<Integer, Integer>> nb = new HashMap<Integer, HashMap<Integer, Integer>>();
		for(int v = 0; v < nbVars; v++)
			nb.put(v, new HashMap<Integer, Integer>());
		
		for(int i = 0; i < k; i++)
		{
			for(Instanciation e : clusters[i])
			{
				for(int v = 0; v < nbVars; v++)
				{
					HashMap<Integer, Integer> occ = nb.get(v);
					Integer curr = occ.get(e.values[v]);
					if(curr == null)
						curr = 0;
					curr += 1;
					occ.put(e.values[v], curr);
				}
			}
			for(int v = 0; v < nbVars; v++)
			{
				Integer bestValue = null;
				Integer nbBest = Integer.MIN_VALUE;
				HashMap<Integer, Integer> occ = nb.get(v);
				for(Integer value : occ.keySet())
				{
					if(occ.get(value) > nbBest)
					{
						nbBest = occ.get(value);
						bestValue = value;
					}
				}
				centres[i].values[v] = bestValue;

			}
		}
	}
	
	private double sumDistance()
	{
		double out = 0;
		for(int i = 0; i < k; i++)
		{
			for(Instanciation e : clusters[i])
				out += centres[i].distance(e);
		}
		return out;
	}

	public Instanciation[] getCluster(int i)
	{
		Instanciation[] out = new Instanciation[clusters[i].size()];
		for(int j = 0; j < out.length; j++)
			out[j] = clusters[i].get(j);
		return out;
	}


	public void save(String namefile)
	{
		ObjectOutputStream oos;
		try {
			oos = new ObjectOutputStream(new FileOutputStream(new File(namefile)));
			oos.writeObject(this);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static Clusters load(String namefile, DatasetInfo dataset)
	{
		ObjectInputStream ois;
		try {
			ois = new ObjectInputStream(new FileInputStream(new File(namefile)));
			Clusters c = (Clusters)ois.readObject();
			ois.close();
			assert c.dataset.equals(dataset) : "Les deux datasets ne correspondent pas!\n\n"+dataset.toString()+"\n\n"+c.dataset.toString();
			for(Instanciation i : c.centres)
				i.dataset = dataset;
			for(int k = 0; k < c.clusters.length; k++)
				for(Instanciation i : c.clusters[k])
					i.dataset = dataset;
			System.out.println("Clusters chargés : "+namefile);
			return c;
		} catch (Exception e) {
			System.err.println("Lecture du cluster impossible : "+e);
		}
		return null;
	}

	public int getNumberCluster()
	{
		return centres.length;
	}

	public Instanciation getClusterCenter(int i)
	{
		return centres[i];
	}
	
}
