package preferences.multipleTree;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import compilateurHistorique.Instanciation;
import compilateurHistorique.DatasetInfo;
import compilateurHistorique.HistoriqueCompile;
import preferences.ProbabilityDistributionLog;
import preferences.heuristiques.MultipleHeuristique;
import preferences.penalty.PenaltyWeightFunction;

/*   (C) Copyright 2017, Gimenez Pierre-François 
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
 * Apprentissage d'une structure basée sur l'ordre lexicographique
 * @author Pierre-François Gimenez
 *
 */

public class ApprentissageGloutonMultipleTree
{
	protected int nbVar;
	protected BigInteger base;
	protected ArrayList<String> variables;
	protected HistoriqueCompile historique;
	protected MultipleHeuristique h;
//	protected Instanciation[] allInstances;
//	protected DatasetInfo dataset;
	
//	public abstract LexicographicMultipleTree apprendDonnees(ArrayList<String> filename, boolean entete);

	public LexicographicMultipleTree apprendDonnees(DatasetInfo dataset, Instanciation[] instances)
	{
//		this.dataset = dataset;
		variables = new ArrayList<String>();
		variables.addAll(dataset.mapVar.keySet());
		nbVar = variables.size();
//		this.allInstances = instances;
		historique = new HistoriqueCompile(dataset);
		historique.compile(instances);
		
		base = BigInteger.ONE;
		for(String var : variables)
			base = base.multiply(BigInteger.valueOf((dataset.vars[dataset.mapVar.get(var)].domain)));
		ArrayList<String> variablesTmp = new ArrayList<String>();
		variablesTmp.addAll(variables);
		LexicographicMultipleTree struct = apprendRecursif(dataset, new Instanciation(dataset), variables, true);
//		System.out.println("Apprentissage fini");
		struct.updateBase(base);
		return struct;
	}
	

	
	/**
	 * Renvoie le meilleur élément qui vérifie les variables déjà fixées
	 * @param element
	 * @param ordreVariables
	 * @return
	 */
/*	public String infereBest(String varARecommander, ArrayList<String> possibles, ArrayList<String> element, ArrayList<String> ordreVariables)
	{
		return struct.infereBest(varARecommander, possibles, element, ordreVariables);
	}
	
	public BigInteger infereRang(ArrayList<String> element, ArrayList<String> ordreVariables)
	{
		// +1 car les rangs commencent à 1 et pas à 0
		BigInteger rang = struct.infereRang(element, ordreVariables).add(BigInteger.ONE);
//		System.out.println(rang);
		return rang;
	}*/

	public BigInteger rangMax()
	{
		return base;
	}

/*	public void affiche(String s)
	{
		struct.affiche(s);
	}*/
/*
	public void save(String filename)
	{
		struct.save(filename);
	}

	public boolean load(String filename)
	{
		struct = LexicographicMultipleTree.load(filename);
		if(struct == null)
			System.out.println("Le chargement a échoué");
		else
			System.out.println("Lecture de "+filename);
		return struct != null;
	}*/
	
	@Override
	public String toString()
	{
		return getClass().getSimpleName()+", heuristique : "+h.toString();
	}

	public String getHeuristiqueName()
	{
//		if(h instanceof VieilleHeuristique)
//			return ((VieilleHeuristique)h).h.getClass().getSimpleName().substring(11);
		return h.getClass().getSimpleName().substring(11);
	}

	private int profondeurMax;
	private int seuil;

	public ApprentissageGloutonMultipleTree(int profondeurMax, int seuil, MultipleHeuristique h)
	{
		this.h = h;
		this.profondeurMax = profondeurMax;
		this.seuil = seuil;
		
	}
	
	@Override
	public int hashCode()
	{
		return profondeurMax*42 + seuil * 20 + h.hashCode();
	}
	
	private LexicographicMultipleTree apprendRecursif(DatasetInfo dataset, Instanciation instance, ArrayList<String> variablesRestantes, boolean preferred)
	{
		ArrayList<String> variablesTmp = new ArrayList<String>();
		variablesTmp.addAll(variablesRestantes);
	
		List<String> vars = h.getRacine(dataset, historique, variablesTmp, instance);

		int pasAssez = 0;
		
		// si on a dépassé la profondeur max
		if(variablesTmp.size() < variables.size() - profondeurMax)
			return apprendOrdre(dataset, instance, variablesTmp);
		
		// pas du tout assez d'exemples
		if(historique.getNbInstances(instance) < seuil)
		{
//			System.out.println("Pas assez du tout !");
			return apprendOrdre(dataset, instance, variablesTmp);
		}
		
		HashMap<List<String>, Integer> mapExemples = historique.getNbInstancesToutesModalitees(vars, true, instance);
		
		/**
		 * Peut-on avoir un split sans risquer de finir avec trop peu d'exemples dans une branche ?
		 */
		for(Integer nbInstances : mapExemples.values())
		{
			if(nbInstances < seuil) // split impossible !
				pasAssez++;
		}
		
		/**
		 * Split
		 */
		LexicographicMultipleTree best = new LexicographicMultipleTree(vars, mapExemples.size(), pasAssez == 0);
		best.setOrdrePref(mapExemples);

		// Si c'était les dernières variables, alors c'est une feuille
		if(variablesTmp.size() == vars.size())
			return best;
		
		for(String s : vars)
			variablesTmp.remove(s);
		int nbMod = mapExemples.size();
		
		if(pasAssez == 0)
		{
			for(int i = 0; i < nbMod; i++)
			{
				// On conditionne par une certaine valeur
				for(int j = 0; j < vars.size(); j++)					
					instance.conditionne(vars.get(j), best.getPref(i).get(j));			
				
				best.setEnfant(i, apprendRecursif(dataset, instance, variablesTmp, i == 0));
				
				for(int j = 0; j < vars.size(); j++)					
					instance.deconditionne(vars.get(j));			
			}
		}
		else
		{
			// Pas de split. On apprend un seul enfant qu'on associe à toutes les branches sortantes.
			LexicographicMultipleTree enfant = apprendRecursif(dataset, instance, variablesTmp, true);
//			for(int i = 0; i < nbMod; i++)
			best.setEnfant(0, enfant);
		}
		// A la fin, le VDD est conditionné de la même manière qu'à l'appel
		return best;
	}
	

	protected LexicographicMultipleTree apprendOrdre(DatasetInfo dataset, Instanciation instance, ArrayList<String> variablesRestantes)
	{
		ArrayList<String> variables = new ArrayList<String>();
		variables.addAll(variablesRestantes);
		int nbVar = variables.size();
		LexicographicMultipleTree[] all = new LexicographicMultipleTree[nbVar];
		int i = 0;
		assert historique.getNbInstances(instance) > 0;

		while(!variables.isEmpty())
		{
			List<String> best = h.getRacine(dataset, historique, variables, instance);
			int nbMod = 1;
			for(String s : best)
			{
				nbMod *= dataset.vars[dataset.mapVar.get(s)].domain;
				variables.remove(s);
			}
			
			all[i] = new LexicographicMultipleTree(best, nbMod, false);
			all[i].setOrdrePref(historique.getNbInstancesToutesModalitees(best, true, instance));
			i++;
		}

		LexicographicMultipleTree struct = null;
		for(int k = i - 1; k >= 0; k--)
		{
			if(struct != null)
				for(int j = 0; j < all[k].nbMod; j++)
					all[k].setEnfant(j, struct);
			struct = all[k];
		}

		return struct;
	}

	/**
	 * Élaguer l'arbre. Commence par les feuilles
	 */
	public void pruneFeuille(PenaltyWeightFunction f, LexicographicMultipleTree struct, DatasetInfo dataset, Instanciation[] instances)
	{
		LinkedList<LexicographicMultipleTree> file = new LinkedList<LexicographicMultipleTree>();
		LinkedList<LexicographicMultipleTree> fileChercheFeuilles = new LinkedList<LexicographicMultipleTree>();
		
		fileChercheFeuilles.add(struct);
		
		while(!fileChercheFeuilles.isEmpty())
		{
			LexicographicMultipleTree s = fileChercheFeuilles.removeFirst();
			file.addFirst(s);
			ArrayList<LexicographicMultipleTree> enfants = s.getEnfants();
			if(!enfants.isEmpty())
			{
				if(s.split)
					for(LexicographicMultipleTree l : enfants)
						fileChercheFeuilles.add(l);
				else
					fileChercheFeuilles.add(enfants.get(0)); // on n'ajoute que celui de gauche
			}
		}
		
		double scoreSansPruning = computeScoreWithMeanRank(f, struct, instances);
		
		while(!file.isEmpty())
		{
			LexicographicMultipleTree s = file.removeFirst();
			ArrayList<LexicographicMultipleTree> enfants = s.getEnfants();
			if(s.split && enfants != null && enfants.size() > 0) // si enfants est nul (ou de taille nulle), c'est que s est une feuille et donc le pruning ne le concerne pas
			{
				LexicographicMultipleTree s2 = (LexicographicMultipleTree) s;
				s.split = false;
				s2.setEnfant(0, enfants.get(0));
				
				double scoreAvecPruning = computeScoreWithMeanRank(f, struct, instances);
//				System.out.println("Score avant : "+scoreSansPruning+", après : "+scoreAvecPruning);
				if(scoreAvecPruning > scoreSansPruning) // on a amélioré le score !
					scoreSansPruning = scoreAvecPruning;
				else // c'était mieux avec le split
				{
					s.split = true;
					for(int i = 0; i < enfants.size(); i++)
						s2.setEnfant(i, enfants.get(i));
				}
			}
			
		}
		
	}
	

	/**
	 * Élaguer l'arbre. Commence par la racine. N'élague qu'à partir de la profondeur spécifiée (profondeur = 1 : on élague dès la racine)
	 */
/*	public void pruneRacineProfondeurMin(PenaltyWeightFunction f, ProbabilityDistributionLog p, int profondeurMin)
	{
		LinkedList<LexicographicMultipleTree> file = new LinkedList<LexicographicMultipleTree>();
		file.add(struct);
		double scoreSansPruning = computeScore(f, p);
		
		while(!file.isEmpty())
		{
			LexicographicMultipleTree s = file.removeFirst();
			ArrayList<LexicographicMultipleTree> enfants = s.getEnfants();
			if(s.profondeur >= profondeurMin && s.split && enfants != null && enfants.size() > 0) // si enfants est nul (ou de taille nulle), c'est que s est une feuille et donc le pruning ne le concerne pas
			{
				LexicographicMultipleTree s2 = (LexicographicMultipleTree) s;
				s.split = false;
				s2.setEnfant(0, enfants.get(0));
				
				double scoreAvecPruning = computeScore(f, p);
//				System.out.println("Score avant : "+scoreSansPruning+", après : "+scoreAvecPruning);
				if(scoreAvecPruning > scoreSansPruning) // on a amélioré le score !
					scoreSansPruning = scoreAvecPruning;
				else // c'était mieux avec le split
				{
					s.split = true;
					for(int i = 0; i < enfants.size(); i++)
						s2.setEnfant(i, enfants.get(i));
				}
			}
			
			if(!enfants.isEmpty())
			{
				if(s.split)
					for(LexicographicMultipleTree l : enfants)
						file.add(l);
				else
					file.add(enfants.get(0)); // on n'ajoute que celui de gauche
			}
		}
		
	}
	*/
	/**
	 * Élaguer l'arbre. Commence par la racine
	 */
/*	public void pruneRacine(PenaltyWeightFunction f, ProbabilityDistributionLog p)
	{
		LinkedList<LexicographicMultipleTree> file = new LinkedList<LexicographicMultipleTree>();
		file.add(struct);
		double scoreSansPruning = computeScore(f, p);
		
		while(!file.isEmpty())
		{
			LexicographicMultipleTree s = file.removeFirst();
			ArrayList<LexicographicMultipleTree> enfants = s.getEnfants();
			if(s.split && enfants != null && enfants.size() > 0) // si enfants est nul (ou de taille nulle), c'est que s est une feuille et donc le pruning ne le concerne pas
			{
				LexicographicMultipleTree s2 = (LexicographicMultipleTree) s;
				s.split = false;
				s2.setEnfant(0, enfants.get(0));
				
				double scoreAvecPruning = computeScore(f, p);
//				System.out.println("Score avant : "+scoreSansPruning+", après : "+scoreAvecPruning);
				if(scoreAvecPruning > scoreSansPruning) // on a amélioré le score !
					scoreSansPruning = scoreAvecPruning;
				else // c'était mieux avec le split
				{
					s.split = true;
					for(int i = 0; i < enfants.size(); i++)
						s2.setEnfant(i, enfants.get(i));
				}
			}
			
			if(!enfants.isEmpty())
			{
				if(s.split)
					for(LexicographicMultipleTree l : enfants)
						file.add(l);
				else
					file.add(enfants.get(0)); // on n'ajoute que celui de gauche
			}
		}
		
	}
	*/
	/**
	 * Calcule le score de l'arbre sur les données précédemment apprises
	 * @param f
	 * @param p
	 * @return
	 */
	public double computeScore(PenaltyWeightFunction f, ProbabilityDistributionLog p, LexicographicMultipleTree struct, Instanciation[] instances)
	{
		BigDecimal LL = new BigDecimal(0);
		HashMap<String, String> map = new HashMap<String, String>();
		for(Instanciation i : instances)
		{
			ArrayList<String> var = i.getVarConditionees();
			for(String v : var)
				map.put(v, i.getValue(v));
			BigDecimal pr = p.logProbability(struct.infereRang(map));
			LL = LL.add(pr);
//			System.out.println(pr);
		}
//		System.out.println("LL : "+LL.doubleValue()+", phi : "+f.phi(allInstances.length)+", taille : "+struct.getNbNoeuds());
		return LL.doubleValue() - f.phi(instances.length) * struct.getNbNoeuds();
	}
	
	public double computeScoreWithMeanRank(PenaltyWeightFunction f, LexicographicMultipleTree struct, Instanciation[] instances)
	{
		BigDecimal empRank = new BigDecimal(struct.sommeRang(instances)).divide(new BigDecimal(struct.getRangMax()), 250, RoundingMode.HALF_EVEN);
//		BigInteger sumRank = struct.sommeRang(instances);
		return - empRank.doubleValue() - f.phi(instances.length) * struct.getNbNoeuds();
	}

}
