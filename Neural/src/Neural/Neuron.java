package Neural;

import java.util.Random;

public class Neuron {
	private double[] wagi;
	private int liczba_wejsc;
	private double poprzedniaSuma = 0;
	private double poprzedniaWartosc = 0;
	private double delta = 0;
	private double dropoutProbability = 0.1;
	private final double ALPHA = 0.01; // You need the same slope value
	private final double ALPHAELU = 1.0; // You can adjust this ALPHAELU parameter
//	private static double eta = 0.1;

	public Neuron() {
		liczba_wejsc = 0;
		wagi = null;
	}

	public Neuron(int liczba_wejsc) {
		this.liczba_wejsc = liczba_wejsc;
		wagi = new double[liczba_wejsc + 1];
		generuj();
	}

	private void generuj() {
		Random r = new Random();
		for (int i = 0; i <= liczba_wejsc; i++)
//			wagi[i] = (r.nextDouble() - 0.5) * 2.0 * 10;// do ogladania
			wagi[i] = (r.nextDouble() - 0.5) * 2.0 * 0.01;// do projektu
	}

	public int liczbaWejsc() {
		return liczba_wejsc;
	}

	public double obliczWyjscie(double[] wejscia, boolean training) {
		double fi = wagi[0];
		// double fi=0.0;
		Random r = new Random();
		for (int i = 1; i <= liczba_wejsc; i++) {
			if (training && r.nextDouble() <= dropoutProbability) { // Apply dropout scaling at training time
				continue;
			}
			fi += wagi[i] * wejscia[i - 1];
		}
		poprzedniaSuma = fi;
		poprzedniaWartosc = fAktywacjiSigma(fi);
//		poprzedniaWartosc = fAktywacjiReLU(fi);
//		poprzedniaWartosc = fAktywacjiLeakyReLU(fi);
		return poprzedniaWartosc;
	}

	public void ustawDelte(double delta) {
		this.delta = delta;
	}

	public double deltaRazyWagi(int n) {
		return delta * wagi[n];
	}

	public void updateWeightsAdam(double[] wejscie, double[] m, double[] v, int t, double beta1,
			double beta2, double learningRate, double epsilon) {
		// Initialize moment estimates and timestamp
		if (m == null || v == null) {
			m = new double[wagi.length];
			v = new double[wagi.length];
			t = 0;
		}

		// Update timestamp
		t++;

		m[0] = beta1 * m[0] + (1 - beta1);
		// Update biased first moment estimate
		for (int i = 1; i < wagi.length; i++) {
			m[i] = beta1 * m[i] + (1 - beta1) * wejscie[i -1];
		}

		v[0] = beta2 * v[0] + (1 - beta2);
		// Update biased second moment estimate
		for (int i = 1; i < wagi.length; i++) {
			v[i] = beta2 * v[i] + (1 - beta2) * wejscie[i - 1] * wejscie[i - 1];
		}

		// Correct bias in moment estimates
		double[] mCorrected = new double[wagi.length];
		double[] vCorrected = new double[wagi.length];
		for (int i = 0; i < wagi.length; i++) {
			mCorrected[i] = m[i] / (1 - Math.pow(beta1, t));
			vCorrected[i] = v[i] / (1 - Math.pow(beta2, t));
		}

		// Update weights
		for (int i = 0; i < wagi.length; i++) {
			wagi[i] -= learningRate * mCorrected[i] / (Math.sqrt(vCorrected[i]) + epsilon);
		}
	}

	public void zmienWagi(double[] wejscie, double eta, double lambda) {
		wagi[0] += eta * delta * fPochodnaReLU(poprzedniaSuma);
		for (int i = 1; i < wagi.length; i++) {
			double gradient = delta * fPochodnaReLU(poprzedniaSuma) * wejscie[i - 1];
			double regularizationTerm = lambda * Math.signum(wagi[i]);
			wagi[i] += eta * (gradient + regularizationTerm);
		}
		delta = 0.0;
		poprzedniaSuma = 0;
		poprzedniaWartosc = 0;
	}

	public void zmienWagi(double[] wejscie, double eta) {
//		eta = 0.1;
		wagi[0] = eta * delta * fPochodnaSigma(poprzedniaSuma);
//		wagi[0] = eta * delta * fPochodnaReLU(poprzedniaSuma);
//		wagi[0] = eta * delta * fPochodnaLeakyReLU(poprzedniaSuma);
		for (int i = 1; i < wagi.length; i++) {
			wagi[i] += eta * delta * fPochodnaSigma(poprzedniaSuma) * wejscie[i - 1];
//			wagi[i] += eta * delta * fPochodnaReLU(poprzedniaSuma) * wejscie[i - 1];
//			wagi[i] += eta * delta * fPochodnaLeakyReLU(poprzedniaSuma) * wejscie[i - 1];
		}
		delta = 0.0;
		poprzedniaSuma = 0;
		poprzedniaWartosc = 0;
	}

	private double fAktywacjiSigma(double x) {
		return 1.0 / (1.0 + Math.exp(-x));
	}

	private double fPochodnaSigma(double x) {
		return fAktywacjiSigma(x) * (1.0 - fAktywacjiSigma(x));
	}

	private double fPochodnaRelu(double x) {
		return x > 0 ? 1 : 0;
	}

	private double fAktywacjiLiniowa(double x) {
		return x;
	}

	private double fPochodnaLiniowa(double x) {
		return fAktywacjiLiniowa(x);
	}

	private double fAktywacjiTanh(double x) {
		return Math.tanh(x);
	}

	private double fPochodnaTanh(double x) {
		return 1.0 - Math.tanh(x) * Math.tanh(x);
	}

	// ReLU
	private double fAktywacjiReLU(double x) {
		return Math.max(0, x);
	}

	private double fPochodnaReLU(double x) {
		return x > 0 ? 1 : 0;
	}

	// Leaky ReLU
	private double fAktywacjiLeakyReLU(double x) {
		return x >= 0 ? x : ALPHA * x;
	}

	private double fPochodnaLeakyReLU(double x) {
		return x >= 0 ? 1 : ALPHA;
	}

	// ELU
	private double fAktywacjiELU(double x) {
		return x >= 0 ? x : ALPHAELU * (Math.exp(x) - 1);
	}

	private double fPochodnaELU(double x) {
		return x >= 0 ? 1 : ALPHAELU * Math.exp(x);
	}
}