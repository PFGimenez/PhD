package recommandation;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

import org.eclipse.recommenders.jayes.BayesNet;
import org.eclipse.recommenders.jayes.BayesNode;
import org.eclipse.recommenders.jayes.inference.jtree.JunctionTreeAlgorithm;
import org.eclipse.recommenders.jayes.io.xmlbif.XMLBIFReader;

import compilateurHistorique.DatasetInfo;
import compilateurHistorique.Instanciation;
import recommandation.parser.ParserProcess;

import java.util.Map;
import java.util.Random;

/*   (C) Copyright 2015, Gimenez Pierre-François
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
 * Algorithme de recommandation avec les réseaux bayésiens qui utilise Jayes
 * @author Pierre-François Gimenez
 *
 */

public class AlgoRBJayes extends AlgoRecoRB
{
	private BayesNet rb;
	private JunctionTreeAlgorithm inferer;
	private Map<BayesNode,String> evidence = new HashMap<BayesNode,String>();
	private Random r = new Random();
	private double lastProba = 1; // utilisé pour calculer P(abc) pour le choix du cluster
	// vaut d'abord P(c), puis P(b|c)*P(c) puis P(a|bc)*P(b|c)*P(c)
	private int datasetSize;
	
	public AlgoRBJayes()
	{
		this("hc");
	}
	
	public AlgoRBJayes(String learningAlgo)
	{
		super(learningAlgo);
		inferer = new JunctionTreeAlgorithm();
	}

	public AlgoRBJayes(ParserProcess pp)
	{
		this(pp.read());
	}
		
	public void describe()
	{
		System.out.println("Bayesian Network with Jayes");
	}
	
	@Override
	public String recommande(String variable, ArrayList<String> possibles)
	{
		double[] beliefsC;
		inferer.setEvidence(evidence);
		beliefsC = inferer.getBeliefs(rb.getNode(variable));
		int best=-1;
		double bestproba=-1;
		for(int i = 0; i < beliefsC.length; i++)
			if(beliefsC[i] > bestproba && (possibles == null || possibles.contains(rb.getNode(variable).getOutcomeName(i))))
			{
				bestproba = beliefsC[i];
				best = i;
			}

		if(best == -1) // toutes les valeurs connues sont impossibles
		{
			assert possibles != null;
			return possibles.get(r.nextInt(possibles.size()));
		}
		return rb.getNode(variable).getOutcomeName(best);
	}

	@Override
	public void setSolution(String variable, String solution)
	{
		try
		{
			// Si cette valeur n'a jamais été observée dans l'ensemble de tests, on ne peut pas la traiter
			rb.getNode(variable).getOutcomeIndex(solution);
			inferer.setEvidence(evidence);
//			System.out.println(variable+" "+solution+" : "+evidence);
			double[] beliefsC = inferer.getBeliefs(rb.getNode(variable));
			int index = rb.getNode(variable).getOutcomeIndex(solution);
			lastProba *= beliefsC[index];
			evidence.put(rb.getNode(variable), solution);
		}
		catch(IllegalArgumentException e) // valeur inconnue : correction de Laplace
		{
			lastProba *= 1. / datasetSize;
		}
		
	}

	@Override
	public void oublieSession()
	{
		lastProba = 1;
		evidence.clear();
	}
	
/*	@Override
	public void apprendDonnees(DatasetInfo datasetinfo, ArrayList<String> filename, int nbIter, boolean entete) 
	{
		super.learnBN(filename, entete);
	}
	*/
	
	@Override
	public void termine()
	{}

	@Override
	public void terminePli()
	{}

	public String recommandeGeneration(String variable, ArrayList<String> possibles)
	{
		double[] beliefsC;
		inferer.setEvidence(evidence);
		beliefsC = inferer.getBeliefs(rb.getNode(variable));

		double choix = (new Random()).nextDouble();
		double total = 0;
		double normalisation = 0;
		
		for(int i = 0; i < beliefsC.length; i++)
			if(possibles.contains(rb.getNode(variable).getOutcomeName(i)))
				normalisation += beliefsC[i];
				
		// Si aucun cas n'est rencontré, on renvoie une valeur au hasard (uniformément tirée)
		if(normalisation == 0)
			return possibles.get((new Random()).nextInt(possibles.size()));

		choix = choix * normalisation;

		for(int i = 0; i < beliefsC.length; i++)
			if(possibles.contains(rb.getNode(variable).getOutcomeName(i)))
			{
				total += beliefsC[i];
				if(choix <= total)
					return rb.getNode(variable).getOutcomeName(i);
			}
		System.out.println("Erreur! "+choix+" "+total);
		return null;
	}
	
	public String toString()
	{
		return getClass().getSimpleName();
	}
	
	@Override
	public void unassign(String variable)
	{
		evidence.remove(rb.getNode(variable));
	}

	@Override
	public void apprendRB(String file)
	{
		File fichier = new File(file);
		InputStream input;
		try {
			input = new FileInputStream(fichier);
			XMLBIFReader reader = new XMLBIFReader(input);			
			rb = reader.read();
			inferer.setNetwork(rb);
			reader.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void apprendDonnees(DatasetInfo dataset, Instanciation[] instances, int code)
	{
		datasetSize = instances.length;
		learnBN(dataset, instances, code);
	}

	public double distance(Instanciation current, Instanciation center)
	{
		return -lastProba;
	}
}
