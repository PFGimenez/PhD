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
	// PARAM 2 : dataset
	// PARAM 3 : debug (optionnel) -d
	// PARAM 4 : output fichier (optionnel) -o
	
	public static void main(String[] args)
	{
		String dataset = "alarm_contraintes";
		int nbPli = 10;

		boolean verbose = false;
		boolean debug = false;
		ValidationCroisee val = new ValidationCroisee(null, verbose, debug);
		
		String prefixData = "datasets/"+dataset+"/";

		ArrayList<String> fichiersPlis = new ArrayList<String>();
		
		AlgoReco[] recoTab = {new AlgoDRC(10, 1), new AlgoRBJayes()};
		
		for(int i = 1; i < nbPli; i++)
		{
			System.out.println("TEST AVEC DURETE "+(i*0.05));
			for(AlgoReco recommandeur : recoTab)
			{
				fichiersPlis.clear();
				fichiersPlis.add(prefixData+"csp"+i+"_set0_exemples");
				fichiersPlis.add(prefixData+"csp"+i+"_set1_exemples");
				val.run(recommandeur, dataset, true, false, 2, fichiersPlis, prefixData+"randomCSP-"+i+".xml", new String[]{prefixData+"BN_csp"+i+"_1.xml", prefixData+"BN_csp"+i+"_0.xml"});
			}
		}
		
		
	}

}

