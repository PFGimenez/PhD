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

import java.io.File;

import br4cp.*;


public class Load {
	
public static void main(String[] args) {
		
		boolean arg_err=false;
		String arg_formefinale="";
		String arg_FichierSortie="";
		boolean flag_fichierSortie;
		boolean flag_beg;
		String warning="";
		boolean warn=true;
		String commande="";
		int arg_affich_text;
		String arg_read;
		
/*		args=new String[6];
		args[0]="big";
		args[1]="-t=+";
		args[2]="bigPrices";
		args[3]="-h=5";
		args[4]="-hcon=0";
		args[5]="-text=3";*/
		

	
	
		if(args.length<1){
			System.out.println("pas assez d'arguments");
			System.exit(0);
		}
		
		

		
		//read
		arg_read="";
		for(int i=0; i<args.length; i++){
			if(!args[i].startsWith("-") && args[i].length()>0){				//nom de fichier
				commande= args[i];
				if(commande.length()>0){				//nom de fichier
					File f = new File(commande);					//extention deja renseignee
					if(f.exists() && !f.isDirectory()) {
						arg_read=commande;
					}else{							
						f = new File(commande+".dot");					//extention deja renseignee
						if(f.exists() && !f.isDirectory()) {
							arg_read=commande+".dot";
						}else{
							f = new File(args[i]+".DOT");					//extention deja renseignee
							if(f.exists() && !f.isDirectory()) {
								arg_read=commande+".DOT";
							}else{												//rien
								System.out.println("fichier en lecture "+args[i]+" inconnu");
								System.out.println("programme interompu");
								System.exit(0);
							}
						}
					}
					System.out.println("entrez un nom de fichier a lire : \"nom_fichier.dot\"");
					System.out.println("programme interompu");
					System.exit(0);
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
		cs=new SALADD(null);
		
		// Le "+1" de heuristiquesVariables[arg_heuristique+1] vient du fait que les numéros d'heuristique commencent à -1 et le tableau commence à 0.
		// Idem pour heuristiquesContraintes avec +2
		
		cs.procedureChargement(arg_read, arg_formefinale, arg_FichierSortie, flag_fichierSortie, flag_beg, arg_affich_text);
	}
	
}


