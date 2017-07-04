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

import java.util.ArrayList;
import evaluation.ValidationCroisee;
import recommandation.*;

/**
 * Protocole d'évaluation
 * @author Pierre-François Gimenez
 *
 */

public class Recom2 {
	
	public static void main(String[] args)
	{
		String fichierContraintes = null; 
		
		if(args.length < 3)
		{
			System.out.println("Usage : Recom2 algo dataset nbPlis [-e] [-c contraintes.xml] [-rb prefix] [-s nb scenarios]");
			return;
		}
		
		String prefixData = args[1]+"/";
		boolean entete = false;
		int nbScenario = 1;
		String prefixRB = prefixData;

		for(int i = 3; i < args.length; i++)
		{
			if(args[i].equals("-e"))
				entete = true;
			else if(args[i].equals("-c"))
				fichierContraintes = args[++i];
			else if(args[i].equals("-rb"))
				prefixRB = args[++i]+"/";
			else if(args[i].equals("-s"))
				nbScenario = Integer.parseInt(args[++i]);
		}

		boolean verbose = true;
		boolean debug = false;
		int nbPlis = Integer.parseInt(args[2]);
		ValidationCroisee val = new ValidationCroisee(verbose, debug, entete, prefixData+"set"+0+"_exemples");

		ArrayList<String> fichiersPlis = new ArrayList<String>();
		String[] rb = new String[nbPlis];
		
		AlgoReco recommandeur = AlgoParser.getDefaultRecommander(args[0]);
		
		if(nbPlis != 1)
			for(int j = 0; j < nbPlis; j++)
				fichiersPlis.add(prefixData+"set"+j+"_exemples");

		if(nbPlis == 1)
			rb[0] = prefixRB+"BN.xml";
		else
			for(int j = 0; j < nbPlis; j++)
				rb[j] = prefixRB+"BN_"+j+".xml";
		
		val.run(recommandeur, prefixData, args[0].toLowerCase().equals("oracle"), nbPlis, fichiersPlis, fichierContraintes, rb, nbScenario);
		
	}

}

