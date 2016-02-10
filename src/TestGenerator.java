import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;

public class TestGenerator {

	public static void main(String[] args) {
		FileWriter fichier;
		BufferedWriter output;
		Random r = new Random();
		try {
			for(int i = 0; i < 2; i++)
			{
				fichier = new FileWriter("datasets/testRB/set"+i+"_exemples.csv");
				output = new BufferedWriter(fichier);
				for(int j = 0; j < 150; j++)
				{
					int a = r.nextInt(3);
					int b = (int)(5*(a+Math.abs(r.nextGaussian())));
					int c = (int)(b+2+2*r.nextGaussian());
					int d = (c-2)/5;
					output.write(a+","+b+","+c+","+d);
					output.newLine();
				}
				output.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
