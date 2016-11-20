/*
Copyright (C) 2016 Pierre-François Gimenez

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>
*/

package tests;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import compilateurHistorique.Instanciation;
import compilateurHistorique.Variable;


/**
 * JUnit test d'instanciation
 * @author Pierre-François Gimenez
 *
 */

public class JUnit_Instanciation
{
	
	private Instanciation instance;
	private Variable[] variables;
	private Variable v1, v2;
	
	@Before
	public void init()
	{
		v1 = new Variable();
		v1.domain = 2;
		v1.values.add("v1-1");
		v1.values.add("v1-2");
		v1.name = "v1";

		v2 = new Variable();
		v2.domain = 3;
		v2.values.add("v2-1");
		v2.values.add("v2-2");
		v2.values.add("v2-3");
		v2.name = "v2";
		
		variables = new Variable[]{v1, v2};
		
		Instanciation.reinit();
		Instanciation.setVars(variables);
		instance = new Instanciation();	
	}
	
	@Test
	public void test_nbVarInstanciees() throws Exception
	{
		Assert.assertEquals(0, instance.getNbVarInstanciees());
		instance.conditionne("v1", "v1-2");
		Assert.assertEquals(1, instance.getNbVarInstanciees());
		instance.conditionne("v2", "v2-2");
		Assert.assertEquals(2, instance.getNbVarInstanciees());
	}
	
	@Test
	public void test_reinit() throws Exception
	{
		Instanciation.reinit();
		Instanciation.setVars(new Variable[]{v2, v1});
		test_nbVarInstanciees();
	}
	
	
	
}
