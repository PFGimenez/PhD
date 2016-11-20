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
	private List<AbstractConstraint> contraintes = new ArrayList<AbstractConstraint>();
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
		boolean[] tire = new boolean[vars.length];
		
		while(nbConnexion < objectif)
		{
			int tailleContraintes;
			do {
				tailleContraintes = r.nextInt(2) + 2; // entre 2 et 3 variables
			} while(getNbConnexions(tailleContraintes) > objectif - nbConnexion);
			
			System.out.println("Taille contraintes : "+tailleContraintes);
			for(int i = 0; i < tire.length; i++)
				tire[i] = false;
			
			Variable[] varsContraintes = new Variable[tailleContraintes];
			int[] indContraintes = new int[tailleContraintes];
			
			for(int i = 0; i < tailleContraintes; i++)
			{
				int v;
				do {
					v = r.nextInt(vars.length);
				} while(tire[v]);
				tire[v] = true;
				varsContraintes[i] = vars[v];
				indContraintes[i] = v;
				System.out.println(vars[v].name);
			}
			
			
			// il faut que la contrainte concerne des variables qui n'étaient pas déjà contraintes entre elles
			boolean ok = false;
			for(int i = 0; i < tailleContraintes; i++)
				for(int j = 0; j < i; j++)
					if(!connected[indContraintes[i]][indContraintes[j]])
					{
						ok = true;
						connected[indContraintes[i]][indContraintes[j]] = true;
						connected[indContraintes[j]][indContraintes[i]] = true;
					}
			
			if(ok)
			{
				contraintes.add(new Constraint(durete, varsContraintes));
				nbConnexion += getNbConnexions(tailleContraintes);
			}
			else
				System.out.println("Annulé !");
			System.out.println(nbConnexion+" / "+objectif);

		}
		
		
	}
	
	private int getNbConnexions(int tailleContraintes)
	{
		return tailleContraintes * (tailleContraintes - 1) / 2;
	}
	
	/**
	 * Génère un CSP aléatoire avec uniquement des variables binaires
	 * @param vars
	 * @param connectivite
	 * @param durete
	 */
/*	public RandomCSP(Variable[] vars, double connectivite, double durete)
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
				connected[var2][var1] = true;
				contraintes.add(new BinaryConstraint(vars[var1], vars[var2], durete));
				nbConnexion++;
			}
			var1 = var2;
		}
	}*/
	
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
		    	AbstractConstraint c  = contraintes.get(i);
		    	writer.println("<relation name=\"relPourcontrainte"+i+"\" arity=\""+c.getNbVariables()+"\" nbTuples=\""+c.getNbAllowed()+"\" semantics=\"supports\">");
		    	c.reinitIterator();
		    	while(c.hasNext())
    			{
    				writer.print(c.next());
    				if(c.hasNext())
    					writer.println(" |");
    				else // dernière valeur
    					writer.println();
    			}
		    	writer.println("</relation>");
		    }
		    writer.println("</relations>");
		    
		    // les contraintes
		    writer.println("<constraints nbConstraints=\""+contraintes.size()+"\">");
		    for(int i = 0; i < contraintes.size(); i++)
		    	writer.println("<constraint name=\"contrainte"+i+"\" arity=\""+contraintes.get(i).getNbVariables()+"\" reference=\"relPourcontrainte"+i+"\" scope=\""+contraintes.get(i).getScope()+"\"/>");
		    writer.println("</constraints>");

		    writer.println("</instance>");
		    writer.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
