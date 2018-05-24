package preferences.completeTree;

import java.util.List;

import compilateurHistorique.DatasetInfo;
import compilateurHistorique.Instanciation;

public interface ApprentissageLex
{
	public LexicographicStructure apprendDonnees(DatasetInfo dataset, List<Instanciation> instances);
}
