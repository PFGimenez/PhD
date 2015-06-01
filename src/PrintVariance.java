import test_independance.*;
import br4cp.SALADD;
import br4cp.Variance;

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
 * Affiche les variances pour les tests d'indépendance
 * @author pgimenez
 *
 */

public class PrintVariance {

	public static void main(String[] args) {
		TestIndependance test = new TestEcartMax();
//		TestIndependance test = new TestKhi2Correction();
//		TestIndependance test = new Testl1moyenne();
//		TestIndependance test = new Testl1max();
//		TestIndependance test = new TestKhi2Max();
//		TestIndependance test = new Testl2moyenne();
//		TestIndependance test = new Testmediane();
//		TestIndependance test = new TestSommeMediane();
//		TestIndependance test = new TestVariancePonderee();
		/*
		SALADD saladd = new SALADD();
		saladd.compilationDHistorique("datasets/set0.xml", 2);
		saladd.calculerVarianceHistorique(test, "smallhist/smallvariance");
		Variance s = saladd.getVariance();
		s.string();
		s.printOrder(test);
		s.printNbModalites();*/ //TODO
	}
}
