public class ExponentialSmoothing {
	
	public static final double[] data = {362, 385, 432, 341, 382, 409, 498, 387, 473, 513, 582, 474, 544, 582, 681, 557, 628, 707, 773, 592, 627, 725, 854, 661};
	public static final int predLen = 10;
	public static final int inputLen = data.length - predLen;

	public static void main(String[] args) {
		double[] s = SES(0.3);
		double[] d = DES(0.3, 0.4);
		double[] t = TES(0.3, 0.4, 0.8, 4);
		for(int i = 0; i < data.length; i++){
			System.out.println(data[i] +"\t"+ s[i] +"\t"+ d[i] +"\t"+ t[i]);
		}
		System.out.println(err(s));
		System.out.println(err(d));
		System.out.println(err(t));
	}
	
	public static double[] SES(double alpha){
		double[] S = new double[data.length];
		// init
		S[0] = data[0];
		// smooth
		for(int t = 1; t < inputLen; t++){
			S[t] = alpha*data[t] + (1-alpha)*S[t-1];
		}
		// forecast
		int t = inputLen-1;
		for(int m = 1; m <= predLen; m++){
			S[t+m] = alpha*S[t] + (1-alpha)*S[t+m-1];
		}
		return S;
	}
	
	public static double[] DES(double alpha, double gama){
		double[] S = new double[data.length];
		double[] b = new double[data.length];
		// init
		S[0] = data[0];
		b[0] = data[1] - data[0];
		// smooth
		for(int t = 1; t < inputLen; t++){
			S[t] = alpha*data[t] + (1-alpha)*(S[t-1] + b[t-1]);
			b[t] = gama*(S[t] - S[t-1]) + (1-gama)*b[t-1];
		}
		// forecast
		int t = inputLen-1;
		for(int m = 1; m <= predLen; m++){
			S[t+m] = S[t] + m*b[t];
		}
		return S;
	}
	
	public static double[] TES(double alpha, double gama, double beta, int L){
		double[] S = new double[data.length];
		double[] b = new double[data.length];
		double[] I = new double[data.length];
		// init
		S[L-1] = data[L-1];
		double sum = 0;
		for(int i = 0; i < L; i++){
			sum += (data[L+i] - data[i]) / L;
		}
		b[L-1] = sum / L;
		int N = data.length / L;
		double[] A = new double[N];
		for(int p = 0; p < N; p++){
			sum = 0;
			for(int i = 0; i < L; i++){
				sum += data[p*L + i];
			}
			A[p] = sum / L;
		}
		for(int i = 0; i < L; i++){
			sum = 0;
			for(int p = 0; p < N; p++){
				sum += data[p*L + i] / A[p];
			}
			I[i] = sum / N;
		}
		// smooth
		for(int t = L; t < inputLen; t++){
			S[t] = alpha*(data[t] / I[t-L]) + (1-alpha)*(S[t-1] + b[t-1]);
			b[t] = gama*(S[t] - S[t-1]) + (1-gama)*b[t-1];
			I[t] = beta*(data[t] / S[t]) + (1-beta)*I[t-L];
		}
		// forecast
		int t = inputLen-1;
		for(int m = 1; m <= predLen; m++){
			S[t+m] = (S[t] + m*b[t]) * I[t-L+1+((m-1)%L)];
		}
		return S;
	}
	
	public static double err(double[] pred){
		double sum = 0;
		for(int i = inputLen; i < data.length; i++){
			double dif = data[i] - pred[i];
			sum += dif*dif;
		}
		return sum/predLen;
	}

}
