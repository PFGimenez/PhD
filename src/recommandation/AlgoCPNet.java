package recommandation;

import java.util.ArrayList;

import compilateurHistorique.HistoriqueCompile;
import compilateurHistorique.DatasetInfo;
import compilateurHistorique.Instanciation;
import preferences.cpnet.CPNet;
import recommandation.parser.ParserProcess;

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

/**
 * Algorithme basé sur les CP-nets
 * @author Pierre-François Gimenez
 *
 */

public class AlgoCPNet extends AlgoRecoRB
{
	private HistoriqueCompile historique;
	private Instanciation instanceReco;
	private String RBfile;
	private CPNet cpnet;
	private Instanciation tmp;
	
	public AlgoCPNet()
	{
		super("hc");
	}
	
	public AlgoCPNet(ParserProcess pp)
	{
		this();
	}
	
	public void describe()
	{
		System.out.println("CP-net");
	}
	
	public void apprendDonnees(DatasetInfo dataset, Instanciation[] instances, long code)
	{
		// apprentissage du RB
		learnBN(dataset, instances, code);
		historique = new HistoriqueCompile(dataset);
		historique.compile(instances);
		cpnet = new CPNet(dataset, historique, RBfile);
		instanceReco = new Instanciation(dataset);
		tmp = new Instanciation(dataset);
	}
	
	@Override
	public String recommande(String variable, ArrayList<String> possibles)
	{
		instanceReco.copy(tmp);
		cpnet.complete(tmp);
		return tmp.getValue(variable);
	}

	@Override
	public void setSolution(String variable, String solution)
	{
		instanceReco.conditionne(variable, solution);
	}

	@Override
	public void oublieSession()
	{
		instanceReco.deconditionneTout();
	}
	
	public String toString()
	{
		return getClass().getSimpleName();
	}
	
	@Override
	public void termine()
	{}
	
	@Override
	public void unassign(String variable)
	{
		instanceReco.deconditionne(variable);
	}

	@Override
	public void apprendRB(String file)
	{
		RBfile = file;
	}

	@Override
	public void terminePli()
	{}

}
