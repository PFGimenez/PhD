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

package preferences.cpnet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import compilateurHistorique.DatasetInfo;
import compilateurHistorique.HistoriqueCompile;
import compilateurHistorique.Instanciation;
import compilateurHistorique.IteratorInstancesPartielles;

/**
 * A node of the CP-net
 * @author pgimenez
 *
 */

public class CPNetNode
{
	public final String variable;
	private HashMap<Instanciation, List<String>> table = new HashMap<Instanciation, List<String>>();
	List<CPNetNode> enfants = new ArrayList<CPNetNode>();
	List<CPNetNode> parents = new ArrayList<CPNetNode>();
	
	public CPNetNode(String var)
	{
		this.variable = var;
	}
	
	@Override
	public int hashCode()
	{
		return variable.hashCode();
	}
	
	@Override
	public boolean equals(Object other)
	{
		return other instanceof CPNetNode && ((CPNetNode)other).variable.equals(variable);
	}
	
	@Override
	public String toString()
	{
		return "CPNetNode ("+variable+")";
	}
	
	public void complete(Instanciation partiel)
	{
		assert checkParentInst(partiel);
		// variable déjà conditionnée
		if(partiel.isConditionne(variable))
			return;
		
		assert checkUniqueLine(partiel);
		
		// on trouve la ligne de la table qui est compatible avec partiel
		for(Instanciation i : table.keySet())
			if(partiel.isCompatible(i))
			{
				// on conditionne avec la valeur préférée
				partiel.conditionne(variable, table.get(i).get(0));
				return;
			}
		
		assert false;
		return;
	}
	
	/**
	 * On vérifie que tous les parents de ce nœud sont bien instanciés
	 * @param partiel
	 * @return
	 */
	private boolean checkParentInst(Instanciation partiel)
	{
		for(CPNetNode p : parents)
			if(!partiel.isConditionne(p.variable))
				return false;
		return true;
	}
	
	/**
	 * Une et une seule ligne doit être compatible avec partiel
	 * @param partiel
	 * @return
	 */
	private boolean checkUniqueLine(Instanciation partiel)
	{
		int compatible = 0;
		for(Instanciation i : table.keySet())
			if(partiel.isCompatible(i))
				compatible++;
		return compatible == 1;
	}

	/**
	 * Apprentissage de la CPT
	 * La structure du CP-net doit être connues (au moins ses parents)
	 * @param historique
	 */
	public void learnTable(HistoriqueCompile historique, DatasetInfo dataset)
	{
		List<String> varParent = new ArrayList<String>();
		for(CPNetNode n : parents)
			varParent.add(n.variable);
		IteratorInstancesPartielles iter = new IteratorInstancesPartielles(new Instanciation(dataset), dataset, varParent);
		
		for(Instanciation inst : iter)
		    table.put(inst, historique.computeOrder(variable, inst));

	}
	
}
