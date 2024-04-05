package Neural;

import java.util.ArrayList;

public class Siec {
	Warstwa[] warstwy;
	int liczba_warstw;
	private static final int liczbaEpok = 500;
	private static final double EPS = 0.25;

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
			wejscia = wyjscie = warstwy[i].obliczWyjscie(wejscia);
		return wyjscie;
	}

	void UczSieZCiagu(ArrayList<double[]> DaneWejsciowe, ArrayList<double[]> DaneWyjsciowe) {

		for (int epoki = 0; epoki < liczbaEpok; epoki++) {
			int LPopOdp = 0;

			for (int nrLitery = 0; nrLitery < DaneWejsciowe.size(); nrLitery++) {
				double[] Wyjscie = DaneWejsciowe.get(nrLitery);
				double[] PopWyjscie = DaneWyjsciowe.get(nrLitery);
				double delta[] = new double[PopWyjscie.length];
				for (int i = 0; i < liczba_warstw; i++)
					Wyjscie = warstwy[i].obliczWyjscie(Wyjscie);

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
					warstwy[i].zmienWagi();
			}
			if (LPopOdp == DaneWejsciowe.size())
				break;
			MLP.labelLiczbaEpok.setText("Liczba Epok Nauki: " + epoki);
		}
	}

}
