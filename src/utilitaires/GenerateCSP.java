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

package utilitaires;

import compilateurHistorique.MultiHistoComp;
import contraintes.RandomCSP;

/**
 * Génération de CSP aléatoire.
 * Lit un réseau bayésien au format bif xml (pour avoir les noms de variables et leurs différentes valeurs) pour créer ensuite un CSP
 * @author Pierre-François Gimenez
 *
 */

public class GenerateCSP
{
	public static void main(String[] args) throws Exception
	{	
		double connectivite = 0.1, durete = 0.1;
		String dataset = "renault_small_header_contraintes";
		String prefixData = "datasets/"+dataset+"/";
		String rbfile = prefixData+"BN_0.xml";
		
		MultiHistoComp hist = new MultiHistoComp(rbfile);
		RandomCSP csp = new RandomCSP(hist.getVariablesLocal(), 5, connectivite, durete);
		
		csp.save(prefixData+"testCSP.xml");
		System.out.println("CSP généré");
	}
}
