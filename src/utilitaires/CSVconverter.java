package utilitaires;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

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

/**
 * Permet de générer des .csv à partir de set_exemples et de set_scenarios
 * @author pgimenez
 *
 */

public class CSVconverter
{
	public static void main(String[] args)
	{
		String dataset = "renault_big_sans_contraintes";
		String prefixData = "datasets/"+dataset+"/";

		final boolean onlyCompilation = true;
		int nbVar = 87;
		try {
			for(int i = 0; i < 10; i++)
			{
				FileWriter fichier, fichier2;
				BufferedWriter output, outputScenario;
				Runtime.getRuntime().exec("cp "+prefixData+"debut.xml "+prefixData+"set"+i+"_exemples_pour_compilation.xml");
				String nomFichier = prefixData+"set"+i+"_exemples.xml";
				if(!onlyCompilation)
				{
					fichier = new FileWriter(prefixData+"set"+i+"_exemples.csv");
					output = new BufferedWriter(fichier);
					fichier2 = new FileWriter(prefixData+"set"+i+"_scenario.csv");
					outputScenario = new BufferedWriter(fichier2);
				}
				FileWriter fichier3 = new FileWriter(prefixData+"set"+i+"_exemples_pour_compilation.xml", true);
				BufferedWriter outputXML = new BufferedWriter(fichier3);
				File fXmlFile = new File("./"+nomFichier);
				DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
				DocumentBuilder dBuilder;
				dBuilder = dbFactory.newDocumentBuilder();
				Document doc = dBuilder.parse(fXmlFile);	
				doc.getDocumentElement().normalize();
				
				HashMap<String, String> exempleMap = new HashMap<String, String>();
				
				NodeList nListTousExemples = doc.getElementsByTagName("exemple");
				
				Node exemple;
				Node varVal;
				int temp = 0, s, total;
				boolean first = true;
				while((exemple = nListTousExemples.item(temp)) != null)
				{
					if(!first)
						outputXML.write("|");
					outputXML.newLine();

					NodeList nList2 = ((Element) exemple).getElementsByTagName("value");
					for(int k = 0; k < nbVar; k++)
					{
						varVal = nList2.item(k);
						Element eElement = (Element) varVal;
						if(!onlyCompilation)
							outputScenario.write(eElement.getAttribute("var"));
						exempleMap.put(eElement.getAttribute("var"), eElement.getAttribute("val"));			
	
						if(!onlyCompilation)
						{
							if(k < nbVar - 1)
								outputScenario.write(",");
							else
								outputScenario.newLine();
						}
					}
					if(first)
					{
						s = 0;
						total = exempleMap.size();
						if(!onlyCompilation)
						{
							for(String c : exempleMap.keySet())
							{
								s++;
								output.write(c);							
								if(s != total)
									output.write(", ");
							}
							output.newLine();
						}
						first = false;
					}
					outputXML.write("1:");
					s = 0;
					total = exempleMap.size();
					for(String c : exempleMap.keySet())
					{
						s++;
						if(!onlyCompilation)
							output.write(exempleMap.get(c));
						outputXML.write(exempleMap.get(c));
						if(s != total)
						{
							if(!onlyCompilation)
								output.write(", ");
							outputXML.write(" ");							
						}
					}
					if(!onlyCompilation)
						output.newLine();
					temp++;
				}
				
				outputXML.newLine();
				outputXML.write("</relation>");
				outputXML.newLine();
				outputXML.write("</relations>");
				outputXML.newLine();
				outputXML.write("<constraints initialCost=\"0\" maximalCost=\"9223372036854775807\" nbConstraints=\"1\">");
				outputXML.newLine();
				outputXML.write("<constraint name=\"historyLarge\" arity=\""+nbVar+"\" reference=\"relLargeHist\" "); 
				outputXML.write("scope=\"");
				for(String c : exempleMap.keySet())
				{
					outputXML.write(c+" ");
				}
				outputXML.write("\"/>");
				outputXML.newLine();
				outputXML.write("</constraints>");
				outputXML.newLine();
				outputXML.write("</instance>");
				outputXML.newLine();
				if(!onlyCompilation)
				{
					output.close();
					outputScenario.close();
				}
				outputXML.close();
			}
			
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("Fini");
	
	}


}
