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
			System.out.println("Usage : ConstrainedRecom algo experiment nbExpe nbPlis");
			return;
		}
		
		String dataset = "insurance2_contraintes";
//		int nbDataset = 3;

		boolean verbose = true;
		boolean debug = false;
		int nbPlis = Integer.parseInt(args[3]);
		ValidationCroisee val = new ValidationCroisee(null, verbose, debug);
		int i = Integer.parseInt(args[2]);
		String prefixData = args[1]+"/";

		ArrayList<String> fichiersPlis = new ArrayList<String>();
		String[] rb = new String[nbPlis];
		
		AlgoReco recommandeur = AlgoParser.getDefaultRecommander(args[0]);
		
		System.out.println("TIGHTNESS "+(i*0.05));

		for(int j = 0; j < nbPlis; j++)
			fichiersPlis.add(prefixData+"csp"+i+"_set"+j+"_exemples");
		fichiersPlis.add(prefixData+"csp"+i+"_set1_exemples");

		for(int j = 0; j < nbPlis; j++)
			rb[j] = prefixData+"BN_csp"+i+"_"+j+".xml";
		
		val.run(recommandeur, dataset, true, false, nbPlis, fichiersPlis, prefixData+"randomCSP-"+i+".xml", rb);
		
	}

}

