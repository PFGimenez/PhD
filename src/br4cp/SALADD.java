package br4cp;

	/*   (C) Copyright 2014, Schmidt Nicolas
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


import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.Map;


import java.io.*;


	
public class SALADD implements Configurator {		
	
	private VDD x;//testVDD;
	private boolean isHistorique;
	private String inX;
	private Protocol p;
	
	private ArrayList<String> historiqueOperations;

	/** 
	 * Constructeur
	 */
	public SALADD(){
		p=null;
		x=null;
		isHistorique=false;
	}

	/** 
	 * Constructeur
	 * pour Protocol BR4CP 
	 */

	public SALADD(Protocol pro){
		if(pro!=null)
			p=pro;
		else
			p=Protocol.BT;
		
		historiqueOperations=new ArrayList<String>();
		inX="";
		
		x=null;
		isHistorique=false;
		
	}
	
	/**
	 * Compilation du fichier de contraintes file_name
	 * 
	 * @param file_name : chemin/nom du fichier a compiler (extention incluse)
	 * @param arg_plus : nature du probleme. TRUE si additif, FALSE si multiplicatif
	 * @param arg_heuristique : heuristique d'ordonnancement des variables a utiliser (valeur conseillée : '5')
	 * @param arg_heuristique_cons : heuristique d'ordonnancement des cointraintes a utiliser (valeur conseillée : '7')
	 * @param arg_affich_text : niveau d'affichage de texte sur la sortie standard. De 0 (pas de texte) à 3 (beaucoup de texte)
	 */
	public void compilation(String file_name, boolean bif, boolean arg_plus, HeuristiqueVariable arg_heuristique, HeuristiqueContraintes arg_heuristique_cons, int arg_affich_text){
		ArrayList<String> s=new ArrayList<String>();
		s.add(file_name);
		compilation(s, bif, arg_plus, arg_heuristique, arg_heuristique_cons, arg_affich_text);
	}

	/**
	 * Compilation du (ou des) fichier(s) de contraintes file_names
	 * Attention : Si plusieurs fichiers, ceux ci doivent porter sur un meme ensemble de variables
	 * 
	 * @param file_names : chemin/nom des fichiers a compiler (extention incluse)
	 * @param arg_plus : nature du probleme. TRUE si additif, FALSE si multiplicatif
	 * @param arg_heuristique : heuristique d'ordonnancement des variables a utiliser (valeur conseillée : '5')
	 * @param arg_heuristique_cons : heuristique d'ordonnancement des cointraintes a utiliser (valeur conseillée : '7')
	 * @param arg_affich_text : niveau d'affichage de texte sur la sortie standard. De 0 (pas de texte) à 3 (beaucoup de texte)
	 */
	public void compilation(ArrayList<String> file_names, boolean bif, boolean arg_plus, HeuristiqueVariable arg_heuristique, HeuristiqueContraintes arg_heuristique_cons, int arg_affich_text){
		
		isHistorique=false;
		
		long start= System.currentTimeMillis();
		long end;
		
		Ordonnancement ord;			
		ord = new Ordonnancement();
		LecteurXML xml=new LecteurXML(ord);
		if(!bif){
			xml.lecture(file_names.get(0));
		}else{	
			xml.lectureBIF(file_names.get(0), arg_plus);
		}

		for(int i=1; i<file_names.size(); i++){
			xml.lectureSuite(file_names.get(i));
		}
		
		
		ord.addVarialbes(xml.getVariables());
		if(xml.getNbVariables()!=ord.size())
			System.out.println("bug nb variables");
		
		
		ord.reordoner(xml.getInvolvedVariablesEntree(), arg_heuristique, false);			//<---
		xml.actualiseVariables();
		xml.compactConstraint();
		
		UniqueHashTable uht=new UniqueHashTable(ord.size());
		x =new VDD(ord.getVariables(), uht, arg_plus);
		uht.ellagage(xml);

			
		x.flagMult=(!arg_plus);											//<---
		x.flagPlus=arg_plus;											//<---
			
	
		int contraintes[][];
		String contraintesS[][];
		Structure Poids[];
		Structure defaultCost;
		boolean softConstraint;
		boolean conflictsConstraint;
		
		System.out.println();
		xml.reorganiseContraintes(arg_heuristique_cons);
		
		if(arg_affich_text==3){
			ord.afficherOrdre();
			xml.afficheOrdreContraintes();
		}
		
	
		for(int i1=0; i1<xml.nbConstraints; i1++){
			int i=xml.equiv(i1);
		
			contraintesS=xml.getConstraintS(i);
			
			
			
			if(contraintesS!=null){
				Poids=xml.getPoid(i);
				if(contraintesS.length!=0){
					defaultCost=xml.getDefaultCost(i);
					softConstraint=xml.getSoftConstraint(i);
					conflictsConstraint=xml.getConflictsConstraint(i);
				

					contraintes=new int[contraintesS.length][contraintesS[0].length];
								
				//traduction en valeur de 0 a n (au lieu de strings)
					for(int j=0; j<contraintes.length; j++){
						for(int k=1; k<contraintes[j].length; k++){
							contraintes[j][k]=ord.getVariables().get(k-1).conv(contraintesS[j][k]);
						}
					}

			
					x.valeurChemin(contraintes, Poids, defaultCost, softConstraint, conflictsConstraint);


					
					//uht.detect();
					if(arg_affich_text>=2){
						end=System.currentTimeMillis();
						System.out.println(i1+":sldd"+(i+1)+"/"+xml.nbConstraints+"  nbnoeuds:" + x.uht.size() + " (" + x.uht.sizeArcs() + ")   " + (end-start)/1000+","+(end-start)%1000 + "s");
					}
				}
			}
		}

	}

	/**
	 * Compilation d'un fichier d'historique en vue de la recomandation
	 * 
	 * @param file_name : chemin/nom du fichier d'historique a compiler (extention incluse)
	 * @param arg_affich_text : niveau d'affichage de texte sur la sortie standard. De 0 (pas de texte) à 3 (beaucoup de texte)
	 */
	public void compilationDHistorique(String file_name, int arg_affich_text){

		isHistorique=true;
		
		long start= System.currentTimeMillis();
		long end;
		
		Ordonnancement ord;			
		ord = new Ordonnancement();
		LecteurXML xml=new LecteurXML(ord);
		
		xml.lecture(file_name);
	
//			xml.month(12,12);
		ord.addVarialbes(xml.getVariables());
//			ord.supprmonth();
		
		if(xml.getNbVariables()!=ord.size())
			System.out.println("bug nb variables");
		
		
//			ord.reordoner(xml.getInvolvedVariablesEntree(), 0, false);			//<---
		xml.actualiseVariables();
//			xml.compactConstraint();
		
		UniqueHashTable uht=new UniqueHashTable(ord.size());
		x =new VDD(ord.getVariables(), uht, true);

			
		x.flagMult=false;											//<---
		x.flagPlus=true;											//<---
			
	
		int contraintes[][];
		String contraintesS[][];
		Structure Poids[];
		Structure defaultCost;
		boolean softConstraint;
		boolean conflictsConstraint;
		
//			xml.reorganiseContraintes(0);
		
	
		for(int i1=0; i1<xml.nbConstraints; i1++){
			int i=xml.equiv(i1);
		
			contraintesS=xml.getConstraintS(i);
			if(contraintesS!=null){
				Poids=xml.getPoid(i);
				if(contraintesS.length!=0){
					defaultCost=xml.getDefaultCost(i);
					softConstraint=xml.getSoftConstraint(i);
					conflictsConstraint=xml.getConflictsConstraint(i);
				

					contraintes=new int[contraintesS.length][contraintesS[0].length];
								
				//traduction en valeur de 0 a n (au lieu de strings)
					for(int j=0; j<contraintes.length; j++){
						for(int k=1; k<contraintes[j].length; k++){
							contraintes[j][k]=ord.getVariables().get(k-1).conv(contraintesS[j][k]);
						}
					}
									
					x.valeurChemin(contraintes, Poids, defaultCost, softConstraint, conflictsConstraint);
					
					//uht.detect();
					if(arg_affich_text>=2){
						end=System.currentTimeMillis();
						System.out.println("nbnoeuds:" + x.uht.size() + " (" + x.uht.sizeArcs() + ")   " + (end-start)/1000+","+(end-start)%1000 + "s");
					}	
					
				}	
			}
		}
	}
	
	
	public void procedureCompilation(ArrayList<String> FichiersACompiler, boolean arg_plus, HeuristiqueVariable arg_heuristique, HeuristiqueContraintes arg_heuristique_cons, String arg_formefinale, String arg_FichierSortie, boolean flag_fichierSortie, boolean flag_beg, int arg_affich_text){
		
		long start= System.currentTimeMillis();
//			long end;
		

		compilation(FichiersACompiler, false, arg_plus, arg_heuristique, arg_heuristique_cons, arg_affich_text);
		

		//affiche les resultats, es supprim les noeuds beg si besoin
		x.affichageResultats(arg_affich_text, start, flag_beg);

		
		x.transformation(arg_formefinale, arg_affich_text);


		if(flag_fichierSortie){
			x.toDot(arg_FichierSortie, false);
		}
		
		
	}
	
	/**
	 * chargement d'un fichier dot representant un diagram de decision
	 * 
	 * @param file_name : chemin/nom du fichier d'historique a compiler (extention incluse)
	 * @param arg_affich_text : niveau d'affichage de texte sur la sortie standard. De 0 (pas de texte) à 3 (beaucoup de texte)
	 */
	public void chargement(String file_name, int arg_affich_text){
		LecteurDot l=new LecteurDot(file_name);
		x=l.getVDD();
		
		if(arg_affich_text>=1)
			System.out.println("chargement " + x.uht.size() + " (" + x.uht.sizeArcs() + ")");
	}


	public VDD procedureChargement(String arg_FichierACharger, String arg_formefinale, String arg_FichierSortie, boolean flag_fichierSortie, boolean flag_beg, int arg_affich_text){

		///////////////lecture fichier////////////////
		chargement(arg_FichierACharger, arg_affich_text);

		
		x.transformation(arg_formefinale, arg_affich_text);

		return x;
	}
	
	
	public void reinitialisation(){
		x.deconditionerAll();
		if(isHistorique)
			historiqueOperations.clear();
		else
			x.minMaxConsistance();
	}
	/**
	 * transformation du SLDD courant en un autre langage
	 * 
	 * @param arg_formefinale : valeurs possibles : {"AADD", "SLDDp", "SLDDt", "ADD"} (avec SLDDp et SLDDt un SLDD de nature respectivement additive et multiplicative).
	 */
	public void transformation(String arg_formefinale){
		x.transformation(arg_formefinale, 0);
	}
	
	/**
	 * donne le nombre de noeuds dans le diagramme
	 * 
	 * @return nombre de noeuds dans le diagramme
	 */
	public int nb_nodes(){
		return x.uht.size();
	}
	
	/**
	 * donne le nombre d'arcs dans le diagramme
	 * 
	 * @return nombre d'arcs dans le diagramme
	 */
	public int nb_edges(){
		return x.uht.sizeArcs();
	}
	
	/**
	 * donne le nombre de modeles du probleme
	 * 
	 * @return nombre de modeles
	 */
	public int nb_models(){
		return x.counting();
	}
	
	/**
	 * donne le nombre d'echantillons dans l'historique correspondant a la configuration en cours
	 * 
	 * @return nombre de modeles
	 */
	public int nb_echantillonsHistorique(){
		if(isHistorique=true)
			return x.countingpondere();
		else
			System.out.println("la fonction nb_echantillonsHistorique() ne conscerne que le traitement des historiques");
		return -1;
	}
	
	/**
	 * calcule la variance existant entre les differentes variables de d'un historique
	 * prefix_file_name 
	 * 
	 * @param methode : methode de calcule de variance utilise. valeur conseillee : '2'
	 * @param prefix_file_name : nom de lecture / sauvegarde (suivant l'existance) du fichier de sauvegarde de la variance
	 */
	public void calculerVarianceHistorique(TestIndependance methode, String prefix_file_name){
		if(isHistorique==true){
			x.variance(methode, prefix_file_name);
		}else{
			System.out.println("la fonction calculerVariance() ne conscerne que le traitement des historiques");
		}
	}
	
	/**
	 * enregistre le diagramme au format .dot
	 * notez que le format .dot peut etre lu par la bibliotheque graphitz afin d'afficher le diagramme. commande : $ dot -Tpdf file_name.dot -o file_name.pdf
	 * 
	 * @param file_name : chemin/nom du fichier de sauvegarde
	 */
	public void saveToDot(String file_name){
		x.toDot(file_name, false);
	}
	
	
	/**
	 * enregistre le diagramme au format .xml
	 * 
	 * @param file_name : chemin/nom du fichier de sauvegarde
	 */
	public void saveToXml(String file_name){
		x.toXML(file_name);
	}

	
	/**
	 * recomandation sur une variable
	 * 
	 * @param var : nom de la variable a recomander
	 * @return un association valeur->probabilite pour la recomandation
	 */
	public Map<String, Double> reco(String var, TestIndependance test){//ààààààààààààààààààààààààààààààààààà
		if(isHistorique){
			Var v=x.getVar(var);
			return x.reco(v, historiqueOperations, test);
		}else{
			System.out.println("la fonction recomandation() ne conscerne que le traitement des historiques");
			return null;
		}
	}
	
	/**
	 * Calcul d'inférence, à utiliser sur un SLDDx appris d'un réseau bayésien
	 * Les valeurs renvoyées sont des probabilités à une constante multiplicative près.
	 * @param var
	 * @param test
	 * @return
	 */
	public Map<String, Double> inference(String var){//ààààààààààààààààààààààààààààààààààà
		Var v=x.getVar(var);
		return x.calculeDistributionAPosteriori(v, historiqueOperations);
	}
	
	public boolean equivalence(SALADD s){
		return this.x.equivalence(s.x);
	}
	
	/**
	 * affecter une valeur à une variable.
	 * 
	 * @param var
	 * @param val
	 */

	
//		public VDD getVDD(){
//			return x;
//		}
	
	
    //////////////
    // Protocol //
    //////////////
    




    	/**
    	 * Read a configuration file. Both the xml format and the textual format
    	 * will be provided. It is up to the solver to choose which format to use.
    	 * Note that the prices are expected to be found in a file with a "_prices"
    	 * postfix.
    	 * 
    	 * 
    	 * @param problemName
    	 *            the path to the problem, without the extension (.xml or .txt)
    	 */
    public void readProblem(String problemName){
    			
    	ArrayList<String> pbnames=new ArrayList<String>();
		pbnames.add(problemName+".xml");

    	String problemNamePriceornot;
    	boolean priced=false;
		if(p==Protocol.FCP || p==Protocol.GC_p || p==Protocol.GC_Expl || p==Protocol.GC_Res ){
			priced=true;
			problemNamePriceornot=problemName+"_P";
			pbnames.add(problemName+"Prices.xml");
		}else{
			problemNamePriceornot=problemName;
		}
    			
    			if(x==null || inX.compareTo(problemNamePriceornot)!=0){
    				File f=new File(problemNamePriceornot+"_compiled.dot");
    				if(f.canRead()){
    					System.out.println("lecture du fichier compilé \""+problemNamePriceornot+"_compiled.xml\"");
    					
    					//LecteurDot lcd=new LecteurDot(problemNamePriceornot+"_compiled");
    					this.chargement(problemNamePriceornot+"_compiled", 0);
    					inX=problemNamePriceornot;
    				}else{
    					System.out.println("compilation (attention, cette operation peut prendre plusieurs minutes)");
    					if(problemName.compareTo("small")==0 || problemName.compareTo("medium")==0 || (problemName.compareTo("big")==0 && priced)){										//si big unpricced, alors heuristique 3
    						procedureCompilation(pbnames, true, new HeuristiqueVariableMCSinvPlusUn(), new HeuristiqueContraintesDomaineMaxDomaineMaxEcartMaxHardFirst(), "", (problemNamePriceornot+"_compiled"), true, true, 0);
    					}else{																				//sinon heuristique 5
    						procedureCompilation(pbnames, true,  new HeuristiqueVariableMCSinv(), new HeuristiqueContraintesDomaineMaxDomaineMaxEcartMaxHardFirst(), "", (problemNamePriceornot+"_compiled"), true, true, 0);
    					}

    					inX=problemNamePriceornot;
    				}
    			}else{
    			//	System.out.println("Réinitialisation du problème");
    				x.deconditionerAll();
    			}
    	}
    
    	/**
    	 * Such method should be used by the configurator to perform the tasks on the configuration problem
    	 * before the first user choice. 
    	 * This method can be used for instance to maintain GIC on the initial configuration problem.
    	 * This method MUST BE called after {@link #readProblem(String)} and before any other method of the interface.
    	 */
		public void initialize(){
			x.minMaxConsistance();
		}

		//public void assignAndPropagate(String var, String val){
    	protected void assignAndPropagateNoMaj(String var, String val){
    		if(!isPresentInCurrentDomain(var, val))
    			System.out.println(val+" non presente dans "+var+". aucune operation effectue...");
    		else{
	    		Var v=x.getVar(var);
				x.conditioner(v, v.conv(val));
				x.minMaxConsistance();
    		}
    	}
    	
    	/**
    	 * Assign a specific value to a variable.
    	 * 
    	 * @param var
    	 * @param val
    	 * @return true iff the assignment can be done
    	 * @pre getCurrentDomainOf(var).contains(val)
    	 */
    	public void assignAndPropagate(String var, String val){
    		if(!isPresentInCurrentDomain(var, val) && !isHistorique)
    			System.out.println(val+" non presente dans "+var+". aucune operation effectue.");
    		else{
	    		Var v=x.getVar(var);
				x.conditioner(v, v.conv(val));
				x.minMaxConsistanceMaj(v.pos, true);
    		}
    		
    		if(isHistorique){
	    		historiqueOperations.add(var);
	    		historiqueOperations.add(val);
    		}
    	}
    	
    	protected void assignAndPropagateOpt(String var, String val){
    		if(!isPresentInCurrentDomain(var, val))
    			System.out.println(val+" non presente dans "+var+". aucune operation effectue..");
    		else{
	    		Var v=x.getVar(var);
				x.conditioner(v, v.conv(val));
				x.minMaxConsistanceMajopt(v.pos, true);
				
				//x.GICup();
    		}
    	}
    	
    	protected void unassignAndRestoreSansMaj(String var){
    		Var v=x.getVar(var);
    		x.deconditioner(v);
    		x.minMaxConsistance();
    	}
    	
    	/**
    	 * Unassign a specific variable
    	 * 
    	 * @param var
    	 */
    	public void unassignAndRestore(String var){
    		Var v=x.getVar(var);
    		x.deconditioner(v);
    		x.minMaxConsistanceMaj(v.pos, false);
    		
    		if(isHistorique){
	    		int index=historiqueOperations.indexOf(var);
	    		historiqueOperations.remove(index);
	    		historiqueOperations.remove(index);
    		}
    	}
    	
    	protected void unassignAndRestoreOpt(String var){
    		Var v=x.getVar(var);
    		x.deconditioner(v);
    		x.minMaxConsistanceMajopt(v.pos, false);
    	}

    	/**
    	 * Get the minimal price of the configurations compatible with the current
    	 * choices.
    	 * 
    	 * @return the cost of the configuration
    	 */
    	public int minCost(){
    		return (int)x.min.getvaldouble();
    	}

    	/**
    	 * Provide a full configuration of minimal cost.
    	 * 
    	 * @return a full assignment var->value of minimal cost (given by {@link #minCost()}
    	 */
    	public Map<String, String> minCostConfiguration(){
    		return x.minCostConfiguration();
    	}

    	/**
    	 * Get the maximal price of the configurations compatible with the current
    	 * choices.
    	 * 
    	 * @return the cost of the configuration
    	 */
    	public int maxCost(){
    		return (int)x.max.getvaldouble();
    	}    	
    	/**
    	 * Provide a full configuration of maximal cost.
    	 * 
    	 * @return a full assignment var->value of maximal cost (given by {@link #maxCost()}
    	 */
    	public Map<String, String> maxCostConfiguration(){
    		return x.maxCostConfiguration();
    	}

    	/**
    	 * @inv getSizeOfCurrentDomain(var) == getCurrentDomainOf(var).size()
    	 */
    	public int getSizeOfCurrentDomainOf(String var){
    		return x.getVar(var).consistenceSize();    		
    	}
    	
    	/**
    	 * @inv getSizeOfDomain(var) == getDomainOf(var).size()
    	 */
    	public int getSizeOfDomainOf(String var){
    		return x.getVar(var).getDomainSize();
    	}


    	/**
    	 * 
    	 * @param var
    	 * @param val
    	 * @return
    	 * @inv isCurrentInCurrentDomain(var,val)==
    	 *      getCurrentDomainOf(var).contains(val)
    	 */
    	public boolean isPresentInCurrentDomain(String var, String val){
    		Var v=x.getVar(var);
    		return v.consVal[v.conv(val)];
    	}

    	public Set<String> getCurrentDomainOf(String var){
    		Set<String> s=new HashSet<String>();
    		Var v=x.getVar(var);
    		for(int i=0; i<v.domain; i++){
    			if(v.consVal[i])
    				s.add(v.valeurs.get(i));
    			
    		}
    		return s;
    	}
    	
    	public Set<String> getDomainOf(String var){
    		Set<String> s=new HashSet<String>();
    		if(x!=null){
	    		Var v=x.getVar(var);
	    		for(int i=0; i<v.domain; i++){
	    			s.add(v.valeurs.get(i));
	    		}
    		}
    		return s;
    	}

    	/**
    	 * Retrieve for each valid value of the variable the minimal cost of the
    	 * configuration.
    	 * 
    	 * @param var
    	 *            a variable id
    	 * @return a map value->mincost
    	 */
    	public Map<String, Integer> minCosts(String var){
    		Var v=x.getVar(var);
    		Map<String, Integer> m;
    		m=x.minCosts(v.pos);
    		x.minMaxConsistanceMaj(v.pos, true);
    		return m;
    	} 

    	/**
    	 * Retrieve for each valid value of the variable the maximal cost of the
    	 * configuration.
    	 * 
    	 * @param var
    	 *            a variable id
    	 * @return a map value->maxcost
    	 */
    	public Map<String, Integer> maxCosts(String var){
    		Var v=x.getVar(var);
    		Map<String, Integer> m;
    		m=x.maxCosts(v.pos);
    		x.minMaxConsistanceMaj(v.pos, true);
    		return m;
    	} 

    	/**
    	 * Get all unassigned variables.
    	 * 
    	 * @return a set of non assigned variables.
    	 */
    	public Set<String> getFreeVariables(){
    		Set<String> s=new HashSet<String>();
    		for(int i=0; i<x.variables.size(); i++){
    			if(x.variables.get(i).consistenceSize()>1)
    				s.add(x.variables.get(i).name);
    		}
    		return s;
    	}

    	/**
    	 * Check that there is no more choice for the user.
    	 * 
    	 * @return true iff there is exactly one value left per variable.
    	 */
    	public boolean isConfigurationComplete(){
    		return getFreeVariables().size()==0;	
    	}

    	/**
    	 * Check there there is at least one value in each domain. Note that
    	 * depending of the level of consistency used, the configuration may of may
    	 * not be finally consistent.
    	 * 
    	 * @return true iff there is at least one value left per variable.
    	 */
    	public boolean isPossiblyConsistent(){
    		System.out.println("m&m : "+x.max.getvaldouble()+" "+x.min.getvaldouble());
    		return x.min.getvaldouble()!=-1;
    	}

    	//lapin compris
    	public Set<String> getAlternativeDomainOf(String var){
    		return null;
    	}
	
    	public void infos(String var){
    		Var v=x.getVar(var);
    		x.countingpondereOnFullDomain(v);
    	}

}


	/*		int iterations=30;
			int []v=new int[iterations];
			int []d=new int[iterations];
			long start2= System.nanoTime();
			long end2;
			long sum=0;
			
			for(int i=0; i<iterations; i++){
				v[i]=(int)Math.floor(Math.random()*x[0].variables.size());
				d[i]=(int)Math.floor(Math.random()*x[0].variables.get(v[i]).domain);
			}	*/	
			
			


			
			/*
			y=x[0].clone();
			x[0]=y.clone();
			end2= System.nanoTime();
			System.out.println("clone :  " + (double)(end2-start2) /1000000000+ "s");

			for(int i=0; i<iterations; i++){
				//v=(int)Math.floor(Math.random()*x[0].variables.size());
				//d=(int)Math.floor(Math.random()*x[0].variables.get(v).domain);
				x[0].conditioner(v[i]+1,d[i]);
				x[0].minMaxConsistance();
				x[0].deconditioner(v[i]+1);
			}
			
			for(int i=0; i<iterations; i++){
				start2= System.nanoTime();
				//v=(int)Math.floor(Math.random()*x[0].variables.size());
				//d=(int)Math.floor(Math.random()*x[0].variables.get(v).domain);
				x[0].conditioner(v[i]+1,d[i]);
				x[0].minMaxConsistance();
				end2=System.nanoTime();
				sum+=end2-start2;
				x[0].deconditioner(v[i]+1);
			}
			System.out.println("opt :  " + (double)(sum) /1000000000+ "s");
			
			sum=0;
			

			for(int i=0; i<iterations; i++){
				start2= System.nanoTime();
				//v=(int)Math.floor(Math.random()*x[0].variables.size());
				//d=(int)Math.floor(Math.random()*x[0].variables.get(v).domain);
				y.conditionerTrue(v[i]+1,d[i]);			
				end2=System.nanoTime();
				sum+=end2-start2;
				y=null;
				y=x[0].clone();
			}
			System.out.println("cd :  " + (double)(sum) /1000000000+ "s");*/


			
			
			//end2=System.nanoTime();
			//System.out.println("co :  " + (double)(end2-start2) /1000000000+ "s");
			//System.out.println("co :  " + (double)(sum) /1000000000+ "s");


/*	String nameFile="CDdaniel";
	LecteurCdXml lxd;
	lxd=new LecteurCdXml();
	lxd.ecritureInit(nameFile);
	for(int j=0; j<1000; j++){
	x[0].deconditionerAll();
	//lxd.lectureXml("smallGloutonSenarDaniel.xml", j, x[0].variables.size());
	lxd.lectureTxt("scenarios-big", j, x[0].variables.size());
	
	System.out.println(j);
	x[0].minMaxConsistance();
	for(int i=0; i<x[0].variables.size(); i++){
		Var v=x[0].getVar(lxd.var[i]);
		if(v.consistenceSize()>1){
			x[0].conditioner(v, v.conv(lxd.dom[i]));
			x[0].minMaxConsistance();
			lxd.ecriture(nameFile, v, lxd.dom[i], (int)x[0].min.getvaldouble(), (int)x[0].max.getvaldouble());
		}
	}
	lxd.ecriture2(nameFile);
	}*/