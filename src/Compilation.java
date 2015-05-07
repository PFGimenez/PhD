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

import heuristique_contraintes.HeuristiqueContraintes;
import heuristique_contraintes.HeuristiqueContraintesAmilastre;
import heuristique_contraintes.HeuristiqueContraintesDomaineMaxDomaineMaxEcartMax;
import heuristique_contraintes.HeuristiqueContraintesDomaineMaxDomaineMaxEcartMaxHardFirst;
import heuristique_contraintes.HeuristiqueContraintesDomaineMaxEcartMaxMin;
import heuristique_contraintes.HeuristiqueContraintesEcartMaxMaxScore;
import heuristique_contraintes.HeuristiqueContraintesInversion;
import heuristique_contraintes.HeuristiqueContraintesProdDomainesEcartMaxHardFirst;
import heuristique_contraintes.HeuristiqueContraintesRandom;
import heuristique_contraintes.HeuristiqueContraintesRien;
import heuristique_contraintes.HeuristiqueContraintesTaille;
import heuristique_contraintes.HeuristiqueContraintesdurete;
import heuristique_variable.HeuristiqueVariable;
import heuristique_variable.HeuristiqueVariable7;
import heuristique_variable.HeuristiqueVariable8;
import heuristique_variable.HeuristiqueVariable9;
import heuristique_variable.HeuristiqueVariableBW;
import heuristique_variable.HeuristiqueVariableForce;
import heuristique_variable.HeuristiqueVariableMCF;
import heuristique_variable.HeuristiqueVariableMCSinv;
import heuristique_variable.HeuristiqueVariableMCSinvPlusUn;
import heuristique_variable.HeuristiqueVariableMCSinvPlusUnAutreVersion;
import heuristique_variable.HeuristiqueVariableOrdreAscendance;
import heuristique_variable.HeuristiqueVariableOrdreChoisi;
import heuristique_variable.HeuristiqueVariableOrdreRandom;

import java.util.ArrayList;
import java.io.File;

import br4cp.*;

public class Compilation {

public static void main(String[] args) {
		
		ArrayList<String> fichiersACompiler;
		HeuristiqueVariable[] heuristiquesVariables = {new HeuristiqueVariableOrdreRandom(), new HeuristiqueVariableOrdreChoisi(), new HeuristiqueVariableMCF(), new HeuristiqueVariableBW(), new HeuristiqueVariableMCSinv(), new HeuristiqueVariableForce(), new HeuristiqueVariableMCSinvPlusUn(), new HeuristiqueVariableOrdreAscendance(), new HeuristiqueVariable7(), new HeuristiqueVariable8(), new HeuristiqueVariable9(), new HeuristiqueVariableMCSinvPlusUnAutreVersion()};
		HeuristiqueContraintes[] heuristiquesContraintes = {new HeuristiqueContraintesInversion(), new HeuristiqueContraintesRandom(), new HeuristiqueContraintesRien(), new HeuristiqueContraintesTaille(), new HeuristiqueContraintesAmilastre(), new HeuristiqueContraintesEcartMaxMaxScore(), null, new HeuristiqueContraintesDomaineMaxDomaineMaxEcartMax(), new HeuristiqueContraintesDomaineMaxEcartMaxMin(), new HeuristiqueContraintesDomaineMaxDomaineMaxEcartMaxHardFirst(), new HeuristiqueContraintesProdDomainesEcartMaxHardFirst(), new HeuristiqueContraintesdurete()};
		boolean arg_err=false;
		boolean arg_plus;
		int arg_heuristique=0;
		int arg_heuristique_cons=0;
		String arg_formefinale="";
		String arg_FichierSortie="";
		boolean flag_fichierSortie;
		boolean flag_beg;
		String warning="";
		boolean warn=true;
		String commande="";
		int arg_affich_text;
		
		/*args=new String[6];
		args[0]="asia";
		args[1]="-t=t";
		args[2]="";
		args[3]="-h=-1";
		args[4]="-hcon=0";
		args[5]="-text=1";*/
	
	
		if(args.length<1){
			System.out.println("pas assez d'arguments");
			System.exit(0);
		}
		

		
			//names
		fichiersACompiler=new ArrayList<String>();
		for(int i=0; i<args.length; i++){
			if(!args[i].startsWith("-") && args[i].length()>0){				//nom de fichier
				File f = new File(args[i]);					//extention deja renseignee
				if(f.exists() && !f.isDirectory()) {
					fichiersACompiler.add(args[i]);
				}else{							
					f = new File(args[i]+".xml");					//extention deja renseignee
					if(f.exists() && !f.isDirectory()) {
						fichiersACompiler.add(args[i]+".xml");
					}else{
						f = new File(args[i]+".XML");					//extention deja renseignee
						if(f.exists() && !f.isDirectory()) {
							fichiersACompiler.add(args[i]+".XML");
						}else{												//rien
							System.out.println("fichier "+args[i]+" inconnu");
						}
					}
				}
			}
		}
			
		if(fichiersACompiler.size()==0){
			System.out.println("aucun fichier a compiler ou a lire");
			System.out.println("programme interompu");
			System.exit(0);
		}
		
		
		//type
		arg_plus=true;
		for(int i=0; i<args.length; i++){
			if(args[i].startsWith("-t=")){
				warn=false;
				//type
				commande= args[i].substring(3);
				if(commande.compareTo("plus")==0 || commande.compareTo("time")==0 || commande.compareTo("p")==0 || commande.compareTo("t")==0 || commande.compareTo("+")==0 || commande.compareTo("*")==0){
					if (commande.compareTo("time")==0 || commande.compareTo("t")==0 || commande.compareTo("*")==0 )
						arg_plus=false;
					else
						arg_plus=true;
				}else{
					arg_err=true;
					System.out.println("erreur :  option \"-t\" doit etre \"plus\" ou \"time\"");
				}
			}
		}
		if(warn){
			warning+="warrning :  type non renseigné \n";
		}

		
		//h
		arg_heuristique=3;
		for(int i=0; i<args.length; i++){
			if(args[i].startsWith("-h=")){
				commande= args[i].substring(3);
						try {
					arg_heuristique=Integer.parseInt(commande);
					if (arg_heuristique>10 || arg_heuristique<-1){
						arg_err=true;
						System.out.println("erreur :  l'heuristique \"-h\" doit etre un nombre compris entre 0 et 10");
					}
				} catch (NumberFormatException e) {
					arg_err=true;
					System.out.println("erreur :  l'heuristique \"-h\" doit etre un nombre");
				}
			}	
		}
			
		//hcontraintes
			
		arg_heuristique_cons=0;
		for(int i=0; i<args.length; i++){
			if(args[i].startsWith("-hcon=")){
				commande= args[i].substring(6);
				try {
					arg_heuristique_cons=Integer.parseInt(commande);
					if (arg_heuristique_cons>10 || arg_heuristique_cons<-1){
						arg_err=true;
						System.out.println("erreur :  l'heuristique \"-hcon\" doit etre un nombre compris entre 0 et 10");
					}
				} catch (NumberFormatException e) {
					arg_err=true;
					System.out.println("erreur :  l'heuristique \"-hcon\" doit etre un nombre");
				}
			}	
		}
		
		
		//finalform
		arg_formefinale="prout";
		for(int i=0; i<args.length; i++){
			if(args[i].startsWith("-ff=")){
				arg_formefinale=args[i].substring(4);
				if(!(arg_formefinale.contains("AADD") || arg_formefinale.contains("SLDDp") || arg_formefinale.contains("SLDDt") || arg_formefinale.contains("ADD") ) ){
					arg_err=true;
					System.out.println("erreur : forme final (" + args[4] + ") non accepté. possibilités : AADD, SLDDp, SLDDt, ADD");
				}
			}
		}
		
		//savename
		flag_fichierSortie=false;
		for(int i=0; i<args.length; i++){
			if(args[i].startsWith("-save=")){
				arg_FichierSortie=args[i].substring(6);
				flag_fichierSortie=true;
			}
		}
		
		//noskip
		
		flag_beg=false;
		for(int i=0; i<args.length; i++){
			if(args[i].compareToIgnoreCase("-noskip")==0){
				flag_beg=true;
			}
		}
		
		//affichetext
		arg_affich_text=1;
		for(int i=0; i<args.length; i++){
			if(args[i].startsWith("-text=")){
				commande= args[i].substring(6);
				try {
					arg_affich_text=Integer.parseInt(commande);
					if (arg_affich_text>3 || arg_affich_text<0){
						arg_err=true;
						System.out.println("erreur :  affichage de texte \"-text\" doit etre un nombre compris entre 0 et 3");
					}
				} catch (NumberFormatException e) {
					arg_err=true;
					System.out.println("erreur :  affichage de texte \"-text\" doit etre un nombre");
				}
			}	
		}
		
		
		if(arg_err){
			System.out.println("programme interompu");
			System.exit(0);
		}
		
		if(warn && arg_affich_text>0){
			System.out.println(warning);
		}
		
		SALADD cs;
		cs=new SALADD();
		
		// Le "+1" de heuristiquesVariables[arg_heuristique+1] vient du fait que les numéros d'heuristique commencent à -1 et le tableau commence à 0.
		// Idem pour heuristiquesContraintes avec +2
		
		cs.procedureCompilation(fichiersACompiler, arg_plus, heuristiquesVariables[arg_heuristique+1], heuristiquesContraintes[arg_heuristique_cons+2], arg_formefinale, arg_FichierSortie, flag_fichierSortie, flag_beg, arg_affich_text);

}
	
}

/*
 * 		ArrayList<String> plop=new ArrayList<String>();
		plop.add("v1");
		plop.add("v2");
		plop.add("v3");
		plop.add("v4");
		plop.add("v5");
		plop.add("v6");
		plop.add("v7");
		plop.add("v8");
		plop.add("v9");
		plop.add("v10");
		plop.add("v11");
		plop.add("v12");
		plop.add("v13");
		plop.add("v14");
		plop.add("v15");
		plop.add("v16");
		plop.add("v17");
		plop.add("v18");
		plop.add("v19");
		plop.add("v21");
		plop.add("v23");
		plop.add("v24");
		plop.add("v25");
		plop.add("v26");
		plop.add("v27");
		plop.add("v28");
		plop.add("v29");
		plop.add("v30");
		plop.add("v31");
		plop.add("v32");
		plop.add("v33");
		plop.add("v34");
		plop.add("v35");
		plop.add("v36");
		plop.add("v37");
		plop.add("v38");
		plop.add("v39");
		plop.add("v40");
		plop.add("v41");
		plop.add("v42");
		plop.add("v43");
		plop.add("v44");
		plop.add("v45");
		plop.add("v46");
		plop.add("v93");
		plop.add("v51");
		plop.add("v52");
		plop.add("v53");
		*/
