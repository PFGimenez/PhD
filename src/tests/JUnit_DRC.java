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

import java.util.HashSet;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import graphOperation.DAG;
import graphOperation.MoralGraph;

/**
 * Tests unitaires pour l'algorithme DRC
 * @author pf
 *
 */

public class JUnit_DRC {

	private DAG dag;
	
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
}
