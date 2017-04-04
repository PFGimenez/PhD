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
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Random;
import java.util.Set;

import compilateur.LecteurCdXml;
import compilateur.SALADD;
import compilateurHistorique.Instanciation;
import compilateurHistorique.IteratorInstances;
import compilateurHistorique.MultiHistoComp;
import graphOperation.GrapheRC;
import recommandation.*;
import recommandation.old.AlgoSaladdOubli;

/**
 * Validation croisée
 * @author Pierre-François Gimenez
 *
 */

public class ValidationCroisee
{
	private String outputFolder;
	private boolean verbose, debug;
	
	public ValidationCroisee(String outputFolder, boolean verbose, boolean debug)
	{
		this.outputFolder = outputFolder;
		this.verbose = verbose;
		this.debug = debug;
		if(verbose)
		{
			System.out.println("Initialisation de la validation croisée");
			System.out.println("Output forder : "+outputFolder);
			System.out.println("Verbose : "+verbose);
			System.out.println("Debug : "+debug);
		}
	}

	public void run(AlgoReco recommandeur, String dataset, boolean entete, boolean oracle, int nbPli, ArrayList<String> fichiersPlis, String fichierContraintes, String[] rb)
	{
		final boolean sleep = debug;
		final boolean outputFichier = outputFolder != null;
		boolean contraintesPresentes = fichierContraintes != null;
		final String prefixData = "datasets/"+dataset+"/";

		// S'il n'y a qu'un seul pli, c'est un peu différent
		boolean half = nbPli == 1;
		if(nbPli < 1 || nbPli > 2)
		{
			System.out.println("nbPli doit valoir 1 ou 2");
			return;
		}

		if(!new File(prefixData).exists())		
		{
			System.out.println("Dataset inconnu : "+dataset);
			return;
		}

		long lastAff;
		
		Random randomgenerator = new Random(0);

		long toutDebut = System.currentTimeMillis();
		
		System.out.println("Début du test de "+recommandeur+" sur "+dataset+(contraintesPresentes ? " avec contraintes." : "."));
/*		System.out.println("Dataset = "+dataset);
		System.out.println("Oracle = "+oracle);
		System.out.println("Entete = "+entete);
		System.out.println("Output fichier = "+outputFichier);
		System.out.println("Contraintes = "+contraintesPresentes);*/
		

		int echec = 0, succes = 0, trivial = 0;

		SALADD contraintes;
		contraintes = null;
		
		if(contraintesPresentes)
		{
			if(new File(fichierContraintes).exists())			
			{
				System.err.print("Compilation des contraintes...");
				contraintes = new SALADD();
				contraintes.compilation(fichierContraintes, true, 4, 0, 0, true);
				contraintes.propagation();
				System.err.println(" finie");
			}
			else
			{
				System.err.println("Fichier de contraintes introuvable : "+fichierContraintes);
				return;
			}
		}
	
		LecteurCdXml lect=new LecteurCdXml();
		// On lit le premier fichier afin de récupére le nombre de variables
		lect.lectureCSV(fichiersPlis.get(1), entete);
		
		ArrayList<String> variables=new ArrayList<String>();
		ArrayList<String> solutions=new ArrayList<String>();
		ArrayList<String> ordre=new ArrayList<String>();
		
		double[][] sauvTemps = new double[lect.nbvar][nbPli*lect.nbligne];
				
		long[] instancesRestantes = new long[lect.nbvar];
		int[] oubliparpos = new int[lect.nbvar];
		int[] parpos = new int[lect.nbvar];
		int[] parposTrivial = new int[lect.nbvar];
		int[] parposnb = new int[lect.nbvar];
		int[] parModalite = new int[1000];
		int[] parModaliteNb = new int[1000];
		int[] parOubli = new int[lect.nbvar];
		int[] parOubliNb = new int[lect.nbvar];
		int[] parTauxOubli = new int[11];
		int[] parTauxOubliNb = new int[11];
		long[] dureePos = new long[lect.nbvar];
		for(int i=0; i<parpos.length; i++){
			oubliparpos[i] = 0;
			parpos[i]=0;
			parposTrivial[i]=0;
			parposnb[i]=0;
			parOubli[i] = 0;
			parOubliNb[i] = 0;
			instancesRestantes[i] = 0;
			dureePos[i] = 0;
		}
		for(int i = 0; i < 11; i++)
		{
			parTauxOubli[i] = 0;
			parTauxOubliNb[i] = 0;
		}
		for(int i = 0; i < 1000; i++)
		{
			parModalite[i] = 0;
			parModaliteNb[i] = 0;
		}
		
		ArrayList<String> learning_set = new ArrayList<String>();

		recommandeur.initHistorique(fichiersPlis, entete);
		
		long duree = 0;
		long avant;
		
		for(int i = 0; i < nbPli; i++)
		{
			MultiHistoComp.reinit();
			IteratorInstances.reinit();
			Instanciation.reinit();
			GrapheRC.reinit();
			
			int nbFileTraining = 0;
			int nbFileTest = 0;
			if((i & 1) == 0)
				nbFileTest++;
			else
				nbFileTraining++;
			
			String fileTraining = fichiersPlis.get(nbFileTraining);
			String fileTest = fichiersPlis.get(nbFileTest);
			
			if(oracle) // l'oracle est particulier : on utilise le test set comme training set
				fileTraining = fileTest;

			if(verbose)
			{
				System.out.println("Training set : "+fileTraining);
				System.out.println("Test set : "+fileTest);
			}
			
			learning_set.clear();
			learning_set.add(fileTraining);

			if(contraintesPresentes)
			{
				contraintes.reinitialisation();
				contraintes.propagation();
			}

			if(recommandeur instanceof AlgoRecoRB)
			{
				if(verbose)
					System.out.println("RB : "+rb[i]);
				((AlgoRecoRB) recommandeur).apprendRB(rb[i]);
			}
			recommandeur.apprendDonnees(learning_set, i, entete);
			lect.lectureCSV(fileTest, entete);

			lastAff = System.currentTimeMillis();
			for(int test=0; test<lect.nbligne; test++)
			{
				variables.clear();
				solutions.clear();
				ordre.clear();
		
				for(int k=0; k<lect.nbvar; k++)
				{
					variables.add(lect.var[k]);
					solutions.add(lect.domall[test][k]);
				}
				
				// on génère un ordre
				boolean[] dejaTire = new boolean[lect.nbvar];
				for(int k = 0; k < lect.nbvar; k++)
					dejaTire[k] = false;

				int n;
				for(int k = 0; k < lect.nbvar; k++)
				{
					n = randomgenerator.nextInt(lect.nbvar);
					do {
						n = randomgenerator.nextInt(lect.nbvar);
					} while(dejaTire[n]);
					ordre.add(lect.var[n]);
					dejaTire[n] = true;
				}
				
				recommandeur.oublieSession();

				for(int occu=0; occu<ordre.size(); occu++)
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
						nbModalites = values.size();
						if(nbModalites == 0)
						{
							System.out.println("Aucune valeur possible !");
							int z = 0;
							z = 1/z;
						}
					}
					
					if(recommandeur instanceof AlgoSaladdOubli)
						instancesRestantes[i] += ((AlgoSaladdOubli) recommandeur).count();
					
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
						recommandeur.setSolution(v, solution);
						if(contraintesPresentes)
							contraintes.assignAndPropagate(v, solution);
						//System.out.println("début trivial : "+(System.currentTimeMillis() - avant));
						continue;
					}
					
					parModaliteNb[nbModalites]++;
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

					sauvTemps[occu][test+i*lect.nbligne] = delta;
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
						parModalite[nbModalites]++;
						parpos[occu]++;
					}
					else
					{
						if(debug)
							System.out.println(" (échec, vraie valeur: "+solution+")");
						echec++;
						if(recommandeur instanceof AlgoSaladdOubli)
						{
							int nbOubli = ((AlgoSaladdOubli)recommandeur).getNbOublis();
							oubliparpos[occu] += nbOubli;
							parOubliNb[nbOubli]++;
							if(occu != 0)
							{
								int taux = (int)(10.*nbOubli/occu);
								parTauxOubliNb[taux]++;
							}
						}
					}
					parposnb[occu]++;
					
					if(System.currentTimeMillis() - lastAff >= 5000)
					{
						// Sauvegarde des résultats

						double[] intervalleSucces = new double[lect.nbvar];

						for(int j = 0; j < lect.nbvar; j++)
						{
							double tmp = ((double)parpos[j])/parposnb[j];
							tmp = tmp*(1-tmp); // variance d'une variable de Bernoulli

							intervalleSucces[j] = 1.96*Math.sqrt(tmp / (i*lect.nbligne+test));			
						}

						
						double[] intervalleTemps = new double[lect.nbvar];

						for(int l = 0; l < lect.nbvar; l++)
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
						}
						

						if(outputFichier)
						{
							PrintWriter writer;
							try {
								String fichier = outputFolder+"/"+recommandeur+"_"+dataset+".data.tmp";
								writer = new PrintWriter(fichier, "UTF-8");
								for(int l=0; l<ordre.size(); l++)
								{
									writer.print(((double)parpos[l])/parposnb[l]);
									if(l < ordre.size()-1)
									writer.print(",");
								}
								writer.println();

								for(int l=0; l<ordre.size(); l++)
								{
									writer.print(intervalleSucces[l]);
									if(l < ordre.size()-1)
									writer.print(",");
								}
								writer.println();

								for(int l=0; l<ordre.size(); l++)
								{
									writer.print(((double)dureePos[l])/(1000000.*parposnb[l]));
									if(l < ordre.size()-1)
									writer.print(",");
								}
								writer.println();

								for(int l=0; l<ordre.size(); l++)
								{
									writer.print(intervalleTemps[l]);
									if(l < ordre.size()-1)
									writer.print(",");
								}
								writer.println();

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
						}
						
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
							for(int l=0; l<ordre.size(); l++)
								System.out.print(((double)parpos[l])/parposnb[l]+", ");
							System.out.println();
							System.out.println("Durée par position: ");
							for(int l=0; l<ordre.size(); l++)
							{
								System.out.print(((double)dureePos[l])/(1000000.*parposnb[l]));
								if(l < ordre.size()-1)
									System.out.print(", ");
							}
							System.out.println();
	
							if(recommandeur instanceof AlgoSaladdOubli)
							{
								System.out.println("Oublis par position: ");
								for(int l=0; l<ordre.size(); l++)
									System.out.print(((double)oubliparpos[l])/parposnb[l]+", ");
								System.out.println();
							}
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
		System.out.println();
		recommandeur.termine();

		System.out.println("*** FIN DU TEST DE "+recommandeur+" SUR "+dataset);
		System.out.println();
		System.out.println();
		/*
		if(contraintesPresentes)
		{
			System.out.println("Succès par position avec trivial: ");
			for(int l=0; l<ordre.size(); l++)
			{
				System.out.print(((double)parpos[l] + parposTrivial[l])/parposnb[0]);
				if(l < ordre.size()-1)
					System.out.print(", ");
			}
			System.out.println();
		}
		*/
		System.out.println("Durée par position (ms): ");
		for(int l=0; l<ordre.size(); l++)
		{
			System.out.print(((double)dureePos[l])/(1000000.*parposnb[l]));
			if(l < ordre.size()-1)
				System.out.print(", ");
		}
		System.out.println();
		
		System.out.println("Succès par position: ");
		for(int l=0; l<ordre.size(); l++)
		{
			System.out.print(((double)parpos[l])/parposnb[l]);
			if(l < ordre.size()-1)
			System.out.print(", ");
		}
		System.out.println();
	
		System.out.println("Taux succès: "+100.*succes/(echec+succes));
		if(contraintesPresentes)
			System.out.println("Taux trivial: "+100.*trivial/(echec+succes+trivial));

		// Inégalité de Hoeffding pour l'intervalle de confiance de la précision (qui est bornée) (inutilisé)
//		double intervalleSucces = Math.sqrt(Math.log(2./0.05)/(2*2*lect.nbligne));
//		System.out.println("Intervalle de confiance à 95% du succès : "+intervalleSucces);

		double[] intervalleSucces = new double[lect.nbvar];

		for(int i = 0; i < lect.nbvar; i++)
		{
			double tmp = ((double)parpos[i])/parposnb[i];
			tmp = tmp*(1-tmp); // variance d'une variable de Bernoulli

			intervalleSucces[i] = 1.96*Math.sqrt(tmp / (nbPli*lect.nbligne));			
		}
		/*
		System.out.println("Intervalle de confiance à 95% du succès: ");
		for(int l=0; l<ordre.size(); l++)
		{
			System.out.print(intervalleSucces[l]);
			if(l < ordre.size()-1)
			System.out.print(", ");
		}
		System.out.println();*/
		
		double[] intervalleTemps = new double[lect.nbvar];

		// Estimation de la variance
		// On suppose n (le nombre d'exemples) assez grand (>= 30) pour que la distribution t(n-1) soit approchée par la loi normale
		if(nbPli*lect.nbligne < 30)
			System.out.println("L'intervalle de confiance temporel n'est pas fiable ! (pas assez d'exemples)");
		
		for(int i = 0; i < lect.nbvar; i++)
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
		}
		/*
		System.out.println("Intervalle de confiance à 95% du temps: ");
		for(int l=0; l<ordre.size(); l++)
		{
			System.out.print(intervalleTemps[l]);
			if(l < ordre.size()-1)
			System.out.print(", ");
		}
		System.out.println();
*/
		if(outputFichier)
		{
			PrintWriter writer;
			try {
				String fichier = outputFolder+"/"+recommandeur+"_"+dataset+".data";
				System.out.println("Sauvegardes des résultats dans "+fichier);
				writer = new PrintWriter(fichier, "UTF-8");
				for(int l=0; l<ordre.size(); l++)
				{
					writer.print(((double)parpos[l])/parposnb[l]);
					if(l < ordre.size()-1)
					writer.print(",");
				}
				writer.println();

				for(int l=0; l<ordre.size(); l++)
				{
					writer.print(intervalleSucces[l]);
					if(l < ordre.size()-1)
					writer.print(",");
				}
				writer.println();

				for(int l=0; l<ordre.size(); l++)
				{
					writer.print(((double)dureePos[l])/(1000000.*parposnb[l]));
					if(l < ordre.size()-1)
					writer.print(",");
				}
				writer.println();

				for(int l=0; l<ordre.size(); l++)
				{
					writer.print(intervalleTemps[l]);
					if(l < ordre.size()-1)
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
		
		System.out.println("Durée totale (ms): "+(System.currentTimeMillis() - toutDebut));
		System.out.println("Durée de la recommandation: "+duree);
		System.out.println("Nombre de recommandations: "+(echec+succes));
		System.out.println("Durée moyenne d'une recommandation (ns): "+((double)duree)/(echec+succes));
	}

}

