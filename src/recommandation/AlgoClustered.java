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

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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
	private int fraction = 1;
	
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
		List<Instanciation> instanciations = null;
		try {
			instanciations = HistoriqueCompile.readPossibleInstances(dataset, filename, entete, learnInvalid ? null : contraintes, fraction);
		} catch (IOException e) {
			e.printStackTrace();
		}

		long code = 0;
		for(Instanciation i : instanciations)
			code += i.hashCode();
		code = Math.abs(code);

		instanceReco = new Instanciation(dataset);
		String sauvegarde = "tmp/"+clusters.length+"-clusters-"+code;
		
		// un seul cluster, cas particulier
		c = Clusters.load(sauvegarde, dataset);
		if(c == null)
		{
			if(clusters.length == 1)
				c = new Clusters(dataset, instanciations);
			else
				c = new Clusters(clusters.length, dataset, instanciations);
			System.out.println("Cluster créé");
			c.save(sauvegarde);
		}
		
		assert c.getNumberCluster() == clusters.length;
		
		int nbInstancesTotal = 0;
		for(int i = 0; i < clusters.length; i++)			
			nbInstancesTotal += c.getCluster(i).size();
			
		coeff = new double[clusters.length];
		for(int i = 0; i < clusters.length; i++)
		{
			coeff[i] = c.getCluster(i).size() * 1. / nbInstancesTotal; 
			assert c.getCluster(i).size() > 0 : "Cluster vide !";
			clusters[i].apprendDonnees(dataset, c.getCluster(i), code * clusters.length + i);
			System.out.println("Cluster "+i+" size : "+c.getCluster(i).size());
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
		if(fraction != 1)
			System.out.println("Fraction learnt : 1/"+fraction);
		System.out.println("Clusters : ");
		for(int i = 0; i < clusters.length; i++)
			clusters[i].describe();
	}

	public String toString()
	{
		return getClass().getSimpleName()+" of "+clusters.length+" "+clusters[0].toString()+(learnInvalid ? " learning invalid outcomes too":"");
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

	public void setFractionLearning(int fraction)
	{
		this.fraction = fraction;
	}
}
