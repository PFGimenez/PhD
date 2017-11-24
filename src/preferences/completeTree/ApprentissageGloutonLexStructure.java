package preferences.completeTree;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import compilateurHistorique.Instanciation;
import compilateurHistorique.DatasetInfo;
import compilateurHistorique.HistoriqueCompile;
import preferences.heuristiques.HeuristiqueComplexe;
import preferences.heuristiques.VieilleHeuristique;

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
 * Apprentissage d'une structure basée sur l'ordre lexicographique
 * @author Pierre-François Gimenez
 *
 */

public abstract class ApprentissageGloutonLexStructure
{
	protected int nbVar;
	protected BigInteger base;
	protected ArrayList<String> variables;
	protected LexicographicStructure struct;
	protected HistoriqueCompile historique;
	protected HeuristiqueComplexe h;
	protected List<Instanciation> allInstances;
	protected DatasetInfo dataset;
	
	
//	public abstract LexicographicStructure apprendDonnees(ArrayList<String> filename, boolean entete);

	public LexicographicStructure apprendDonnees(DatasetInfo dataset, List<Instanciation> instances)
	{
		this.allInstances = instances;
		// Contraintes contient des variables supplémentaire
//		LecteurCdXml lect = new LecteurCdXml();
//		lect.lectureCSV(filename.get(0), entete);
		this.dataset = dataset;
		variables = new ArrayList<String>();
		variables.addAll(dataset.mapVar.keySet());
		nbVar = variables.size();

		historique = new HistoriqueCompile(dataset);
		historique.compile(instances);
		assert historique.getNbInstancesTotal() > 0;
		
		base = BigInteger.ONE;
		for(String var : variables)
			base = base.multiply(BigInteger.valueOf((dataset.vars[dataset.mapVar.get(var)].domain)));
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

	protected LexicographicOrder apprendOrdre(Instanciation instance, ArrayList<String> variablesRestantes)
	{
		ArrayList<String> variables = new ArrayList<String>();
		variables.addAll(variablesRestantes);
		int nbVar = variables.size();
		LexicographicOrder[] all = new LexicographicOrder[nbVar];

		assert historique.getNbInstances(instance) > 0;

		for(int i = 0; i < nbVar; i++)
		{
			String best = h.getRacine(dataset, historique, variables, instance);
			variables.remove(best);
			all[i] = new LexicographicOrder(best, dataset.vars[dataset.mapVar.get(best)].domain, i+1);
			all[i].setOrdrePref(historique.getNbInstancesToutesModalitees(best, true, instance));
		}

		LexicographicOrder struct = null;
		for(int i = nbVar - 1; i >= 0; i--)
		{
			all[i].setEnfant(struct);
			struct = all[i];
		}

		return struct;
	}

	public void affiche(String s)
	{
		struct.affiche(s);
	}

	public void save(String filename)
	{
		struct.save(filename);
	}

	public boolean load(String filename)
	{
		struct = LexicographicStructure.load(filename);
		if(struct == null)
			System.out.println("Le chargement a échoué");
		else
			System.out.println("Lecture de "+filename);
		return struct != null;
	}
	
	@Override
	public String toString()
	{
		return getClass().getSimpleName();
	}

	public String getHeuristiqueName()
	{
		if(h instanceof VieilleHeuristique)
			return ((VieilleHeuristique)h).h.getClass().getSimpleName().substring(11);
		return h.getClass().getSimpleName().substring(11);
	}

}
