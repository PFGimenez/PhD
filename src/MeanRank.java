import java.util.ArrayList;

import preferences.completeTree.ApprentissageGloutonLexTree;
import preferences.heuristiques.HeuristiqueDuel;
import recommandation.AlgoLexTree;

public class MeanRank {
	
	public static void main(String[] args)
	{
		if(args.length < 1)
		{
			System.out.println("Usage : MeanRank dataset");
			return;
		}
		
		String dataset = args[0].trim();
		String prefixData = "datasets/"+dataset+"/";
		
		AlgoLexTree recommandeurPrune = new AlgoLexTree(new ApprentissageGloutonLexTree(300, 20, new HeuristiqueDuel()), true);
		AlgoLexTree recommandeurNoPrune = new AlgoLexTree(new ApprentissageGloutonLexTree(300, 20, new HeuristiqueDuel()), false);
		
		ArrayList<String> learning_set = new ArrayList<String>();
		learning_set.add(prefixData+"set0_exemples");
		learning_set.add(prefixData+"set1_exemples");
		recommandeurPrune.initHistorique(learning_set, true);
		recommandeurPrune.apprendDonnees(learning_set, 0, true);
		recommandeurPrune.printMeanRank();
		
		recommandeurNoPrune.initHistorique(learning_set, true);
		recommandeurNoPrune.apprendDonnees(learning_set, 0, true);
		recommandeurNoPrune.printMeanRank();	
	}
}
