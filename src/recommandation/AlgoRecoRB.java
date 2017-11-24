/*   (C) Copyright 2015, Gimenez Pierre-François
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

package recommandation;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import compilateurHistorique.DatasetInfo;
import compilateurHistorique.Instanciation;

/**
 * Algorithme de recommandation qui se base sur un réseau bayésien
 * @author Pierre-François Gimenez
 *
 */

public abstract class AlgoRecoRB extends Clusturable
{
	public abstract void apprendRB(String file);
	protected String algo;
	
	public AlgoRecoRB(String algo)
	{
		this.algo = algo;
	}
	
	public void learnBN(List<String> filename, boolean entete)
	{
		Random r = new Random();
		learnBN(filename, entete, "/tmp/BN"+r.nextInt());
	}

	public void learnBN(List<String> filename, boolean entete, String outfile)
	{
		if(!new File(outfile).exists())
		{
			String command = "Rscript --vanilla script_learn_BN.R "+outfile+" "+algo+" "+(entete ? "TRUE" : "FALSE");
			for(String n : filename)
				command += " "+n+".csv";

			try {
				Thread.sleep(500);
			} catch (InterruptedException e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
			}
			Process proc = null;
			try {
				proc = Runtime.getRuntime().exec(command);
				proc.waitFor();
				
			} catch (IOException | InterruptedException e) {
				e.printStackTrace();
			}
			if(!new File(outfile).exists())
			{
				try {
					System.out.println("Erreur lors de l'apprentissage ! "+command);
					BufferedReader output = new BufferedReader(new InputStreamReader(proc.getInputStream()));
					String line;
					line = output.readLine();
					while(line != null)
					{
						System.out.println(line);
						line = output.readLine();
					}
					output = new BufferedReader(new InputStreamReader(proc.getErrorStream()));
					line = output.readLine();
					while(line != null)
					{
						System.err.println(line);
						line = output.readLine();
					}
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				assert false;
			}
			System.out.println("Bayesian network "+outfile+" not found ; learning done.");
		}		
		apprendRB(outfile);
	}
	

	public void learnBN(DatasetInfo dataset, List<Instanciation> instances, long code)
	{
		String rbFile = "tmp/RB-"+code+".xml";
		System.out.println("Réseau bayésien : "+rbFile);
		if(new File(rbFile).exists())
			apprendRB(rbFile);
		else
		{
			BufferedWriter output = null;
			try {
				String tmpCsv = "/tmp/temporary-csv-file";
				FileWriter fichier = new FileWriter(tmpCsv+".csv");
				output = new BufferedWriter(fichier);
				
				// L'entête
				for(int i = 0; i < dataset.vars.length; i++)
				{
					if(i != 0)
						output.write(",");
					output.write(dataset.vars[i].name);
				}
				
				for(int j = 0; j < instances.size(); j++)
				{
					output.newLine();
					for(int i = 0; i < dataset.vars.length; i++)
					{
						if(i != 0)
							output.write(",");
						output.write(dataset.vars[i].values.get(instances.get(j).values[i]));
					}
				}
				output.close();
				learnBN(Arrays.asList(tmpCsv), true, rbFile);
			}
			catch(IOException e)
			{
				e.printStackTrace();
			}
			finally
			{
				try {
					if(output != null)
						output.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
	}
}
