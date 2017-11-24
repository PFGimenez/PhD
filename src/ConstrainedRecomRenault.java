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
 * Travaille uniquement sur les datesets de renault
 * @author Pierre-François Gimenez
 *
 */

public class ConstrainedRecomRenault {
	
	// PARAM 1 : algo
	// PARAM 2 : dataset
	// PARAM 3 : debug (optionnel) -d
	// PARAM 4 : output fichier (optionnel) -o
	
	public static void main(String[] args)
	{
		String dataset = "renault_big_header_contraintes";

		boolean verbose = true;
		boolean debug = false;
		
		String prefixData = "datasets/"+dataset+"/";

		ArrayList<String> fichiersPlis = new ArrayList<String>();
//		ValidationCroisee val = new ValidationCroisee(verbose, debug, true, prefixData+"set0_exemples");
		
		AlgoReco[] recoTab = {/*new AlgoDRC(10, 1), */new AlgoRBJayes()};
		
		for(AlgoReco recommandeur : recoTab)
		{
			fichiersPlis.clear();
			fichiersPlis.add(prefixData+"set0_exemples");
			fichiersPlis.add(prefixData+"set1_exemples");
			ValidationCroisee.run(recommandeur, dataset, true, debug, verbose, 2, fichiersPlis, null, prefixData+"contraintes.xml", new String[]{prefixData+"BN_1.xml", prefixData+"BN_0.xml"}, 1, 1);
		}
		
		
	}

}

