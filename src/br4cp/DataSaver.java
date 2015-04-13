package br4cp;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * Classe statique qui gere les sauvegardes et chargements.
 * @author Stud, pf
 *
 */

public class DataSaver {

    private DataSaver()
    {}
    
    public static void supprimer(String filename)
    {
        // System.out.println("Suppression de " + filename);
        try
        {
            (new java.io.File(filename)).delete();
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public static <T> void sauvegarder(T obj, String filename)
    {
    	try {
			java.io.File fichier_creation;
			FileOutputStream fichier;
			ObjectOutputStream oos;
			
			fichier_creation = new java.io.File(filename);
			fichier_creation.createNewFile();
			fichier = new FileOutputStream(filename);
			oos = new ObjectOutputStream(fichier);
			oos.writeObject(obj);
			oos.flush();
			oos.close();
		}
		catch(Exception e)
		{
			System.out.println("Erreur lors de la création ou de l'écriture du fichier. Vérifiez que vous avez les droits et que le dossier existe bien.");
			e.printStackTrace();
		}
    }
    
    public static Object charger(String filename)
    {
		try {
			FileInputStream fichier = new FileInputStream(filename);
			ObjectInputStream ois = new ObjectInputStream(fichier);
			Object obj = ois.readObject();
			ois.close();
			return obj;
		}
		catch(Exception e)
		{
			e.printStackTrace();
			return null;
		}
    }
}