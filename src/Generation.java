import java.io.File;
import java.util.ArrayList;
import java.util.Random;
import java.util.Set;

import recommandation.*;
import recommandation.autres.XMLconverter;
import recommandation.autres.XMLconverter2;
import compilateur.SALADD;


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
 * Génération de données par simulation de session de configuration
 * @author pgimenez
 *
 */

public class Generation {

	public static void main(String[] args)
	{	
		Random randomgenerator = new Random();
		int nbGenere = 10000;
		String dataset = "renault_small";
		
		String prefixData = "datasets/"+dataset+"/";
		String cheminBif = prefixData+"rb.xml";
		
		AlgoRB generateur;
		AlgoReco conversionXML1, conversionXML2;
		
		generateur = new AlgoRB(cheminBif);			// Algorithme à réseau bayésien (hc)
		conversionXML1 = new XMLconverter(prefixData);
		conversionXML2 = new XMLconverter2(prefixData);
		
		String fichierContraintes = prefixData+"contraintes.xml";

		SALADD contraintes = new SALADD();
		if(new File(fichierContraintes).exists())
			contraintes.compilation(fichierContraintes, true, 4, 0, 0);
		else
			contraintes.createBlankVDD(cheminBif, true, 0);
		contraintes.propagation();
		
		ArrayList<String> variables = new ArrayList<String>();
		variables.addAll(contraintes.getFreeVariables());
		generateur.initialisation(variables);
				
		generateur.apprendDonneesPourGeneration(cheminBif);
		
		int nbVar = variables.size();
		
		for(int test=0; test<nbGenere; test++)
		{
			generateur.oublieSession();
			
			boolean[] dejaTire = new boolean[nbVar];
			for(int i = 0; i < nbVar; i++)
				dejaTire[i] = false;
			
			int n;
			ArrayList<String> ordre = new ArrayList<String>();
			for(int i = 0; i < nbVar; i++)
			{
				do {
					n = randomgenerator.nextInt(nbVar);
				} while(dejaTire[n]);
				ordre.add(variables.get(n));
				dejaTire[n] = true;
			}
			
			for(int occu=0; occu<ordre.size(); occu++)
			{
				String v = ordre.get(occu);
				Set<String> values = contraintes.getCurrentDomainOf(v);
				
				ArrayList<String> values_array = new ArrayList<String>();
				values_array.addAll(values);
				String r = generateur.recommande(v, values_array);
				
				conversionXML1.setSolution(v, r);
				conversionXML2.setSolution(v, r);
				
				generateur.setSolution(v, r);
				contraintes.assignAndPropagate(v, r);
			}
			
			contraintes.reinitialisation();
			contraintes.propagation();
		}
		
	}

}
