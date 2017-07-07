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
 * Protocole d'évaluation de DRC sur des datasets contraints
 * @author Pierre-François Gimenez
 *
 */

public class ConstrainedRecom {
	
	// PARAM 1 : algo
	
	public static void main(String[] args)
	{
		if(args.length < 4)
		{
			System.out.println("Usage : ConstrainedRecom algo dataset nbExpe nbPlis [-c nbCSP]");
			System.out.println("nbExpe = 0 (pas de contraintes), 1 (dureté = 0.05) ou 2 (dureté = 0.1)");
			return;
		}
		
		boolean verbose = true;
		boolean debug = false;

//		String dataset = "insurance2_contraintes";
//		int nbDataset = 3;
		int nbCSP = 1;
		
		for(int i = 4; i < args.length; i++)
		{
			if(args[i].equals("-c"))
				nbCSP = Integer.parseInt(args[++i]);
			if(args[i].equals("-d"))
				debug = true;
		}
		
		int nbPlis = Integer.parseInt(args[3]);
		int i = Integer.parseInt(args[2]);
		String prefixData = args[1]+"/";
		ValidationCroisee val = new ValidationCroisee(verbose, debug, true, prefixData+"csp0_0_set0_exemples");

		ArrayList<String> fichiersPlis = new ArrayList<String>();
		String[] rb = new String[nbPlis];
		
		AlgoReco recommandeur = AlgoParser.getDefaultRecommander(args[0]);
		
		System.out.println("TIGHTNESS "+(i*0.15));

		for(int c = 0; c < nbCSP; c++)
		{
			fichiersPlis.clear();
			for(int j = 0; j < nbPlis; j++)
				fichiersPlis.add(prefixData+"csp"+i+"_"+c+"_set"+j+"_exemples");
	
			for(int j = 0; j < nbPlis; j++)
				rb[j] = prefixData+"BN_csp"+i+"_"+c+"_"+j+".xml";
			
			val.run(recommandeur, prefixData, recommandeur instanceof AlgoOubliRien, nbPlis, fichiersPlis, null, prefixData+"randomCSP-"+i+"_"+c+".xml", rb, 1);
		}
	}

}

