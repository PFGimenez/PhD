package utilitaires;

public class TrouveContreexemple {

	public static void main(String[] args)
	{

		int s1 = 0, s2 = 0, s3 = 0, s4 = 0;
		int max = 10000;
		for(int i = max/3; i <= max; i+=5)
		{
			System.out.println(i);
			for(int j = (max-i)/2; j <= Math.min(i,max-i); j+=5)
			{
				int k = max-i-j;
				if(k > j)
					continue;
				for(int l = max/2; l <= max; l++)
				{
					boolean x = false;
					boolean y = false;
					for(int n = Math.max(i/2, l/3); n <= Math.min(i, l); n++)
						for(int o = 0; o <= Math.min(j, l-n); o++)
						{
							if(Math.min(n, i-n) >= Math.max(o, j-l) && Math.min(o, j-l) >= Math.max(l-o-n, k-l+o+n))
							{
								s1 = n;
								s2 = o;
								x = true;
							}
							else if(Math.min(Math.min(n, o),l-o-n) >= Math.max(Math.max(i-n, j-o), k-l+o+n))
							{
								s3 = n;
								s4 = o;
								y = true;
							}
						}
					if(x && y)
						System.out.println(i+" "+j+" "+l+" "+" "+s1+" "+s2+" "+s3+" "+s4);
				}
			}
		}
		
/*		int max = 10000;
		for(int i = 8800; i < max; i++)
		{
			System.out.println(i);
			for(int j = 0; j < Math.min(i,max-i); j++)
				for(int k = 0; k < Math.min(j,max-i-j); k++)
					for(int l = 0; l < Math.min(k,max-i-j-k); l++)
						for(int m = 0; m < Math.min(l,max-i-j-k-l); m++)
						{
							int n = max-i-j-k-l-m;
							if(n >= m)
								continue;

							double[] t = {(i+j)/((double)max), (k+l)/((double)max), (m+n)/((double)max)};
							double e1 = 0;
							for(int a = 0; a < t.length; a++)
								if(t[a] != 0)
									e1 -= t[a]*Math.log(t[a]);
							e1 /= Math.log(3);
							double[] t2 = {(i+k+m)/((double)max), (j+l+n)/((double)max)};
							double e2 = 0;
							for(int a = 0; a < t2.length; a++)
								if(t2[a] != 0)
									e2 -= t2[a]*Math.log(t2[a]);
							e2 /= Math.log(2);
							if(e2 < e1)
								System.out.println(i+" "+j+" "+k+" "+l+" "+m+" "+n+" "+e1+" "+e2);
						}
		}
		*/
	}
}
