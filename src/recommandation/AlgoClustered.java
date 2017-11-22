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

package recommandation;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;

import compilateur.SALADD;
import compilateurHistorique.Clusters;
import compilateurHistorique.DatasetInfo;
import compilateurHistorique.HistoriqueCompile;
import compilateurHistorique.Instanciation;
import recommandation.parser.AlgoParser;
import recommandation.parser.ParserProcess;

/**
 * Adapte un algorithme existant à l'utilisation d'un cluster
 * @author pgimenez
 *
 */

public class AlgoClustered implements AlgoReco
{
	private Clusturable[] clusters;
	private double[] coeff;
	private Instanciation instanceReco;
	private Clusters c;
	private HashMap<String, Double> meanMetric = new HashMap<String, Double>();
	private HashMap<String, Double> sumMetric = new HashMap<String, Double>();
	private int nbMetric = 0;
	private boolean learnInvalid = false;
	protected SALADD contraintes;
	
	@Override
	public void apprendContraintes(SALADD contraintes)
	{
		this.contraintes = contraintes;
		for(Clusturable c : clusters)
			c.apprendContraintes(contraintes);
	}
	
	public boolean isOracle()
	{
		boolean out = true;
		for(Clusturable c : clusters)
			out &= c.isOracle();
		return out;
	}
	
	public void setLearnInvalid()
	{
		learnInvalid = true;
	}
	
	public AlgoClustered(ParserProcess pp)
	{
		int nbClusters = Integer.parseInt(pp.read());
		Class<? extends Clusturable> c = null;
		c = AlgoParser.getAlgoReco(pp.read());

		clusters = new Clusturable[nbClusters];
		for(int i = 0; i < nbClusters; i++)
			try {
				clusters[i] = c.getConstructor(ParserProcess.class).newInstance(i < nbClusters - 1 ? pp.clone() : pp);
			} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException e) {
				e.printStackTrace();
			}
	}
	
/*	public AlgoClustered(Class<? extends Clusturable> c, int nbClusters, boolean verbose, String[] args, Integer k)
	{
		this.verbose = verbose;
		clusters = new Clusturable[nbClusters];
		for(int i = 0; i < nbClusters; i++)
			try {
				clusters[i] = c.getConstructor(String[].class, Integer.class).newInstance(args, k);
			} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException e) {
				e.printStackTrace();
			}
	}*/

	@Override
	public void apprendDonnees(DatasetInfo dataset, ArrayList<String> filename, int nbIter, boolean entete)
	{
		Instanciation[] instanciations = HistoriqueCompile.readPossibleInstances(dataset, filename, entete, learnInvalid ? null : contraintes);

		long code = 0;
		for(int i = 0; i < instanciations.length; i++)
			code += instanciations[i].hashCode();
		code = Math.abs(code);
		
/*		int code = 0;
		for(String s : filename)
			code += s.hashCode();
		code = Math.abs(code);
		code += 845;*/
		instanceReco = new Instanciation(dataset);
		String sauvegarde = "tmp/"+clusters.length+"-clusters-"+code;
		
		// un seul cluster, cas particulier
		if(clusters.length == 1)
		{
			c = new Clusters(dataset, instanciations);
			System.out.println("Cluster trivial créé");
		}
		else
		{
			c = Clusters.load(sauvegarde, dataset);
			if(c == null)
			{
				c = new Clusters(clusters.length, dataset, instanciations);
				System.out.println("Cluster créé");
				c.save(sauvegarde);
			}
		}		
		
		assert c.getNumberCluster() == clusters.length;
		
		int nbInstancesTotal = 0;
		for(int i = 0; i < clusters.length; i++)			
			nbInstancesTotal += c.getCluster(i).length;
			
		coeff = new double[clusters.length];
		for(int i = 0; i < clusters.length; i++)
		{
			coeff[i] = c.getCluster(i).length * 1. / nbInstancesTotal; 
			assert c.getCluster(i).length > 0 : "Cluster vide !";
			clusters[i].apprendDonnees(dataset, c.getCluster(i), code * clusters.length + i);
		}
	}
	
	private Clusturable getNearestCluster(Instanciation instanceReco)
	{
		Clusturable best = clusters[0];
		double minDistance = best.distance(instanceReco, c.getClusterCenter(0));
		for(int i = 1; i < clusters.length; i++)
		{
			double candidat = clusters[i].distance(instanceReco, c.getClusterCenter(i));
			if(candidat < minDistance)
			{
				best = clusters[i];
				minDistance = candidat;
			}
		}
		return best;
	}

	@Override
	public String recommande(String variable, ArrayList<String> possibles)
	{
		return getNearestCluster(instanceReco).recommande(variable, possibles);
	}

	@Override
	public void setSolution(String variable, String solution)
	{
		instanceReco.conditionne(variable, solution);
		for(int i = 0; i < clusters.length; i++)
			clusters[i].setSolution(variable, solution);
	}

	@Override
	public void unassign(String variable)
	{
		instanceReco.deconditionne(variable);
		for(int i = 0; i < clusters.length; i++)
			clusters[i].unassign(variable);
	}

	@Override
	public void oublieSession()
	{
		instanceReco.deconditionneTout();
		for(int i = 0; i < clusters.length; i++)
			clusters[i].oublieSession();
	}

	@Override
	public void termine()
	{
		for(int i = 0; i < clusters.length; i++)
			clusters[i].termine();
		for(String s : meanMetric.keySet())
			meanMetric.put(s, meanMetric.get(s) / nbMetric);
		for(String s : sumMetric.keySet())
			sumMetric.put(s, sumMetric.get(s) / nbMetric);
		System.out.println("Métriques : "+meanMetric+"\n"+sumMetric);
	}

	@Override
	public void describe()
	{
		System.out.println("Clusters : ");
		for(int i = 0; i < clusters.length; i++)
			clusters[i].describe();
	}

	public String toString()
	{
		return getClass().getSimpleName()+" of "+clusters.length+" "+clusters[0].toString();
	}

	@Override
	public void terminePli()
	{
		for(int i = 0; i < clusters.length; i++)
		{
			clusters[i].terminePli();
			HashMap<String, Double> map = clusters[i].metricCoeff();
			for(String s : map.keySet())
			{
				Double tmp = meanMetric.get(s);
				if(tmp == null)
					tmp = 0.;
				meanMetric.put(s, tmp + map.get(s) * coeff[i]);
			}
			map = clusters[i].metric();
			for(String s : map.keySet())
			{
				Double tmp = sumMetric.get(s);
				if(tmp == null)
					tmp = 0.;
				sumMetric.put(s, tmp + map.get(s));
			}
		}
		nbMetric++;
	}
}
