/*   (C) Copyright 2017, Gimenez Pierre-François
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

package recommandation;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * A small parser
 * @author Pierre-François Gimenez
 *
 */

public class AlgoParser
{
	public static Class<? extends AlgoReco> getAlgoReco(String nom)
	{
		if(nom.contains("old-drc"))
			return AlgoOldDRC.class;
		else if(nom.contains("drc"))
			return AlgoDRC.class;
		else if(nom.contains("rc"))
			return AlgoOldDRC.class;
		else if(nom.contains("jointree"))
			return AlgoRBJayes.class;
		else if(nom.contains("v-maj"))
			return AlgoVoisinsMajorityVoter.class;
		else if(nom.contains("v-pop"))
			return AlgoVoisinsMostPopular.class;
		else if(nom.contains("v-nai"))
			return AlgoVoisinsNaive.class;
		else if(nom.contains("oracle"))
			return AlgoOubliRien.class;
//		else if(nom.contains("lextree-old"))
//			recommandeur = new AlgoLexTree(new ApprentissageGloutonLexTree(300, 20, new VieilleHeuristique(new HeuristiqueEnropieNormalisee())), prefixData, false);
		else if(nom.contains("lextree-new"))
			return AlgoLexTree.class;
		else if(nom.contains("nai"))
			return AlgoRBNaif.class;
		else
		{
			System.out.println("Algo inconnu : "+nom);
			return null;
		}
	}
	
	public static AlgoReco getDefaultRecommander(String nom)
	{
		Class<? extends AlgoReco> reco = getAlgoReco(nom);
		try {
			Constructor<? extends AlgoReco> c = reco.getConstructor();
			return c.newInstance();
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException
				| InvocationTargetException | NoSuchMethodException | SecurityException e) {
			e.printStackTrace();
		}
		return null;
	}
	
}
