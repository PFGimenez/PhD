package compilateurHistorique;

import java.util.HashMap;
import java.util.List;
import java.util.Random;

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

/**
 * Classe qui s'occupe du voisinage
 * @author Pierre-François Gimenez
 *
 */

public class Neighborhood {

	private int[][] configurations;
	private int nbConf;
	private Variable[] vars;
	private HashMap<String, Integer> mapVar;
	private Random r = new Random();
	
	public int[] getEmptyConf()
	{
		int[] out = new int[vars.length];
		for(int i = 0; i < out.length; i++)
			out[i] = -1;
		return out;
	}
	/*
	public void compileHistorique(DatasetInfo dataset, ArrayList<String> filename, boolean entete)
	{
		vars = dataset.vars;
		mapVar = new HashMap<String, Integer>();
				
		nbConf = 0;
		
		for(String s : filename)
		{
			LecteurCdXml lect = new LecteurCdXml();
			lect.lectureCSV(s, entete);
			nbConf += lect.nbligne;
		}
		
		configurations = new int[nbConf][vars.length];
		
		for(int i = 0; i < vars.length; i++)
			mapVar.put(vars[i].name, i);
		
		int c = 0;
		for(String s : filename)
		{
			LecteurCdXml lect = new LecteurCdXml();
			lect.lectureCSV(s, entete);
			
			for(int i = 0; i < lect.nbligne; i++)
			{
				for(int k = 0; k < lect.nbvar; k++)
				{
					String var = lect.var[k];	
					configurations[c][mapVar.get(var)] = vars[mapVar.get(var)].values.indexOf(lect.domall[i][k]);
				}
				c++;
			}
		}
	}*/
	
	/**
	 * Apprend l'historique
	 * @param filename
	 * @param entete
	 */
	public void compileHistorique(DatasetInfo dataset, Instanciation[] allInstances)
	{
		vars = dataset.vars;
		mapVar = dataset.mapVar;
				
		nbConf = allInstances.length;
		
		configurations = new int[nbConf][vars.length];
		
		for(int i = 0; i < vars.length; i++)
			mapVar.put(vars[i].name, i);

		for(int i = 0; i < nbConf; i++)
			for(int k = 0; k < vars.length; k++)
			{
				String var = vars[k].name;
				configurations[i][k] = vars[k].values.indexOf(allInstances[i].getValue(var));
			}
	}
	
	/**
	 * Recommande une valeur par weighted majority voter
	 * @param conf
	 * @param var
	 * @return
	 */
	public String weightedMajorityVoter(int[] conf, String varString, int nbVoisins, List<String> possibles)
	{
		Variable var = vars[mapVar.get(varString)];
		int scoreMax = Integer.MIN_VALUE;
		int indiceMax = -1;
		int[] neighbors = getNeighborhood(conf, nbVoisins);
		
/*		for(int i = 0; i < nbVoisins; i++)
			System.out.print(configurations[neighbors[i]][mapVar.get(var.name)]+" ");
		System.out.println();*/
		
		for(int j = 0; j < var.domain; j++)
		{
			// valeur interdite
			if(possibles != null && !possibles.contains(var.values.get(j)))
				continue;
			
			int scoreTmp = 0;
			for(int i = 0; i < nbVoisins; i++)
			{
				if(configurations[neighbors[i]][mapVar.get(var.name)] == j)
					scoreTmp += valeursCommunes(conf, configurations[neighbors[i]]);
			}
			if(scoreTmp > scoreMax)
			{
				scoreMax = scoreTmp;
				indiceMax = j;
			}
//			System.out.println(j+" "+scoreTmp);
		}
		
		if(indiceMax == -1) // toutes les valeurs connues sont impossibles
		{
			assert possibles != null;
			return possibles.get(r.nextInt(possibles.size()));
		}
		
		return var.values.get(indiceMax);
	}
	
	/**
	 * Renvoie un tableau d'indices, qui contient les "nbVoisins" plus proches voisins de la configuration "indice"
	 * @param indice
	 * @param nbVoisin
	 * @return
	 */
	public int[] getNeighborhood(int[] conf, int nbVoisins)
	{
		int[] indices = new int[nbVoisins];
		int[] distances = new int[nbVoisins];

		for(int i = 0; i < nbVoisins; i++)
			distances[i] = -1;
		
		for(int i = 0; i < nbConf; i++)
		{
			int commun = valeursCommunes(conf, configurations[i]);

			int plusPetit = distances[0];
			int plusPetitIndice = 0;
			
			for(int j = 1; j < nbVoisins; j++)
			{
				if(plusPetit > distances[j])
				{
					plusPetit = distances[j];
					plusPetitIndice = j;
				}
			}
			
			if(commun > plusPetit)
			{
				distances[plusPetitIndice] = commun;
				indices[plusPetitIndice] = i;
			}
		}
		/*
		for(int i = 0; i < nbVoisins; i++)
			System.out.print(distances[i]+" ");
		System.out.println();*/
		/*
		for(int k = 0; k < nbVoisins; k++)
			System.out.print(indices[k]+" ");
		System.out.println();*/

		return indices;
	}
	
	/**
	 * Renvoie le nombre de valeurs communes entre conf1 et conf2
	 * @param conf1
	 * @param conf2﻿
	 * @return
	 */
	public int valeursCommunes(int[] conf1, int[] conf2)
	{
		int out = 0;
		for(int i = 0; i < conf1.length; i++)
			if(conf1[i] != -1 && conf1[i] == conf2[i])
				out++;
//		System.out.println(out);
		return out;
	}

	public void set(int[] conf, String variable, String solution)
	{
		conf[mapVar.get(variable)] = vars[mapVar.get(variable)].values.indexOf(solution);
	}

	public void unset(int[] conf, String variable)
	{
		conf[mapVar.get(variable)] = -1;
	}

	public String naiveBayesVoter(int[] conf, String varString, int nbVoisins, List<String> possibles)
	{
		Variable var = vars[mapVar.get(varString)];
		double scoreMax = Integer.MIN_VALUE;
		int indiceMax = -1;
		int[] neighbors = getNeighborhood(conf, nbVoisins);

		for(int i = 0; i < var.domain; i++)
		{
			if(possibles != null && !possibles.contains(var.values.get(i)))
				continue;

			double scoreTmp = 0;
			for(int j = 0; j < nbVoisins; j++)
				if(configurations[neighbors[j]][mapVar.get(var.name)] == i)
					scoreTmp++;
			
			if(scoreTmp != 0)
			{			
				scoreTmp /= nbVoisins;
				
				for(int j = 0; j < vars.length; j++)
				{
					if(conf[j] == -1)
						continue;
					
					double num = 0, denum = 0;
					for(int l = 0; l < nbVoisins; l++)
						if(configurations[neighbors[l]][mapVar.get(var.name)] == i)
						{
							denum++;
							if(configurations[neighbors[l]][j] == conf[j])
								num++;
						}
					
					scoreTmp *= (num + 1) / (denum + nbVoisins);
				}
			}
			if(scoreTmp > scoreMax)
			{
				scoreMax = scoreTmp;
				indiceMax = i;
			}
		}
		
		if(indiceMax == -1) // toutes les valeurs connues sont impossibles
		{
			assert possibles != null;
			return possibles.get(r.nextInt(possibles.size()));
		}
		
		return var.values.get(indiceMax);
	}

	public String mostPopularChoice(int[] conf, String varString, int nbVoisins, List<String> possibles)
	{
		Variable var = vars[mapVar.get(varString)];
		double scoreMax = 0;
		int indiceMax = 0;
		int[] neighbors = getNeighborhood(conf, nbVoisins);
		/*
		for(int i = 0; i < nbVoisins; i++)
		{
			for(int j = 0; j < vars.length; j++)
				System.out.print(configurations[neighbors[i]][j]+" ");
			System.out.println("("+neighbors[i]+")");
		}
		*/
		for(int i = 0; i < nbVoisins; i++)
		{
			double scoreTmp = 1;
			for(int j = 0; j < vars.length; j++)
			{
				if(conf[j] != -1)
				{
//					System.out.println("Non à "+j+" : "+conf[j]);
					continue;
				}
				int val = configurations[neighbors[i]][j];
				double base = 0;
				for(int k = 0; k < nbVoisins; k++)
					if(configurations[neighbors[k]][j] == val)
						base++;
				scoreTmp *= base / nbVoisins;
//				System.out.println("Base pour "+j+" : "+base / nbVoisins);
			}

//			System.out.println("P : "+scoreTmp);
			
			// calcul du dénominateur
			int denom = nbVoisins;
			for(int l = 0; l < nbVoisins; l++)
			{
				boolean idem = true;

				for(int j = 0; j < vars.length; j++)
				{
					if(conf[j] != -1)
						continue;
					
					if(configurations[neighbors[i]][j] != configurations[neighbors[l]][j])
					{
						idem = false;
						break;
					}
				}
				if(idem)
					denom++;
			}
			
			// calcul du numérateur
			for(int u = 0; u < vars.length; u++)
			{
				if(conf[u] == -1)
					continue;

				if(configurations[neighbors[i]][u] != conf[u]) // le numérateur vaut 1
				{
					scoreTmp *= 1 / denom;
					continue;
				}
				
				double num = 1;
				for(int l = 0; l < nbVoisins; l++)
				{
					boolean idem = true;

					for(int j = 0; j < vars.length; j++)
					{
						if(conf[j] != -1)
							continue;
						
						if(configurations[neighbors[i]][j] != configurations[neighbors[l]][j])
						{
							idem = false;
							break;
						}
					}
					if(idem)
						num++;
				}
				scoreTmp *= num / denom;
			}
			
			if(scoreTmp > scoreMax)
			{
				scoreMax = scoreTmp;
				indiceMax = i;
			}
//			System.out.println("Résultats pour voisin : "+i+" "+scoreTmp);
		}
		String val = var.values.get(configurations[neighbors[indiceMax]][mapVar.get(var.name)]);
		// valeur interdite : on en choisit une autre au hasard
		if(possibles != null && !possibles.contains(val))
			return possibles.get(r.nextInt(possibles.size()));
		return val;
	}
}
