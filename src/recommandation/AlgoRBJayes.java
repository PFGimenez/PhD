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

import java.util.Map;

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
 * @author pgimenez
 *
 */

public class AlgoRBJayes implements AlgoReco
{
	private BayesNet rb;
	private JunctionTreeAlgorithm inferer;
	private Map<BayesNode,String> evidence = new HashMap<BayesNode,String>();
	private String last;
	
	@Override
	public void initialisation(ArrayList<String> variables)
	{
		inferer = new JunctionTreeAlgorithm();
//		inferer.getFactory().setFloatingPointType(double.class);
	}
	
	@Override
	public String recommande(String variable, ArrayList<String> possibles)
	{
		double[] beliefsC;
		try
		{
			inferer.setEvidence(evidence);
			beliefsC = inferer.getBeliefs(rb.getNode(variable));

		}
		catch(IllegalArgumentException e)
		{
			// Si la dernière valeur solution n'a jamais été observée dans l'ensemble de tests, on ne peut pas la traiter
			evidence.remove(rb.getNode(last));
			inferer.setEvidence(evidence);
			beliefsC = inferer.getBeliefs(rb.getNode(variable));
		}

		int best=0;
		double bestproba=-1;
		
		for(int i = 0; i < beliefsC.length; i++)
		{
			if(beliefsC[i] > bestproba){
				bestproba = beliefsC[i];
				best = i;
			}
		}
		return rb.getNode(variable).getOutcomeName(best);
	}

	@Override
	public void setSolution(String variable, String solution)
	{
		evidence.put(rb.getNode(variable), solution);
		last = variable;

	}

	@Override
	public void oublieSession()
	{
		evidence.clear();
	}

	@Override
	public void apprendContraintes(String filename)
	{}

	@Override
	public void apprendDonnees(ArrayList<String> filename, int nbIter) 
	{
		String dataset = "renault_big";
		String prefixData = "datasets/"+dataset+"/";
		File fichier = new File(prefixData+"BN_"+nbIter+".xml");
		InputStream input;
		try {
			input = new FileInputStream(fichier);
			XMLBIFReader reader = new XMLBIFReader(input);			
			rb = reader.read();
			inferer.setNetwork(rb);
			reader.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void termine()
	{}
	/*
	public static void main(String[] args)
	{
		String dataset = "renault_big_sans_contraintes";
		String prefixData = "datasets/"+dataset+"/";
		File fichier = new File(prefixData+"BN_"+0+".xml");
		InputStream input;
		try {
			input = new FileInputStream(fichier);
			XMLBIFReader reader = new XMLBIFReader(input);			
			BayesNet rb = reader.read();
			JunctionTreeAlgorithm inferer = new JunctionTreeAlgorithm();
			inferer.setNetwork(rb);
			Map<BayesNode,String> evidence = new HashMap<BayesNode,String>();
//			evidence.put(rb.getNode("v55"), "999");
			evidence.put(rb.getNode("v6"), "1");
			double[] beliefsC = inferer.getBeliefs(rb.getNode("v7"));
			for(int i = 0; i < beliefsC.length; i++)
				System.out.println(beliefsC[i]);
			reader.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
*/
}
