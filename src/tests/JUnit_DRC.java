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
		dag = new DAG("datasets/hailfinder/BN_1.xml");
	}
	
	@Test
	public void test_moralisation() throws Exception
	{
		MoralGraph gm = new MoralGraph(dag, dag.dag[0].keySet());
		dag.printGraphe("test-dag");
		gm.printGraphe("test-moral");
	}	
	
	@Test
	public void test_separation() throws Exception
	{
		MoralGraph gm = new MoralGraph(dag, dag.dag[0].keySet());
		dag.printGraphe("test-dag");
		gm.computeSeparator();
		gm.printGraphe("test-moral");
	}
}
