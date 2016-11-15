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

package contraintes;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import compilateurHistorique.Variable;

/**
 * Un CSP aléatoire
 * @author Pierre-François Gimenez
 *
 */

public class RandomCSP
{
	private List<Constraint> contraintes = new ArrayList<Constraint>();
	private static Random r = new Random();
	private Variable[] vars;

	/**
	 * Génère un CSP aléatoire
	 * @param vars
	 * @param connectivite
	 * @param durete
	 */
	public RandomCSP(Variable[] vars, double connectivite, double durete)
	{
		this.vars = vars;
		int objectif = (int) (vars.length*(vars.length-1) / 2. * connectivite + .5);
		boolean[][] connected = new boolean[vars.length][vars.length];
		for(int i = 0; i < vars.length; i++)
			for(int j = 0; j < vars.length; j++)
				connected[i][j] = false;
		int nbConnexion = 0;
		int var1 = r.nextInt(vars.length);
		while(nbConnexion < objectif)
		{
			int var2 = r.nextInt(vars.length);
			if(!connected[var1][var2])
			{
				connected[var1][var2] = true;
				contraintes.add(new Constraint(vars[var1], vars[var2], durete));
				nbConnexion++;
			}
			var1 = var2;
		}
	}
	
	/**
	 * Sauvegarde le CSP sous le format XCSP
	 * @param file
	 */
	public void save(String file)
	{
		try{
		    PrintWriter writer = new PrintWriter(file, "UTF-8");
		    writer.println("<instance>");
		    writer.println("<presentation format=\"XCSP 2.1\" type=\"CSP\" name=\"random\"></presentation>");
		    
		    // les domaines
		    writer.println("<domains nbDomains=\""+vars.length+"\">");
		    for(Variable v : vars)
		    {
			    writer.println("<domain name=\"D"+v.name+"\" nbValues=\""+v.domain+"\">");
			    for(String s : v.values)
			    	writer.print(" "+s);
			    writer.println();
			    writer.println("</domain>");
		    }
		    writer.println("</domains>");
		    
		    // les variables
		    writer.println("<variables nbVariables=\""+vars.length+"\">");
		    for(Variable v : vars)
			    writer.println("<variable name=\""+v.name+"\" domain=\"D"+v.name+"\"/>");
		    writer.println("</variables>");
		    
		    // les relations
		    writer.println("<relations nbRelations=\""+contraintes.size()+"\">");
		    for(int i = 0; i < contraintes.size(); i++)
		    {
		    	Constraint c  = contraintes.get(i);
		    	writer.println("<relation name=\"relPourcontrainte"+i+"\" arity=\"2\" nbTuples=\""+c.nbAllowed+"\" semantics=\"supports\">");
		    	int nbEcrit = 0;
		    	for(int k = 0; k < c.var1.domain; k++)
		    		for(int l = 0; l < c.var2.domain; l++)
		    			if(c.allowedValues[k][l])
		    			{
		    				nbEcrit++;
		    				writer.print(c.var1.values.get(k)+" "+c.var2.values.get(l));
		    				if(nbEcrit == c.nbAllowed) // dernière valeur
		    					writer.println();
		    				else
		    					writer.println(" |");
		    			}
		    	writer.println("</relation>");
		    }
		    writer.println("</relations>");
		    
		    // les contraintes
		    writer.println("<constraints nbConstraints=\""+contraintes.size()+"\">");
		    for(int i = 0; i < contraintes.size(); i++)
		    	writer.println("<constraint name=\"contrainte"+i+"\" arity=\"2\" reference=\"relPourcontrainte"+i+"\" scope=\""+contraintes.get(i).var1.name+" "+contraintes.get(i).var2.name+"\"/>");
		    writer.println("</constraints>");

		    writer.println("</instance>");
		    writer.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
