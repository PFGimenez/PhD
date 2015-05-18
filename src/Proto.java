import java.util.ArrayList;

import br4cp.LecteurCdXml;
import br4cp.Protocol;
import br4cp.SALADD;

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

public class Proto {

	public static void main(String[] args) {
		
		SALADD cs;
		cs=new SALADD(Protocol.BT);
		long sum=0, temps, start, start2, start3, temps2, temps3, sum2=0, sum3=0;
		int nbactions=0;
		LecteurCdXml lecteur=new LecteurCdXml();

		
		cs.readProblem("asia");
		//cs.transformation("AADD");

		//cs.readProblem("big");
		

		//big fcp 22141 39933
		//big bt 85862 117646
		//0med fcp 4865 5776
		//0med bt 3678 4371
		//0small fcp 2217 2813
		//0small bt 2937 3412
		
		//000cancer 1719 1954
		//00asia 2900 3229
		//00car 615 756
		//0alarm 2590 2921
		//hail 7678 9157

		//00smallp 1176 1352
		//00medp 6394 8451
		//0bigp 13560 20854

		
		//HeuristiqueVariable[] heuristiquesVariables = {new HeuristiqueVariableOrdreRandom(), new HeuristiqueVariableOrdreChoisi(), new HeuristiqueVariableMCF(), new HeuristiqueVariableBW(), new HeuristiqueVariableMCSinv(), new HeuristiqueVariableForce(), new HeuristiqueVariableMCSinvPlusUn(), new HeuristiqueVariableOrdreAscendance(), new HeuristiqueVariable7(), new HeuristiqueVariable8(), new HeuristiqueVariable9(), new HeuristiqueVariableMCSinvPlusUnAutreVersion()};
		//HeuristiqueContraintes[] heuristiquesContraintes = {new HeuristiqueContraintesInversion(), new HeuristiqueContraintesRandom(), new HeuristiqueContraintesRien(), new HeuristiqueContraintesTaille(), new HeuristiqueContraintesAmilastre(), new HeuristiqueContraintesEcartMaxMaxScore(), null, new HeuristiqueContraintesDomaineMaxDomaineMaxEcartMax(), new HeuristiqueContraintesDomaineMaxEcartMaxMin(), new HeuristiqueContraintesDomaineMaxDomaineMaxEcartMaxHardFirst(), new HeuristiqueContraintesProdDomainesEcartMaxHardFirst(), new HeuristiqueContraintesdurete()};

		//ArrayList<String> aaa=new ArrayList<>();
		//aaa.add("small.xml");
		//aaa.add("smallPrices.xml");
		//cs.compilation(aaa, true, heuristiquesVariables[4], heuristiquesContraintes[2], 1);
		cs.initialize();
		cs.initialize();
		cs.initialize();
		
		start=System.currentTimeMillis();
		for(int i=0; i<10000; i++)
			cs.initialize();
		temps=System.currentTimeMillis()-start;

		cs.transformation("AADD");

		start2=System.currentTimeMillis();
		for(int i=0; i<10000; i++)
			cs.initialize();
		temps2=System.currentTimeMillis()-start2;
		
		System.out.println(temps+" "+temps2);
/*
		//cs.readProblem("hbigPrices");
		//cs.initialize();
		for(int i=0; i<100; i++){
			cs.readProblem("big");
			//cs.transformation("AADD");
		//int i=0;
			lecteur.lectureXml("scenarios-big-minimal.xml", i);
			start2=System.currentTimeMillis();
			//cs.readProblem("souffleuse");
			cs.initialize();
			temps2=System.currentTimeMillis()-start2;

			//System.out.println(System.currentTimeMillis()-start2+"ms init");
			//System.out.print(cs.getFreeVariables().size()+" ");
			//System.out.println(cs.getFreeVariables());
			//System.out.println();
			start=System.currentTimeMillis();
			temps3=0;
			for(int j=0; j<lecteur.dom.length; j++){
				start3=System.currentTimeMillis();
				//System.out.println(lecteur.dom[j]+" -> "+cs.getCurrentDomainOf(lecteur.var[j]));
				cs.assignAndPropagate(lecteur.var[j], lecteur.dom[j]);
				//System.out.println(cs.maxCost() +" " + cs.minCost());
				temps3+=System.currentTimeMillis()-start3;
				//System.out.println(temps+"ms");
				cs.getFreeVariables();
				//System.out.print(cs.getFreeVariables().size()+" ");
				//System.out.println(cs.getFreeVariables());
			}
			temps=System.currentTimeMillis()-start;
			//temps2=System.currentTimeMillis()-start2;
			System.out.println(i+"("+lecteur.dom.length+") "+temps+"ms "+temps2+"ms "+temps3+"ms "+cs.isConfigurationComplete()+" "+cs.maxCost()+" "+cs.minCost());
			nbactions+=lecteur.dom.length;
			sum+=temps;
			sum2+=temps2;
			sum3+=temps3;
		}
			
			//BT							h=3
			//0(33) 349ms 1901ms true 0 0
			//0(33) 441ms 2025ms true 0 0
			//0(33) 411ms 2001ms true 0 0
			//FCP							h=5
			//0(33) 358ms 1509ms true 8998 8998
			//0(33) 282ms 1438ms true 8998 8998
			//0(33) 304ms 1435ms true 8998 8998
//
			//BT							h=5
			//0(33) 511ms 3492ms true 0 0
			//0(33) 484ms 3474ms true 0 0
			//0(33) 499ms 3594ms true 0 0
			//FCP							h=3
			//0(33) 671ms 3015ms true 8998 8998
			//0(33) 662ms 2748ms true 8998 8998
			//0(33) 738ms 3015ms true 8998 8998

			
			System.out.println("---");
			System.out.println(sum+"ms");
			System.out.println(sum2+"ms");
			System.out.println(sum3+"ms");
			System.out.println(nbactions+" actions");
			*/
		}
			
}
