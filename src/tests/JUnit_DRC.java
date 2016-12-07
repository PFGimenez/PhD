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

package tests;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.recommenders.jayes.BayesNet;
import org.eclipse.recommenders.jayes.BayesNode;
import org.eclipse.recommenders.jayes.inference.jtree.JunctionTreeAlgorithm;
import org.eclipse.recommenders.jayes.io.xmlbif.XMLBIFReader;
import org.junit.Before;
import org.junit.Test;

import compilateurHistorique.Instanciation;
import compilateurHistorique.IteratorInstancesPartielles;
import compilateurHistorique.MultiHistoComp;
import graphOperation.DAG;
import graphOperation.InferenceDRC;
import graphOperation.MoralGraph;

/**
 * Tests unitaires pour l'algorithme DRC
 * @author pf
 *
 */

public class JUnit_DRC {

	private DAG dag;
	private BayesNet rb;
	private JunctionTreeAlgorithm inferer;

	@Before
	public void init()
	{
		dag = new DAG("datasets/congress/BN_1.xml");
	}
	
	@Test
	public void test_separation() throws Exception
	{
		Set<String> instancies = new HashSet<String>();
		instancies.add("V10");
		instancies.add("V7");
		instancies.add("V4");
		instancies.add("V13");
		MoralGraph gm = new MoralGraph(dag, instancies, true);
		gm.printGraphe("test-moral-brut");
		gm.computeDijkstra();
		gm.getZ();
		gm.prune();
		gm.printGraphe("test-moral-prune");
		dag.printGraphe("test-dag");
		gm.computeSeparator();
		gm.printGraphe("test-moral-separe");
	}
	
	@Test
	public void test_inference() throws Exception
	{
		List<String> file = new ArrayList<String>();
		file.add("datasets/congress/set0_exemples");
		MultiHistoComp histo = new MultiHistoComp(file, false, null);
		histo.compile(file, false);
		InferenceDRC drc = new InferenceDRC(10, dag, histo);
		Instanciation u = new Instanciation();
		
		File fichier = new File("datasets/congress/BN_1.xml");
		InputStream input;
		try {
			input = new FileInputStream(fichier);
			XMLBIFReader reader = new XMLBIFReader(input);			
			rb = reader.read();
			inferer = new JunctionTreeAlgorithm();
			inferer.setNetwork(rb);
			reader.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		Map<BayesNode,String> evidence = new HashMap<BayesNode,String>();

		u.conditionne("V4", "y");
		u.conditionne("V7", "y");
		u.conditionne("V9", "n");
		u.conditionne("V5", "y");
		u.conditionne("V14", "y");
		u.conditionne("V16", "n");
		double p = drc.infere(u, u.getEVConditionees());
		System.out.println(p);
		
		inferer.setEvidence(evidence);
		double[] proba = inferer.getBeliefs(rb.getNode("V1"));
		for(int i = 0; i < proba.length; i++)
			System.out.println("Jayes : "+rb.getNode("V1").getOutcomeName(i)+" "+proba[i]);

	}
}
