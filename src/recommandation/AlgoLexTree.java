package recommandation;

import java.util.ArrayList;
import java.util.HashMap;

import compilateur.SALADD;
import preferences.ApprentissageLexStructure;
import preferences.LexicographicStructure;

// Recommandation par apprentissage de préférences

public class AlgoLexTree implements AlgoReco {

	private ApprentissageLexStructure algo;
	private LexicographicStructure struct;
	private HashMap<String, String> valeurs;
//	private String dataset;
	
	public AlgoLexTree(ApprentissageLexStructure algo, String dataset)
	{
		this.algo = algo;
//		this.dataset = dataset;
		valeurs = new HashMap<String, String>();
	}
	
	@Override
	public void apprendContraintes(SALADD contraintes)
	{}
	
	@Override
	public void apprendDonnees(ArrayList<String> filename, int nbIter, boolean entete)
	{
//		System.out.println(dataset+algo.toString()+"-"+nbIter);
		// Tout est déjà calculé
//		if(!algo.load(dataset+algo.toString()+"-"+nbIter))
//		{
		struct = algo.apprendDonnees(filename, entete);
//			algo.save(dataset+algo.toString()+"-"+nbIter);
//		}
	}

	@Override
	public String recommande(String variable, ArrayList<String> possibles)
	{
		return struct.infereBest(variable, possibles, valeurs);
	}

	@Override
	public void setSolution(String variable, String solution)
	{
		valeurs.put(variable, solution);
	}

	@Override
	public void oublieSession()
	{
		valeurs.clear();
	}

	@Override
	public void termine()
	{}

	public String toString()
	{
		return getClass().getSimpleName();
	}

	public void initHistorique(ArrayList<String> filename, boolean entete)
	{
		algo.apprendDomainesVariables(filename, entete);
	}

	@Override
	public void unassign(String variable)
	{
		valeurs.remove(variable);
	}
}