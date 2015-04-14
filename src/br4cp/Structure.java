package br4cp;

import java.util.ArrayList;


abstract class Structure {
	
	public Structure(){
	}
	
	public abstract Structure copie();
	
	public abstract void operation(Structure str);
	
	//initialisation de la valeur a partir de l'operation de deux structures (util pour la fonction add (times)
	public abstract void initOperation(Structure str, Structure str2);
	
	public abstract Structure normaliseInf(ArrayList<Structure> liste);
	
	public abstract void normaliseSup(ArrayList<Structure>liste, Structure remonte);
	
	public abstract void normaliseSup(Structure str, Structure remonte);
	
	public abstract boolean isNeutre();
	
	public abstract boolean min(Structure comp1, Structure comp2);
	
	public abstract boolean max(Structure comp1, Structure comp2);
	
	public abstract boolean isabsorbant();
	
	public abstract void toNeutre();
	
	public abstract String toDot();
	
	public abstract String toTxt();
	
	public abstract int hashCode();
	
	public abstract boolean equals(Structure comp);
	
	public abstract String printstr();
	
	public abstract boolean inaccessible();
	
	public abstract void rendreInaccessible();
	
	public abstract double getvaldouble();
}
