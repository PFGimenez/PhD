package recommandation.methode_oubli;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import compilateur.SALADD;
import compilateur.VDD;
import compilateur.Var;
import compilateur.test_independance.TestIndependance;

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
 * Méthode d'oubli par d-séparation et décomposition
 * @author pgimenez
 *
 */

public class OubliParDSeparationDecomposition extends MethodeDSeparation {

	private ArrayList<String> variablesDSepareesDeV = new ArrayList<String>();
	private ArrayList<ArrayList<String>> clusters = new ArrayList<ArrayList<String>>();
	protected ArrayList<Var> dejavucluster = new ArrayList<Var>();
	protected ArrayList<String> dejavuclusterVal = new ArrayList<String>();

	public OubliParDSeparationDecomposition(int seuil, TestIndependance test, String prefixData)
	{
		super(seuil, test, prefixData);
	}
	
	@Override
	public Map<String, Double> recommandation(Var v, HashMap<String, String> historiqueOperations, VDD vdd, ArrayList<String> possibles, SALADD contraintes)
	{
//		System.out.println("Recherche de recommandation pour "+v.name);
		
		Map<String, Double> m = null, tmp;
		done.clear();
		variablesDSepareesDeV.clear();
		clusters.clear();
		nbOubli = 0;
		dejavu.clear();
		dejavuVal.clear();
		ArrayList<String> connues = new ArrayList<String>();
		
		// Calcul de la proba de v a priori
		for(String s: historiqueOperations.keySet())
    		vdd.deconditioner(vdd.getVar(s));

		m = vdd.countingpondereOnPossibleDomain(v, possibles);
		
		System.out.println("Proba a priori de "+v.name);
		for(String valeur: m.keySet())
			System.out.println(valeur+" : "+m.get(valeur));
		
		for(String s: historiqueOperations.keySet())
			vdd.conditioner(vdd.getVar(s), vdd.getVar(s).conv(historiqueOperations.get(s)));

		// Construction de l'ensemble des variables connues
		for(String s: historiqueOperations.keySet())
		{
			System.out.println(vdd.getVar(s).name+" est connu");
			connues.add(vdd.getVar(s).name);
		}

		// D-séparation initiale
		rechercheEnProfondeur(connues, v.name, false, contraintes);
		
		// On sauvegarde les variables dont dépend v
		for(String var : connues)
			if(!done.contains(var))
				variablesDSepareesDeV.add(var);
		connues.removeAll(variablesDSepareesDeV);
		System.out.println("Nombre de variables connues: "+historiqueOperations.size());
		System.out.println("Nombre de variables oubliables directement: "+variablesDSepareesDeV.size());
		System.out.println("Nombre de variables restantes: "+connues.size());
		
		// Regroupement des variables qui ne sont pas indépendantes entre elles sachant v
		connues.add(v.name);
		for(String var : connues)
		{
			if(var.equals(v.name))
				continue;
			
			System.out.println("Recherche de cluster pour "+var);
			// On vérifie que cette variable n'a pas déjà été catégorisée
			boolean varDejaVu = false;
			for(ArrayList<String> liste : clusters)
				if(liste.contains(var))
				{
					varDejaVu = true;
					break;
				}
			if(varDejaVu)
			{
				System.out.println(var+" déjà trouvée");
				continue;
			}

			done.clear();
			rechercheEnProfondeur(connues, var, false, contraintes);
			ArrayList<String> cluster = new ArrayList<String>();
			for(String varDone : done)
				if(!varDone.equals(v) && connues.contains(varDone))
				{
					cluster.add(varDone);
					System.out.println("Ajout de "+varDone +" dans le cluster");
				}
			clusters.add(cluster); // done contient le cluster, qu'on sauvegarde
		}
		
		System.out.println("Nb clusters : "+clusters.size());
		int nb = 0;
		for(ArrayList<String> c : clusters)
			nb += c.size();
		System.out.println(nb+" = "+(connues.size()-1));
		
		// On déconditionne toutes les variables qui étaient de toute façon d-séparées
		for(String s: historiqueOperations.keySet())
		{
			Var connue = vdd.getVar(s);
			if(!variablesDSepareesDeV.contains(connue.name))
			{
	    		dejavu.add(connue);
	    		dejavuVal.add(historiqueOperations.get(s));
	    		vdd.deconditioner(connue);
	    		nbOubli++;
			}
		}
		
		// On applique la formule en mettant m à la puissance 1-n (n étant le nombre de clusters)
		if(clusters.size() != 1)
		{
			for(String valeur: m.keySet())
				m.put(valeur, Math.pow(m.get(valeur), 1-clusters.size()));
		}
		
		int k = 0;
		// On déconditionne par cluster
		for(ArrayList<String> cluster : clusters)
		{
			dejavucluster.clear();
			dejavuclusterVal.clear();
			// si la variable n'est pas dans le cluster, on l'oublie
			for(String s: historiqueOperations.keySet())
			{
				Var connue = vdd.getVar(s);
				if(!cluster.contains(connue.name))
				{
		    		dejavucluster.add(connue);
		    		dejavuclusterVal.add(historiqueOperations.get(s));
		    		vdd.deconditioner(connue);
				}
			}
			
			// Si cet oubli ne suffit pas, on fait de l'oubli classique, par indépendance
			super.restaureSeuil(historiqueOperations, possibles.size(), vdd, v);
			
			// On fait le produit des proba
			System.out.println("P(v | U_"+k+") : ");
			k++;
			tmp = vdd.countingpondereOnPossibleDomain(v, possibles);
			for(String valeur: tmp.keySet())
				System.out.println(valeur+" : "+tmp.get(valeur));
				
			for(String valeur: tmp.keySet())
				m.put(valeur, tmp.get(valeur)*m.get(valeur));
				
			for(int i = 0; i < dejavucluster.size(); i++)
		    	vdd.conditioner(dejavucluster.get(i), dejavucluster.get(i).conv(dejavuclusterVal.get(i)));
		}
		
		// m n'est pas normalisé
    	return m;
	}
	
	public String toString()
	{
		return getClass().getSimpleName() + "-"+seuil;
	}

	
}

