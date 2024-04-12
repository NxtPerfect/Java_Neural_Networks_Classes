package Neural;

import java.util.ArrayList;

public class Siec {
	Warstwa[] warstwy;
	int liczba_warstw;
	private static final int liczbaEpok = 100;
	private static final double EPS = 0.1;
	private static final double learningRateDecay = 0.55;
	private static int attempts = 20;

	public Siec() {
		warstwy = null;
		this.liczba_warstw = 0;
	}

	public Siec(int liczba_wejsc, int liczba_warstw, int[] lnww) {
		this.liczba_warstw = liczba_warstw;
		warstwy = new Warstwa[liczba_warstw];
		for (int i = 0; i < liczba_warstw; i++)
			warstwy[i] = new Warstwa((i == 0) ? liczba_wejsc : lnww[i - 1], lnww[i]);
	}

	double[] obliczWyjscie(double[] wejscia) {
		double[] wyjscie = null;
		for (int i = 0; i < liczba_warstw; i++)
			wyjscie = warstwy[i].obliczWyjscie(wejscia);
		return wyjscie;
	}

	private double computeMSE(double[] predicted, double[] actual) {
		if (predicted.length != actual.length) {
			throw new IllegalArgumentException("Arrays must have the same length");
		}

		double sum = 0.0;
		for (int i = 0; i < predicted.length; i++) {
			double error = predicted[i] - actual[i];
			sum += error * error; // Squaring the error
		}

		return sum / predicted.length; // Computing the average
	}

	void UczSieZCiagu(ArrayList<double[]> DaneWejsciowe, ArrayList<double[]> DaneWyjsciowe) {
		double obecnyEPS = EPS;
		for (int epoki = 0; epoki < liczbaEpok; epoki++) {
			int LPopOdp = 0;
			double totalLoss = 0;
			double lastAverageLoss = 1;

			for (int nrLitery = 0; nrLitery < DaneWejsciowe.size(); nrLitery++) {
				double[] Wyjscie = DaneWejsciowe.get(nrLitery);
				double[] PopWyjscie = DaneWyjsciowe.get(nrLitery);
				double delta[] = new double[PopWyjscie.length];
				for (int i = 0; i < liczba_warstw; i++)
					Wyjscie = warstwy[i].obliczWyjscie(Wyjscie);

				double loss = computeMSE(Wyjscie, PopWyjscie);
				totalLoss += loss;

				boolean CzyBlisko = true;
				for (int i = 0; i < Wyjscie.length; i++)
					if (Math.abs(delta[i] = (PopWyjscie[i] - Wyjscie[i])) > EPS)
						CzyBlisko = false;
				if (CzyBlisko)
					LPopOdp++;
				for (int i = warstwy.length - 1; i > 0; i--) {
					warstwy[i].ustawDelteWNeuronach(delta);
					delta = warstwy[i].obliczDolnaWarstwaDelta();
				}
				warstwy[0].ustawDelteWNeuronach(delta);
				for (int i = 0; i < warstwy.length; i++)
					warstwy[i].zmienWagi(obecnyEPS);
			}

			double averageLoss = totalLoss / DaneWejsciowe.size();
			if (averageLoss > lastAverageLoss) attempts -= 1;
			lastAverageLoss = averageLoss;
			double accuracy = (double) (LPopOdp / DaneWejsciowe.size()) * 100.0;
			System.out.println("Epoka: " + epoki + " Loss: " + String.format("%.6f", averageLoss) + " Accuracy: "
					+ accuracy + "%");
			if (LPopOdp == DaneWejsciowe.size() && epoki >= 0.7 * liczbaEpok)
				break;
			if (attempts == 0)
				break;
			MLP.labelLiczbaEpok.setText("Epoki: " + epoki);
			obecnyEPS *= learningRateDecay;
		}
	}

}
