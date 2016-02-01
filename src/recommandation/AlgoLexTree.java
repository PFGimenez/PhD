package recommandation;

import java.util.ArrayList;
import compilateur.SALADD;
import preferences.ApprentissageLexStructure;

// Recommandation par apprentissage de préférences

public class AlgoLexTree implements AlgoReco {

	private ApprentissageLexStructure algo;
	private ArrayList<String> element = new ArrayList<String>();
	private ArrayList<String> ordreVariables = new ArrayList<String>();
	private String dataset;
	
	public AlgoLexTree(ApprentissageLexStructure algo, String dataset)
	{
		this.algo = algo;
		this.dataset = dataset;
	}
	
	@Override
	public void apprendContraintes(SALADD contraintes)
	{}
	
	@Override
	public void apprendDonnees(ArrayList<String> filename, int nbIter, boolean entete)
	{
		System.out.println(dataset+algo.toString()+"-"+nbIter);
		// Tout est déjà calculé
		if(!algo.load(dataset+algo.toString()+"-"+nbIter))
		{
			algo.apprendDonnees(filename, entete);
			algo.save(dataset+algo.toString()+"-"+nbIter);
		}
	}

	@Override
	public String recommande(String variable, ArrayList<String> possibles)
	{
		return algo.infereBest(variable, possibles, element, ordreVariables);
	}

	@Override
	public void setSolution(String variable, String solution)
	{
		element.add(solution);
		ordreVariables.add(variable);
	}

	@Override
	public void oublieSession()
	{
		element.clear();
		ordreVariables.clear();
	}

	@Override
	public void termine()
	{}

	public String toString()
	{
		return getClass().getSimpleName();
	}
}