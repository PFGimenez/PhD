package preferences.loiProbabilite;

import java.math.BigInteger;

/**
 * Interface d'une loi de probabilité
 * @author pgimenez
 *
 */

public interface Loi
{
	public double getVraisemblance(BigInteger rang);
}
