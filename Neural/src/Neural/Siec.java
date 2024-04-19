package Neural;

import java.util.ArrayList;
import java.util.Collections;

public class Siec {
	Warstwa[] warstwy;
	int liczba_warstw;
	private static final int liczbaEpok = 10000;
	private static final double EPS = 0.1;
	private static final double learningRate = 0.01;
	private static final double lambda = 0.0001;
	private static int patience = 400;

	// Adam
	private double beta1 = 0.9; // Exponential decay rates for moment estimates
	private double beta2 = 0.999;
	private double epsilon = 1e-8;
	private double[] m; // First moment estimate
	private double[] v; // Second moment estimate
	private int t = 0; // Timestamp

	public Siec() {
		warstwy = null;
		this.liczba_warstw = 0;
	}

	public Siec(int liczba_wejsc, int liczba_warstw, int[] lnww) {
		this.liczba_warstw = liczba_warstw;
		warstwy = new Warstwa[liczba_warstw];
		for (int i = 0; i < liczba_warstw; i++)
			warstwy[i] = new Warstwa((i == 0) ? liczba_wejsc : lnww[i - 1], lnww[i]);
		m = new double[liczba_wejsc + 1];
		v = new double[liczba_wejsc + 1];
	}

	double[] obliczWyjscie(double[] wejscie, boolean training) {
		double[] wyjscie = null;
		for (int i = 0; i < liczba_warstw; i++)
			wejscie = wyjscie = warstwy[i].obliczWyjscie(wejscie, training);
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

	void UczSieZCiaguLambda(ArrayList<double[]> DaneWejsciowe, ArrayList<double[]> DaneWyjsciowe, boolean training) {
		double bestLambda = 0.0;
		double bestValidationLoss = Double.MAX_VALUE;

		// Define range of lambda values
		double[] lambdaValues = { 0.001, 0.01, 0.1, 1.0, 10.0 }; // Example values, adjust as needed
		double averageLoss = 0;

		for (double lambda : lambdaValues) {
			double obecnyLearningRate = learningRate;

			ArrayList<Integer> indices = new ArrayList<>();
			for (int i = 0; i < DaneWejsciowe.size(); i++) {
				indices.add(i);
			}
			double lastAverageLoss = 1;
			for (int epoki = 0; epoki < liczbaEpok; epoki++) {
				int LPopOdp = 0;
				double totalLoss = 0;

				Collections.shuffle(indices);

				for (int idx : indices) {
					double[] Wyjscie = DaneWejsciowe.get(idx);
					double[] PopWyjscie = DaneWyjsciowe.get(idx);
					double delta[] = new double[PopWyjscie.length];
					for (int i = 0; i < liczba_warstw; i++)
						Wyjscie = warstwy[i].obliczWyjscie(Wyjscie, training);

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
						warstwy[i].zmienWagi(obecnyLearningRate, lambda);
				}

				averageLoss = totalLoss / DaneWejsciowe.size();
				if (averageLoss >= lastAverageLoss) {
					patience--;
					System.out.println("Lost patience: " + patience);
				}
				if (LPopOdp >= DaneWejsciowe.size() * 0.9) {
					patience--;
					System.out.println("Lost patience: " + patience);
				}
				lastAverageLoss = averageLoss;
				double accuracy = (double) ((double) LPopOdp / (double) DaneWejsciowe.size()) * 100.0;
				System.out.println("Epoka: " + epoki + " Loss: " + String.format("%.6f", averageLoss) + " Accuracy: "
						+ String.format("%.2f", accuracy) + "%");
				System.out.println(LPopOdp + " / " + DaneWejsciowe.size());
				if (LPopOdp == DaneWejsciowe.size()) {
					System.out.println("Avoiding overfitting.");
					break;
				}
				if (patience <= 0) {
					System.out.println("Ran out of patience.");
					break;
				}
				MLP.labelLiczbaEpok.setText("Epoki: " + epoki);
				MLP.labelIloscCU.setText("Ilosc CU: " + DaneWejsciowe.size());
//			obecnyLearningRate *= learningRateDecay;
			}
			if (averageLoss < bestValidationLoss) {
				bestValidationLoss = averageLoss;
				bestLambda = lambda;
			}
		}
		System.out.println("Best loss: " + bestValidationLoss + "for lambda: " + bestLambda);
	}

	void UczSieZCiagu(ArrayList<double[]> DaneWejsciowe, ArrayList<double[]> DaneWyjsciowe, boolean training) {
		double obecnyLearningRate = learningRate;

		ArrayList<Integer> indices = new ArrayList<>();
		for (int i = 0; i < DaneWejsciowe.size(); i++) {
			indices.add(i);
		}
		double lastAverageLoss = 1;
		for (int epoki = 0; epoki < liczbaEpok; epoki++) {
			int LPopOdp = 0;
			double totalLoss = 0;

			Collections.shuffle(indices);

			for (int idx : indices) {
				double[] Wyjscie = DaneWejsciowe.get(idx);
				double[] PopWyjscie = DaneWyjsciowe.get(idx);
				double delta[] = new double[PopWyjscie.length];
				for (int i = 0; i < liczba_warstw; i++)
					Wyjscie = warstwy[i].obliczWyjscie(Wyjscie, training);

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
					warstwy[i].zmienWagi(obecnyLearningRate);
//					warstwy[i].zmienWagi(obecnyLearningRate, m, v, t, beta1, beta2, learningRate, epsilon);
			}

			double averageLoss = totalLoss / DaneWejsciowe.size();
			if (averageLoss >= lastAverageLoss) {
				patience--;
//				System.out.println("Lost patience: " + patience);
			}
			if (LPopOdp >= DaneWejsciowe.size() * 0.9) {
				patience--;
//				System.out.println("Lost patience: " + patience);
			}
			lastAverageLoss = averageLoss;
			double accuracy = (double) ((double) LPopOdp / (double) DaneWejsciowe.size()) * 100.0;
//			if (epoki % 100 == 0) {
			System.out.println("Epoka: " + epoki + " Loss: " + String.format("%.6f", averageLoss) + " Accuracy: "
					+ String.format("%.2f", accuracy) + "%");
			System.out.println(LPopOdp + " / " + DaneWejsciowe.size());
//			}
			if (LPopOdp == DaneWejsciowe.size()) {
//			if (LPopOdp == DaneWejsciowe.size() || patience == 0) {
				System.out.println("Avoiding overfitting.");
				break;
			}
			if (patience <= 0) {
				System.out.println("Ran out of patience.");
				break;
			}
			MLP.labelLiczbaEpok.setText("Epoki: " + epoki);
			MLP.labelIloscCU.setText("Ilosc CU: " + DaneWejsciowe.size());
//			obecnyLearningRate *= learningRateDecay;
		}
	}

}
