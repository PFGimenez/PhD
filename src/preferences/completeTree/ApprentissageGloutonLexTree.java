package preferences.completeTree;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedList;

import compilateurHistorique.Instanciation;
import preferences.ProbabilityDistributionLog;
import preferences.heuristiques.HeuristiqueComplexe;
import preferences.penalty.PenaltyWeightFunction;

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
 * Apprentissage d'un arbre lexicographique incomplet
 * @author Pierre-François Gimenez
 *
 */

public class ApprentissageGloutonLexTree extends ApprentissageGloutonLexStructure
{
	private int profondeurMax;
	private int seuil;

	public ApprentissageGloutonLexTree(int profondeurMax, int seuil, HeuristiqueComplexe h)
	{
		this.h = h;
		this.profondeurMax = profondeurMax;
		this.seuil = seuil;
	}
	
	private LexicographicStructure apprendRecursif(Instanciation instance, ArrayList<String> variablesRestantes, boolean preferred)
	{
		ArrayList<String> variablesTmp = new ArrayList<String>();
		variablesTmp.addAll(variablesRestantes);
	
		String var = h.getRacine(historique, variablesTmp, instance);
		
		int pasAssez = 0;
		
		// si on a dépassé la profondeur max
		if(variablesTmp.size() < variables.size() - profondeurMax)
			return apprendOrdre(instance, variablesTmp);
		
		// pas du tout assez d'exemples
		if(historique.getNbInstances(instance) < seuil)
		{
//			System.out.println("Pas assez du tout !");
			return apprendOrdre(instance, variablesTmp);
		}
		
		/**
		 * Peut-on avoir un split sans risquer de finir avec trop peu d'exemples dans une branche ?
		 */
		for(String val : historique.getValues(var))
		{
			instance.conditionne(var, val);
			int nbInstances = historique.getNbInstances(instance);
			instance.deconditionne(var);
			
			if(nbInstances < seuil) // split impossible !
				pasAssez++;
		}

		/**
		 * Split
		 */
		LexicographicTree best = new LexicographicTree(var, historique.nbModalites(var), pasAssez == 0);
		best.setOrdrePref(historique.getNbInstancesToutesModalitees(var, null, true, instance));

		// Si c'était la dernière variable, alors c'est une feuille
		if(variablesTmp.size() == 1)
			return best;
		
		variablesTmp.remove(best.getVar());
		int nbMod = best.getNbMod();
		
		if(pasAssez == 0)
		{
			for(int i = 0; i < nbMod; i++)
			{
				// On conditionne par une certaine valeur
				instance.conditionne(best.getVar(), best.getPref(i));			
				best.setEnfant(i, apprendRecursif(instance, variablesTmp, i == 0));
				instance.deconditionne(best.getVar());
			}
		}
		else
		{
			// Pas de split. On apprend un seul enfant qu'on associe à toutes les branches sortantes.
			LexicographicStructure enfant = apprendRecursif(instance, variablesTmp, true);
//			for(int i = 0; i < nbMod; i++)
			best.setEnfant(0, enfant);
		}
		// A la fin, le VDD est conditionné de la même manière qu'à l'appel
		return best;
	}
	
	public LexicographicStructure apprendDonnees(ArrayList<String> filename, boolean entete)
	{
		return apprendDonnees(filename, entete, -1);
	}
	
	public LexicographicStructure apprendDonnees(ArrayList<String> filename, boolean entete, int nbExemplesMax)
	{
		super.apprendDonnees(filename, entete, nbExemplesMax);
		ArrayList<String> variablesTmp = new ArrayList<String>();
		variablesTmp.addAll(variables);
		struct = apprendRecursif(new Instanciation(), variables, true);
//		System.out.println("Apprentissage fini");
		struct.updateBase(base);
		return struct;
	}
	
	@Override
	public String toString()
	{
		return super.toString()+"-"+profondeurMax;
	}

	/**
	 * Élaguer l'arbre. Commence par la racine
	 */
	public void prune(PenaltyWeightFunction f, ProbabilityDistributionLog p)
	{
		LinkedList<LexicographicStructure> file = new LinkedList<LexicographicStructure>();
		file.add(struct);
		double scoreSansPruning = computeScore(f, p);
		
		while(!file.isEmpty())
		{
			LexicographicStructure s = file.removeFirst();
			ArrayList<LexicographicStructure> enfants = s.getEnfants();
			if(s.split && enfants != null && enfants.size() > 0) // si enfants est nul (ou de taille nulle), c'est que s est une feuille et donc le pruning ne le concerne pas
			{
				LexicographicTree s2 = (LexicographicTree) s;
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
					for(LexicographicStructure l : enfants)
						file.add(l);
				else
					file.add(enfants.get(0)); // on n'ajoute que celui de gauche
			}
		}
		
	}
	
	/**
	 * Calcule le score de l'arbre sur les données précédemment apprises
	 * @param f
	 * @param p
	 * @return
	 */
	public double computeScore(PenaltyWeightFunction f, ProbabilityDistributionLog p)
	{
		BigDecimal LL = new BigDecimal(0);
		for(Instanciation i : allInstances)
		{
			ArrayList<String> val = new ArrayList<String>();
			ArrayList<String> var = i.getVarConditionees();
			for(String v : var)
				val.add(i.getValue(v));
			LL = LL.add(p.logProbability(struct.infereRang(val, var)));
		}
//		System.out.println("LL : "+LL.doubleValue()+", phi : "+f.phi(allInstances.length)+", nb noeud : "+struct.getNbNoeuds());
		return LL.doubleValue() - f.phi(allInstances.length) * struct.getNbNoeuds();
	}
}
