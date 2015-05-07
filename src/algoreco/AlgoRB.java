package algoreco;

import heuristique_contraintes.HeuristiqueContraintesRien;
import heuristique_variable.HeuristiqueVariableMCSinv;

import java.util.ArrayList;
import java.util.Map;

import br4cp.SALADD;

public class AlgoRB implements AlgoReco
{

	private SALADD x;
	
	@Override
	public void initialisation()
	{
		x = new SALADD();
	}
	
	@Override
	public String recommande(String variable, ArrayList<String> possibles)
	{
		Map<String, Double> recomandations=x.calculeDistributionAPosteriori(variable);
		String best="";
		double bestproba=-1;
		
		for(String value: possibles)
		{
			if(recomandations.get(value) == null)
				continue;
			if(recomandations.get(value)>bestproba){
				bestproba=recomandations.get(value);
				best=value;
			}
		}
		return best;
	}

	@Override
	public void setSolution(String variable, String solution)
	{
		x.assignAndPropagate(variable, solution);
	}

	@Override
	public void oublieSession()
	{
		x.reinitialisation();
	}

	@Override
	public void apprendContraintes(String filename)
	{}

	@Override
	public void apprendDonnees(ArrayList<String> filename, int nbIter) 
	{
		x.compilation("bn"+nbIter+".xml", true, false, new HeuristiqueVariableMCSinv(), new HeuristiqueContraintesRien(), 0);
		x.initialize();
	}

}
