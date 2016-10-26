package preferences.penalty;

/**
 * Akaike Information Criterion
 * @author pgimenez
 *
 */

public class AIC implements PenaltyWeightFunction
{
	private double c;
	
	public AIC(double c)
	{
		this.c = c;
	}
	
	@Override
	public double phi(int n)
	{
		return c;
	}

}
