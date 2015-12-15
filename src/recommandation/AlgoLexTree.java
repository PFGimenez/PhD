package recommandation;

import java.io.File;
import java.util.ArrayList;
import java.util.Random;

import compilateur.LecteurCdXml;
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
	public void initialisation(ArrayList<String> variables)
	{
		
	}
	
	@Override
	public void apprendContraintes(String fichierContraintes)
	{
		SALADD contraintes;
		contraintes = new SALADD();

		if(new File(fichierContraintes).exists())
		{
			contraintes.compilation(fichierContraintes, true, 4, 0, 0);
			contraintes.propagation();
		}
		else
		{
			// TODO: et s'il n'y a pas de contraintes?
			int z=0;
			z = 1/z;
		}

		algo.initOrder(contraintes);
		
	}
	
	@Override
	public void apprendDonnees(ArrayList<String> filename, int nbIter)
	{System.out.println(dataset+algo.toString()+"-"+nbIter);
		// Tout est déjà calculé
		if(!algo.load(dataset+algo.toString()+"-"+nbIter))
		{
			algo.apprendDonnees(filename);
			algo.save(dataset+algo.toString()+"-"+nbIter);
		}
	}

	@Override
	public String recommande(String variable, ArrayList<String> possibles, SALADD contraintes)
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