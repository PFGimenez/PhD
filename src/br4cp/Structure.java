package br4cp;

import java.util.ArrayList;


class Structure {
	
	public Structure(){
	}
	
	public Structure copie(){
				System.out.println("@Structure : impossible? (aucune classe fille?)");
		return null;
	}
	
	public void operation(Structure str){
//				System.out.println("@Structure : cas impossible1");
		//nadau
	}
	
	//initialisation de la valeur a partir de l'operation de deux structures (util pour la fonction add (times)
	public void initOperation(Structure str, Structure str2){
				System.out.println("@Structure : cas impossible10");
	}
	
	public Structure normaliseInf(ArrayList<Structure> liste){
			System.out.println("@Structure : cas impossible2");
		return new Structure();
	}
	
	public void normaliseSup(ArrayList<Structure>liste, Structure remonte){
				System.out.println("@Structure : cas impossible3");
		//yalalheeeeee
	}
	
	public void normaliseSup(Structure str, Structure remonte){
			System.out.println("@Structure : cas impossible4");
		//yalalheeeeee
	}
	
	public boolean isNeutre(){
		System.out.println("@Structure : cas impossible5");
		return true;
	}
	
	public boolean min(Structure comp1, Structure comp2){
			System.out.println("@Structure : cas impossible13");
		return false;
	}
	
	public boolean max(Structure comp1, Structure comp2){
			System.out.println("@Structure : cas impossible14");
		return false;
	}
	
	public boolean isabsorbant(){
			System.out.println("@Structure : cas impossible12");
		return false;
	}
	
	public void toNeutre(){
			System.out.println("@Structure : cas impossible6");
	}
	
	public String toDot(){
		System.out.println("@Structure : cas impossible7");
		return "";
	}
	
	public String toTxt(){
			System.out.println("@Structure : cas impossible7.2");
		return "";
	}
	
	public int hashCode(){
		System.out.println("@Structure : cas impossible8");
		return 0;
	}
	
	public boolean equals(Structure comp){
			System.out.println("@Structure : cas impossible9");
		return true;
	}
	
	public String printstr(){
			System.out.println("@Structure : cas impossible0");
		return "Structure";
	}
	
	public boolean inaccessible(){
			System.out.println("@Structure : cas impossible14");
		return false;
	}
	
	public void rendreInaccessible(){
			System.out.println("@Structure : cas impossible15");
	}
	
	public double getvaldouble(){
			System.out.println("@Structure : cas impossible16");
		return 0;
	}
}
