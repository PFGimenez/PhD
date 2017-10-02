package compilateur;

/*   (C) Copyright 2013, Schmidt Nicolas
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

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import java.io.*;
import java.util.HashMap;
import java.util.Map;

 
public class LecteurCdXml {
	
	public String[] dom;
	public String[] var;

	public String[][] domall;
	public String[][] ordre;

	
	public int nbligne;
	public int nbvar;

	
	public LecteurCdXml(){

	}
	
	public void lectureXml(String nomFichier, int senar) {
		
		NodeList nList;
		try {
		File fXmlFile = new File("./"+nomFichier);
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		Document doc = dBuilder.parse(fXmlFile);
		doc.getDocumentElement().normalize();

		  /*System.out.println("domain : " + getTagValue("domain", eElement));
	      String attrValue = eElement.getAttribute("name");*/
		

		
		//////senar numb//////
		nList = doc.getElementsByTagName("scenario");
		Node nNode = nList.item(senar);
		if (nNode.getNodeType() == Node.ELEMENT_NODE) {
			Element eElement = (Element) nNode;
	
			NodeList nList2 = eElement.getElementsByTagName("add");

			dom=new String[nList2.getLength()];
			var=new String[nList2.getLength()];
			
			
			for(int temp = 0; temp < nList2.getLength(); temp++) {
				Node nNode2 = nList2.item(temp);
				if (nNode2.getNodeType() == Node.ELEMENT_NODE) {
					Element eElement2 = (Element) nNode2;
					var[temp]=eElement2.getAttribute("variable");
					dom[temp]=eElement2.getAttribute("value");
				}
			}
		}
					

		} catch (Exception e) {
			e.printStackTrace();
		}		
							
	}
	
public int lectureTxt(String nomFichier, int senar, int size) {
		int month=-1;
	
		String ligne="";
		int egal;
		dom=new String[size];
		
		InputStream ips;
		InputStreamReader ipsr=null;
		BufferedReader br=null;
		
		try {
			ips=new FileInputStream(nomFichier+".txt"); 
			ipsr=new InputStreamReader(ips);
			br=new BufferedReader(ipsr);
			
			for(int i=0; i<=senar; i++){
				ligne=br.readLine();
			}
			
			br.close();
			if(ligne.contains("m1 ")) month=1;
			if(ligne.contains("m2 ")) month=2;
			if(ligne.contains("m3 ")) month=3;
			if(ligne.contains("m4 ")) month=4;
			if(ligne.contains("m5 ")) month=5;
			if(ligne.contains("m6 ")) month=6;
			if(ligne.contains("m7 ")) month=7;
			if(ligne.contains("m8 ")) month=8;
			if(ligne.contains("m9 ")) month=9;
			if(ligne.contains("m10 ")) month=10;
			if(ligne.contains("m11 ")) month=11;
			if(ligne.contains("m12 ")) month=12;

			
			
			
			ligne = ligne.substring(16);
			var=ligne.split(" ");
			for(int j=0; j<var.length; j++){
				egal=var[j].indexOf('=');
				dom[j]=var[j].substring(egal+1, var[j].length());
				var[j]=var[j].substring(0, egal);
			}
				
			
			
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
		return month;
							
	}
	
	public void ecritureInit(String nameFile){
    	FileWriter fW;
		String name_file= "./" + nameFile + ".xml";
		try{
			fW = new FileWriter(name_file);
			fW.close(); 
		}catch(java.io.IOException exc){System.out.println("pb de fichier: " + exc);}
		//fichier
		
	}
	
	public void ecriture(String nameFile, Var v, String dom, int min, int max){
    	FileWriter fW;
		String name_file= "./" + nameFile + ".xml";
		String s, smin, smax, temp1, temp2;
		
		try{
			fW = new FileWriter(name_file, true);
			smin=Integer.toString(min);
			temp1=smin.substring(0, smin.length()-2);
			temp2=smin.substring(smin.length()-2, smin.length());
			smin=temp1+","+temp2;
			smax=Integer.toString(max);
			temp1=smax.substring(0, smax.length()-2);
			temp2=smax.substring(smax.length()-2, smax.length());
			smax=temp1+","+temp2;
			
			s="\t\t<add variable=\""+v.name+"\" value=\""+dom+"\" min=\""+smin+"\" max=\""+smax+"\"/>\n";
			fW.write(s);
	    	fW.close(); 
    	
		}catch(java.io.IOException exc){System.out.println("pb de fichier: " + exc);}
    
    }
	
	public void ecriture2(String nameFile){
    	FileWriter fW;
		String name_file= "./" + nameFile + ".xml";
		String s;
		
		try{
			fW = new FileWriter(name_file, true);
			s="\t</scenario>\n\t<scenario type=\"GC-U\">\n";
			fW.write(s);
	    	fW.close(); 
    	
		}catch(java.io.IOException exc){System.out.println("pb de fichier: " + exc);}
	}
	
	
	public void lectureCSV(String nomFichier, boolean entete) {
		
		String ligne="";
		nbligne=1;
		
		InputStream ips;
		InputStreamReader ipsr=null;
		BufferedReader br=null;
		
		/*
		 * Permet de ne pas avoir de doublons d'objets
		 */
		Map<String, String> values = new HashMap<String, String>();

		String separateur = ",";

		try {
			ips=new FileInputStream(nomFichier+".csv"); 
			ipsr=new InputStreamReader(ips);
			br=new BufferedReader(ipsr);
			ligne=br.readLine();

			if(!ligne.contains(separateur))
				separateur = ";";
			if(!ligne.contains(separateur))
				separateur = " ";
			
			nbvar = ligne.split(separateur).length;
			
			while(br.readLine()!=null)
				nbligne++;

			var=new String[nbvar];

			domall=new String[nbligne][nbvar];
			
			br.close();
			ips=new FileInputStream(nomFichier+".csv"); 
			ipsr=new InputStreamReader(ips);
			br=new BufferedReader(ipsr);
			
			if(entete)
			{
				nbligne--; // la première ne compte pas
				ligne=br.readLine();
				var = ligne.split(separateur);
				for(int i = 0; i < var.length; i++)
					var[i] = var[i].trim();
			}
			else // pas d'entete : on donne les noms par défaut aux variables
			{
				// On reprend la même convention que R qui s'occupe d'apprendre les réseaux bayésiens
				for(int i = 0; i < nbvar; i++)
					var[i] = "V"+(i+1);
			}
			
			for(int j=0; j<nbligne; j++)
			{
				ligne=br.readLine();
				domall[j] = ligne.split(separateur);
				assert domall[j].length == var.length : "Ligne "+j+" a "+domall[j].length+" champ et pas "+var.length;

				for(int i = 0; i < var.length; i++)
				{
					domall[j][i] = domall[j][i].trim();
					String val = values.get(domall[j][i]);
					if(val == null)
						values.put(domall[j][i], domall[j][i]);
					else
						domall[j][i] = val;
				}
			}
			br.close();
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
									
	}
	
	public void lectureCSVordre(String nomFichier) {
		
		String ligne="";
		
		InputStream ips;
		InputStreamReader ipsr=null;
		BufferedReader br=null;
		
		try {
			ips=new FileInputStream(nomFichier+".csv"); 
			ipsr=new InputStreamReader(ips);
			br=new BufferedReader(ipsr);
			
			ordre=new String[nbligne][nbvar];

			int id;
			for(int j=0; j<nbligne; j++){
				ligne=br.readLine();
				id=ligne.indexOf(',');
				for(int i=0; i<nbvar-1; i++){
					id=ligne.indexOf(',');
					ordre[j][i]=ligne.substring(0, id).trim();
					ligne=ligne.substring(id+1);
				}
				ordre[j][nbvar-1]=ligne.trim();
			}
				
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
									
	}

			
}

