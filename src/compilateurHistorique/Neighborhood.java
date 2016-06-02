package compilateurHistorique;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import compilateur.LecteurCdXml;

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
 * @author pgimenez
 *
 */

public class Neighborhood {

	private int[][] configurations;
	private int nbConf;
	private Variable[] vars;
	private HashMap<String, Integer> mapVar;
	
	public int[] getEmptyConf()
	{
		int[] out = new int[vars.length];
		for(int i = 0; i < out.length; i++)
			out[i] = -1;
		return out;
	}
	
	/**
	 * Apprend l'historique. On suppose que initVariables a déjà été appelé
	 * @param filename
	 * @param entete
	 */
	public void compileHistorique(ArrayList<String> filename, boolean entete)
	{
//		vars = initVariables(filename, entete);
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
	}
	
	/**
	 * Recommande une valeur par weighted majority voter
	 * @param conf
	 * @param var
	 * @return
	 */
	public String weightedMajorityVoter(int[] conf, String varString, int nbVoisins)
	{
		Variable var = vars[mapVar.get(varString)];
		int scoreMax = Integer.MAX_VALUE;
		int indiceMax = 0;
		int[] neighbors = getNeighborhood(conf, nbVoisins);
		
		for(int j = 0; j < var.domain; j++)
		{
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
			distances[i] = 0;
		
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
	 * @param conf2I already have. You're not wrong﻿
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
	
	/**
	 * Initialise les valeurs et les domaines des variables.
	 * IL N'Y A PAS D'APPRENTISSAGE SUR LES VALEURS
	 * @param filename
	 * @param entete
	 * @return
	 */
	public void initVariables(ArrayList<String> filename, boolean entete)
	{
		// Vérification de toutes les valeurs possibles pour les variables
		vars = null;
		LecteurCdXml lect = null;
		
		int nbvar = 0;
		
		int[] conversion = null;
		for(String s : filename)
		{
			lect = new LecteurCdXml();
			lect.lectureCSV(s, entete);

			if(vars == null)
			{
				nbvar = lect.nbvar;
				vars = new Variable[nbvar];
				conversion = new int[lect.nbvar];
				int j = 0;
				for(int i = 0; i < lect.nbvar; i++)
				{
					conversion[i] = j;
					vars[j] = new Variable();
					vars[j].name = lect.var[i];
					vars[j].domain = 0;
					j++;
				}
			}

			for(int i = 0; i < lect.nbligne; i++)
			{
				for(int k = 0; k < lect.nbvar; k++)
				{
					if(conversion[k] == -1)
						continue;
					String value = lect.domall[i][k];
					if(!vars[conversion[k]].values.contains(value))
					{
						vars[conversion[k]].values.add(value);
						vars[conversion[k]].domain++;
					}
				}
			}
		}

//		return vars;
	}

	public void set(int[] conf, String variable, String solution)
	{
		conf[mapVar.get(variable)] = vars[mapVar.get(variable)].values.indexOf(solution);
	}
}
