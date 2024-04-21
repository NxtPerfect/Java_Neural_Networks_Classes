package Neural;

import java.util.Random;

public class Neuron {
	private double[] wagi;
	private double PoprzedniaSuma = 0;
	private double PoprzedniaWartosc = 0;
	private int liczbaWejsc;
	private static double LearningRate = 0.1;

	private double delta = 0;

	public Neuron() {
		liczbaWejsc = 0;
		wagi = null;
	}

	public Neuron(int liczba_wejsc) {
		this.liczbaWejsc = liczba_wejsc;
		wagi = new double[liczba_wejsc + 1];
		generuj();
	}

	private void generuj() {
		Random r = new Random();
		for (int i = 0; i <= liczbaWejsc; i++)
			// wagi[i]=(r.nextDouble()-0.5)*2.0*10;//do ogladania
			wagi[i] = (r.nextDouble() - 0.5) * 2.0 * 0.01;// do projektu
	}

	public int LiczbaWejsc() {
		return liczbaWejsc;
	}

	public double ObliczWyjscie(double[] wejscie) {
		double fi = wagi[0];
		for (int i = 1; i <= liczbaWejsc; i++)
			fi += wagi[i] * wejscie[i - 1];

		PoprzedniaSuma = fi;
		PoprzedniaWartosc = fAktywcjiSigma(fi);

		return PoprzedniaWartosc;
	}

	public void UstawDelte(double delta) {
		this.delta = delta;
	}

	public double DeltaRazyWagi(int n) {
		return delta * wagi[n];
	}

	public void ZmienWagi(double[] wejscie) {

		wagi[0] = LearningRate * delta * fPochodnaSigma(PoprzedniaSuma);
		for (int i = 1; i < wagi.length; i++)
			wagi[i] += LearningRate * delta * fPochodnaSigma(PoprzedniaSuma) * wejscie[i - 1];

		delta = 0.0;
		PoprzedniaSuma = 0;
		PoprzedniaWartosc = 0;
	}

	private double fAktywcjiSigma(double x) {
		return 1.0 / (1.0 + Math.exp(-x));
	}

	private double fPochodnaSigma(double x) {
		return fAktywcjiSigma(x) * (1.0 - fAktywcjiSigma(x));
	}

}
