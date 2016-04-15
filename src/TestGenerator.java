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
				for(int j = 0; j < 5000; j++)
				{
					int a = (int) Math.abs(2*r.nextGaussian()) % 4;
					int b = (int) Math.abs(2*r.nextGaussian()) % 3;
					int c = (int) Math.abs(2*r.nextGaussian()) % 5;
					int d = (a+b+c)/3;
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
