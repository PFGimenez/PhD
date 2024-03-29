package preferences.completeTree;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import compilateurHistorique.DatasetInfo;
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
 * Génération d'un k-LP-tree
 * @author Pierre-François Gimenez
 *
 */

public class DynamicallyGeneratedKLexTree implements LexTreeInterface
{
	private int k;
	private Random random;
	private double coeffSplit;
	private BigInteger rangMax;
	private double rangMaxLog;
	private LexicographicMultipleTree root;
	private ArrayList<Variable> varsl;
	private DatasetInfo dataset;
	
	/**
	 * Prépare la racine de l'arbre généré dynamiquement
	 * @param vars
	 * @param coeffSplit
	 * @param seed
	 */
	public DynamicallyGeneratedKLexTree(DatasetInfo dataset, Variable[] vars, double coeffSplit, int k)
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
		
		this.k = k;
		this.dataset = dataset;
		int nbVar = random.nextInt(k)+1;
		List<String> l = new ArrayList<String>();
		int domain = 1;
		List<Integer> all = new ArrayList<Integer>();
		for(int i = 0; i < vars.length; i++)
			all.add(i);
		for(int i = 0; i < k; i++)
		{
			int j = all.remove(random.nextInt(all.size()));
			l.add(vars[j].name);
			domain *= vars[j].domain;
		}
		root = new LexicographicMultipleTree(dataset, l, domain, random.nextDouble() < coeffSplit);
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
		private List<String> val, var;
		
		public InfereRangExplorer(List<String> val, List<String> var)
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
		LexicographicMultipleTree n = null, enfant = root;
		int indiceEnfant = -1;
		
		do {
			if(enfant == null) // pas d'enfant : on le crée
			{
				Variable top = variablesTmp.get(random.nextInt(variablesTmp.size()));
				// TODO
//				enfant = new LexicographicMultipleTree(top.name, top.domain, random.nextDouble() < coeffSplit, n.profondeur+1);
//				System.out.println("Création d'un enfant : "+top.name);
//				System.out.println("Split ? "+n.split);
				enfant.setOrdrePrefRandom();
				n.setEnfant(indiceEnfant, enfant);
				n.updateBaseChildren();
			}
			n = enfant;
			
/*			Iterator<Variable> iter = variablesTmp.iterator();
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
			}*/
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
	public BigInteger infereRang(List<String> val, List<String> var)
	{
		// On prépare l'arbre
		completeTree(new InfereRangExplorer(val, var));
		// L'arbre est prêt : on peut inférer le rang
		return root.infereRang(val, var);
	}

	public double getRangMaxLog() {
		return rangMaxLog;
	}

	@Override
	public int getTailleTable()
	{
		return 0;
	}

}
