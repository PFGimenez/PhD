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
		
//		System.out.println("Proba a priori de "+v.name);
//		for(String valeur: m.keySet())
//			System.out.println(valeur+" : "+m.get(valeur));
		
//		for(String s: historiqueOperations.keySet())
//			vdd.conditioner(vdd.getVar(s), vdd.getVar(s).conv(historiqueOperations.get(s)));

		// Construction de l'ensemble des variables connues
		for(String s: historiqueOperations.keySet())
		{
//			System.out.println(vdd.getVar(s).name+" est connu");
			connues.add(vdd.getVar(s).name);
		}

		// D-séparation initiale
		rechercheEnProfondeur(connues, v.name, false, contraintes);
		
		// On sauvegarde les variables dont dépend v
		for(String var : connues)
			if(!done.contains(var))
				variablesDSepareesDeV.add(var);
		connues.removeAll(variablesDSepareesDeV);
		
//		System.out.println("Nombre de variables connues: "+historiqueOperations.size());
//		System.out.println("Nombre de variables oubliables directement: "+variablesDSepareesDeV.size());
//		System.out.println("Nombre de variables restantes: "+connues.size());
		
		// Regroupement des variables qui ne sont pas indépendantes entre elles sachant v
		connues.add(v.name);
		ArrayList<String> connuesSauv = new ArrayList<String>(connues);
		for(String var : connues)
		{
			if(var.equals(v.name))
				continue;
			
//			System.out.println("Recherche de cluster pour "+var);
			// On vérifie que cette variable n'a pas déjà été catégorisée
			boolean varDejaVu = false;
//			int k = 0;
			for(ArrayList<String> liste : clusters)
				if(liste.contains(var))
				{
//					System.out.println(var+" déjà trouvée dans le cluster "+k);
					varDejaVu = true;
//					k++;
					break;
				}
			if(varDejaVu)
			{
//				System.out.println(var+" déjà trouvée");
				continue;
			}

			done.clear();
			connuesSauv.remove(var);
			rechercheEnProfondeur(connuesSauv, var, false, contraintes);
			connuesSauv.add(var);
			ArrayList<String> cluster = new ArrayList<String>();
			for(String varDone : done)
				if(!varDone.equals(v.name) && connues.contains(varDone))
				{
					cluster.add(varDone);
//					System.out.println("Ajout de "+varDone +" dans le cluster");
				}
			clusters.add(cluster); // done contient le cluster, qu'on sauvegarde
		}
		
//		int nb = 0;
		int nbmax = 0;
		for(ArrayList<String> c : clusters)
		{
			if(c.size() > nbmax)
				nbmax = c.size();
//			nb += c.size();
		}
		System.out.println("Nb clusters : "+clusters.size()+". Taille max :"+nbmax);
//		System.out.println(nb+" = "+(connues.size()-1));
	
		
		// On applique la formule en mettant m à la puissance 1-n (n étant le nombre de clusters)
		if(clusters.size() != 1)
		{
			for(String valeur: m.keySet())
				m.put(valeur, Math.pow(m.get(valeur), 1-clusters.size()));
		}
		
//		int k = 0;
		// Pour chaque cluster, on ne va garder que les variables de ce cluster
		for(ArrayList<String> cluster : clusters)
		{
			// On reconditionne toutes les variables du cluster
			for(String s: historiqueOperations.keySet())
			{
				Var connue = vdd.getVar(s);
				if(cluster.contains(connue.name))
					vdd.conditioner(connue, connue.conv(historiqueOperations.get(s)));
			}
						
			if(vdd.countingpondere() < seuil * (possibles.size() - 1))
			{
				System.out.println("Clusturisation nécessaire!");
				
				// On déconditionne avant de faire l'appel
				for(String s: historiqueOperations.keySet())
				{
					Var connue = vdd.getVar(s);
					if(cluster.contains(connue.name))
						vdd.deconditioner(connue);
				}

				tmp = clusturize(cluster, vdd, connues, v, possibles, contraintes, historiqueOperations);
			}
			else
				tmp = vdd.countingpondereOnPossibleDomain(v, possibles);

			// On fait le produit des proba
//			System.out.println("P(v | U_"+k+") : ");
//			k++;
//			for(String valeur: tmp.keySet())
//				System.out.println(valeur+" : "+tmp.get(valeur));
				
			for(String valeur: tmp.keySet())
				m.put(valeur, tmp.get(valeur)*m.get(valeur));
				
		}
		
		// Si le plus grand cluster est trop grand, on le découpe
/*		while(true)
		{
			nbmax = 0;
			for(ArrayList<String> c : clusters)
			{
				if(c.size() > nbmax)
					nbmax = c.size();
			}
			System.out.println("Il y a "+clusters.size()+" clusters, taille max : "+nbmax);
			
			// Le plus grand cluster est plus petit que le seuil fixé? On arrête
			if(nbmax <= seuilTailleCluster)
				break;
		}*/
		// m n'est pas normalisé
    	return m;
	}
	
	public String toString()
	{
		return getClass().getSimpleName() + "-"+seuil;
	}

	/**
	 * Découpe un cluster en d'autres clusters
	 * Modifie clusters en ajoutant les nouveaux clusters
	 * A cet appel, les variables du cluster sont conditionnées
	 * Renvoie des probas
	 */
	private Map<String, Double> clusturize(ArrayList<String> cluster, VDD vdd, ArrayList<String> connues, Var v, ArrayList<String> possibles, SALADD contraintes, HashMap<String, String> historiqueOperations)
	{
		ArrayList<ArrayList<String>> clustersTmp = new ArrayList<ArrayList<String>>();
		ArrayList<ArrayList<String>> clustersMax = new ArrayList<ArrayList<String>>();
		Var bmax = null;
		int tailleMax = cluster.size();
		
		System.out.println("Taille initiale : "+tailleMax);
		
		// Le cluster est trop gros, on n'a pas assez d'exemples. Il faut le découper
		// b va servir à découper le cluster
//		int k = 0;
		for(Var b : vdd.variables)
		{
			// Il faut que B ne soit pas connu et que A != B
			if(connues.contains(b.name))
				continue;
			connues.add(b.name);
			
//			System.out.println("b = "+b.name);
			clustersTmp.clear();
			for(String var : cluster)
			{
				// On vérifie que cette variable n'a pas déjà été catégorisée
				boolean varDejaVu = false;

				for(ArrayList<String> liste : clustersTmp)
					if(liste.contains(var))
					{
//						System.out.println(var+" déjà trouvée dans le cluster "+k);
						varDejaVu = true;
//						k++;
						break;
					}
				if(varDejaVu)
				{
	//				System.out.println(var+" déjà trouvée");
					continue;
				}
	
				done.clear();
				connues.remove(var);
				rechercheEnProfondeur(connues, var, false, contraintes);
				connues.add(var);
				ArrayList<String> clusterTmp = new ArrayList<String>();
				
//				System.out.println("Variable recherchée : "+var);
				
//				for(String varDone : done)
//					System.out.println(varDone);
				
				for(String varDone : done)
					if(!varDone.equals(v.name) && !varDone.equals(b.name) && connues.contains(varDone))
					{
						clusterTmp.add(varDone);
//						System.out.println("Ajout de "+varDone +" dans le cluster");
					}
				
				clustersTmp.add(clusterTmp); // done contient le cluster, qu'on sauvegarde
			}
			connues.remove(b.name);

			int tailleMaxTmp = 0;
			for(ArrayList<String> c : clustersTmp)
				if(c.size() > tailleMaxTmp)
					tailleMaxTmp = c.size();
						
			// On cherche à minimiser la taille max
			if(tailleMax > tailleMaxTmp)
			{
				tailleMax = tailleMaxTmp;
				clustersMax.clear();
				clustersMax.addAll(clustersTmp);
				bmax = b;
			}
		}
		
		System.out.println("Avec b="+bmax.name+", taille max ="+tailleMax);

		// Maintenant qu'on a trouvé la meilleure partition possible, on calcule la proba

		ArrayList<String> bvalues = new ArrayList<String>(contraintes.getCurrentDomainOf(bmax.name));
		
		Map<String, Double> out = new HashMap<String, Double>();
		Map<String, Double> tmpb = new HashMap<String, Double>();
		Map<String, Double> tmp;
		
		// Initialisation
		for(String value : possibles)
			out.put(value, 1.);
		
		for(String bvalue : bvalues)
		{
			// On calcule P(B)
			vdd.conditioner(bmax, bmax.conv(bvalue));
			
			tmp = vdd.countingpondereOnPossibleDomain(v, possibles);
			for(String value : possibles)
				tmpb.put(value, Math.pow(tmp.get(value), 1-clustersMax.size()));
			
			// On calcule les P(B, Ui)
			for(ArrayList<String> c : clustersMax)
			{
				for(String s: historiqueOperations.keySet())
				{
					Var connue = vdd.getVar(s);
					if(c.contains(connue.name))
						vdd.conditioner(connue, connue.conv(historiqueOperations.get(s)));
				}
				
				// Si cet oubli ne suffit pas, on fait de l'oubli classique, par indépendance
//				super.restaureSeuil(historiqueOperations, possibles.size(), vdd, v);

				tmp = vdd.countingpondereOnPossibleDomain(v, possibles);
				for(String value : possibles)
					tmpb.put(value, tmp.get(value)*tmpb.get(value));
			}
			
			

			for(String value : possibles)
				out.put(value, tmpb.get(value) + out.get(value));
			
		}
		
		return out;
	}
	
}

