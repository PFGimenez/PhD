package preferences.penalty;

/**
 * Bayesian Information Criterion
 * @author pgimenez
 *
 */

public class BIC implements PenaltyWeightFunction
{
	@Override
	public double phi(int n)
	{
		return Math.log(n)/2;
	}

}
