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
import compilateur.Var;

/**
 * Classe pour manipuler un historique compilé
 * @author pgimenez
 *
 */

public class HistoComp implements Serializable
{
	private static final long serialVersionUID = 1L;
	private VDD arbre;
	private String[] values;
	private int nbVarInstanciees;
	private HashMap<String, Integer> mapVar; // associe au nom d'une variable sa position dans values
	
	public HistoComp(String[] ordre)
	{
		mapVar = new HashMap<String, Integer>();

		for(int i = 0; i < ordre.length; i++)
			mapVar.put(ordre[i], i);
		
		VDD.setOrdreVariables(ordre.length);
		arbre = new VDD();
		values = new String[ordre.length];
		deconditionneTout();
	}
	
	public HistoComp(Var[] ordre)
	{
		mapVar = new HashMap<String, Integer>();

		for(int i = 0; i < ordre.length; i++)
			mapVar.put(ordre[i].name, i);

		VDD.setOrdreVariables(ordre);
		arbre = new VDD();
		values = new String[ordre.length];
		deconditionneTout();
	}

	public void compileHistorique(String filename)
	{
		ArrayList<String> f = new ArrayList<String>();
		f.add(filename);
		compileHistorique(f);
	}
	
	public void compileHistorique(ArrayList<String> filename)
	{
		for(String s : filename)
		{
			LecteurCdXml lect = new LecteurCdXml();
			lect.lectureCSV(s);
			String[] values = new String[lect.nbvar];
//			System.out.println(lect.nbligne+" exemples");
			for(int i = 0; i < lect.nbligne; i++)
			{
				for(int k = 0; k < lect.nbvar; k++)
				{
					String var = lect.var[k].trim();	
//					System.out.print(var+" ("+lect.domall[i][k]+"), ");
					values[mapVar.get(var)] = lect.domall[i][k];
				}
//				System.out.println();
				arbre.addInstanciation(values);
			}
		}
	}

	/**
	 * Sauvegarde par sérialisation
	 */
	public void save(String s)
	{
		File fichier =  new File(s+".sav") ;
		ObjectOutputStream oos;
		try {
			oos = new ObjectOutputStream(new FileOutputStream(fichier));
			oos.writeObject(this);
			oos.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Chargement d'un historique compilé sauvegardé
	 * @return
	 */
	public static HistoComp load(String s)
	{
		HistoComp out;
		File fichier =  new File(s+".sav") ;
		ObjectInputStream ois;
		try {
			ois = new ObjectInputStream(new FileInputStream(fichier));
			out = (HistoComp)ois.readObject() ;
			ois.close();
			return out;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}

	public void conditionne(String v, String value)
	{
		conditionne(mapVar.get(v), value);
	}
	
	public void conditionne(Var v, String value)
	{
		conditionne(v.name, value);
	}
	
	private void conditionne(int v, String value)
	{
		if(values[v] != value)
		{
			if(values[v] == null)
				nbVarInstanciees++;
			values[v] = value;
		}
	}
	
	public void deconditionne(String v)
	{
		deconditionne(mapVar.get(v));
	}
	
	public void deconditionne(Var v)
	{
		deconditionne(v.name);
	}

	private void deconditionne(int v)
	{
		if(values[v] != null)
		{
			nbVarInstanciees--;
			values[v] = null;
		}
	}
	
	public void deconditionneTout()
	{
		for(int i = 0; i < values.length; i++)
			values[i] = null;
		nbVarInstanciees = 0;
	}
	
	public HashMap<String, Integer> getNbInstancesToutesModalitees(Var var, ArrayList<String> possibles)
	{
		return getNbInstancesToutesModalitees(var.name, possibles);
	}

	public HashMap<String, Integer> getNbInstancesToutesModalitees(String var, ArrayList<String> possibles)
	{
		return getNbInstancesToutesModalitees(mapVar.get(var), possibles);
	}
	
	private HashMap<String, Integer> getNbInstancesToutesModalitees(int var, ArrayList<String> possibles)
	{
		String sauv = values[var];
		
		if(values[var] != null)
			System.out.println("Attention, variable déjà instanciée");
			
		deconditionne(var);
		HashMap<String, Integer> out;

		if(possibles != null)
		{
			out = new HashMap<String, Integer>();
			for(String p : possibles)
			{
				values[var] = p;
				out.put(p, arbre.getNbInstances(values, nbVarInstanciees + 1));
			}
		}
		else
			out = arbre.getNbInstancesToutesModalitees(var, values, nbVarInstanciees);

		values[var] = null;
		conditionne(var, sauv);
		return out;
	}
	
	public int getNbInstances()
	{
		return arbre.getNbInstances(values, nbVarInstanciees);
	}

}
