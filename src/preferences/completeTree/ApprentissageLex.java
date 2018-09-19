package preferences.completeTree;

import java.util.List;

import compilateurHistorique.DatasetInfo;
import compilateurHistorique.Instanciation;

public interface ApprentissageLex
{
	public LexTreeInterface apprendDonnees(DatasetInfo dataset, List<Instanciation> instances);
}
