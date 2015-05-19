package algoreco;

import heuristique_contraintes.HeuristiqueContraintesRien;
import heuristique_variable.HeuristiqueVariableMCSinv;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import br4cp.SALADD;

/**
 * Algorithme de recommandation pour les réseaux bayésiens naïfs et naïfs augmentés
 * @author pgimenez
 *
 */

public class AlgoRBNaif implements AlgoReco
{
	private String param;
	private HashMap<String, SALADD> x = new HashMap<String, SALADD>();
	private ArrayList<String> variables;
	private HashMap<String, String> assignement = new HashMap<String, String>();
	
	public AlgoRBNaif(String param)
	{
		this.param = param;
	}
	
	@Override
	public void initialisation(ArrayList<String> variables)
	{
		this.variables = variables;
	}
	
	@Override
	public String recommande(String variable, ArrayList<String> possibles)
	{
		for(String v: variables)
			if(assignement.get(v) != null)
				x.get(variable).assignAndPropagate(v, assignement.get(v));
		Map<String, Double> recomandations=x.get(variable).calculeDistributionAPosteriori(variable);
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
		assignement.put(variable, solution);
	}

	@Override
	public void oublieSession()
	{
		assignement.clear();
		for(String v: variables)
			if(x.get(v) != null)
				x.get(v).reinitialisation();
	}

	@Override
	public void apprendContraintes(String filename)
	{}

	@Override
	public void apprendDonnees(ArrayList<String> filename, int nbIter) 
	{
		System.out.println("Compilation des réseaux bayésiens...");
		for(String v: variables)
		{
			String file = "bn"+"_"+param+"_"+v+"_"+nbIter+".xml";
			if(new File(file).exists())
			{
				SALADD s = new SALADD();
				s.compilation(file, true, false, new HeuristiqueVariableMCSinv(), new HeuristiqueContraintesRien(), 3);
				s.initialize();
				x.put(v, s);
				System.out.println(v+": ok");
			}
			else
				System.out.println(file+" introuvable");
		}
		System.out.println("Compilation terminée");
	}

}
