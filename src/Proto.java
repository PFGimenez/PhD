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

public class Proto {

	public static void main(String[] args) {
		
		SALADD cs;
		cs=new SALADD(Protocol.BT);
		long sum=0, temps, start, start2, start3, temps2, sum2=0;
		int nbactions=0;
		LecteurCdXml lecteur=new LecteurCdXml();

		
		cs.readProblem("small");
		
		//cs.readProblem("big");
		
		
		
		//cs.readProblem("hbigPrices");
		//cs.initialize();
		//for(int i=0; i<1000; i++){
/*		int i=0;
			lecteur.lectureXml("scenarios-big-minimal.xml", i);
			start2=System.currentTimeMillis();
			cs.readProblem("souffleuse");
			cs.initialize();
			System.out.println(System.currentTimeMillis()-start2+"ms init");
			System.out.print(cs.getFreeVariables().size()+" ");
			System.out.println(cs.getFreeVariables());
			System.out.println();
			start=System.currentTimeMillis();
			for(int j=0; j<lecteur.dom.length; j++){
				start3=System.currentTimeMillis();
				System.out.println(lecteur.dom[j]+" -> "+cs.getCurrentDomainOf(lecteur.var[j]));
				cs.assignAndPropagate(lecteur.var[j], lecteur.dom[j]);
				System.out.println(cs.maxCost() +" " + cs.minCost());
				temps=System.currentTimeMillis()-start3;
				System.out.println(temps+"ms");
				cs.getFreeVariables();
				System.out.print(cs.getFreeVariables().size()+" ");
				System.out.println(cs.getFreeVariables());
			}
			temps=System.currentTimeMillis()-start;
			temps2=System.currentTimeMillis()-start2;
			System.out.println(i+"("+lecteur.dom.length+") "+temps+"ms "+temps2+"ms "+cs.isConfigurationComplete()+" "+cs.maxCost()+" "+cs.minCost());
			nbactions+=lecteur.dom.length;
			sum+=temps;
			sum2+=temps2;*/

			
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
			System.out.println(nbactions+" actions");
			
		}
			
}
