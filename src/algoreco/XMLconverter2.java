package algoreco;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Utilisé afin de générer les fichiers d'apprentissage sous forme XML
 * @author pf
 *
 */

public class XMLconverter2 implements AlgoReco
{
	private FileWriter fichier;
	private BufferedWriter output;
	
	boolean firstTime;
	
	@Override
	public void initialisation(ArrayList<String> variables)
	{}

	@Override
	public void apprendContraintes(String filename)
	{}

	@Override
	public void apprendDonnees(ArrayList<String> filename, int nbIter)
	{
		try {
			if(output != null)
			{
				output.write("</exemple>");
				output.newLine();
				output.close();
			}
			fichier = new FileWriter("datasets/set"+nbIter+"_exemples.xml");
			output = new BufferedWriter(fichier);
			output.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
			output.newLine();
			output.newLine();
		} catch (IOException e) {
			e.printStackTrace();
		}
		firstTime = true;
	}

	@Override
	public String recommande(String variable, ArrayList<String> possibles)
	{
		return possibles.get(0);
	}

	@Override
	public void setSolution(String variable, String solution) {
		try {
			output.write("	<value var=\""+variable+"\" val=\""+solution+"\">");
			output.newLine();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

	@Override
	public void oublieSession() {
		try {
			if(!firstTime)
			{
				output.write("</exemple>");
				output.newLine();
				output.newLine();
			}
			firstTime = false;
			output.write("<exemple>");
			output.newLine();
			output.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void termine()
	{
		try {
			output.write("</exemple>");
			output.newLine();
			output.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
