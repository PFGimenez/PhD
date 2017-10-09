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

/**
 * Un CP-net acyclique
 * @author pgimenez
 *
 */

public class CPNet {

	private HashMap<String, CPNetNode> nodes = new HashMap<String, CPNetNode>();
	private List<CPNetNode> nodesTriTopologique = new ArrayList<CPNetNode>();
	private DatasetInfo dataset;
	
	public CPNet(DatasetInfo dataset, HistoriqueCompile historique)
	{
		this.dataset = dataset;
		learn(historique);
	}
	
	private void learn(HistoriqueCompile historique)
	{
		
	}
	
	/**
	 * Construction de la liste triée topologiquement à partir des noeuds
	 */
	private void constructTriTopologique()
	{
		nodesTriTopologique.clear();
		List<CPNetNode> startNodes = new ArrayList<CPNetNode>();
		
		for(CPNetNode n : nodes.values())
			if(n.parents.isEmpty())
				startNodes.add(n);
		
		while(!startNodes.isEmpty())
		{
			CPNetNode n = startNodes.remove(0);
			nodesTriTopologique.add(n);
			for(CPNetNode e : n.enfants)
			{
				boolean parents = false;
				for(CPNetNode p : e.parents)
					if(!nodesTriTopologique.contains(p))
						parents = true;
				if(!parents)
					startNodes.add(e);
			}
		}
		assert nodesTriTopologique.size() == nodes.size();
	}
	
	/**
	 * Complète avec l'extension préférée
	 * @param inst
	 */
	public void complete(Instanciation inst)
	{
		for(CPNetNode n : nodesTriTopologique)
			n.complete(inst);
	}
}
