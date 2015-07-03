package recommandation.methode_oubli;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import methode_oubli.MethodeDSeparation;
import compilateur.VDD;
import compilateur.Var;
import compilateur.test_independance.TestIndependance;

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
 * Méthode d'oubli qui effectue la d-séparation après la restauration par indépendance
 * @author pgimenez
 *
 */

public class OubliParDSeparationApres extends MethodeDSeparation {

	public OubliParDSeparationApres(int seuil, TestIndependance test)
	{
		super(seuil, test);
	}
	
	@Override
	public Map<String, Double> recommandation(Var v, HashMap<String, String> historiqueOperations, VDD vdd, ArrayList<String> possibles)
	{
		done.clear();
		nbOubli = 0;
		dejavu.clear();
		dejavuVal.clear();
		ArrayList<String> connues = new ArrayList<String>();
		Map<String, Double> m;
		
		super.restaure(historiqueOperations, vdd, v);
		
    	for(String s: historiqueOperations.keySet())
		{
			Var var = vdd.getVar(s);
			if(!dejavu.contains(var))
				connues.add(var.name);
		}

		rechercheEnProfondeur(connues, v.name, false, 0);
		
		for(String s: historiqueOperations.keySet())
		{
			Var connue = vdd.getVar(s);
			if(!done.contains(connue.name) && !dejavu.contains(connue))
			{
	    		dejavu.add(connue);
	    		dejavuVal.add(historiqueOperations.get(s));
	    		vdd.deconditioner(connue);
	    		nbOubli++;
			}
		}
		
    	m = vdd.countingpondereOnPossibleDomain(v, possibles);
    	
    	super.reconditionne(vdd);

    	return m;
	}
	
}
