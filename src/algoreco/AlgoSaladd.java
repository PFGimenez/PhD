package algoreco;

import heuristique_contraintes.HeuristiqueContraintesRien;
import heuristique_variable.HeuristiqueVariableMCSinvPlusUn;

import java.util.ArrayList;
import java.util.Map;

import test_independance.TestIndependance;
import test_independance.TestKhi2Max;
import br4cp.SALADD;

public class AlgoSaladd implements AlgoReco
{
	private TestIndependance testInd;
	private SALADD saladd;
	
	public AlgoSaladd(TestIndependance testInd)
	{
		this.testInd = testInd;
	}
	
	@Override
	public void initialisation() {
		testInd = new TestKhi2Max();
		saladd = new SALADD();
		saladd.initialize();

	}

	@Override
	public void apprendContraintes(String filename)
	{
		saladd.compilation("small.xml", false, true, new HeuristiqueVariableMCSinvPlusUn(), new HeuristiqueContraintesRien(), 0);
	}

	@Override
	public void apprendDonnees(ArrayList<String> filename, int nbIter) {
		// TODO
		saladd.compilationDHistorique(filename, 2);
		
	}

	@Override
	public String recommande(String variable, ArrayList<String> possibles) {
		Map<String, Double> recomandations=saladd.reco(variable, testInd);
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
		saladd.assignAndPropagate(variable, solution);		
	}

	@Override
	public void oublieSession() {
		saladd.reinitialisation();
	}

}
