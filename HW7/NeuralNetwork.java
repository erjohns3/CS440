import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;

public class NeuralNetwork {

	public static void main(String[] args) {
		final double e = 2.71828;
		final int h = 1;
		double[][] w1 = new double[h][100];
		double[] b1 = new double[h];
		double[][] w2 = new double[1][h];
		double b2 = 1;

		int[] y0 = new int[100];
		double[] x1 = new double[h];
		double[] y1 = new double[h];
		double x2;
		double y2;

		double error = 0;

		BufferedReader br = null;
		String currentLine = null;

		for(int i=0; i< 7000; i++){
			try {
				br = new BufferedReader(new FileReader("mushroom-training.txt"));
				currentLine = br.readLine();
			} catch (final IOException ioe) {
				ioe.printStackTrace();
			}

			String[] splits = currentLine.split(",");
			int t = splits[0].charAt(0) - '0';
			for(int j=0; j<100; j++){
				y0[j] = splits[j+1].charAt(0) - '0';
			}

			x2 = b2;
			for(int j=0; j<h; j++){
				x1[j] = b1[j];
				for(int k=0; k<100; k++){
					x1[j] += w1[j][k] * y0[k];
				}
				y1[j] = 1/(1+Math.pow(e,-x1[j]));
				x2 += w2[1][j] * y1[j];
			}
			y2 = 1/(1+Math.pow(e,-x2));

			error += Math.pow(t - y2, 2) / 2;
		}
	}

}
