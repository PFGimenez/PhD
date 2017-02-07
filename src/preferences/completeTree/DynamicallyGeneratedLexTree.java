package preferences.completeTree;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Random;

import compilateurHistorique.Variable;

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
 * Génération d'un arbre lexicographique
 * @author Pierre-François Gimenez
 *
 */

public class DynamicallyGeneratedLexTree implements LexTreeInterface
{
	private Random random;
	private double coeffSplit;
	private BigInteger rangMax;
	private double rangMaxLog;
	private LexicographicTree root;
	private ArrayList<Variable> varsl;
	
	/**
	 * Prépare la racine de l'arbre généré dynamiquement
	 * @param vars
	 * @param coeffSplit
	 * @param seed
	 */
	public DynamicallyGeneratedLexTree(Variable[] vars, double coeffSplit)
	{
		random = new Random();
		
		rangMax = BigInteger.ONE;
		for(int i = 0; i < vars.length; i++)
			rangMax = rangMax.multiply(BigInteger.valueOf(vars[i].domain));

		rangMaxLog = 0;
		for(int i = 0; i < vars.length; i++)
			rangMaxLog += Math.log(vars[i].domain);

		varsl = new ArrayList<Variable>();
		for(int i = 0; i < vars.length; i++)
			varsl.add(vars[i]);

		Variable top = vars[random.nextInt(vars.length)];
		
		root = new LexicographicTree(top.name, top.domain, random.nextDouble() < coeffSplit, 1);
		root.setOrdrePrefRandom();
		root.updateBaseNoRecursive(rangMax);
	}
	
	@Override
	public BigInteger getRangMax() {
		return rangMax;
	}
	
	
	
	private interface LexTreeExplorer
	{
		public int getDirection(LexicographicTree n);
	}
	
	/**
	 * Explorer à partir d'une configuration
	 * @author Pierre-François Gimenez
	 *
	 */
	private class InfereRangExplorer implements LexTreeExplorer
	{
		private ArrayList<String> val, var;
		
		public InfereRangExplorer(ArrayList<String> val, ArrayList<String> var)
		{
			this.var = var;
			this.val = val;
		}
		
		@Override
		public int getDirection(LexicographicTree n)
		{
			return n.getPref(val.get(var.indexOf(n.variable)));
		}
	}
	
	/**
	 * Explorer à partir d'un rang
	 * @author Pierre-François Gimenez
	 *
	 */
	private class InfereConfigurationExplorer implements LexTreeExplorer
	{
		private BigInteger rang;
		
		public InfereConfigurationExplorer(BigInteger rang)
		{
			this.rang = rang;
		}
		
		@Override
		public int getDirection(LexicographicTree n)
		{
			BigInteger base = n.base;
			int i;
			for(i = 0; i < n.nbMod; i++)
				if(rang.compareTo(base.multiply(BigInteger.valueOf(i+1))) < 0)
					break;
			rang = rang.mod(base);
			return i;
		}
	}
	
	/**
	 * Complète une branche de l'arbre généré dynamiquement en suivant les directions de l'explorer
	 * @param explorer
	 */
	public void completeTree(LexTreeExplorer explorer)
	{
		ArrayList<Variable> variablesTmp = new ArrayList<Variable>();
		variablesTmp.addAll(varsl);
		LexicographicTree n = null, enfant = root;
		int indiceEnfant = -1;
		
		do {
			if(enfant == null) // pas d'enfant : on le crée
			{
				Variable top = variablesTmp.get(random.nextInt(variablesTmp.size()));				
				enfant = new LexicographicTree(top.name, top.domain, random.nextDouble() < coeffSplit, n.profondeur+1);
//				System.out.println("Création d'un enfant : "+top.name);
//				System.out.println("Split ? "+n.split);
				enfant.setOrdrePrefRandom();
				n.setEnfant(indiceEnfant, enfant);
				n.updateBaseChildren();
			}
			n = enfant;
			
			Iterator<Variable> iter = variablesTmp.iterator();
			while(iter.hasNext())
				if(iter.next().name.equals(n.variable))
				{
					iter.remove();
					break;
				}

			if(!variablesTmp.isEmpty())
			{
				indiceEnfant = explorer.getDirection(n);
				if(n.getEnfants().size() == 0) // pas encore d'enfant : il faudra le créer
					enfant = null;
				else
					enfant = (LexicographicTree) n.getEnfants().get(indiceEnfant);
			}
		}
		while(!variablesTmp.isEmpty());
	}
	
	@Override
	public HashMap<String, String> getConfigurationAtRank(BigInteger rang)
	{
		// On prépare l'arbre
		completeTree(new InfereConfigurationExplorer(rang));
		// L'arbre est prêt : on peut inférer la configuration
		return root.getConfigurationAtRank(rang);
	}
	
	@Override
	public BigInteger infereRang(ArrayList<String> val, ArrayList<String> var)
	{
		// On prépare l'arbre
		completeTree(new InfereRangExplorer(val, var));
		// L'arbre est prêt : on peut inférer le rang
		return root.infereRang(val, var);
	}

	public double getRangMaxLog() {
		return rangMaxLog;
	}

}
