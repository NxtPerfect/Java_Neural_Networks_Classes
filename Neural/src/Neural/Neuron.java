package Neural;

import java.util.Random;

public class Neuron {
	private double[] wagi;
	private int liczba_wejsc;
	private double poprzedniaSuma = 0;
	private double poprzedniaWartosc = 0;
	private double delta = 0;
	private static double eta = 0.1;

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
	
	public int LiczbaWejsc() {
		return liczba_wejsc;
	}

	public double oblicz_wyjscie(double[] wejscia) {
		double fi = wagi[0];
		// double fi=0.0;
		for (int i = 1; i <= liczba_wejsc; i++)
			fi += wagi[i] * wejscia[i - 1];
		poprzedniaSuma = fi;
		poprzedniaWartosc = fAktywacjiSigma(fi);
		return poprzedniaWartosc;
//		double wynik = 1.0 / (1.0 + Math.exp(-fi));// funkcja aktywacji sigma -unip
		// double wynik=(fi>0.0)?1.0:0.0;//skok jednostkowy
		// double wynik=fi; //f.a. liniowa
//		double wynik = Math.max(0, fi); // funkcja liniowa ReLU (Rectified Linear Unit)
//		return wynik;
	}
	
	public void UstawDelte(double delta) {
		this.delta = delta;
	}	
	
	public double DeltaRazyWagi(int n) {
		return delta*wagi[n];
	}
	
	public void ZmienWagi(double[] wejscie) {
		
		wagi[0] = eta*delta*fPochodna(poprzedniaSuma);
		for(int i=1;i<wagi.length;i++)
			wagi[i] += eta*delta*fPochodna(poprzedniaSuma)*wejscie[i-1];
			
		delta = 0.0;
		poprzedniaSuma=0;
		poprzedniaWartosc=0;
	}

	private double fAktywacjiSigma(double x) {
		return 1.0/(1.0+Math.exp(-x));
	}
	
	private double fPochodna(double x) {
		return fAktywacjiSigma(x)*(1.0-fAktywacjiSigma(x));
	}
	
    private double fAktywcjiLiniowa(double x) {
        return x;
    }

    private double fPochodnaLiniowa(double x) {
        return fAktywcjiLiniowa(x);
    }

    private double fAktywacjiTanh(double x) {
        return Math.tanh(x);
    }

    private double fPochodnaTanh(double x) {
        return 1.0 - Math.tanh(x) * Math.tanh(x);
    }

    //ReLU
    private double fAktywacjiReLU(double x) {
        return Math.max(0, x);
    }

    private double fPochodnaReLU(double x) {
        return x > 0 ? 1 : 0;
    }
}