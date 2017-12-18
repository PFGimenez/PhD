package evaluation;
/*   (C) Copyright 2016, Gimenez Pierre-François 
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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.Set;

import compilateur.SALADD;
import compilateurHistorique.DatasetInfo;
import compilateurHistorique.HistoriqueCompile;
import compilateurHistorique.Instanciation;
import recommandation.*;

/**
 * Validation croisée
 * @author Pierre-François Gimenez
 *
 */

public class ValidationCroisee
{
	public static void run(AlgoReco recommandeur, String dataset, boolean entete, boolean debug, boolean verbose, int nbPli, ArrayList<String> fichiersPlis, ArrayList<String> fichiersPourApprentissage, String fichierContraintes, String[] rb, int nbScenario, int nbPlisApprentissage)
	{
		assert nbPlisApprentissage <= fichiersPlis.size() - 1;
		
		boolean oracle = recommandeur.isOracle();
		final boolean sleep = debug;
//		final boolean outputFichier = outputFolder != null;
		boolean contraintesPresentes = fichierContraintes != null;
//		final String prefixData = "datasets/"+dataset+"/";
		final String prefixData = dataset;

		// S'il n'y a qu'un seul pli, c'est un peu différent
		boolean half = nbPli == 1;

		if(!new File(prefixData).exists())		
		{
			System.out.println("Dataset inconnu : "+dataset);
			return;
		}

		long lastAff;
		
		Random randomgenerator = new Random(0);

		long toutDebut = System.currentTimeMillis();
		
		System.out.println("Date du test :"+new Date());
		System.out.println("Début du test de "+recommandeur+" sur "+dataset+(contraintesPresentes ? " avec contraintes." : "."));
		System.out.println("Nb plis = "+nbPli);
		System.out.println("Nb de scénarios par configuration = "+nbScenario);
		System.out.println("Dataset = "+dataset);
		System.out.println("Oracle = "+oracle);
		System.out.println("Entete = "+entete);
//		System.out.println("Output fichier = "+outputFichier);
		System.out.println("Contraintes = "+fichierContraintes);

		SALADD contraintes = null;
		
		if(contraintesPresentes)
		{
			if(new File(fichierContraintes).exists())			
			{
				System.err.print("Compilation des contraintes...");
				contraintes = new SALADD();
				contraintes.compilation(fichierContraintes, true, 4, 0, 0, true);
				contraintes.propagation();
				System.err.println(" finie");
				recommandeur.apprendContraintes(contraintes);
			}
			else
			{
				System.err.println("Fichier de contraintes introuvable : "+fichierContraintes);
				return;
			}
		}
	
		if(half)
			fichiersPlis.add(dataset+"training");
		
		ArrayList<String> variables=new ArrayList<String>();
		ArrayList<String> solutions=new ArrayList<String>();
		ArrayList<String> ordre=new ArrayList<String>();
		
//		double[][] sauvTemps = new double[nbVar][nbPli*lect.nbligne];
				

		
		ArrayList<String> learning_set = new ArrayList<String>();

		ArrayList<String> allFiles = new ArrayList<String>();
		allFiles.addAll(fichiersPlis);
		if(fichiersPourApprentissage != null)
			allFiles.addAll(fichiersPourApprentissage);
		
		DatasetInfo datasetinfo = new DatasetInfo(allFiles, entete);
		

		int[] parpos;
		int[] parposTrivial;
		int[] parposnb;
		long[] dureePos;
		int echec, succes, trivial;
		int echecPli, succesPli, trivialPli;
		int nbVar;
		int nbTests;
		
//		LecteurCdXml lect = new LecteurCdXml();
		// On lit le premier fichier afin de récupére le nombre de variables
//		lect.lectureCSV(jeu, entete);
		nbVar = datasetinfo.vars.length;
		parpos = new int[nbVar];
		parposTrivial = new int[nbVar];
		parposnb = new int[nbVar];
		dureePos = new long[nbVar];

		echec = 0;
		succes = 0;
		trivial = 0;
		nbTests = 0;
		for(int i = 0; i < nbVar; i++)
		{
			parpos[i]=0;
			parposTrivial[i]=0;
			parposnb[i]=0;
			dureePos[i] = 0;
		}
		
		long duree = 0;
		long avant;
		
		recommandeur.describe();

		for(int i = 0; i < nbPli; i++)
		{
			trivialPli = 0;
			succesPli = 0;
			echecPli = 0;
			learning_set.clear();

			String fileTest;
			if(half)
			{
				learning_set.add(dataset+"training");
				fileTest = dataset+"testing";
			}
			else
			{
				fileTest = fichiersPlis.get(i);
				if(oracle) // l'oracle est particulier : on utilise le test set comme training set
					learning_set.add(fichiersPlis.get(i));
				else
				{
					int j = 0;
					while(learning_set.size() < nbPlisApprentissage)
					{
						if(i != j)
							learning_set.add(fichiersPlis.get(j));
						j++;
					}
					if(fichiersPourApprentissage != null)
						learning_set.addAll(fichiersPourApprentissage);
				}
			}
			
			if(verbose)
			{
				System.out.println();
				System.out.println("Training set : "+learning_set);
				System.out.println("Test set : "+fileTest);
			}

			
			if(contraintesPresentes)
			{
				contraintes.reinitialisation();
				contraintes.propagation();
			}

/*			if(recommandeur instanceof AlgoRecoRB)
			{
				if(verbose)
					System.out.println("RB : "+rb[i]);
				((AlgoRecoRB) recommandeur).apprendRB(rb[i]);
			}*/
			
			recommandeur.apprendDonnees(datasetinfo, learning_set, i, entete);
			
			List<String> testf = new ArrayList<String>();
			testf.add(fileTest);
			List<Instanciation> instances = null;
			try {
				instances = HistoriqueCompile.readPossibleInstances(datasetinfo, testf, entete, contraintes, 1);
			} catch (IOException e1) {
				e1.printStackTrace();
				continue;
			}
/*			LecteurCdXml lect = new LecteurCdXml();
			try {
				lect.lectureCSV(fileTest, entete);
			} catch (IOException e1) {
				e1.printStackTrace();
				continue;
			}*/

			lastAff = System.currentTimeMillis();
			int test = -1;
			for(Instanciation inst : instances)
			{
				assert inst.isCompatibleWithConstraints(contraintes);
				test++;
				for(int bwa = 0; bwa < nbScenario; bwa++)
				{
					variables.clear();
					solutions.clear();
					ordre.clear();
					
					for(int k=0; k<nbVar; k++)
					{
						variables.add(datasetinfo.vars[k].name);
						solutions.add(inst.getValue(datasetinfo.vars[k].name));
					}					
					
					nbTests++;

	//				System.out.println("Produit valide");
					
					// on génère un ordre
					List<Integer> index = new ArrayList<Integer>();
					for(int k = 0; k < nbVar; k++)
						index.add(k);
					
					for(int k = 0; k < nbVar; k++)
						ordre.add(datasetinfo.vars[index.remove(randomgenerator.nextInt(nbVar-k))].name);

					recommandeur.oublieSession();
	
					for(int occu=0; occu<nbVar; occu++)
					{
						if(sleep)
							try {
								Thread.sleep(300);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
	
	//					avant = System.currentTimeMillis();
						int k = variables.indexOf(ordre.get(occu));
	//					System.out.println("Recherche de "+ordre.get(occu)+" : "+k);
						String v = variables.get(k);
						String solution = solutions.get(k);
						Set<String> values = null;
						int nbModalites = 0;
						
						if(contraintes != null)
						{
							values = contraintes.getCurrentDomainOf(v);		
							assert values.contains(solution) : values+" ne contient pas la solution "+solution;
							nbModalites = values.size();
							assert nbModalites > 0;
						}

						if(contraintes != null && nbModalites == 1)
						{
							if(debug)
							{
								ArrayList<String> values_array = new ArrayList<String>();
								values_array.addAll(values);
								System.out.println(occu+" variables connues. Possible: "+values.iterator().next()+". Scénario pour "+v+": "+solution+" (trivial)");
							}
							parposTrivial[occu]++;
							trivial++;
							trivialPli++;
							recommandeur.setSolution(v, solution);
							if(contraintesPresentes)
								contraintes.assignAndPropagate(v, solution);
							//System.out.println("début trivial : "+(System.currentTimeMillis() - avant));
							continue;
						}
						
//						parModaliteNb[nbModalites]++;
						ArrayList<String> values_array = null;
						
						if(contraintes != null)
						{
							values_array = new ArrayList<String>();
							values_array.addAll(values);
						}
	
						//System.out.println("début : "+(System.currentTimeMillis() - avant));
						avant = System.nanoTime();
	
						String r = recommandeur.recommande(v, values_array);
						long delta = (System.nanoTime() - avant);
	
						assert values == null || values.contains(r) : "La recommandation "+r+" n'est pas possible ! "+values;
	//					sauvTemps[occu][test+i*lect.nbligne] = delta;
						dureePos[occu] += delta;
						duree += delta;
						
						//System.out.println("reco : "+(System.currentTimeMillis() - avant));
						if(contraintes != null && debug)
							System.out.print(occu+" variables connues. "+values_array.size()+" possibles. ");
						if(debug)
							System.out.print("Recommandation ("+occu+" var. connues) pour "+v+": "+r);
	//					avant = System.currentTimeMillis();
						recommandeur.setSolution(v, solution);
						
						if(contraintesPresentes)
							contraintes.assignAndPropagate(v, solution);
						
	//					matricesConfusion.get(v)
	//						[contraintes.getVar(v).conv(solution)]
	//						[contraintes.getVar(v).conv(r)]++;
	
						if(r != null && solution.compareTo(r)==0)
						{
							if(debug)
								System.out.println(" (succès)");
							succes++;
							succesPli++;
							/*
							if(recommandeur instanceof AlgoSaladdOubli)
							{
								int nbOubli = ((AlgoSaladdOubli)recommandeur).getNbOublis();
								oubliparpos[occu] += nbOubli;
								parOubli[nbOubli]++;
								parOubliNb[nbOubli]++;
								if(occu != 0)
								{
									int taux = (int)(10.*nbOubli/occu);
									parTauxOubli[taux]++;
									parTauxOubliNb[taux]++;
								}
							}
							parModalite[nbModalites]++;*/
							parpos[occu]++;
						}
						else
						{
							if(debug)
								System.out.println(" (échec, vraie valeur: "+solution+")");
							echec++;
							echecPli++;
/*							if(recommandeur instanceof AlgoSaladdOubli)
							{
								int nbOubli = ((AlgoSaladdOubli)recommandeur).getNbOublis();
								oubliparpos[occu] += nbOubli;
								parOubliNb[nbOubli]++;
								if(occu != 0)
								{
									int taux = (int)(10.*nbOubli/occu);
									parTauxOubliNb[taux]++;
								}
							}*/
						}
						parposnb[occu]++;
						
						if(System.currentTimeMillis() - lastAff >= 5000)
						{
							// Sauvegarde des résultats
	
							double[] intervalleSucces = new double[nbVar];
	
							for(int j = 0; j < nbVar; j++)
							{
								double tmp = ((double)parpos[j])/parposnb[j];
								tmp = tmp*(1-tmp); // variance d'une variable de Bernoulli
	
								intervalleSucces[j] = 1.96*Math.sqrt(tmp / (i*instances.size()+test));			
							}
	
							
	/*						double[] intervalleTemps = new double[nbVar];
	
							for(int l = 0; l < nbVar; l++)
							{
								double tmp = 0;
								for(int j = 0; j < i*lect.nbligne+test; j++)
								{
									tmp += (sauvTemps[l][j]/1000000. - dureePos[l]/(1000000.*parposnb[l])) * (sauvTemps[l][j]/1000000. - dureePos[l]/(1000000.*parposnb[l]));				
								}
								tmp = tmp / (i*lect.nbligne+test-1); // estimateur de la variance non biaisé
	//							System.out.println("Variance : "+tmp);
	//							intervalleTemps[i] = Math.sqrt(tmp / 0.05);
								intervalleTemps[l] = 1.96*Math.sqrt(tmp / (i*lect.nbligne+test));			
							}*/
							
	/*
							if(outputFichier)
							{
								PrintWriter writer;
								try {
									String fichier = outputFolder+"/"+recommandeur+"_"+dataset+".data.tmp";
									writer = new PrintWriter(fichier, "UTF-8");
									for(int l=0; l<nbVar; l++)
									{
										writer.print(((double)parpos[l])/parposnb[l]);
										if(l < nbVar-1)
										writer.print(",");
									}
									writer.println();
	
									for(int l=0; l<nbVar; l++)
									{
										writer.print(intervalleSucces[l]);
										if(l < nbVar-1)
										writer.print(",");
									}
									writer.println();
	
									for(int l=0; l<nbVar; l++)
									{
										writer.print(((double)dureePos[l])/(1000000.*parposnb[l]));
										if(l < nbVar-1)
										writer.print(",");
									}
									writer.println();*/
	/*
									for(int l=0; l<nbVar; l++)
									{
										writer.print(intervalleTemps[l]);
										if(l < nbVar-1)
										writer.print(",");
									}
									writer.println();
	*//*
									String out = "Résultat partiel : pli ";
									if(!half)
										out += i+" ";
									out += "à "+Math.round(test*10000./lect.nbligne)/100.+"%";
									writer.println(out);
									writer.close();
	
								} catch (FileNotFoundException e) {
									e.printStackTrace();
								} catch (UnsupportedEncodingException e) {
									e.printStackTrace();
								}
							}*/
							
							lastAff = System.currentTimeMillis();
	/*						String efface = "\r                ";
							String out = "\rPli ";
							if(!half)
								out += i+" ";
							out += "à "+Math.round(test*10000./lect.nbligne)/100.+"%";
							try {
								System.out.write(efface.getBytes());
								System.out.write(out.getBytes());
							} catch (IOException e) {
								e.printStackTrace();
							}*/
							if(verbose)
							{
								System.out.println();
								System.out.println("Taux succès: "+100.*succes/(echec+succes));
								if(contraintesPresentes)
									System.out.println("Taux trivial: "+100.*trivial/(echec+succes+trivial));
								System.out.println("Durée: "+(duree));
								System.out.println("Durée moyenne d'une recommandation en ms: "+((double)duree)/(1000000.*(echec+succes)));
								System.out.println("Succès par position: ");
								for(int l=0; l<nbVar; l++)
									System.out.print(((double)parpos[l])/parposnb[l]+", ");
								System.out.println();
								System.out.println("Durée par position: ");
								for(int l=0; l<nbVar; l++)
								{
									System.out.print(((double)dureePos[l])/(1000000.*parposnb[l]));
									if(l < nbVar-1)
										System.out.print(", ");
								}
								System.out.println();
		
/*								if(recommandeur instanceof AlgoSaladdOubli)
								{
									System.out.println("Oublis par position: ");
									for(int l=0; l<nbVar; l++)
										System.out.print(((double)oubliparpos[l])/parposnb[l]+", ");
									System.out.println();
								}*/
							}
						}
						//System.out.println("après : "+(System.currentTimeMillis() - avant));
					}
	//				avant = System.currentTimeMillis();
					if(contraintesPresentes)
					{
						contraintes.reinitialisation();
						contraintes.propagation();
					}
					//System.out.println("prog : "+(System.currentTimeMillis() - avant));
				}
			}
			recommandeur.terminePli();
			System.out.println("Taux succès sur le pli : "+100.*succesPli/(echecPli+succesPli));
			if(contraintesPresentes)
				System.out.println("Taux trivial sur le pli : "+100.*trivialPli/(echecPli+succesPli+trivialPli));
		}
		recommandeur.termine();
		System.out.println();

		System.out.println("*** FIN DU TEST DE "+recommandeur+" SUR "+dataset+" avec "+(succes+echec)+" recommandations non triviales et "+nbTests+" configurations"+(contraintesPresentes ? " et contraintes "+fichierContraintes+".":"."));
		System.out.println();
		System.out.println();
		/*
		if(contraintesPresentes)
		{
			System.out.println("Succès par position avec trivial: ");
			for(int l=0; l<nbVar; l++)
			{
				System.out.print(((double)parpos[l] + parposTrivial[l])/parposnb[0]);
				if(l < nbVar-1)
					System.out.print(", ");
			}
			System.out.println();
		}
		*/
		System.out.println("Durée par position (ms): ");
		for(int l=0; l<nbVar; l++)
		{
			System.out.print(((double)dureePos[l])/(1000000.*parposnb[l]));
			if(l < nbVar-1)
				System.out.print(", ");
		}
		System.out.println();
		
		System.out.println("Succès par position: ");
		for(int l=0; l<nbVar; l++)
		{
			System.out.print(((double)parpos[l])/parposnb[l]);
			if(l < nbVar-1)
				System.out.print(", ");
		}
		System.out.println();
	
		System.out.println("Taux succès: "+100.*succes/(echec+succes));
		System.out.println("Taux erreur: "+100.*echec/(echec+succes));
		if(contraintesPresentes)
			System.out.println("Taux trivial: "+100.*trivial/(echec+succes+trivial));

		// Inégalité de Hoeffding pour l'intervalle de confiance de la précision (qui est bornée) (inutilisé)
//		double intervalleSucces = Math.sqrt(Math.log(2./0.05)/(2*2*lect.nbligne));
//		System.out.println("Intervalle de confiance à 95% du succès : "+intervalleSucces);
/*
		double[] intervalleSucces = new double[nbVar];

		for(int i = 0; i < nbVar; i++)
		{
			double tmp = ((double)parpos[i])/parposnb[i];
			tmp = tmp*(1-tmp); // variance d'une variable de Bernoulli

			intervalleSucces[i] = 1.96*Math.sqrt(tmp / (nbPli*lect.nbligne));			
		}*/
		/*
		System.out.println("Intervalle de confiance à 95% du succès: ");
		for(int l=0; l<nbVar; l++)
		{
			System.out.print(intervalleSucces[l]);
			if(l < nbVar-1)
			System.out.print(", ");
		}
		System.out.println();*/
		/*
		double[] intervalleTemps = new double[nbVar];

		// Estimation de la variance
		// On suppose n (le nombre d'exemples) assez grand (>= 30) pour que la distribution t(n-1) soit approchée par la loi normale
		if(nbPli*lect.nbligne < 30)
			System.out.println("L'intervalle de confiance temporel n'est pas fiable ! (pas assez d'exemples)");*/
		/*
		for(int i = 0; i < nbVar; i++)
		{
			double tmp = 0;
			for(int j = 0; j < nbPli*lect.nbligne; j++)
			{
				tmp += (sauvTemps[i][j]/1000000. - dureePos[i]/(1000000.*parposnb[i])) * (sauvTemps[i][j]/1000000. - dureePos[i]/(1000000.*parposnb[i]));				
			}
			tmp = tmp / (nbPli*lect.nbligne-1); // estimateur de la variance non biaisé
//			System.out.println("Variance : "+tmp);
//			intervalleTemps[i] = Math.sqrt(tmp / 0.05);
			intervalleTemps[i] = 1.96*Math.sqrt(tmp / (nbPli*lect.nbligne));			
		}*/
		/*
		System.out.println("Intervalle de confiance à 95% du temps: ");
		for(int l=0; l<nbVar; l++)
		{
			System.out.print(intervalleTemps[l]);
			if(l < nbVar-1)
			System.out.print(", ");
		}
		System.out.println();
*//*
		if(outputFichier)
		{
			PrintWriter writer;
			try {
				String fichier = outputFolder+"/"+recommandeur+"_"+dataset+".data";
				System.out.println("Sauvegardes des résultats dans "+fichier);
				writer = new PrintWriter(fichier, "UTF-8");
				for(int l=0; l<nbVar; l++)
				{
					writer.print(((double)parpos[l])/parposnb[l]);
					if(l < nbVar-1)
					writer.print(",");
				}
				writer.println();

				for(int l=0; l<nbVar; l++)
				{
					writer.print(intervalleSucces[l]);
					if(l < nbVar-1)
					writer.print(",");
				}
				writer.println();

				for(int l=0; l<nbVar; l++)
				{
					writer.print(((double)dureePos[l])/(1000000.*parposnb[l]));
					if(l < nbVar-1)
					writer.print(",");
				}
				writer.println();

				for(int l=0; l<nbVar; l++)
				{
					writer.print(intervalleTemps[l]);
					if(l < nbVar-1)
					writer.print(",");
				}
				writer.println();

				writer.close();
				(new File(outputFolder+"/"+recommandeur+"_"+dataset+".data.tmp")).delete();

			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}
		*/
		System.out.println("Durée totale (ms): "+(System.currentTimeMillis() - toutDebut));
		System.out.println("Durée de la recommandation: "+duree);
		System.out.println("Nombre de recommandations: "+(echec+succes));
		System.out.println("Durée moyenne d'une recommandation (ns): "+((double)duree)/(echec+succes));
	}

}

