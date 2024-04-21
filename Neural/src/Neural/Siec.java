package Neural;

import java.util.ArrayList;
import java.util.Collections;
import java.util.ArrayList;

public class Siec {

	static Warstwa[] warstwy;
	static int liczbaWarstw;
	private static final int MaksLiczbaEpok = 12000;
	private static final double MarginesBledu = 0.2; // Maksymalny margines błędu
	private static int patience = 100; // Cierpliwość sieci, po dojściu do zera przerywa trenowanie

	public Siec() {
		warstwy = null;
	}

	public Siec(int liczba_wejsc, int liczba_warstw, int[] lnww) {
		this.liczbaWarstw = liczba_warstw;
		warstwy = new Warstwa[liczba_warstw];
		for (int i = 0; i < liczba_warstw; i++)
			warstwy[i] = new Warstwa((i == 0) ? liczba_wejsc : lnww[i - 1], lnww[i]);
	}

	// Oblicza wyjście warstwy
	double[] ObliczWyjscie(double[] wejscie) {
		double[] wyjscie = null;
		for (int i = 0; i < liczbaWarstw; i++)
			wejscie = wyjscie = warstwy[i].ObliczWyjscie(wejscie);
		return wyjscie;
	}

	// Trenuje sieć
	void Trenuj(ArrayList<double[]> DaneWejsciowe, ArrayList<double[]> DaneWyjsciowe) {
		int OstatniaLiczbaPopOdp = 0;
		ArrayList<Integer> litery = new ArrayList<Integer>();

		// Dodaj liczby od 0 do wielkości danych wejściowych
		// wykorzystuje to do losowania danych wejściowych
		// aby wymieszać dane wejściowe dla każdej epoki
		// w celu zapobiegnięcia overfittingu
		for (int i = 0; i < DaneWejsciowe.size(); i++)
			litery.add(i);

		for (int epoki = 0; epoki < MaksLiczbaEpok; epoki++) {
			int LiczbaPopOdp = 0;

			// Przemieszaj dane wejściowe
			Collections.shuffle(litery);
			for (int nrLitery : litery) {
				double[] Wyjscie = DaneWejsciowe.get(nrLitery);
				double[] PopWyjscie = DaneWyjsciowe.get(nrLitery);
				double delta[] = new double[PopWyjscie.length];
				for (int i = 0; i < liczbaWarstw; i++)
					Wyjscie = warstwy[i].ObliczWyjscie(Wyjscie);

				boolean CzyBlisko = true;
				for (int i = 0; i < Wyjscie.length; i++)
					// Jeśli delta była większe niż marginesBledu, to nie jest blisko
					if (Math.abs(delta[i] = (PopWyjscie[i] - Wyjscie[i])) > MarginesBledu)
						CzyBlisko = false;

				// Jeśli blisko ++
				// inaczej 0
				LiczbaPopOdp += CzyBlisko ? 1 : 0;
				for (int i = warstwy.length - 1; i > 0; i--) {
					warstwy[i].UstawDeleteWNeuronach(delta);
					delta = warstwy[i].ObliczDolnaWarstwaDelta();
				}
				warstwy[0].UstawDeleteWNeuronach(delta);
				for (int i = 0; i < warstwy.length; i++)
					warstwy[i].ZmienWagi();
			}
			System.out.println("Poprawne/wszystkie: " + LiczbaPopOdp + "/" + DaneWejsciowe.size() + " Celność: "
					+ String.format("%.2f", ((double) LiczbaPopOdp / (double) DaneWejsciowe.size()) * 100.0)
					+ "% Epoka: " + epoki);
			if (LiczbaPopOdp <= OstatniaLiczbaPopOdp)
				patience--;
			if (LiczbaPopOdp == DaneWejsciowe.size() || patience <= 0)
				break;
			Test1.labelLiczbaEpok.setText("Liczba Epok Trenowania: " + epoki);
			OstatniaLiczbaPopOdp = LiczbaPopOdp;
		}
	}

//	void Trenuj(ArrayList<double[]> DaneWejsciowe, ArrayList<double[]> DaneWyjsciowe) {
//		int OstatniaLiczbaPopOdp = 0;
//		for (int epoki = 0; epoki < MaksLiczbaEpok; epoki++) {
//			int LiczbaPopOdp = 0;
//
//			for (int nrLitery = 0; nrLitery < DaneWejsciowe.size(); nrLitery++) {
//				double[] Wyjscie = DaneWejsciowe.get(nrLitery);
//				double[] PopWyjscie = DaneWyjsciowe.get(nrLitery);
//				double delta[] = new double[PopWyjscie.length];
//				for (int i = 0; i < liczbaWarstw; i++)
//					Wyjscie = warstwy[i].ObliczWyjscie(Wyjscie);
//
//				boolean CzyBlisko = true;
//				for (int i = 0; i < Wyjscie.length; i++)
//					if (Math.abs(delta[i] = (PopWyjscie[i] - Wyjscie[i])) > MarginesBledu)
//						CzyBlisko = false;
//				if (CzyBlisko)
//					LiczbaPopOdp++;
//				for (int i = warstwy.length - 1; i > 0; i--) {
//					warstwy[i].UstawDeleteWNeuronach(delta);
//					delta = warstwy[i].ObliczDolnaWarstwaDelta();
//				}
//				warstwy[0].UstawDeleteWNeuronach(delta);
//				for (int i = 0; i < warstwy.length; i++)
//					warstwy[i].ZmienWagi();
//			}
//			System.out.println(LiczbaPopOdp + "/" + DaneWejsciowe.size() + " " + String.format("%.2f",((double) LiczbaPopOdp / (double) DaneWejsciowe.size()) * 100.0) + "% " + epoki);
//			if (LiczbaPopOdp <= OstatniaLiczbaPopOdp)
//				patience--;
//			if (LiczbaPopOdp == DaneWejsciowe.size() || patience <= 0)
//				break;
//			Test1.labelLiczbaEpok.setText("Liczba Epok Trenowania: " + epoki);
//			OstatniaLiczbaPopOdp = LiczbaPopOdp;
//		}
//	}
}
