package compilateurHistorique;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

import compilateur.LecteurCdXml;

/**
 * Comme histocomp, mais adapté pour manipuler plusieurs sous-arbres
 * @author pgimenez
 *
 */

public class MultiHistoComp implements Serializable
{
	private static final long serialVersionUID = 1L;
	private VDD arbre;
	private static Variable[] variables;
	private Variable[] variablesLocal;
	private static HashMap<String, Integer> mapVar; // associe au nom d'une variable sa position dans values
	private HashMap<String, Integer> mapVarLocal;
	
	private final static boolean usePrecalcul = false;
	
	private static HashMap<Integer, Integer>[][] nbInstancesPaire = null;
	private static HashMap<Integer, Integer>[] nbInstancesPriori = null;
	private ArrayList<String> varAConserver;
	
	// Ces deux variables ne sont utilisées que quand un réseau bayésien est utilisé
	private static int[][] cpt;
	private static int[][] famille;
	private static HashMap<String,ArrayList<String>> familleHashMap;
	
/*	public HistoComp(String[] ordre, ArrayList<String> filename, boolean entete)
	{
		mapVar = new HashMap<String, Integer>();

		for(int i = 0; i < ordre.length; i++)
			mapVar.put(ordre[i], i);
		
		VDD.setOrdreVariables(ordre.length);
		instance = new Instanciation(ordre.length);
		arbre = new VDD();
//		values = new String[ordre.length];
		deconditionneTout();
		
		compileHistorique(filename, entete);
	}*/
	
	public MultiHistoComp(ArrayList<String> filenameInit, boolean entete, ArrayList<String> varAConserver)
	{
		variablesLocal = initVariables(filenameInit, entete, varAConserver);
//		stat = new int[variables.length+1];
//		nbInstancesTriplet = (HashMap<Integer, Integer>[][][]) new HashMap[variables.length][variables.length][variables.length];
	}
	
	public void compile(ArrayList<String> filename, boolean entete)
	{
		compile(filename, entete, -1, null);
	}
	

	public static void saveCPT(String namefile)
	{
		System.out.println("Sauvegarde des CPT");
		ObjectOutputStream oos;
		try {
			oos = new ObjectOutputStream(new FileOutputStream(new File(namefile)));
			oos.writeObject(cpt);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static boolean loadCPT(String namefile)
	{
		ObjectInputStream ois;
		try {
			System.out.println("Chargement des CPT");
			ois = new ObjectInputStream(new FileInputStream(new File(namefile)));
			cpt = (int[][])ois.readObject();
			ois.close();
			return true;
		} catch (Exception e) {
			System.out.println("Lecture des CPT impossible");
			e.printStackTrace();
		}
		return false;
	}
	
	public static void initFamille(HashMap<String,ArrayList<String>> famille)
	{
		familleHashMap = famille;
		MultiHistoComp.famille = new int[variables.length][variables.length];
		for(String s : famille.keySet())
		{
			int[] list = new int[famille.get(s).size()];
			int k = 0;
			for(String p : famille.get(s))
				list[k++] = mapVar.get(p);
			MultiHistoComp.famille[mapVar.get(s)] = list;
		}
	}
	
	public void initCPT()
	{
		System.out.println("Apprentissage des CPT");
		
		cpt = new int[variables.length][];
		for(String s : familleHashMap.keySet())
		{
			IteratorInstancesPartielles iter = new IteratorInstancesPartielles(new Instanciation(), variables, mapVar, familleHashMap.get(s));
			int[] tmp = new int[iter.getNbInstances()];
			int k = 0;
			while(iter.hasNext())
			{
				Instanciation instance = iter.next();
//				System.out.println(k+" "+instance.getIndexCache(this.famille.get(s)));
				tmp[k++] = VDD.getNbInstancesStatic(arbre, instance.values, instance.nbVarInstanciees);
			}
			cpt[mapVar.get(s)] = tmp;
		}
	}

	/**
	 * On met les variables avec le plus de valuations en bas de l'arbre afin de limiter le nombre de nœuds
	 */
	private void triSimpleVariablesLocal()
	{
		for(int i = 0; i < variablesLocal.length; i++)
		{
			int indicemax = 0;
			for(int j = 1; j < variablesLocal.length-i; j++) // j cpmmence à 1 car indicemax vaut déjà 0
			{
				if(variablesLocal[j].domain > variablesLocal[indicemax].domain)
					indicemax = j;
			}
			// On ne fait l'échange que s'il y a besoin
			if(variablesLocal[variablesLocal.length-1-i].domain != variablesLocal[indicemax].domain)
			{
				Variable tmp = variablesLocal[variablesLocal.length-1-i];
				variablesLocal[variablesLocal.length-1-i] = variablesLocal[indicemax];
				variablesLocal[indicemax] = tmp;
			}
		}
	}
	
	private int domainTmp(Variable v, ArrayList<String> variablesFixees)
	{
		if(variablesFixees.contains(v.name))
			return 2 * v.domain;
		return 2 * v.domain + 1;
	}
	
	/**
	 * On met les variables avec le plus de valuations en bas de l'arbre afin de limiter le nombre de nœuds
	 */
	private void triVariablesLocalAvecVarFixees(ArrayList<String> variablesFixees)
	{
		for(int i = 0; i < variablesLocal.length; i++)
		{
			int indicemax = 0;
			for(int j = 1; j < variablesLocal.length-i; j++) // j cpmmence à 1 car indicemax vaut déjà 0
			{
				if(domainTmp(variablesLocal[j], variablesFixees) > domainTmp(variablesLocal[indicemax], variablesFixees))
					indicemax = j;
			}
			// On ne fait l'échange que s'il y a besoin
			if(domainTmp(variablesLocal[variablesLocal.length-1-i], variablesFixees) != domainTmp(variablesLocal[indicemax], variablesFixees))
			{
				Variable tmp = variablesLocal[variablesLocal.length-1-i];
				variablesLocal[variablesLocal.length-1-i] = variablesLocal[indicemax];
				variablesLocal[indicemax] = tmp;
			}
		}
	}

	public void compile(ArrayList<String> filename, boolean entete, ArrayList<String> variablesFixees)
	{
		compile(filename, entete, -1, variablesFixees);
	}
	
	@SuppressWarnings("unchecked")
	public void compile(ArrayList<String> filename, boolean entete, int nbExemplesMax, ArrayList<String> variablesFixees)
	{
		if(variablesFixees == null)
			triSimpleVariablesLocal();
		else
			triVariablesLocalAvecVarFixees(variablesFixees);
		
		if(variables == null)
		{
			variables = variablesLocal;
			IteratorInstances.setVars(variables);
		}
		
		if(mapVar == null)
		{
			mapVar = new HashMap<String, Integer>();
	
			for(int i = 0; i < variables.length; i++)
				mapVar.put(variables[i].name, i);
		}
		
		mapVarLocal = new HashMap<String, Integer>();
		
		for(int i = 0; i < variablesLocal.length; i++)
			mapVarLocal.put(variablesLocal[i].name, i);		
		
		for(int i = 0; i < variablesLocal.length; i++)
			variablesLocal[i].profondeur = mapVar.get(variablesLocal[i].name);

//		VDD.setOrdreVariables(variables);
		arbre = new VDD(variablesLocal);
//		values = new String[ordre.length];
		
		Instanciation.setVars(variables, mapVar);
		InstanceMemoryManager.getMemoryManager().createInstanciation();
		Instanciation.setMemoryManager(InstanceMemoryManager.getMemoryManager());
//		instance = new Instanciation();
//		deconditionneTout();
		
		compileHistorique(filename, entete, nbExemplesMax);
		
		if(nbInstancesPriori == null && usePrecalcul)
		{
			apprendPrecalcul();
		}
/*		System.out.println("Apprentissage des triplets…");
		for(int i = 0; i < variables.length - 1; i++)
			for(int j = i + 1; j < variables.length; j++)
				for(int k = j + 1; k < variables.length; k++)
				{
					nbInstancesTriplet[i][j][k] = new HashMap<Integer,Integer>();
					for(int vi = 0; vi < variables[i].domain; vi++)
						for(int vj = 0; vj < variables[j].domain; vj++)
							for(int vk = 0; vk < variables[k].domain; vk++)
							{
								Instanciation val = new Instanciation();
								val.conditionne(i, vi);
								val.conditionne(j, vj);
								val.conditionne(k, vk);
								nbInstancesTriplet[i][j][k].put((vi*variables[j].domain+vj)*variables[k].domain+vk, arbre.getNbInstances(val.values, val.nbVarInstanciees));
							}
				}*/
	}
	
	@SuppressWarnings("unchecked")
	public void apprendPrecalcul()
	{
		System.out.println("Apprentissage des proba a priori et des paires…");
	
		nbInstancesPaire = (HashMap<Integer, Integer>[][]) new HashMap[variablesLocal.length][variablesLocal.length];
		nbInstancesPriori = (HashMap<Integer, Integer>[]) new HashMap[variablesLocal.length];
	
		for(int i = 0; i < variablesLocal.length; i++)
		{
			nbInstancesPriori[i] = new HashMap<Integer,Integer>();
			for(int vi = 0; vi < variablesLocal[i].domain; vi++)
			{
				Instanciation val = new Instanciation();
				val.conditionne(i, vi);
				nbInstancesPriori[i].put(vi, arbre.getNbInstances(val.values, val.nbVarInstanciees));
			}
		}
			
		for(int i = 0; i < variablesLocal.length - 1; i++)
			for(int j = i + 1; j < variablesLocal.length; j++)
			{
				nbInstancesPaire[i][j] = new HashMap<Integer,Integer>();
				for(int vi = 0; vi < variablesLocal[i].domain; vi++)
					for(int vj = 0; vj < variablesLocal[j].domain; vj++)
					{
						Instanciation val = new Instanciation();
						val.conditionne(i, vi);
						val.conditionne(j, vj);
						nbInstancesPaire[i][j].put(vi*variablesLocal[j].domain+vj, arbre.getNbInstances(val.values, val.nbVarInstanciees));
					}
			}
	}
	
	/**
	 * Initialise les valeurs et les domaines des variables.
	 * IL N'Y A PAS D'APPRENTISSAGE SUR LES VALEURS
	 * @param filename
	 * @param entete
	 * @return
	 */
	private Variable[] initVariables(ArrayList<String> filename, boolean entete, ArrayList<String> varAConserver)
	{
		this.varAConserver = varAConserver;
		// Vérification de toutes les valeurs possibles pour les variables
		Variable[] vars = null;
		LecteurCdXml lect = null;
		
		int nbvar = 0;
		if(varAConserver != null)
			nbvar = varAConserver.size();
		
		int[] conversion = null;
		for(String s : filename)
		{
			lect = new LecteurCdXml();
			lect.lectureCSV(s, entete);

			if(vars == null)
			{
				if(varAConserver == null)
					nbvar = lect.nbvar;
				vars = new Variable[nbvar];
				conversion = new int[lect.nbvar];
				int j = 0;
				for(int i = 0; i < lect.nbvar; i++)
				{
					if(varAConserver == null || varAConserver.contains(lect.var[i]))
					{
						conversion[i] = j;
						vars[j] = new Variable();
						vars[j].name = lect.var[i];
						vars[j].domain = 0;
						j++;
					}
					else
						conversion[i] = -1;
				}
			}

			for(int i = 0; i < lect.nbligne; i++)
			{
				for(int k = 0; k < lect.nbvar; k++)
				{
					if(conversion[k] == -1)
						continue;
					String value = lect.domall[i][k];
					if(!vars[conversion[k]].values.contains(value))
					{
						vars[conversion[k]].values.add(value);
						vars[conversion[k]].domain++;
					}
				}
			}
		}
//		System.out.print(lect.nbvar+ " variables. Domaines :");
//		System.out.print(" "+vars[0].domain);
//		for(int k = 1; k < lect.nbvar; k++)
//			System.out.print(", "+vars[k].domain);
//		System.out.println();
		return vars;
	}
	
	private void compileHistorique(ArrayList<String> filename, boolean entete, int nbExemplesMax)
	{
		for(String s : filename)
		{
			LecteurCdXml lect = new LecteurCdXml();
			lect.lectureCSV(s, entete);
			Integer[] values = new Integer[lect.nbvar];
//			System.out.println(lect.nbligne+" exemples");
			int indiceMax;
			if(nbExemplesMax == -1)
				indiceMax = lect.nbligne;
			else
				indiceMax = Math.min(nbExemplesMax, lect.nbligne);
			
			for(int i = 0; i < indiceMax; i++)
			{
				for(int k = 0; k < lect.nbvar; k++)
				{
					String var = lect.var[k];	
					if(varAConserver == null || varAConserver.contains(var))
//					System.out.print(var+" ("+lect.domall[i][k]+"), ");
						values[mapVar.get(var)] = variablesLocal[mapVarLocal.get(var)].values.indexOf(lect.domall[i][k]);
				}
//				System.out.println();
				arbre.addInstanciation(values);
			}
		}
//		arbre.computeLineaire();
		System.out.println(getNbNoeuds()+" noeuds");
	}
	
	/**
	 * Retourne des proba (entre 0 et 1 donc)
	 * @param variable
	 * @param possibles
	 * @return
	 */
	public HashMap<String, Double> getProbaToutesModalitees(String variable, ArrayList<String> possibles, boolean withZero, Instanciation instance)
	{
		HashMap<String, Double> out = new HashMap<String, Double>();
		HashMap<String, Integer> exemples = getNbInstancesToutesModalitees(variable, possibles, withZero, instance);
		
		double somme = 0.;
		for(Integer i : exemples.values())
			somme += i;
		
		for(String s : exemples.keySet())
			out.put(s, exemples.get(s) / somme);
		
		return out;
	}

	public HashMap<String, Integer> getNbInstancesToutesModalitees(String variable, Instanciation instance)
	{
		return getNbInstancesToutesModalitees(variable, null, false, instance);
	}

	/**
	 * Retourne le nombre d'exemples pour chaque modalité
	 * @param variable
	 * @param possibles
	 * @return
	 */
	public HashMap<String, Integer> getNbInstancesToutesModalitees(String variable, ArrayList<String> possibles, boolean withZero, Instanciation instance)
	{
		int var = mapVar.get(variable);
		if(instance.values[var] != null)
		{
			System.out.println("Attention, variable déjà instanciée");
			instance.deconditionne(var);
		}
		
		HashMap<String, Integer> out = new HashMap<String, Integer>();;

		if(withZero)
			for(String s : variablesLocal[var].values)
				out.put(s, 0);
		
//		System.out.println("Nb exemples : " + arbre.getNbInstances(values, nbVarInstanciees));
		

/*		if(possibles != null) // commenté car pas du tout efficace en temps d'exécution
		{
			out = new HashMap<String, Integer>();
			for(String p : possibles)
			{
				values[var] = p;
				out.put(p, arbre.getNbInstances(values, nbVarInstanciees + 1));
			}
			values[var] = null;
		}
		else
		{*/
			arbre.getNbInstancesToutesModalitees(out, var, instance.values, possibles, instance.nbVarInstanciees);
/*			int somme = 0;
			for(Integer i : out.values())
				somme += i;
			if(somme != arbre.getNbInstances(values, nbVarInstanciees))
				System.out.println("Erreur de calcul du nombre d'instances!");*/
//		}
		
//		conditionne(var, sauv);
		return out;
	}
	
	public int nbModalites(String v)
	{
		return variablesLocal[mapVar.get(v)].domain;
	}

	public static int getNbInstancesCPT(Instanciation instance, int profondeur)
	{
		return cpt[profondeur][instance.getIndexCPT(famille[profondeur])];
	}

	int delay = 0;
	
	public final int getNbInstancesAPriori(String var, String value)
	{
		return nbInstancesPriori[mapVar.get(var)].get(variables[mapVar.get(var)].values.indexOf(value));
	}
	
	public final int getNbInstancesPaires(String var1, String value1, String var2, String value2)
	{
		if(mapVar.get(var1) < mapVar.get(var2))
			return nbInstancesPaire[mapVar.get(var1)][mapVar.get(var2)].get(variables[mapVar.get(var1)].values.indexOf(value1)*variables[mapVar.get(var2)].domain+variables[mapVar.get(var2)].values.indexOf(value2));
		else
			return nbInstancesPaire[mapVar.get(var2)][mapVar.get(var1)].get(variables[mapVar.get(var2)].values.indexOf(value2)*variables[mapVar.get(var1)].domain+variables[mapVar.get(var1)].values.indexOf(value1));
	}
	
	public double getProbaRBNaif(Instanciation instanceReco, String var, String value)
	{
		double priori = nbInstancesPriori[mapVar.get(var)].get(variables[mapVar.get(var)].values.indexOf(value));
		double proba = priori;
		for(Variable v : variables)
		{
			if(instanceReco.values[v.profondeur] != null)
			{
				proba *= getNbInstancesPaires(var, value, v.name, v.values.get(instanceReco.values[v.profondeur]));
				proba /= priori;
			}
		}
		return proba;
	}
	
	public final int getNbInstances(Instanciation instance)
	{
//		System.out.println(mapVarLocal.size());
/*		stat[instance.nbVarInstanciees]++;
		
		if(delay++ % 10000 == 0)
		{
			for(int i = 0; i < variables.length; i++)
				System.out.print(stat[i]+", ");
			System.out.println();
		}*/
		
		if(usePrecalcul)
		{
			if(instance.nbVarInstanciees == 1)
			{
				Integer[] t = instance.getHash(1);
	//			System.out.println("i : "+t[0]+", vi : "+t[1]+", j : "+t[2]+", vj : "+t[3]);
				return nbInstancesPriori[t[0]].get(t[1]);
			}
			else if(instance.nbVarInstanciees == 2)
			{
				Integer[] t = instance.getHash(2);
	//			System.out.println("i : "+t[0]+", vi : "+t[1]+", j : "+t[2]+", vj : "+t[3]);
				return nbInstancesPaire[t[0]][t[2]].get(t[1]*variables[t[2]].domain+t[3]);
			}
		}
/*		else if(instance.nbVarInstanciees == 3)
		{
			Integer[] t = instance.getHash(3);
//			System.out.println("i : "+t[0]+", vi : "+t[1]+", j : "+t[2]+", vj : "+t[3]);
			return nbInstancesTriplet[t[0]][t[2]][t[4]].get((t[1]*variables[t[2]].domain+t[3])*variables[t[4]].domain+t[5]);
		}*/
		return VDD.getNbInstancesStatic(arbre, instance.values, instance.nbVarInstanciees);
	}	
	
	@Deprecated
	public int getNbInstancesAncien(Instanciation instance)
	{
		if(instance.nbVarInstanciees == 1)
		{
			Integer[] t = instance.getHash(1);
//			System.out.println("i : "+t[0]+", vi : "+t[1]+", j : "+t[2]+", vj : "+t[3]);
			return nbInstancesPriori[t[0]].get(t[1]);
		}
		else if(instance.nbVarInstanciees == 2)
		{
			Integer[] t = instance.getHash(2);
//			System.out.println("i : "+t[0]+", vi : "+t[1]+", j : "+t[2]+", vj : "+t[3]);
			return nbInstancesPaire[t[0]][t[2]].get(t[1]*variablesLocal[t[2]].domain+t[3]);
		}
		return arbre.getNbInstances(instance.values, instance.nbVarInstanciees);
	}
	
	public int getNbNoeuds()
	{
		return arbre.getNbNoeuds();
	}
	
	public ArrayList<String> getVarConnues(Instanciation instance)
	{
		ArrayList<String> varConnues = new ArrayList<String>();
		for(int i = 0; i < variablesLocal.length; i++)
			if(instance.values[i] != null)
				varConnues.add(variablesLocal[i].name);
		return varConnues;
	}
	/*
	public IteratorInstances getIterator(String var, Instanciation instance)
	{
		ArrayList<String> cutset = new ArrayList<String>();
		cutset.add(var);
		return new IteratorInstances(instance, variables, mapVar, cutset);
	}
*/

	/**
	 * Retourne le nombre total d'exemples
	 * @return
	 */
	public final int getNbInstancesTotal()
	{
		return arbre.nbInstances;
	}

	public final ArrayList<String> getValues(String variable)
	{
		return variables[mapVar.get(variable)].values;
	}
	
	public static HashMap<String, Integer> getMapVar()
	{
		return mapVar;
	}

	public void printADD(int nb)
	{
		arbre.print(nb);
	}
	
}