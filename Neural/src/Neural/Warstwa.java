package Neural;

public class Warstwa {
	Neuron[] neurony;
	int liczba_neuronow;
	double[] PopWejscie;

	public Warstwa() {
		neurony = null;
		liczba_neuronow = 0;
	}

	public Warstwa(int liczba_wejsc, int liczba_neuronow) {
		this.liczba_neuronow = liczba_neuronow;
		neurony = new Neuron[liczba_neuronow];
		for (int i = 0; i < liczba_neuronow; i++)
			neurony[i] = new Neuron(liczba_wejsc);
	}

	double[] obliczWyjscie(double[] wejscie, boolean training) {
		PopWejscie = wejscie;
		double[] wyjscie = new double[liczba_neuronow];
		for (int i = 0; i < liczba_neuronow; i++)
			wyjscie[i] = neurony[i].obliczWyjscie(wejscie, training);
		return wyjscie;
	}

	public double[] obliczDolnaWarstwaDelta() {
		int LicznikNeuronowDolnejWar = neurony[0].liczbaWejsc();
		double[] delta = new double[LicznikNeuronowDolnejWar];

		for (int i = 0; i < LicznikNeuronowDolnejWar; i++) {
			for (int j = 0; j < neurony.length; j++) {
				delta[i] += neurony[j].deltaRazyWagi(i);
			}
		}
		return delta;
	}

	public void ustawDelteWNeuronach(double[] delta) {
		for (int i = 0; i < delta.length; i++) {
			neurony[i].ustawDelte(delta[i]);
		}
	}

	public void zmienWagi(double eps) {
		for (int i = 0; i < neurony.length; i++)
			neurony[i].zmienWagi(PopWejscie, eps);
	}
}
